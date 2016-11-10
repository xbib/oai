package org.xbib.oai.client.getrecord;

import org.xbib.helianthus.common.http.AggregatedHttpMessage;
import org.xbib.oai.client.ClientOAIResponse;

import java.io.IOException;
import java.io.Writer;

/**
 *
 */
public class GetRecordResponse implements ClientOAIResponse {

    @Override
    public void to(Writer writer) throws IOException {

    }

    @Override
    public void receivedResponse(AggregatedHttpMessage message, Writer writer) throws IOException {

    }
}
