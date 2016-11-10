package org.xbib.oai.server.verb;

import org.xbib.oai.exceptions.OAIException;
import org.xbib.oai.server.OAIServer;
import org.xbib.oai.server.identify.IdentifyServerRequest;
import org.xbib.oai.server.identify.IdentifyServerResponse;

/**
 *
 */
public class Identify extends AbstractVerb {
    
    public Identify(IdentifyServerRequest request, IdentifyServerResponse response) {
        super(request, response);
    }

    @Override
    public void execute(OAIServer adapter) throws OAIException {
        try {
            beginDocument();
            beginOAIPMH(adapter.getBaseURL());            
            beginElement("Identify");
            endElement("Identify");
            endOAIPMH();
            endDocument();
        } catch (Exception e) {
            throw new OAIException(e.getMessage(), e);
        }
    }

}
