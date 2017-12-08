package org.xbib.oai.server.verb;

import org.xbib.oai.exceptions.OAIException;
import org.xbib.oai.server.OAIServer;
import org.xbib.oai.server.AbstractOAIRequest;
import org.xbib.oai.server.AbstractOAIResponse;

/**
 *
 */
public class ListMetadataFormats extends AbstractVerb {

    public ListMetadataFormats(AbstractOAIRequest request, AbstractOAIResponse response) {
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
