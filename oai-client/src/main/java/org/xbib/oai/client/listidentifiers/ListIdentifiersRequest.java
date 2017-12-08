package org.xbib.oai.client.listidentifiers;

import org.xbib.oai.client.AbstractOAIRequest;

/**
 *
 */
public class ListIdentifiersRequest extends AbstractOAIRequest {

    public ListIdentifiersRequest() {
        super();
        addParameter(VERB_PARAMETER, LIST_IDENTIFIERS);
    }
}
