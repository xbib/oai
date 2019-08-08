package org.xbib.oai.client;

import org.xbib.net.URL;
import org.xbib.oai.OAIConstants;
import org.xbib.oai.OAIRequest;
import org.xbib.oai.util.ResumptionToken;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Client OAI request.
 */
public abstract class AbstractOAIRequest implements OAIRequest {

    private final URL.Builder urlBuilder;

    private DateTimeFormatter dateTimeFormatter;

    private ResumptionToken<?> token;

    private String set;

    private String metadataPrefix;

    private Instant from;

    private Instant until;

    private boolean retry;

    protected AbstractOAIRequest(URL url) {
        this.urlBuilder = URL.builder()
                .scheme(url.getScheme())
                .host(url.getHost())
                .port(url.getPort())
                .path(url.getPath());
    }

    public URL getURL() {
        return urlBuilder.build();
    }

    protected void addParameter(String name, String value) {
        if (value != null && !value.isEmpty()) {
            urlBuilder.queryParam(name, value).build();
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
            // note: resumption token may have characters that are illegal in URIs, like '|'
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

    @Override
    public String toString() {
        return "[request:metadataPrefix=" + getMetadataPrefix()
                + ",set=" + getSet()
                + ",from=" + getFrom()
                + ",until=" + getUntil()
                + "]";
    }
}
