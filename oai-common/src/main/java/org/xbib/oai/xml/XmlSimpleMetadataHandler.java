package org.xbib.oai.xml;

import org.xbib.oai.OAIConstants;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.io.Writer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.Location;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;

/**
 *
 */
public class XmlSimpleMetadataHandler extends SimpleMetadataHandler implements OAIConstants {

    private static final Logger logger = Logger.getLogger(XmlSimpleMetadataHandler.class.getName());

    private static final XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();

    private static final XMLEventFactory eventFactory = XMLEventFactory.newInstance();

    private List<String> namespaces = new ArrayList<>();

    private Deque<Collection<Namespace>> nsStack = new ArrayDeque<>();

    private Locator locator;

    private XMLEventWriter eventWriter;

    private Writer writer;

    private String id;

    private boolean needToCallStartDocument = false;

    public XmlSimpleMetadataHandler setWriter(Writer writer) {
        this.writer = writer;
        try {
            outputFactory.setProperty("javax.xml.stream.isRepairingNamespaces", Boolean.TRUE);
            this.eventWriter = outputFactory.createXMLEventWriter(writer);
        } catch (XMLStreamException e) {
            logger.log(Level.FINE, e.getMessage(), e);
        }
        return this;
    }

    public Writer getWriter() {
        return writer;
    }

    public XmlSimpleMetadataHandler setEventWriter(XMLEventWriter eventWriter) {
        this.eventWriter = eventWriter;
        return this;
    }

    public XMLEventWriter getEventWriter() {
        return eventWriter;
    }

