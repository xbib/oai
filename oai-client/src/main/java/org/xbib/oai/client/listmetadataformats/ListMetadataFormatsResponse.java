package org.xbib.oai.client.listmetadataformats;

import org.xbib.netty.http.common.HttpResponse;
import org.xbib.oai.client.AbstractOAIResponse;
import org.xbib.oai.exceptions.OAIException;

import java.io.Writer;

/**
 *
 */
public class ListMetadataFormatsResponse extends AbstractOAIResponse {

    @Override
    public void receivedResponse(HttpResponse message, Writer writer) throws OAIException {
        // not implemented yet
    }
}
