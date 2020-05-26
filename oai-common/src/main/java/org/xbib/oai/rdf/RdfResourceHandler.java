package org.xbib.oai.rdf;

import org.xbib.content.rdf.RdfContentParams;
import org.xbib.content.rdf.io.xml.AbstractXmlResourceHandler;
import org.xbib.content.rdf.io.xml.XmlHandler;
import org.xbib.content.resource.IRI;
import org.xbib.content.resource.NamespaceContext;
import org.xbib.oai.OAIConstants;

import javax.xml.namespace.QName;

/**
 * A default RDF resource handler for OAI.
 */
public class RdfResourceHandler extends AbstractXmlResourceHandler<RdfContentParams> implements OAIConstants {

    public RdfResourceHandler(RdfContentParams params) {
        super(params);
    }

    @Override
    public void identify(QName name, String value, IRI identifier) {
        // do nothing
    }

    @Override
    public boolean isResourceDelimiter(QName name) {
        return OAIDC_NS_URI.equals(name.getNamespaceURI())
                && DC_PREFIX.equals(name.getLocalPart());
    }

    @Override
    public boolean skip(QName name) {
        boolean b = OAIDC_NS_URI.equals(name.getNamespaceURI())
                && DC_PREFIX.equals(name.getLocalPart());
        b = b || name.getLocalPart().startsWith("@");
        return b;
    }

    @Override
    public void addToPredicate(QName parent, String content) {
        // do nothing
    }

    @Override
    public Object toObject(QName parent, String content) {
        return content;
    }

    @Override
    public XmlHandler<RdfContentParams> setNamespaceContext(NamespaceContext namespaceContext) {
        return this;
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        return getParams().getNamespaceContext();
    }
}
