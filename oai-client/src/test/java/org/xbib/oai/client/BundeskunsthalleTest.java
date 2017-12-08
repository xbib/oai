package org.xbib.oai.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.xbib.helianthus.client.Clients;
import org.xbib.helianthus.client.http.HttpClient;
import org.xbib.helianthus.common.http.AggregatedHttpMessage;
import org.xbib.helianthus.common.http.HttpHeaderNames;
import org.xbib.helianthus.common.http.HttpHeaders;
import org.xbib.helianthus.common.http.HttpMethod;
import org.xbib.marc.Marc;
import org.xbib.marc.json.MarcJsonWriter;
import org.xbib.marc.xml.MarcContentHandler;
import org.xbib.oai.client.identify.IdentifyRequest;
import org.xbib.oai.client.identify.IdentifyResponse;
import org.xbib.oai.client.listrecords.ListRecordsRequest;
import org.xbib.oai.client.listrecords.ListRecordsResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;

/**
 *
 */
public class BundeskunsthalleTest {

    private static final Logger logger = LogManager.getLogger(BundeskunsthalleTest.class.getName());

    @Test
    public void testListRecords() throws Exception {
        String spec = "http://www.bundeskunsthalle.de/cgi-bin/bib/oai-pmh";
        try (OAIClient oaiClient = new OAIClient().setURL(new URL(spec), true)) {
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
            logger.debug("identifyResponse = {}", response.content().toStringUtf8());
            identifyResponse.receivedResponse(response, new StringWriter());
            String granularity = identifyResponse.getGranularity();
            logger.info("granularity = {}", granularity);
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
                        logger.debug("response = {}", response.headers());
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
                        InputStream inputStream = new ByteArrayInputStream(response.content().array());
                        Marc.builder()
                                .setInputStream(inputStream)
                                .setCharset(StandardCharsets.UTF_8)
                                .setContentHandler(new MarcContentHandler()
                                        .setFormat("MarcXML")
                                        .setType("Bibliographic")
                                        .addNamespace("http://www.loc.gov/MARC21/slim")
                                        .setMarcListener(writer))
                                .build()
                                .xmlReader().parse();
                        listRecordsResponse.receivedResponse(response, new StringWriter());
                        listRecordsRequest = oaiClient.resume(listRecordsRequest, listRecordsResponse.getResumptionToken());
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                        listRecordsRequest = null;
                    }
                }
                writer.endCollection();
                writer.endDocument();
            }
        } catch (ConnectException | ExecutionException e) {
            logger.warn("skipped, can not connect, exception is:", e);
        } catch (InterruptedException | IOException e) {
            throw e;
        }
    }
}
