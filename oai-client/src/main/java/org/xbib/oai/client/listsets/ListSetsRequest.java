package org.xbib.oai.client.listsets;

import org.xbib.oai.client.AbstractOAIRequest;

/**
 *
 */
public class ListSetsRequest extends AbstractOAIRequest {

    public ListSetsRequest() {
        super();
        addParameter(VERB_PARAMETER, LIST_SETS);
    }

}
