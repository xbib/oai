package org.xbib.oai.client;

import org.xbib.oai.OAIConstants;
import org.xbib.oai.OAIRequest;
import org.xbib.oai.util.ResumptionToken;
import org.xbib.oai.util.URIBuilder;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Client OAI request
 */
public class ClientOAIRequest implements OAIRequest {

    private static final Logger logger = Logger.getLogger(ClientOAIRequest.class.getName());

    private URIBuilder uriBuilder;

    private DateTimeFormatter dateTimeFormatter;

    private ResumptionToken<?> token;

    private String set;

    private String metadataPrefix;

    private Instant from;

    private Instant until;

    private boolean retry;

    protected ClientOAIRequest() {
        uriBuilder = new URIBuilder();
    }

    public void setURL(URL url) {
        try {
            URI uri = url.toURI();
            uriBuilder.scheme(uri.getScheme())
                    .authority(uri.getAuthority())
                    .path(uri.getPath());
        } catch (URISyntaxException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            throw new IllegalArgumentException("invalid URI " + url);
        }
    }

    public URL getURL() throws MalformedURLException {
        return uriBuilder.build().toURL();
    }

    public String getPath() {
        return uriBuilder.buildGetPath();
    }

    public void addParameter(String name, String value) {
        if (value != null && !value.isEmpty()) {
            uriBuilder.addParameter(name, value);
        }
    }

    @Override
    public void setSet(String set) {
        this.set = set;
        addParameter(OAIConstants.SET_PARAMETER, set);
    }

    public String getSet() {
        return set;
    }

    @Override
    public void setMetadataPrefix(String prefix) {
        this.metadataPrefix = prefix;
        addParameter(OAIConstants.METADATA_PREFIX_PARAMETER, prefix);
    }

    public String getMetadataPrefix() {
        return metadataPrefix;
    }

    public void setDateTimeFormatter(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public void setFrom(Instant from) {
        this.from = from;
        String fromStr = dateTimeFormatter == null ? from.toString() : dateTimeFormatter.format(from);
        addParameter(OAIConstants.FROM_PARAMETER, fromStr);
    }

    public Instant getFrom() {
        return from;
    }

    @Override
    public void setUntil(Instant until) {
        this.until = until;
        String untilStr = dateTimeFormatter == null ? until.toString() : dateTimeFormatter.format(until);
        addParameter(OAIConstants.UNTIL_PARAMETER, untilStr);
    }

    public Instant getUntil() {
        return until;
    }

    @Override
    public void setResumptionToken(ResumptionToken<?> token) {
        this.token = token;
        if (token != null && token.toString() != null) {
            // resumption token may have characters that are illegal in URIs like '|'
            addParameter(OAIConstants.RESUMPTION_TOKEN_PARAMETER, token.toString());
        }
    }

    @Override
    public ResumptionToken<?> getResumptionToken() {
        return token;
    }

    public void setRetry(boolean retry) {
        this.retry = retry;
    }

    public boolean isRetry() {
        return retry;
    }

    class GetRecord extends ClientOAIRequest {

        public GetRecord() {
            addParameter(OAIConstants.VERB_PARAMETER, OAIConstants.GET_RECORD);
        }
    }

    class Identify extends ClientOAIRequest {

        public Identify() {
            addParameter(OAIConstants.VERB_PARAMETER, OAIConstants.IDENTIFY);
        }
    }

    class ListIdentifiers extends ClientOAIRequest {

        public ListIdentifiers() {
            addParameter(OAIConstants.VERB_PARAMETER, OAIConstants.LIST_IDENTIFIERS);
        }
    }

    class ListMetadataFormats extends ClientOAIRequest {

        public ListMetadataFormats() {
            addParameter(OAIConstants.VERB_PARAMETER, OAIConstants.LIST_METADATA_FORMATS);
        }
    }

    class ListRecordsRequest extends ClientOAIRequest {

        public ListRecordsRequest() {
            addParameter(OAIConstants.VERB_PARAMETER, OAIConstants.LIST_RECORDS);
        }

    }

    class ListSetsRequest extends ClientOAIRequest {

        public ListSetsRequest() {
            addParameter(OAIConstants.VERB_PARAMETER, OAIConstants.LIST_SETS);
        }
    }
}
