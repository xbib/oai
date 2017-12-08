package org.xbib.oai.client.listmetadataformats;

import org.xbib.oai.client.AbstractOAIRequest;

/**
 *
 */
public class ListMetadataFormatsRequest extends AbstractOAIRequest {

    public ListMetadataFormatsRequest() {
        super();
        addParameter(VERB_PARAMETER, LIST_METADATA_FORMATS);
    }
}
