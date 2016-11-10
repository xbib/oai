package org.xbib.oai.client.listmetadataformats;

import org.xbib.oai.client.ClientOAIRequest;
import org.xbib.oai.OAIRequest;

/**
 *
 */
public class ListMetadataFormatsRequest extends ClientOAIRequest implements OAIRequest {

    public ListMetadataFormatsRequest() {
        super();
        addParameter(VERB_PARAMETER, LIST_METADATA_FORMATS);
    }

}
