package org.xbib.oai.server;

import org.junit.Test;
import org.xbib.oai.server.identify.IdentifyServerRequest;
import org.xbib.oai.server.identify.IdentifyServerResponse;

import java.io.StringWriter;
import javax.xml.stream.XMLOutputFactory;

/**
 *
 */
public class SimpleServiceTest {

    @Test
    public void testIdentifyService() throws Exception {
        OAIServer service = OAIServiceFactory.getDefaultService();
        StringWriter sw = new StringWriter();
        XMLOutputFactory factory  = XMLOutputFactory.newInstance();
        IdentifyServerRequest request = new IdentifyServerRequest();
        IdentifyServerResponse response = new IdentifyServerResponse();
        response.setConsumer(factory.createXMLEventWriter(sw));
        service.identify(request, response);
    }

}
