package org.xbib.oai.client.identify;

import org.xbib.oai.client.ClientOAIRequest;
import org.xbib.oai.OAIRequest;

/**
 *
 */
public class IdentifyRequest extends ClientOAIRequest implements OAIRequest {

    public IdentifyRequest() {
        super();
        addParameter(VERB_PARAMETER, IDENTIFY);
    }
}
