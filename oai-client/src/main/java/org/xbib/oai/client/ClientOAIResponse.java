package org.xbib.oai.client;

import org.xbib.helianthus.common.http.AggregatedHttpMessage;
import org.xbib.oai.OAIResponse;

import java.io.IOException;
import java.io.Writer;

/**
 * Default OAI response.
 */
public interface ClientOAIResponse extends OAIResponse {

    void receivedResponse(AggregatedHttpMessage message, Writer writer) throws IOException;
}
