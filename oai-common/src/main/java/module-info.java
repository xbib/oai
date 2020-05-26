module org.xbib.oai {
    exports org.xbib.oai;
    exports org.xbib.oai.exceptions;
    exports org.xbib.oai.rdf;
    exports org.xbib.oai.util;
    exports org.xbib.oai.xml;
    requires org.xbib.content.rdf;
    requires org.xbib.content.resource;
    requires org.xbib.content.xml;
    requires java.xml;
}
