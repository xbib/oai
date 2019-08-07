package org.xbib.oai.client.listidentifiers;

import org.xbib.net.URL;
import org.xbib.oai.client.AbstractOAIRequest;

/**
 *
 */
public class ListIdentifiersRequest extends AbstractOAIRequest {

    public ListIdentifiersRequest(URL url) {
        super(url);
        addParameter(VERB_PARAMETER, LIST_IDENTIFIERS);
    }
}
