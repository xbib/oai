package org.xbib.oai.util;

import java.time.Instant;

/**
 *
 */
public class RecordHeader {

    private String identifier;
    
    private Instant date;
    
    private String set;
    
    public RecordHeader setIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }
    
    public String getIdentifier() {
        return identifier;
    }
    
    public RecordHeader setDate(Instant date) {
        this.date = date;
        return this;
    }
    
    public Instant getDate() {
        return date;
    }
    
    public RecordHeader setSetspec(String setSpec) {
        this.set = setSpec;
        return this;
    }
    
    public String getSetSpec() {
        return set;
    }
}
