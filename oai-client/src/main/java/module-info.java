module org.xbib.oai.client {
    exports org.xbib.oai.client;
    exports org.xbib.oai.client.getrecord;
    exports org.xbib.oai.client.identify;
    exports org.xbib.oai.client.listidentifiers;
    exports org.xbib.oai.client.listrecords;
    exports org.xbib.oai.client.listsets;
    requires org.xbib.oai;
    requires org.xbib.net.url;
    requires org.xbib.netty.http.common;
    requires org.xbib.content.xml;
    requires java.xml;
}
