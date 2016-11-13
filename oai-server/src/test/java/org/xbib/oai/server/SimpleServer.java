package org.xbib.oai.server;

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
import org.xbib.oai.server.verb.Identify;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;

/**
 *
 */
public class SimpleServer implements OAIServer {

    @Override
    public void identify(IdentifyServerRequest request, IdentifyServerResponse response)
            throws OAIException {
        new Identify(request, response).execute(this);        
    }

    @Override
    public void listMetadataFormats(ListMetadataFormatsServerRequest request, ListMetadataFormatsServerResponse response)
            throws OAIException {
    }

    @Override
    public void listSets(ListSetsServerRequest request, ListSetsServerResponse response)
            throws OAIException {
    }

    @Override
    public void listIdentifiers(ListIdentifiersServerRequest request, ListIdentifiersServerResponse response)
            throws OAIException {
    }

    @Override
    public void listRecords(ListRecordsServerRequest request, ListRecordsServerResponse response)
            throws OAIException {
    }

    @Override
    public void getRecord(GetRecordServerRequest request, GetRecordServerResponse response)
            throws OAIException {
    }

    @Override
    public URL getURL() {
        try {
            return new URL("http://localhost:8080/oai");
        } catch (MalformedURLException e) {
            //
        }
        return null;
    }

    @Override
    public Date getLastModified() {
        return new Date();
    }

    @Override
    public String getRepositoryName() {
        return "Test Repository Name";
    }

    @Override
    public URL getBaseURL() {
        return getURL();
    }

    @Override
    public String getProtocolVersion() {
        return "2.0";
    }

    @Override
    public String getAdminEmail() {
        return "joergprante@gmail.com";
    }

    @Override
    public String getEarliestDatestamp() {
        return "2012-01-01T00:00:00Z";
    }

    @Override
    public String getDeletedRecord() {
        return "no";
    }

    @Override
    public String getGranularity() {
        return "YYYY-MM-DDThh:mm:ssZ";
    }
        
}
