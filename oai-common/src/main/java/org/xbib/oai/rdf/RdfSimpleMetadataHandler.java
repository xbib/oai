package org.xbib.oai.rdf;

import org.xbib.content.rdf.RdfContentBuilder;
import org.xbib.content.rdf.RdfContentParams;
import org.xbib.content.rdf.Resource;
import org.xbib.content.rdf.internal.DefaultAnonymousResource;
import org.xbib.content.rdf.io.xml.XmlResourceHandler;
import org.xbib.content.resource.IRI;
import org.xbib.content.resource.IRINamespaceContext;
import org.xbib.oai.OAIConstants;
import org.xbib.oai.xml.SimpleMetadataHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 *  RDF metadata handler.
 */
public class RdfSimpleMetadataHandler extends SimpleMetadataHandler implements OAIConstants {

    private RdfResourceHandler handler;

    private Resource resource;

    private RdfContentBuilder<?> builder;

    private RdfContentParams params;

    public RdfSimpleMetadataHandler() {
        this(RdfSimpleMetadataHandler::getDefaultContext);
    }

    public RdfSimpleMetadataHandler(RdfContentParams params) {
        this.params = params;
        this.resource = new DefaultAnonymousResource();
        // set up our default handler
        this.handler = new RdfResourceHandler(params);
        handler.setDefaultNamespace(NS_PREFIX, NS_URI);
    }

    public static IRINamespaceContext getDefaultContext() {
        IRINamespaceContext context = IRINamespaceContext.newInstance();
        context.addNamespace(DC_PREFIX, DC_NS_URI);
        context.addNamespace(OAIDC_NS_PREFIX, OAIDC_NS_URI);
        return context;
    }

    public IRINamespaceContext getContext() {
        return params.getNamespaceContext();
    }

    public Resource getResource() {
        return resource;
    }

    public RdfSimpleMetadataHandler setHandler(RdfResourceHandler handler) {
        handler.setDefaultNamespace(NS_PREFIX, NS_URI);
        this.handler = handler;
        return this;
    }

    public XmlResourceHandler<RdfContentParams> getHandler() {
        return handler;
    }

    public RdfSimpleMetadataHandler setBuilder(RdfContentBuilder<?> builder) {
        this.builder = builder;
        return this;
    }

    @Override
    public void startDocument() throws SAXException {
        if (handler != null) {
            handler.startDocument();
        }
    }

    /**
     * At the endStream of each OAI metadata, the resource context receives the identifier from
     * the metadata header. The resource context is pushed to the RDF output.
     * Any IOException is converted to a SAXException.
     *
     * @throws SAXException if SaX fails
     */
    @Override
    public void endDocument() throws SAXException {
        String id = getHeader().getIdentifier().trim();
        if (handler != null) {
            handler.identify(null, id, null);
            resource.setId(IRI.create(id));
            handler.endDocument();
            try {
                if (builder != null) {
                    builder.receive(resource);
                }
            } catch (IOException e) {
                throw new SAXException(e);
            }
        }
    }

    @Override
    public void startPrefixMapping(String prefix, String namespaceURI) throws SAXException {
        if (handler != null) {
            handler.startPrefixMapping(prefix, namespaceURI);
            if (prefix.isEmpty()) {
                handler.setDefaultNamespace("oai", namespaceURI);
            }
        }
    }

    @Override
    public void endPrefixMapping(String string) throws SAXException {
        if (handler != null) {
            handler.endPrefixMapping(string);
        }
    }

    @Override
    public void startElement(String ns, String localname, String string2, Attributes atrbts) throws SAXException {
        if (handler != null) {
            handler.startElement(ns, localname, string2, atrbts);
        }
    }

    @Override
    public void endElement(String ns, String localname, String string2) throws SAXException {
        if (handler != null) {
            handler.endElement(ns, localname, string2);
        }
    }

    @Override
    public void characters(char[] chars, int i, int i1) throws SAXException {
        if (handler != null) {
            handler.characters(chars, i, i1);
        }
    }
}
