package org.xbib.oai.client;

import static org.junit.Assert.assertTrue;

import io.netty.handler.codec.http.HttpHeaderNames;
import org.junit.Test;
import org.xbib.net.URL;
import org.xbib.netty.http.client.Client;
import org.xbib.netty.http.client.Request;
import org.xbib.oai.client.identify.IdentifyRequest;
import org.xbib.oai.client.identify.IdentifyResponse;
import org.xbib.oai.client.listrecords.ListRecordsRequest;
import org.xbib.oai.client.listrecords.ListRecordsResponse;
import org.xbib.oai.xml.SimpleMetadataHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class ArxivClientTest {

    private static final Logger logger = Logger.getLogger(ArxivClientTest.class.getName());

    @Test
    public void testListRecordsArxiv() {
        final URL url = URL.create("http://export.arxiv.org/oai2/");
        try (OAIClient client = new OAIClient(url)) {
            IdentifyRequest identifyRequest = client.newIdentifyRequest();
            Client httpClient  = Client.builder()
                    .setConnectTimeoutMillis(60 * 1000)
                    .setReadTimeoutMillis(60 * 1000)
                    .build();
            IdentifyResponse identifyResponse = new IdentifyResponse();
            Request request = Request.get()
                    .url(identifyRequest.getURL())
                    .addHeader(HttpHeaderNames.ACCEPT.toString(), "utf-8")
                    .build()
                    .setResponseListener(resp -> {
                        logger.log(Level.INFO,
                                " body = " + resp.getBodyAsString(StandardCharsets.UTF_8));
                        StringWriter sw = new StringWriter();
                        identifyResponse.receivedResponse(resp, sw);
                    });
            httpClient.execute(request).get();
            String granularity = identifyResponse.getGranularity();
            logger.log(Level.INFO, "granularity = " + granularity);
            DateTimeFormatter dateTimeFormatter = "YYYY-MM-DD".equals(granularity) ?
                    DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.of("GMT")) : null;
            // ArXiv wants us to wait 20 secs between *every* HTTP request, so we must wait here
            logger.log(Level.INFO,"waiting 20 seconds");
            Thread.sleep(20 * 1000L);
            ListRecordsRequest listRecordsRequest = client.newListRecordsRequest();
            listRecordsRequest.setDateTimeFormatter(dateTimeFormatter);
            listRecordsRequest.setFrom(Instant.parse("2016-11-01T00:00:00Z"));
            listRecordsRequest.setUntil(Instant.parse("2016-11-02T00:00:00Z"));
            listRecordsRequest.setMetadataPrefix("arXiv");
            Handler handler  = new Handler();
            File file = File.createTempFile("arxiv.", ".xml");
            file.deleteOnExit();
            FileWriter fileWriter = new FileWriter(file);
            while (listRecordsRequest != null) {
                try {
                    listRecordsRequest.addHandler(handler);
                    ListRecordsResponse listRecordsResponse = new ListRecordsResponse(listRecordsRequest);
                    logger.log(Level.INFO,"sending " + listRecordsRequest.getURL());
                    request = Request.get()
                            .url(listRecordsRequest.getURL())
                            .addHeader(HttpHeaderNames.ACCEPT.toString(), "utf-8")
                            .build()
                            .setResponseListener(resp -> {
                                listRecordsResponse.receivedResponse(resp, fileWriter);
                                logger.log(Level.FINE, "response headers = " + resp.getHeaders() +
                                        " resumption-token = " + listRecordsResponse.getResumptionToken());
                            });
                    httpClient.execute(request).get();
                    listRecordsRequest = client.resume(listRecordsRequest, listRecordsResponse.getResumptionToken());
                } catch (IOException e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    listRecordsRequest = null;
                }
            }
            fileWriter.close();
            httpClient.shutdownGracefully();
            logger.log(Level.INFO, "count = " + handler.count());
            assertTrue(handler.count() > 0L);
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
