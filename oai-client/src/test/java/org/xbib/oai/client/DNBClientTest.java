package org.xbib.oai.client;

import static org.junit.Assert.assertEquals;

import io.netty.handler.codec.http.HttpHeaderNames;
import org.junit.Ignore;
import org.junit.Test;
import org.xbib.net.URL;
import org.xbib.netty.http.client.Client;
import org.xbib.netty.http.client.Request;
import org.xbib.oai.client.identify.IdentifyRequest;
import org.xbib.oai.client.listrecords.ListRecordsRequest;
import org.xbib.oai.client.listrecords.ListRecordsResponse;
import org.xbib.oai.xml.SimpleMetadataHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
@Ignore
public class DNBClientTest {

    private static final Logger logger = Logger.getLogger(DNBClientTest.class.getName());

    @Test
    public void testIdentify() {
        URL url = URL.create("http://services.dnb.de/oai/repository");
        try (OAIClient client = new OAIClient(url)) {
            IdentifyRequest identifyRequest = client.newIdentifyRequest();
            Client httpClient = client.getHttpClient();
            assertEquals("/oai/repository?verb=Identify", identifyRequest.getURL().toString());
            Request request = Request.get()
                    .url(url.resolve(identifyRequest.getURL()))
                    .build()
                    .setResponseListener(resp -> logger.log(Level.INFO, resp.getBodyAsString(StandardCharsets.UTF_8)));
            httpClient.execute(request).get();
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Test
    public void testListRecordsDNB() {
        URL url = URL.create("http://services.dnb.de/oai/repository");
        try (OAIClient client = new OAIClient(url)) {
            ListRecordsRequest listRecordsRequest = client.newListRecordsRequest();
            listRecordsRequest.setFrom(Instant.parse("2016-01-01T00:00:00Z"));
            listRecordsRequest.setUntil(Instant.parse("2016-01-10T00:00:00Z"));
            listRecordsRequest.setSet("bib");
            listRecordsRequest.setMetadataPrefix("PicaPlus-xml");
            Handler handler = new Handler();
            File file = File.createTempFile("dnb-bib-pica.", ".xml");
            file.deleteOnExit();
            FileWriter fileWriter = new FileWriter(file);
            while (listRecordsRequest != null) {
                try {
                    ListRecordsResponse listRecordsResponse = new ListRecordsResponse(listRecordsRequest);
                    listRecordsRequest.addHandler(handler);
                    Request request = Request.get()
                            .url(url.resolve(listRecordsRequest.getURL()))
                            .addHeader(HttpHeaderNames.ACCEPT.toString(), "utf-8")
                            .build()
                            .setResponseListener(resp -> listRecordsResponse.receivedResponse(resp, fileWriter));
                    client.getHttpClient().execute(request).get();
                    listRecordsRequest = client.resume(listRecordsRequest, listRecordsResponse.getResumptionToken());
                } catch (IOException e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    listRecordsRequest = null;
                }
            }
            fileWriter.close();
            logger.log(Level.INFO, "count=" + handler.count());
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
