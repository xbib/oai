package org.xbib.oai.client.listidentifiers;

import org.xbib.oai.OAIRequest;
import org.xbib.oai.client.ClientOAIRequest;

/**
 *
 */
public class ListIdentifiersRequest extends ClientOAIRequest implements OAIRequest {

    public ListIdentifiersRequest() {
        super();
        addParameter(VERB_PARAMETER, LIST_IDENTIFIERS);
    }
}
