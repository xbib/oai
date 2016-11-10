package org.xbib.oai;

import org.xbib.oai.util.ResumptionToken;

import java.time.Instant;

/**
 *  OAI request API.
 */
public interface OAIRequest extends OAIConstants {

    void setSet(String set);

    void setMetadataPrefix(String prefix);

    void setFrom(Instant from);

    void setUntil(Instant until);

    void setResumptionToken(ResumptionToken<?> token);

    ResumptionToken<?> getResumptionToken();
}
