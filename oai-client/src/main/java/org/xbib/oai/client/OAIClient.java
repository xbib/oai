package org.xbib.oai.client;

import org.xbib.helianthus.client.ClientBuilder;
import org.xbib.helianthus.client.ClientFactory;
import org.xbib.helianthus.client.http.HttpClient;
import org.xbib.oai.client.getrecord.GetRecordRequest;
import org.xbib.oai.client.identify.IdentifyRequest;
import org.xbib.oai.client.listidentifiers.ListIdentifiersRequest;
import org.xbib.oai.client.listmetadataformats.ListMetadataFormatsRequest;
import org.xbib.oai.client.listrecords.ListRecordsRequest;
import org.xbib.oai.client.listsets.ListSetsRequest;
import org.xbib.oai.util.ResumptionToken;

import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;

/**
 * OAI client.
 */
public class OAIClient implements AutoCloseable {

    private HttpClient client;

    private ClientFactory clientFactory;

    private URL url;

    public OAIClient setURL(URL url) throws URISyntaxException {
        return setURL(url, false);
    }

    public OAIClient setURL(URL url, boolean trustAlways) throws URISyntaxException {
        this.url = url;
        this.clientFactory = ClientFactory.DEFAULT;
        this.client = new ClientBuilder("none+" + url.toURI())
                .factory(clientFactory)
                .defaultResponseTimeout(Duration.ofMinutes(1L)) // maybe not enough for extreme slow archive servers...
                .build(HttpClient.class);
        return this;
    }

    public URL getURL() {
        return url;
    }

    public HttpClient getHttpClient() {
        return client;
    }

    public ClientFactory getFactory() {
        return clientFactory;
    }

    /**
     * This verb is used to retrieve information about a repository.
     * Some of the information returned is required as part of the OAI-PMH.
     * Repositories may also employ the Identify verb to return additional
     * descriptive information.
     * @return identify request
     */
    public IdentifyRequest newIdentifyRequest() {
        IdentifyRequest request = new IdentifyRequest();
        request.setURL(url);
        return request;
    }

    /**
     * This verb is used to retrieve the metadata formats available
     * from a repository. An optional argument restricts the request
     * to the formats available for a specific item.
     * @return list metadata formats request
     */
    public ListMetadataFormatsRequest newListMetadataFormatsRequest() {
        ListMetadataFormatsRequest request = new ListMetadataFormatsRequest();
        request.setURL(getURL());
        return request;
    }

    /**
     * This verb is used to retrieve the set structure of a repository,
     * useful for selective harvesting.
     * @return list sets request
     */
    public ListSetsRequest newListSetsRequest() {
        ListSetsRequest request = new ListSetsRequest();
        request.setURL(getURL());
        return request;
    }

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
    public ListIdentifiersRequest newListIdentifiersRequest() {
        ListIdentifiersRequest request = new ListIdentifiersRequest();
        request.setURL(getURL());
        return request;
    }

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
    public GetRecordRequest newGetRecordRequest() {
        GetRecordRequest request = new GetRecordRequest();
        request.setURL(getURL());
        return request;
    }

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
    public ListRecordsRequest newListRecordsRequest() {
        ListRecordsRequest request = new ListRecordsRequest();
        request.setURL(getURL());
        return request;
    }

    public IdentifyRequest resume(IdentifyRequest request, ResumptionToken<?> token) {
        if (request.isRetry()) {
            request.setRetry(false);
            return request;
        }
        if (token == null) {
            return null;
        }
        IdentifyRequest nextRequest = newIdentifyRequest();
        nextRequest.setResumptionToken(token);
        return nextRequest;
    }

    public ListRecordsRequest resume(ListRecordsRequest request, ResumptionToken<?> token) {
        if (request.isRetry()) {
            request.setRetry(false);
            return request;
        }
        if (token == null) {
            return null;
        }
        ListRecordsRequest nextRequest = newListRecordsRequest();
        nextRequest.setResumptionToken(token);
        return nextRequest;
    }

    public ListIdentifiersRequest resume(ListIdentifiersRequest request, ResumptionToken<?> token) {
        if (request.isRetry()) {
            request.setRetry(false);
            return request;
        }
        if (token == null) {
            return null;
        }
        ListIdentifiersRequest nextRequest = newListIdentifiersRequest();
        nextRequest.setResumptionToken(token);
        return nextRequest;
    }

    public ListMetadataFormatsRequest resume(ListMetadataFormatsRequest request, ResumptionToken<?> token) {
        if (request.isRetry()) {
            request.setRetry(false);
            return request;
        }
        if (token == null) {
            return null;
        }
        ListMetadataFormatsRequest nextRequest = newListMetadataFormatsRequest();
        nextRequest.setResumptionToken(token);
        return nextRequest;
    }

    public ListSetsRequest resume(ListSetsRequest request, ResumptionToken<?> token) {
        if (request.isRetry()) {
            request.setRetry(false);
            return request;
        }
        if (token == null) {
            return null;
        }
        ListSetsRequest nextRequest = newListSetsRequest();
        nextRequest.setResumptionToken(token);
        return nextRequest;
    }

    public GetRecordRequest resume(GetRecordRequest request, ResumptionToken<?> token) {
        if (request.isRetry()) {
            request.setRetry(false);
            return request;
        }
        if (token == null) {
            return null;
        }
        GetRecordRequest nextRequest = newGetRecordRequest();
        nextRequest.setResumptionToken(token);
        return nextRequest;
    }

    @Override
    public void close() {
        // nothing to close
    }
}
