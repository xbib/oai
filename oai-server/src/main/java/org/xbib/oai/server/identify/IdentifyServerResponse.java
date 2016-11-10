package org.xbib.oai.server.identify;

import org.xbib.oai.server.ServerOAIResponse;

import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 */
public class IdentifyServerResponse extends ServerOAIResponse {

    private String repositoryName;

    private URL baseURL;

    private String protocolVersion;

    private List<String> adminEmails = new ArrayList<>();

    private Date earliestDatestamp;

    private String deletedRecord;

    private String granularity;

    private String compression;

    @Override
    public void to(Writer writer) throws IOException {
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }
    
    public String getRepositoryName() {
        return repositoryName;
    }
    
    public void setBaseURL(URL url) {
        this.baseURL = url;
    }
    
    public URL getBaseURL() {
        return baseURL;
    }
    
    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }
    
    public String getProtocolVersion() {
        return protocolVersion;
    }
    
    public void addAdminEmail(String email) {
        adminEmails.add(email);
    }
    
    public List<String> getAdminEmails() {
        return adminEmails;
    }
    
    public void setEarliestDatestamp(Date earliestDatestamp) {
        this.earliestDatestamp = earliestDatestamp;
    }
    
    public Date getEarliestDatestamp() {
        return earliestDatestamp;
    }
    
    public void setDeletedRecord(String deletedRecord) {
        this.deletedRecord = deletedRecord;
    }
    
    public String getDeleteRecord() {
        return deletedRecord;
    }
    
    public void setGranularity(String granularity) {
        this.granularity = granularity;
    }
    
    public String getGranularity() {
        return granularity;
    }
    
    public void setCompression(String compression) {
        this.compression = compression;
    }
    
    public String getCompression() {
        return compression;
    }

}
