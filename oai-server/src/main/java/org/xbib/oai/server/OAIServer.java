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

import java.net.URL;
import java.util.Date;

/**
 *  OAI server.
 */
public interface OAIServer {

    URL getURL();

    /**
     * This verb is used to retrieve information about a repository. 
     * Some of the information returned is required as part of the OAI-PMH.
     * Repositories may also employ the Identify verb to return additional 
     * descriptive information.
     * @param request request
     * @param response response
     * @throws OAIException if verb fails
     */
    void identify(IdentifyServerRequest request, IdentifyServerResponse response) throws OAIException;
    
    /**
     * This verb is an abbreviated form of ListRecords, retrieving only 
     * headers rather than records. Optional arguments permit selective 
     * harvesting of headers based on set membership and/or datestamp. 
     * Depending on the repository's support for deletions, a returned 
     * header may have a status attribute of "deleted" if a record 
     * matching the arguments specified in the request has been deleted.
     * @param request request
     * @param response response
     * @throws OAIException if verb fails
     */
    void listIdentifiers(ListIdentifiersServerRequest request, ListIdentifiersServerResponse response) throws OAIException;

    /**
     * This verb is used to retrieve the metadata formats available 
     * from a repository. An optional argument restricts the request 
     * to the formats available for a specific item.
     * @param request request
     * @param response response
     * @throws OAIException if verb fails
     */
    void listMetadataFormats(ListMetadataFormatsServerRequest request, ListMetadataFormatsServerResponse response)
            throws OAIException;
    
    /**
     * This verb is used to retrieve the set structure of a repository, 
     * useful for selective harvesting.
     * @param request request
     * @param response response
     * @throws OAIException if verb fails
     */
    void listSets(ListSetsServerRequest request, ListSetsServerResponse response) throws OAIException;

    /**
     * This verb is used to harvest records from a repository. 
     * Optional arguments permit selective harvesting of records based on 
     * set membership and/or datestamp. Depending on the repository's 
     * support for deletions, a returned header may have a status 
     * attribute of "deleted" if a record matching the arguments 
     * specified in the request has been deleted. No metadata 
     * will be present for records with deleted status.
     * @param request request
     * @param response response
     * @throws OAIException if verb fails
     */
    void listRecords(ListRecordsServerRequest request, ListRecordsServerResponse response) throws OAIException;
    
    /**
     * This verb is used to retrieve an individual metadata record from 
     * a repository. Required arguments specify the identifier of the item 
     * from which the record is requested and the format of the metadata 
     * that should be included in the record. Depending on the level at 
     * which a repository tracks deletions, a header with a "deleted" value 
     * for the status attribute may be returned, in case the metadata format 
     * specified by the metadataPrefix is no longer available from the 
     * repository or from the specified item.
     * @param request request
     * @param response response
     * @throws OAIException if verb fails
     */
    void getRecord(GetRecordServerRequest request, GetRecordServerResponse response) throws OAIException;

    Date getLastModified();
    
    String getRepositoryName();
    
    URL getBaseURL();

    String getProtocolVersion();

    String getAdminEmail();
    
    String getEarliestDatestamp();
    
    String getDeletedRecord();
    
    String getGranularity();
    
}
