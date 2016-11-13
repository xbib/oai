package org.xbib.oai.client;

import java.net.URISyntaxException;
import java.net.URL;

import org.xbib.helianthus.client.ClientFactory;
import org.xbib.helianthus.client.http.HttpClient;
import org.xbib.oai.OAIConstants;
import org.xbib.oai.client.getrecord.GetRecordRequest;
import org.xbib.oai.client.identify.IdentifyRequest;
import org.xbib.oai.client.listidentifiers.ListIdentifiersRequest;
import org.xbib.oai.client.listmetadataformats.ListMetadataFormatsRequest;
import org.xbib.oai.client.listrecords.ListRecordsRequest;
import org.xbib.oai.client.listsets.ListSetsRequest;
import org.xbib.oai.util.ResumptionToken;

/**
 * OAI client API
 *
 */
public interface OAIClient extends OAIConstants, AutoCloseable {

    OAIClient setURL(URL uri, boolean trustAlways) throws URISyntaxException;

    OAIClient setURL(URL uri) throws URISyntaxException;

    URL getURL();

    HttpClient getHttpClient();

    ClientFactory getFactory();

    /**
     * This verb is used to retrieve information about a repository.
     * Some of the information returned is required as part of the OAI-PMH.
     * Repositories may also employ the Identify verb to return additional
     * descriptive information.
     * @return identify request
     */
    IdentifyRequest newIdentifyRequest();

    IdentifyRequest resume(IdentifyRequest request, ResumptionToken<?> token);

    /**
     * This verb is an abbreviated form of ListRecords, retrieving only
     * headers rather than records. Optional arguments permit selective
     * harvesting of headers based on set membership and/or datestamp.
     * Depending on the repository's support for deletions, a returned
     * header may have a status attribute of "deleted" if a record
     * matching the arguments specified in the request has been deleted.
     * @return list identifiers request
     *
     */
    ListIdentifiersRequest newListIdentifiersRequest();

    ListIdentifiersRequest resume(ListIdentifiersRequest request, ResumptionToken<?> token);

    /**
     * This verb is used to retrieve the metadata formats available 
     * from a repository. An optional argument restricts the request 
     * to the formats available for a specific item.
     * @return list metadata formats request
     */
    ListMetadataFormatsRequest newListMetadataFormatsRequest();

    ListMetadataFormatsRequest resume(ListMetadataFormatsRequest request, ResumptionToken<?> token);

    /**
     * This verb is used to retrieve the set structure of a repository, 
     * useful for selective harvesting.
     * @return list sets request
     */
    ListSetsRequest newListSetsRequest();

    ListSetsRequest resume(ListSetsRequest request, ResumptionToken<?> token);

    /**
     * This verb is used to harvest records from a repository. 
     * Optional arguments permit selective harvesting of records based on 
     * set membership and/or datestamp. Depending on the repository's 
     * support for deletions, a returned header may have a status 
     * attribute of "deleted" if a record matching the arguments 
     * specified in the request has been deleted. No metadata 
     * will be present for records with deleted status.
     * @return list records request
     */
    ListRecordsRequest newListRecordsRequest();

    ListRecordsRequest resume(ListRecordsRequest request, ResumptionToken<?> token);

    /**
     * This verb is used to retrieve an individual metadata record from 
     * a repository. Required arguments specify the identifier of the item 
     * from which the record is requested and the format of the metadata 
     * that should be included in the record. Depending on the level at 
     * which a repository tracks deletions, a header with a "deleted" value 
     * for the status attribute may be returned, in case the metadata format 
     * specified by the metadataPrefix is no longer available from the 
     * repository or from the specified item.
     * @return get record request
     */
    GetRecordRequest newGetRecordRequest();

    GetRecordRequest resume(GetRecordRequest request, ResumptionToken<?> token);

}
