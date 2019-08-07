package org.xbib.oai.client.listmetadataformats;

import org.xbib.net.URL;
import org.xbib.oai.client.AbstractOAIRequest;

/**
 *
 */
public class ListMetadataFormatsRequest extends AbstractOAIRequest {

    public ListMetadataFormatsRequest(URL url) {
        super(url);
        addParameter(VERB_PARAMETER, LIST_METADATA_FORMATS);
    }
}
