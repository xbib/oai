package org.xbib.oai.client.getrecord;

import org.xbib.net.URL;
import org.xbib.oai.client.AbstractOAIRequest;

/**
 *
 */
public class GetRecordRequest extends AbstractOAIRequest {

    public GetRecordRequest(URL url) {
        super(url);
        addParameter(VERB_PARAMETER, GET_RECORD);
    }
}
