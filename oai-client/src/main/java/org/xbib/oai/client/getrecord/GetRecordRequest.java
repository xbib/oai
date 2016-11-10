package org.xbib.oai.client.getrecord;

import org.xbib.oai.client.ClientOAIRequest;

/**
 *
 */
public class GetRecordRequest extends ClientOAIRequest {

    public GetRecordRequest() {
        super();
        addParameter(VERB_PARAMETER, GET_RECORD);
    }
}
