package org.xbib.oai.client.listrecords;

import org.xbib.content.xml.transform.TransformerURIResolver;
import org.xbib.content.xml.util.XMLUtil;
import org.xbib.netty.http.common.HttpResponse;
import org.xbib.oai.client.AbstractOAIResponse;
import org.xbib.oai.exceptions.BadVerbException;
import org.xbib.oai.exceptions.BadArgumentException;
import org.xbib.oai.exceptions.BadResumptionTokenException;
import org.xbib.oai.exceptions.NoRecordsMatchException;
import org.xbib.oai.exceptions.OAIException;
import org.xbib.oai.util.ResumptionToken;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

/**
 *
 */
public class ListRecordsResponse extends AbstractOAIResponse {

    private static final String[] RETRY_AFTER_HEADERS = {
            "retry-after", "Retry-after", "Retry-After"
    };

    private final ListRecordsRequest request;

    private ListRecordsFilterReader filterreader;

    private long retryAfterMillis;

    private String error;

    private Instant date;

    public ListRecordsResponse(ListRecordsRequest request) {
        this.request = request;
        this.retryAfterMillis = 20L * 1000L; // 20 seconds by default
    }

    public ListRecordsResponse setRetryAfter(long millis) {
        this.retryAfterMillis = millis;
        return this;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public Instant getDate() {
        return date;
    }

    @Override
    public void receivedResponse(HttpResponse message, Writer writer) throws OAIException {
        String content = message.getBodyAsString(StandardCharsets.UTF_8);
        int status = message.getStatus().getCode();
        if (status == 503) {
            long secs = retryAfterMillis / 1000;
            if (message.getHeaders() != null) {
                for (String retryAfterHeader : RETRY_AFTER_HEADERS) {
                    String retryAfter = message.getHeaders().getHeader(retryAfterHeader);
                    if (retryAfter == null) {
                        continue;
                    }
                    secs = Long.parseLong(retryAfter);
                    if (!isDigits(retryAfter)) {
                        // parse RFC date, e.g. Fri, 31 Dec 1999 23:59:59 GMT
                        Instant instant = Instant.from(DateTimeFormatter.RFC_1123_DATE_TIME.parse(retryAfter));
                        secs = ChronoUnit.SECONDS.between(instant, Instant.now());
                    }
                }
            }
            request.setRetry(true);
            try {
                if (secs > 0L) {
                    Thread.sleep(1000 * secs);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return;
        }
        if (status == 429) {
            try {
                Thread.sleep(10000L);
            } catch (InterruptedException e) {
                // ignore
            }
        }
        if (status != 200) {
            throw new OAIException("status  = " + status + " response = " + content);
        }
        // activate XSLT only if OAI XML content type is returned
        String contentType = message.getHeaders().getHeader("content-type");
        if (contentType != null && !contentType.startsWith("text/xml")) {
            throw new OAIException("no XML content type in response: " + contentType);
        }
        this.filterreader = new ListRecordsFilterReader(request, this);
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setURIResolver(new TransformerURIResolver("xsl"));
            Transformer transformer = transformerFactory.newTransformer();
            Source source = new SAXSource(filterreader, new InputSource(new StringReader(XMLUtil.sanitize(content))));
            StreamResult streamResult = new StreamResult(writer);
            transformer.transform(source, streamResult);
            if ("noRecordsMatch".equals(error)) {
                throw new NoRecordsMatchException("metadataPrefix=" + request.getMetadataPrefix()
                        + ",set=" + request.getSet()
                        + ",from=" + request.getFrom()
                        + ",until=" + request.getUntil());
            } else if ("badResumptionToken".equals(error)) {
                throw new BadResumptionTokenException(request.getResumptionToken());
            } else if ("badArgument".equals(error)) {
                throw new BadArgumentException();
            } else if ("badVerb".equals(error)) {
                throw new BadVerbException(error);
            } else if (error != null) {
                throw new OAIException(error);
            }
        } catch (TransformerException t) {
            throw new OAIException(t);
        }
    }

    private boolean isDigits(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public ResumptionToken<?> getResumptionToken() {
        return filterreader != null ? filterreader.getResumptionToken() : null;
    }

}
