package org.xbib.oai.client.listrecords;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.util.AsciiString;
import org.xbib.content.xml.transform.TransformerURIResolver;
import org.xbib.content.xml.util.XMLUtil;
import org.xbib.helianthus.common.http.AggregatedHttpMessage;
import org.xbib.oai.client.AbstractOAIResponse;
import org.xbib.oai.client.TooManyRequestsException;
import org.xbib.oai.exceptions.BadArgumentException;
import org.xbib.oai.exceptions.BadResumptionTokenException;
import org.xbib.oai.exceptions.NoRecordsMatchException;
import org.xbib.oai.exceptions.OAIException;
import org.xbib.oai.util.ResumptionToken;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    private static final Logger logger = Logger.getLogger(ListRecordsResponse.class.getName());
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
    public void receivedResponse(AggregatedHttpMessage message, Writer writer) throws IOException {
        String content = message.content().toStringUtf8();
        int status = message.status().code();
        if (status == 503) {
            long secs = retryAfterMillis / 1000;
            if (message.headers() != null) {
                for (String retryAfterHeader : RETRY_AFTER_HEADERS) {
                    String retryAfter = message.headers().get(AsciiString.of(retryAfterHeader));
                    if (retryAfter == null) {
                        continue;
                    }
                    secs = Long.parseLong(retryAfter);
                    if (!isDigits(retryAfter)) {
                        // parse RFC date, e.g. Fri, 31 Dec 1999 23:59:59 GMT
                        Instant instant = Instant.from(DateTimeFormatter.RFC_1123_DATE_TIME.parse(retryAfter));
                        secs = ChronoUnit.SECONDS.between(instant, Instant.now());
                        logger.log(Level.INFO, MessageFormat.format("parsed delay seconds is {0}", secs));
                    }
                    logger.log(Level.INFO, MessageFormat.format("setting delay seconds to {0}", secs));
                }
            }
            request.setRetry(true);
            try {
                if (secs > 0L) {
                    logger.log(Level.INFO, MessageFormat.format("waiting for {0} seconds (retry-after)", secs));
                    Thread.sleep(1000 * secs);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.log(Level.SEVERE, "interrupted");
            }
            return;
        }
        if (status == 429) {
            throw new TooManyRequestsException();
        }
        if (status != 200) {
            throw new IOException("status  = " + status + " response = " + content);
        }
        // activate XSLT only if OAI XML content type is returned
        String contentType = message.headers().get(HttpHeaderNames.CONTENT_TYPE);
        if (contentType != null && !contentType.startsWith("text/xml")) {
            throw new IOException("no XML content type in response: " + contentType);
        }
        this.filterreader = new ListRecordsFilterReader(request, this);
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setURIResolver(new TransformerURIResolver("xsl"));
            Transformer transformer = transformerFactory.newTransformer();
            Source source = new SAXSource(filterreader, new InputSource(new StringReader(XMLUtil.sanitize(content))));
            StreamResult streamResult = new StreamResult(writer);
            logger.log(Level.FINE, "transforming");
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
            } else if (error != null) {
                throw new OAIException(error);
            }
        } catch (TransformerException t) {
            throw new IOException(t);
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
