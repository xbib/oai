package org.xbib.oai.client;

import io.netty.handler.codec.http.HttpHeaderNames;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.xbib.net.URL;
import org.xbib.netty.http.client.Client;
import org.xbib.netty.http.client.api.Request;
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
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
class DNBClientTest {

    private static final Logger logger = Logger.getLogger(DNBClientTest.class.getName());

    @Test
    void testBibdat() {
        URL url = URL.create("http://services.dnb.de/oai/repository");
        try (Client httpClient = Client.builder()
                .setConnectTimeoutMillis(60 * 1000)
                .setReadTimeoutMillis(60 * 1000)
                .build();
             OAIClient oaiClient = new OAIClient(url)) {
            IdentifyRequest identifyRequest = oaiClient.newIdentifyRequest();
            IdentifyResponse identifyResponse = new IdentifyResponse();
            Request request = Request.get()
                    .url(identifyRequest.getURL())
                    .setResponseListener(resp -> {
                        logger.log(Level.INFO, resp.getBodyAsString(StandardCharsets.UTF_8));
                        StringWriter sw = new StringWriter();
                        identifyResponse.receivedResponse(resp, sw);
                    })
                    .build();
            httpClient.execute(request).get();
            String granularity = identifyResponse.getGranularity();
            logger.log(Level.INFO, "granularity = " + granularity);
            ListRecordsRequest listRecordsRequest = oaiClient.newListRecordsRequest();
            listRecordsRequest.setFrom(Instant.parse("2016-01-01T00:00:00Z"));
            listRecordsRequest.setUntil(Instant.parse("2016-01-10T00:00:00Z"));
            listRecordsRequest.setSet("bib");
            listRecordsRequest.setMetadataPrefix("PicaPlus-xml");
            Handler handler = new Handler();
            File file =  new File("build/dnb-bib-pica.xml");
            FileWriter fileWriter = new FileWriter(file);
            while (listRecordsRequest != null) {
                try {
                    ListRecordsResponse listRecordsResponse = new ListRecordsResponse(listRecordsRequest);
                    listRecordsRequest.addHandler(handler);
                    request = Request.get()
                            .url(listRecordsRequest.getURL())
                            .addHeader(HttpHeaderNames.ACCEPT.toString(), "utf-8")
                            .setResponseListener(resp -> listRecordsResponse.receivedResponse(resp, fileWriter))
                            .build();
                    httpClient.execute(request).get();
                    listRecordsRequest = oaiClient.resume(listRecordsRequest, listRecordsResponse.getResumptionToken());
                } catch (ConnectException e) {
                    logger.log(Level.WARNING, e.getMessage(), e);
                } catch (IOException e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    listRecordsRequest = null;
                }
            }
            fileWriter.close();
            logger.log(Level.INFO, "count=" + handler.count());
            assertTrue(handler.count() > 0);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "skipped, HTTP exception");
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
