package org.xbib.oai.client.listsets;

import org.xbib.net.URL;
import org.xbib.oai.client.AbstractOAIRequest;

/**
 *
 */
public class ListSetsRequest extends AbstractOAIRequest {

    public ListSetsRequest(URL url) {
        super(url);
        addParameter(VERB_PARAMETER, LIST_SETS);
    }

}
