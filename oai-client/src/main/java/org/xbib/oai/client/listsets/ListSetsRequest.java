package org.xbib.oai.client.listsets;

import org.xbib.oai.client.ClientOAIRequest;

/**
 *
 */
public class ListSetsRequest extends ClientOAIRequest {

    public ListSetsRequest() {
        super();
        addParameter(VERB_PARAMETER, LIST_SETS);
    }

}
