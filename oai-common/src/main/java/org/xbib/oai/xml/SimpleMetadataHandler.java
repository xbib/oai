package org.xbib.oai.xml;

import org.xbib.content.xml.util.XMLFilterReader;
import org.xbib.oai.util.RecordHeader;

/**
 *
 */
public class SimpleMetadataHandler extends XMLFilterReader implements MetadataHandler {
    
    private RecordHeader header;

    @Override
    public SimpleMetadataHandler setHeader(RecordHeader header) {
        this.header = header;
        return this;
    }

    @Override
    public RecordHeader getHeader() {
        return header;
    }
    
}
