package org.xbib.oai.client;

import org.xbib.helianthus.common.http.AggregatedHttpMessage;
import org.xbib.oai.OAIResponse;

import java.io.IOException;
import java.io.Writer;

/**
 * Default OAI response.
 */
public abstract class AbstractOAIResponse implements OAIResponse {

    public abstract void receivedResponse(AggregatedHttpMessage message, Writer writer) throws IOException;
}
