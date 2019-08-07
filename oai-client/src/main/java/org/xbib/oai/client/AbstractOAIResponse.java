package org.xbib.oai.client;

import org.xbib.netty.http.common.HttpResponse;
import org.xbib.oai.OAIResponse;
import org.xbib.oai.exceptions.OAIException;

import java.io.Writer;

/**
 * Default OAI response.
 */
public abstract class AbstractOAIResponse implements OAIResponse {

    public abstract void receivedResponse(HttpResponse message, Writer writer) throws OAIException;
}
