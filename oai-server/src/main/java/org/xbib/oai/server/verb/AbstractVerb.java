package org.xbib.oai.server.verb;

import org.xbib.oai.OAIConstants;
import org.xbib.oai.exceptions.OAIException;
import org.xbib.oai.server.AbstractOAIRequest;
import org.xbib.oai.server.AbstractOAIResponse;
import org.xbib.oai.server.OAIServer;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLStreamException;

/**
 *
 */
public abstract class AbstractVerb {

    private static final XMLEventFactory eventFactory = XMLEventFactory.newInstance();

    private static final TimeZone tz = TimeZone.getTimeZone("GMT");

    private static final String NS_URI = "http://www.w3.org/2001/XMLSchema-instance";

    private static final String NS_PREFIX = "xsi";

    private final AbstractOAIRequest request;

    private final AbstractOAIResponse response;

    public AbstractVerb(AbstractOAIRequest request, AbstractOAIResponse response) {
        this.request = request;
        this.response = response;
    }

    public abstract void execute(OAIServer server) throws OAIException;
    
    protected void beginDocument() throws XMLStreamException {
        response.getConsumer().add(eventFactory.createStartDocument());
    }
    
    protected void endDocument() throws XMLStreamException {
        response.getConsumer().add(eventFactory.createEndDocument());
    }
    
    protected void beginElement(String name) throws XMLStreamException {
        response.getConsumer().add(eventFactory.createStartElement(toQName(OAIConstants.NS_URI, name), null, null));
    }
    
    protected void endElement(String name) throws XMLStreamException {
        response.getConsumer().add(eventFactory.createEndElement(toQName(OAIConstants.NS_URI, name), null));
    }
   
    protected void element(String name, String value) throws XMLStreamException {
        response.getConsumer().add(eventFactory.createStartElement(toQName(OAIConstants.NS_URI, name), null, null));
        response.getConsumer().add(eventFactory.createCharacters(value));
        response.getConsumer().add(eventFactory.createEndElement(toQName(OAIConstants.NS_URI, name), null));
    }

    protected void element(String name, Date value) throws XMLStreamException {
        response.getConsumer().add(eventFactory.createStartElement(toQName(OAIConstants.NS_URI, name), null, null));
        response.getConsumer().add(eventFactory.createCharacters(formatDate(value)));
        response.getConsumer().add(eventFactory.createEndElement(toQName(OAIConstants.NS_URI, name), null));
    }

    protected void beginOAIPMH(URL baseURL) throws XMLStreamException {
        response.getConsumer().add(eventFactory.createStartElement(toQName(OAIConstants.NS_URI, "OAI-PMH"), null, null));
        response.getConsumer().add(eventFactory.createNamespace(OAIConstants.NS_URI));
        response.getConsumer().add(eventFactory.createNamespace(NS_PREFIX, NS_URI));
        response.getConsumer().add(eventFactory.createAttribute(NS_PREFIX, NS_URI,
                "schemaLocation", "http://www.openarchives.org/OAI/2.0/ http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd"));
        element("responseDate", new Date());
        request(request.getParameterMap(), baseURL);
    }

    protected void endOAIPMH() throws XMLStreamException {
        response.getConsumer().add(eventFactory.createEndElement(toQName(OAIConstants.NS_URI, "OAI-PMH"), null));
    }
    
    protected void request(Map<String, String> attrs, URL baseURL) throws XMLStreamException {
        response.getConsumer().add(eventFactory.createStartElement(toQName(OAIConstants.NS_URI, OAIConstants.REQUEST),
                null, null));
        for (Map.Entry<String, String> me : attrs.entrySet()) {
                response.getConsumer().add(eventFactory.createAttribute(me.getKey(), me.getValue()));
        }
        response.getConsumer().add(eventFactory.createCharacters(baseURL.toExternalForm()));
        response.getConsumer().add(eventFactory.createEndElement(toQName(OAIConstants.NS_URI, OAIConstants.REQUEST),
                null));
    }

    private QName toQName(String namespaceUri, String qname) {
        int i = qname.indexOf(':');
        if (i == -1) {
            return new QName(namespaceUri, qname);
        } else {
            String prefix = qname.substring(0, i);
            String localPart = qname.substring(i + 1);
            return new QName(namespaceUri, localPart, prefix);
        }
    }

    private String formatDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        format.setTimeZone(tz);
        return format.format(date);
    }
}
