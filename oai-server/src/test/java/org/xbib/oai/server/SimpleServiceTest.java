package org.xbib.oai.server;

import org.junit.jupiter.api.Test;
import org.xbib.oai.server.identify.IdentifyServerRequest;
import org.xbib.oai.server.identify.IdentifyServerResponse;

import java.io.StringWriter;
import javax.xml.stream.XMLOutputFactory;

/**
 *
 */
class SimpleServiceTest {

    @Test
    void testIdentifyService() throws Exception {
        StringWriter sw = new StringWriter();
        XMLOutputFactory factory  = XMLOutputFactory.newInstance();
        IdentifyServerRequest request = new IdentifyServerRequest();
        IdentifyServerResponse response = new IdentifyServerResponse();
        response.setConsumer(factory.createXMLEventWriter(sw));
        OAIServer service = OAIServiceFactory.getDefaultService();
        if (service != null) {
            service.identify(request, response);
        }
    }
}
