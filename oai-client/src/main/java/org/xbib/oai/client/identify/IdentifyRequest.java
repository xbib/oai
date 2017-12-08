package org.xbib.oai.client.identify;

import org.xbib.oai.client.AbstractOAIRequest;

/**
 *
 */
public class IdentifyRequest extends AbstractOAIRequest {

    public IdentifyRequest() {
        super();
        addParameter(VERB_PARAMETER, IDENTIFY);
    }
}
