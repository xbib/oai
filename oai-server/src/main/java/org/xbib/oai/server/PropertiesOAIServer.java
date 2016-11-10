package org.xbib.oai.server;

import org.xbib.oai.OAISession;
import org.xbib.oai.exceptions.OAIException;
import org.xbib.oai.server.getrecord.GetRecordServerRequest;
import org.xbib.oai.server.getrecord.GetRecordServerResponse;
import org.xbib.oai.server.identify.IdentifyServerRequest;
import org.xbib.oai.server.identify.IdentifyServerResponse;
import org.xbib.oai.server.listidentifiers.ListIdentifiersServerRequest;
import org.xbib.oai.server.listidentifiers.ListIdentifiersServerResponse;
import org.xbib.oai.server.listmetadataformats.ListMetadataFormatsServerRequest;
import org.xbib.oai.server.listmetadataformats.ListMetadataFormatsServerResponse;
import org.xbib.oai.server.listrecords.ListRecordsServerRequest;
import org.xbib.oai.server.listrecords.ListRecordsServerResponse;
import org.xbib.oai.server.listsets.ListSetsServerRequest;
import org.xbib.oai.server.listsets.ListSetsServerResponse;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Properties;

/**
 *
 */
public class PropertiesOAIServer implements OAIServer {

    private static final String ADAPTER_URI = "uri";

    private static final String STYLESHEET = "stylesheet";

    private static final String REPOSITORY_NAME = "identify.repositoryName";

    private static final String BASE_URL = "identify.baseURL";

    private static final String PROTOCOL_VERSION = "identify.protocolVersion";

    private static final String ADMIN_EMAIL = "identify.adminEmail";

    private static final String EARLIEST_DATESTAMP = "identify.earliestDatestamp";

    private static final String DELETED_RECORD = "identify.deletedRecord";

    private static final String GRANULARITY = "identify.granularity";

    private Properties properties;

    public PropertiesOAIServer(Properties properties) {
        this.properties = properties;
    }

    @Override
    public URL getURL() {
        try {
            return new URL(properties.getProperty(ADAPTER_URI).trim());
        } catch (MalformedURLException e) {
            //
        }
        return null;
    }

    public String getStylesheet() {
        return properties.getProperty(STYLESHEET);
    }

    @Override
    public String getRepositoryName() {
        return properties.getProperty(REPOSITORY_NAME);
    }

    @Override
    public URL getBaseURL() {
        try {
            return new URL(properties.getProperty(BASE_URL));
        } catch (MalformedURLException e) {
            return null;
        }
    }

    @Override
    public String getProtocolVersion() {
        return properties.getProperty(PROTOCOL_VERSION);
    }

    @Override
    public String getAdminEmail() {
        return properties.getProperty(ADMIN_EMAIL);
    }

    @Override
    public String getEarliestDatestamp() {
        return properties.getProperty(EARLIEST_DATESTAMP);
    }

    @Override
    public String getDeletedRecord() {
        return properties.getProperty(DELETED_RECORD);
    }

    @Override
    public String getGranularity() {
        return properties.getProperty(GRANULARITY);
    }

    @Override
    public OAISession newSession() {
        return null;
    }

    @Override
    public Date getLastModified() {
        return null;
    }

    @Override
    public void identify(IdentifyServerRequest request, IdentifyServerResponse response)
            throws OAIException {
        // not implemented yet
    }

    @Override
    public void listMetadataFormats(ListMetadataFormatsServerRequest request, ListMetadataFormatsServerResponse response)
            throws OAIException {
        // not implemented yet
    }

    @Override
    public void listSets(ListSetsServerRequest request, ListSetsServerResponse response)
            throws OAIException {
        // not implemented yet
    }

    @Override
    public void listIdentifiers(ListIdentifiersServerRequest request, ListIdentifiersServerResponse response)
            throws OAIException {
        // not implemented yet
    }

    @Override
    public void listRecords(ListRecordsServerRequest request, ListRecordsServerResponse response)
            throws OAIException {
        // not implemented yet
    }

    @Override
    public void getRecord(GetRecordServerRequest request, GetRecordServerResponse response)
            throws OAIException {
        // not implemented yet
    }
}
