package org.xbib.oai.client;

import io.netty.handler.codec.http.HttpHeaderNames;
import org.junit.Ignore;
import org.junit.Test;
import org.xbib.marc.Marc;
import org.xbib.marc.json.MarcJsonWriter;
import org.xbib.marc.xml.MarcContentHandler;
import org.xbib.net.URL;
import org.xbib.netty.http.client.Client;
import org.xbib.netty.http.client.Request;
import org.xbib.oai.client.identify.IdentifyRequest;
import org.xbib.oai.client.identify.IdentifyResponse;
import org.xbib.oai.client.listrecords.ListRecordsRequest;
import org.xbib.oai.client.listrecords.ListRecordsResponse;
import org.xbib.oai.exceptions.OAIException;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class BundeskunsthalleTest {

    private static final Logger logger = Logger.getLogger(BundeskunsthalleTest.class.getName());

    @Test
    @Ignore // takes too long time and creates files
    public void testListRecords() throws Exception {
        URL url = URL.create("http://www.bundeskunsthalle.de/cgi-bin/bib/oai-pmh");
        try (OAIClient oaiClient = new OAIClient(url)) {
            IdentifyRequest identifyRequest = oaiClient.newIdentifyRequest();
            Client httpClient = oaiClient.getHttpClient();
            IdentifyResponse identifyResponse = new IdentifyResponse();
            Request request = Request.get()
                    .url(url.resolve(identifyRequest.getURL()))
                    .addHeader(HttpHeaderNames.ACCEPT.toString(), "utf-8")
                    .build()
                    .setResponseListener(resp -> {
                        StringWriter sw = new StringWriter();
                        identifyResponse.receivedResponse(resp, sw);
                    });
            httpClient.execute(request).get();

            /*AggregatedHttpMessage response = client.execute(HttpHeaders.of(HttpMethod.GET, identifyRequest.getPath())
                    .set(HttpHeaderNames.ACCEPT, "utf-8")).aggregate().get();*/
            // follow a maximum of 10 HTTP redirects
            /*int max = 10;
            while (response.followUrl() != null && max-- > 0) {
                URI uri = URI.create(response.followUrl());
                client = Clients.newClient(oaiClient.getFactory(), "none+" + uri, HttpClient.class);
                response = client.execute(HttpHeaders.of(HttpMethod.GET, response.followUrl())
                        .set(HttpHeaderNames.ACCEPT, "utf-8")).aggregate().get();
            }*/
            String granularity = identifyResponse.getGranularity();
            logger.log(Level.INFO, "granularity = " + granularity);
            DateTimeFormatter dateTimeFormatter = "YYYY-MM-DD".equals(granularity) ?
                    DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.of("GMT")) : null;
            ListRecordsRequest listRecordsRequest = oaiClient.newListRecordsRequest();
            listRecordsRequest.setDateTimeFormatter(dateTimeFormatter);
            listRecordsRequest.setMetadataPrefix("marcxml");
            try (MarcJsonWriter writer = new MarcJsonWriter("bk-bulk%d.jsonl", 1000,
                    MarcJsonWriter.Style.ELASTICSEARCH_BULK, 65536, false)
                    .setIndex("testindex", "testtype")) {
                writer.startDocument();
                writer.beginCollection();
                while (listRecordsRequest != null) {
                    try {
                        ListRecordsResponse listRecordsResponse = new ListRecordsResponse(listRecordsRequest);
                        logger.log(Level.INFO,"sending " + listRecordsRequest.getURL());
                        StringWriter sw = new StringWriter();
                        request = Request.get()
                                .url(url.resolve(listRecordsRequest.getURL()))
                                .addHeader(HttpHeaderNames.ACCEPT.toString(), "utf-8")
                                .build()
                                .setResponseListener(resp -> {
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
                                        throw new OAIException(e);
                                    }
                                    listRecordsResponse.receivedResponse(resp, sw);
                                    logger.log(Level.FINE, "response headers = " + resp.getHeaders() +
                                            " resumption-token = {}" + listRecordsResponse.getResumptionToken());
                                });
                        httpClient.execute(request).get();
                        // follow a maximum of 10 HTTP redirects
                        /*max = 10;
                        while (response.followUrl() != null && max-- > 0) {
                            URI uri = URI.create(response.followUrl());
                            client = Clients.newClient(oaiClient.getFactory(), "none+" + uri, HttpClient.class);
                            response = client.execute(HttpHeaders.of(HttpMethod.GET, response.followUrl())
                                    .set(HttpHeaderNames.ACCEPT, "utf-8")).aggregate().get();
                        }*/
                        listRecordsRequest = oaiClient.resume(listRecordsRequest, listRecordsResponse.getResumptionToken());
                    } catch (IOException e) {
                        logger.log(Level.SEVERE, e.getMessage(), e);
                        listRecordsRequest = null;
                    }
                }
                writer.endCollection();
                writer.endDocument();
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }
    }
}
