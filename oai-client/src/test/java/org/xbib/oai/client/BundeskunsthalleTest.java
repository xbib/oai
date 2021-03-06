package org.xbib.oai.client;

import io.netty.handler.codec.http.HttpHeaderNames;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.xbib.marc.Marc;
import org.xbib.marc.json.MarcJsonWriter;
import org.xbib.marc.xml.MarcContentHandler;
import org.xbib.net.URL;
import org.xbib.netty.http.client.Client;
import org.xbib.netty.http.client.api.Request;
import org.xbib.oai.client.identify.IdentifyRequest;
import org.xbib.oai.client.identify.IdentifyResponse;
import org.xbib.oai.client.listrecords.ListRecordsRequest;
import org.xbib.oai.client.listrecords.ListRecordsResponse;
import org.xbib.oai.exceptions.OAIException;

import java.io.IOException;
import java.io.StringWriter;
import java.net.ConnectException;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
class BundeskunsthalleTest {

    private static final Logger logger = Logger.getLogger(BundeskunsthalleTest.class.getName());

    @Test
    @Disabled("takes long time")
    void testListRecords() {
        URL url = URL.create("https://www.bundeskunsthalle.de/cgi-bin/bib/oai-pmh");
        try (Client httpClient = Client.builder()
                .setConnectTimeoutMillis(60 * 1000)
                .setReadTimeoutMillis(60 * 1000)
                .build();
              OAIClient oaiClient = new OAIClient(url)) {
            IdentifyRequest identifyRequest = oaiClient.newIdentifyRequest();
            IdentifyResponse identifyResponse = new IdentifyResponse();
            Request request = Request.get()
                    .url(identifyRequest.getURL())
                    .addHeader(HttpHeaderNames.ACCEPT.toString(), "utf-8")
                    .setFollowRedirect(true)
                    .setResponseListener(resp -> {
                        logger.log(Level.INFO,
                                "status = " + resp.getStatus() +
                                " body = " + resp.getBodyAsString(StandardCharsets.UTF_8));
                        StringWriter sw = new StringWriter();
                        identifyResponse.receivedResponse(resp, sw);
                    })
                    .build();
            httpClient.execute(request).get();
            String granularity = identifyResponse.getGranularity();
            logger.log(Level.INFO, "granularity = " + granularity);
            DateTimeFormatter dateTimeFormatter = "YYYY-MM-DD".equals(granularity) ?
                    DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.of("UTC")) : null;
            ListRecordsRequest listRecordsRequest = oaiClient.newListRecordsRequest();
            listRecordsRequest.setDateTimeFormatter(dateTimeFormatter);
            listRecordsRequest.setMetadataPrefix("marcxml");
            try (MarcJsonWriter writer = new MarcJsonWriter("build/bk-bulk%d.jsonl", 1000,
                    EnumSet.of(MarcJsonWriter.Style.ELASTICSEARCH_BULK), 65536, false)
                    .setIndex("testindex", "testtype")) {
                writer.startDocument();
                writer.beginCollection();
                while (listRecordsRequest != null) {
                    try {
                        ListRecordsResponse listRecordsResponse = new ListRecordsResponse(listRecordsRequest);
                        logger.log(Level.INFO, "sending " + listRecordsRequest.getURL());
                        request = Request.get()
                                .url(listRecordsRequest.getURL())
                                .addHeader(HttpHeaderNames.ACCEPT.toString(), "utf-8")
                                .setFollowRedirect(true)
                                .setTimeoutInMillis(60 * 1000)
                                .setResponseListener(resp -> {
                                    logger.log(Level.FINE,
                                            "status = " + resp.getStatus() +
                                                    " headers = " + resp.getHeaders() +
                                                    " resumptiontoken = " + listRecordsResponse.getResumptionToken());
                                    StringWriter sw = new StringWriter();
                                    listRecordsResponse.receivedResponse(resp, sw);
                                    try {
                                        Marc.builder()
                                                .setInputStream(resp.getBodyAsStream())
                                                .setCharset(StandardCharsets.UTF_8)
                                                .setContentHandler(new MarcContentHandler()
                                                        .setFormat("MarcXML")
                                                        .setType("Bibliographic")
                                                        .addNamespace("http://www.loc.gov/MARC21/slim")
                                                        .setMarcListener(writer))
                                                .build()
                                                .xmlReader().parse();
                                    } catch (IOException e) {
                                        throw new OAIException("MARC parser exception: " + e.getMessage(), e);
                                    }
                                })
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
                writer.endCollection();
                writer.endDocument();
            }
            logger.log(Level.INFO, "completed");
        } catch (Exception e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }
    }
}
