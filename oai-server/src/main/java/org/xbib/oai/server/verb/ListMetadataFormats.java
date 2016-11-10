package org.xbib.oai.server.verb;

import org.xbib.oai.exceptions.OAIException;
import org.xbib.oai.server.OAIServer;
import org.xbib.oai.server.ServerOAIRequest;
import org.xbib.oai.server.ServerOAIResponse;

/**
 *
 */
public class ListMetadataFormats extends AbstractVerb {

    public ListMetadataFormats(ServerOAIRequest request, ServerOAIResponse response) {
        super(request, response);
    }

    @Override
    public void execute(OAIServer adapter) throws OAIException {
        try {
            beginDocument();
            beginOAIPMH(adapter.getBaseURL());            
            beginElement("ListMetadataFormats");
            endElement("ListMetadataFormats");
            endOAIPMH();
            endDocument();
        } catch (Exception e) {
            throw new OAIException(e.getMessage(), e);
        }
    }
    
}
