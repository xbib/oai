package org.xbib.oai.client.identify;

import org.xbib.net.URL;
import org.xbib.oai.client.AbstractOAIRequest;

/**
 *
 */
public class IdentifyRequest extends AbstractOAIRequest {

    public IdentifyRequest(URL url) {
        super(url);
        addParameter(VERB_PARAMETER, IDENTIFY);
    }
}
