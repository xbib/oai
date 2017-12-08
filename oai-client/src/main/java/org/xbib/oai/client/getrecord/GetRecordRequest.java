package org.xbib.oai.client.getrecord;

import org.xbib.oai.client.AbstractOAIRequest;

/**
 *
 */
public class GetRecordRequest extends AbstractOAIRequest {

    public GetRecordRequest() {
        super();
        addParameter(VERB_PARAMETER, GET_RECORD);
    }
}
