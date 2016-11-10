package org.xbib.oai.server;

import org.xbib.oai.OAIConstants;
import org.xbib.oai.OAIRequest;
import org.xbib.oai.util.ResumptionToken;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public abstract class ServerOAIRequest implements OAIRequest {

    private String path;

    private Map<String, String> parameters;

    private ResumptionToken<?> token;

    private String set;

    private String metadataPrefix;

    private Instant from;

    private Instant until;

    private boolean retry;

    protected ServerOAIRequest() {
        this.parameters = new HashMap<>();
    }

    @Override
    public void setSet(String set) {
        this.set = set;
        parameters.put(OAIConstants.SET_PARAMETER, set);
    }

    public String getSet() {
        return set;
    }

    @Override
    public void setMetadataPrefix(String prefix) {
        this.metadataPrefix = prefix;
        parameters.put(OAIConstants.METADATA_PREFIX_PARAMETER, prefix);
    }

    public String getMetadataPrefix() {
        return metadataPrefix;
    }

    @Override
    public void setFrom(Instant from) {
        this.from = from;
        parameters.put(OAIConstants.FROM_PARAMETER, from.toString());
    }

    public Instant getFrom() {
        return from;
    }

    @Override
    public void setUntil(Instant until) {
        this.until = until;
        parameters.put(OAIConstants.UNTIL_PARAMETER, until.toString());
    }

    public Instant getUntil() {
        return until;
    }

    @Override
    public void setResumptionToken(ResumptionToken<?> token) {
        this.token = token;
        if (token != null) {
            parameters.put(OAIConstants.RESUMPTION_TOKEN_PARAMETER, token.toString());
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

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getParameterMap() {
        return parameters;
    }
}
