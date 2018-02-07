package org.xbib.oai.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.xbib.helianthus.client.Clients;
import org.xbib.helianthus.client.http.HttpClient;
import org.xbib.helianthus.common.http.AggregatedHttpMessage;
import org.xbib.helianthus.common.http.HttpHeaderNames;
import org.xbib.helianthus.common.http.HttpHeaders;
import org.xbib.helianthus.common.http.HttpMethod;
import org.xbib.oai.client.identify.IdentifyRequest;
import org.xbib.oai.client.identify.IdentifyResponse;
import org.xbib.oai.client.listrecords.ListRecordsRequest;
import org.xbib.oai.client.listrecords.ListRecordsResponse;
import org.xbib.oai.xml.SimpleMetadataHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.URI;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 */
@Ignore
public class DOAJClientTest {

    private static final Logger logger = LogManager.getLogger(DOAJClientTest.class.getName());

    @Test
    @Ignore // takes too long time
    public void testListRecordsDOAJ() throws Exception {
        // will redirect to https://doaj.org/oai
        try (OAIClient oaiClient = new OAIClient().setURL(new URL("http://doaj.org/oai"), true)) {
            IdentifyRequest identifyRequest = oaiClient.newIdentifyRequest();
            HttpClient client = oaiClient.getHttpClient();
            AggregatedHttpMessage response = client.execute(HttpHeaders.of(HttpMethod.GET, identifyRequest.getPath())
                            .set(HttpHeaderNames.ACCEPT, "utf-8")).aggregate().get();
            // follow a maximum of 10 HTTP redirects
            int max = 10;
            while (response.followUrl() != null && max-- > 0) {
                URI uri = URI.create(response.followUrl());
                client = Clients.newClient(oaiClient.getFactory(), "none+" + uri, HttpClient.class);
                response = client.execute(HttpHeaders.of(HttpMethod.GET, response.followUrl())
                                .set(HttpHeaderNames.ACCEPT, "utf-8")).aggregate().get();
            }
            IdentifyResponse identifyResponse = new IdentifyResponse();
            String content = response.content().toStringUtf8();
            logger.debug("identifyResponse = {}", content);
            identifyResponse.receivedResponse(response, new StringWriter());
            String granularity = identifyResponse.getGranularity();
            logger.info("granularity = {}", granularity);
            DateTimeFormatter dateTimeFormatter = "YYYY-MM-DD".equals(granularity) ?
                    DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.of("GMT")) : null;
            ListRecordsRequest listRecordsRequest = oaiClient.newListRecordsRequest();
            listRecordsRequest.setDateTimeFormatter(dateTimeFormatter);
            listRecordsRequest.setFrom(Instant.parse("2017-01-01T00:00:00Z"));
            listRecordsRequest.setUntil(Instant.parse("2018-01-01T00:00:00Z"));
            listRecordsRequest.setMetadataPrefix("oai_dc");
            Handler handler = new Handler();
            File file =  File.createTempFile("doaj.", ".xml");
            file.deleteOnExit();
            FileWriter fileWriter = new FileWriter(file);
            while (listRecordsRequest != null) {
                try {
                    ListRecordsResponse listRecordsResponse = new ListRecordsResponse(listRecordsRequest);
                    logger.debug("response = {}", response.headers());
                    listRecordsRequest.addHandler(handler);
                    client = oaiClient.getHttpClient();
                    response = client.execute(HttpHeaders.of(HttpMethod.GET, listRecordsRequest.getPath())
                                    .set(HttpHeaderNames.ACCEPT, "utf-8")).aggregate().get();
                    // follow a maximum of 10 HTTP redirects
                    max = 10;
                    while (response.followUrl() != null && max-- > 0) {
                        URI uri = URI.create(response.followUrl());
                        client = Clients.newClient(oaiClient.getFactory(), "none+" + uri, HttpClient.class);
                        response = client.execute(HttpHeaders.of(HttpMethod.GET, response.followUrl())
                                .set(HttpHeaderNames.ACCEPT, "utf-8")).aggregate().get();
                    }
                    listRecordsResponse.receivedResponse(response, fileWriter);
                    listRecordsRequest = oaiClient.resume(listRecordsRequest, listRecordsResponse.getResumptionToken());
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                    listRecordsRequest = null;
                }
            }
            fileWriter.close();
            logger.info("count={}", handler.count());
        } catch (ConnectException | ExecutionException e) {
            logger.warn("skipped, can not connect, exception is:", e);
        } catch (InterruptedException | IOException e) {
            throw e;
        }
    }

    class Handler extends SimpleMetadataHandler {

        final AtomicLong count = new AtomicLong(0L);

        @Override
        public void startDocument() {
            logger.debug("start doc");
        }

        @Override
        public void endDocument() {
            logger.debug("end doc");
            count.incrementAndGet();
        }

        long count() {
            return count.get();
        }
    }
}
