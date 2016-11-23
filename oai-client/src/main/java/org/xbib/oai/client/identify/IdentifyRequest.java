package org.xbib.oai.client.identify;

import org.xbib.oai.OAIRequest;
import org.xbib.oai.client.ClientOAIRequest;

/**
 *
 */
public class IdentifyRequest extends ClientOAIRequest implements OAIRequest {

    public IdentifyRequest() {
        super();
        addParameter(VERB_PARAMETER, IDENTIFY);
    }
}
