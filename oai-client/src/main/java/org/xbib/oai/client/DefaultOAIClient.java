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
 * Default OAI client.
 */
public class DefaultOAIClient implements OAIClientMethods, AutoCloseable {

    private HttpClient client;

    private ClientFactory clientFactory;

    private URL url;

    @Override
    public DefaultOAIClient setURL(URL url) throws URISyntaxException {
        return setURL(url, false);
    }

    @Override
    public DefaultOAIClient setURL(URL url, boolean trustAlways) throws URISyntaxException {
        this.url = url;
        this.clientFactory = ClientFactory.DEFAULT;
        this.client = new ClientBuilder("none+" + url.toURI())
                .factory(clientFactory)
                .defaultResponseTimeout(Duration.ofMinutes(1L)) // maybe not enough for extreme slow archive servers...
                .build(HttpClient.class);
        return this;
    }

    @Override
    public URL getURL() {
        return url;
    }

    @Override
    public HttpClient getHttpClient() {
        return client;
    }

    @Override
    public ClientFactory getFactory() {
        return clientFactory;
    }

    @Override
    public IdentifyRequest newIdentifyRequest() {
        IdentifyRequest request = new IdentifyRequest();
        request.setURL(url);
        return request;
    }

    @Override
    public ListMetadataFormatsRequest newListMetadataFormatsRequest() {
        ListMetadataFormatsRequest request = new ListMetadataFormatsRequest();
        request.setURL(getURL());
        return request;
    }

    @Override
    public ListSetsRequest newListSetsRequest() {
        ListSetsRequest request = new ListSetsRequest();
        request.setURL(getURL());
        return request;
    }

    @Override
    public ListIdentifiersRequest newListIdentifiersRequest() {
        ListIdentifiersRequest request = new ListIdentifiersRequest();
        request.setURL(getURL());
        return request;
    }

    @Override
    public GetRecordRequest newGetRecordRequest() {
        GetRecordRequest request = new GetRecordRequest();
        request.setURL(getURL());
        return request;
    }

    @Override
    public ListRecordsRequest newListRecordsRequest() {
        ListRecordsRequest request = new ListRecordsRequest();
        request.setURL(getURL());
        return request;
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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
