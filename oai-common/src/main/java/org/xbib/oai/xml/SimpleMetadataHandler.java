package org.xbib.oai.xml;

import org.xbib.content.xml.util.XMLFilterReader;
import org.xbib.oai.util.RecordHeader;

/**
 *
 */
public class SimpleMetadataHandler extends XMLFilterReader implements MetadataHandler {
    
    private RecordHeader header;

    public SimpleMetadataHandler setHeader(RecordHeader header) {
        this.header = header;
        return this;
    }
    
    public RecordHeader getHeader() {
        return header;
    }
    
}