    public String getIdentifier() {
        return id;
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    public Location getCurrentLocation() {
        if (locator != null) {
            return new SAXLocation(locator);
        } else {
            return null;
        }
    }

    @Override
    public void startDocument() {
        if (eventWriter == null) {
            return;
        }
        namespaces.clear();
        nsStack.clear();
        eventFactory.setLocation(getCurrentLocation());
        needToCallStartDocument = true;
    }

    @Override
    public void endDocument() throws SAXException {
        if (eventWriter == null) {
            return;
        }
        this.id = getHeader().getIdentifier().trim();
        try {
            eventFactory.setLocation(getCurrentLocation());
            eventWriter.add(eventFactory.createEndDocument());
        } catch (XMLStreamException e) {
            throw new SAXException(e);
        }
        namespaces.clear();
        nsStack.clear();
    }

    @Override
    public void startPrefixMapping(String prefix, String namespaceURI) {
        if (eventWriter == null) {
            return;
        }
        if (namespaces == null) {
            namespaces = new ArrayList<>();
        }
        if (prefix == null) {
            namespaces.add("");
            namespaces.add(namespaceURI);
            return;
        } else if ("xml".equals(prefix)) {
            return;
        }
        namespaces.add(prefix);
        namespaces.add(namespaceURI);
    }

    @Override
    public void endPrefixMapping(String string) {
        // not used
    }

    @Override
    public void startElement(String uri, String localname, String qname, Attributes attributes) throws SAXException {
        if (eventWriter == null) {
            return;
        }
        if (needToCallStartDocument) {
            try {
                eventWriter.add(eventFactory.createStartDocument());
            } catch (XMLStreamException e) {
                logger.log(Level.FINE, e.getMessage(), e);
            }
            needToCallStartDocument = false;
        }
        Collection<Attribute> attr = new ArrayList<>();
        Collection<Namespace> ns = new ArrayList<>();
        createStartEvents(attributes, attr, ns);
        nsStack.add(ns);
        try {
            String[] q = {null, null};
            parseQName(qname, q);
            eventFactory.setLocation(getCurrentLocation());
            eventWriter.add(eventFactory.createStartElement(q[0], uri,
                    q[1], attr.iterator(), ns.iterator()));
        } catch (XMLStreamException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void endElement(String uri, String localname, String qname) throws SAXException {
        if (eventWriter == null) {
            return;
        }
        String[] q = {null, null};
        parseQName(qname, q);
        Iterator<Namespace> nsIter = nsStack.getLast().iterator();
        try {
            eventFactory.setLocation(getCurrentLocation());
            eventWriter.add(eventFactory.createEndElement(q[0], uri, q[1], nsIter));
        } catch (XMLStreamException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void characters(char[] chars, int i, int i1) throws SAXException {
        if (eventWriter == null) {
            return;
        }
        try {
            eventFactory.setLocation(getCurrentLocation());
            eventWriter.add(eventFactory.createCharacters(new String(chars, i, i1)));
        } catch (XMLStreamException e) {
            throw new SAXException(e);
        }
    }

    private void createStartEvents(Attributes attributes, Collection<Attribute> attrList, Collection<Namespace> nsList) {
        Map<String, Namespace> nsMap = null;
        List<Attribute> attrs = null;
        if (namespaces != null) {
            final int nDecls = namespaces.size();
            for (int i = 0; i < nDecls; i++) {
                final String prefix = namespaces.get(i);
                String uri = namespaces.get(i++);
                Namespace ns = createNamespace(prefix, uri);
                if (nsMap == null) {
                    nsMap = new HashMap<>();
                }
                nsMap.put(prefix, ns);
            }
        }
        String[] qname = {null, null};
        for (int i = 0, s = attributes.getLength(); i < s; i++) {
            parseQName(attributes.getQName(i), qname);
            String attrPrefix = qname[0];
            String attrLocal = qname[1];
            String attrQName = attributes.getQName(i);
            String attrValue = attributes.getValue(i);
            String attrURI = attributes.getURI(i);
            if ("xmlns".equals(attrQName) || "xmlns".equals(attrPrefix)) {
                if (!attrValue.isEmpty() && nsMap != null && !nsMap.containsKey(attrPrefix)) {
                    Namespace ns = createNamespace(attrPrefix, attrValue);
                    nsMap = new HashMap<>();
                    nsMap.put(attrPrefix, ns);
                }
            } else {
                Attribute attribute;
                if (attrPrefix.length() > 0 && !attrValue.isEmpty()) {
                    attribute = eventFactory.createAttribute(attrPrefix, attrURI, attrLocal, attrValue);
                } else {
                    attribute = eventFactory.createAttribute(attrLocal, attrValue);
                }
                if (attrs == null) {
                    attrs = new ArrayList<>();
                }
                attrs.add(attribute);
            }
        }
        if (nsMap != null) {
            nsList.addAll(nsMap.values());
        }
        if (attrs != null) {
            attrList.addAll(attrs);
        }
    }

    private void parseQName(String qName, String[] results) {
        String prefix;
        String local;
        int idx = qName.indexOf(':');
        if (idx >= 0) {
            prefix = qName.substring(0, idx);
            local = qName.substring(idx + 1);
        } else {
            prefix = "";
            local = qName;
        }
        results[0] = prefix;
        results[1] = local;
    }

    private Namespace createNamespace(String prefix, String uri) {
        if (prefix == null || prefix.length() == 0) {
            return eventFactory.createNamespace(uri);
        } else {
            return eventFactory.createNamespace(prefix, uri);
        }
    }

    private static final class SAXLocation implements Location {
        private int lineNumber;
        private int columnNumber;
        private String publicId;
        private String systemId;

        private SAXLocation(Locator locator) {
            lineNumber = locator.getLineNumber();
            columnNumber = locator.getColumnNumber();
            publicId = locator.getPublicId();
            systemId = locator.getSystemId();
        }

        @Override
        public int getLineNumber() {
            return lineNumber;
        }

        @Override
        public int getColumnNumber() {
            return columnNumber;
        }

        @Override
        public int getCharacterOffset() {
            return -1;
        }

        @Override
        public String getPublicId() {
            return publicId;
        }

        @Override
        public String getSystemId() {
            return systemId;
        }
    }

}
