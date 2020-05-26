package org.xbib.oai.client;

import io.netty.handler.codec.http.HttpHeaderNames;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.xbib.net.URL;
import org.xbib.netty.http.client.Client;
import org.xbib.netty.http.client.api.Request;
import org.xbib.oai.client.identify.IdentifyRequest;
import org.xbib.oai.client.identify.IdentifyResponse;
import org.xbib.oai.client.listrecords.ListRecordsRequest;
import org.xbib.oai.client.listrecords.ListRecordsResponse;
import org.xbib.oai.xml.SimpleMetadataHandler;

import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
class DOAJClientTest {

    private static final Logger logger = Logger.getLogger(DOAJClientTest.class.getName());

    @Test
    @Disabled("takes long time")
    void testListRecordsDOAJ() {
        URL url = URL.create("https://doaj.org/oai");
        try (Client httpClient = Client.builder()
                .setConnectTimeoutMillis(60 * 1000)
                .setReadTimeoutMillis(60 * 1000)
                .build();
             OAIClient oaiClient = new OAIClient(url)) {
            IdentifyRequest identifyRequest = oaiClient.newIdentifyRequest();
            IdentifyResponse identifyResponse = new IdentifyResponse();
            Request request = Request.get()
                    .url(url.resolve(identifyRequest.getURL()))
                    .addHeader(HttpHeaderNames.ACCEPT.toString(), "utf-8")
                    .setResponseListener(resp -> {
                        StringWriter sw = new StringWriter();
                        identifyResponse.receivedResponse(resp, sw);
                    })
                    .build();
            httpClient.execute(request).get();
            String granularity = identifyResponse.getGranularity();
            logger.log(Level.INFO, "granularity = " + granularity);

            DateTimeFormatter dateTimeFormatter = "YYYY-MM-DD".equals(granularity) ?
                    DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.of("GMT")) : null;
            ListRecordsRequest listRecordsRequest = oaiClient.newListRecordsRequest();
            listRecordsRequest.setDateTimeFormatter(dateTimeFormatter);
            listRecordsRequest.setFrom(Instant.parse("2008-01-01T00:00:00Z"));
            listRecordsRequest.setUntil(Instant.parse("2018-01-01T00:00:00Z"));
            listRecordsRequest.setMetadataPrefix("oai_dc");
            Handler handler = new Handler();
            try (Writer writer = Files.newBufferedWriter(Paths.get("build/doaj.xml"))) {
                while (listRecordsRequest != null) {
                    ListRecordsResponse listRecordsResponse = new ListRecordsResponse(listRecordsRequest);
                    listRecordsRequest.addHandler(handler);
                    logger.log(Level.INFO, "sending " + listRecordsRequest.getURL());
                    request = Request.get()
                            .url(url.resolve(listRecordsRequest.getURL()))
                            .addHeader(HttpHeaderNames.ACCEPT.toString(), "utf-8")
                            .setResponseListener(resp -> {
                                listRecordsResponse.receivedResponse(resp, writer);
                                logger.log(Level.FINE, "response headers = " + resp.getHeaders() +
                                        " resumption-token = {}" + listRecordsResponse.getResumptionToken());
                            })
                            .build();
                    httpClient.execute(request).get();
                    listRecordsRequest = oaiClient.resume(listRecordsRequest, listRecordsResponse.getResumptionToken());
                }
            }
            logger.log(Level.INFO, "count = " + handler.count());
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    static class Handler extends SimpleMetadataHandler {

        final AtomicLong count = new AtomicLong(0L);

        @Override
        public void startDocument() {
            logger.log(Level.FINE, "start doc");
        }

        @Override
        public void endDocument() {
            logger.log(Level.FINE, "end doc");
            count.incrementAndGet();
        }

        long count() {
            return count.get();
        }
    }
}
