package org.xbib.oai.client.identify;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xbib.netty.http.common.HttpResponse;
import org.xbib.oai.client.AbstractOAIResponse;
import org.xbib.oai.exceptions.OAIException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 *
 */
public class IdentifyResponse extends AbstractOAIResponse {

    private String repositoryName;

    private URL baseURL;

    private String protocolVersion;

    private List<String> adminEmails = new ArrayList<>();

    private Date earliestDatestamp;

    private String deletedRecord;

    private String granularity;

    private String compression;

    @Override
    public void receivedResponse(HttpResponse message, Writer writer) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(message.getBodyAsString(StandardCharsets.UTF_8)));
            Document doc = db.parse(is);
            setGranularity(getString("granularity", doc.getDocumentElement()));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new OAIException(e);
        }
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

    private String getString(String tagName, Element element) {
        NodeList list = element.getElementsByTagName(tagName);
        if (list != null && list.getLength() > 0) {
            NodeList subList = list.item(0).getChildNodes();
            if (subList != null && subList.getLength() > 0) {
                return subList.item(0).getNodeValue();
            }
        }
        return null;
    }
}
