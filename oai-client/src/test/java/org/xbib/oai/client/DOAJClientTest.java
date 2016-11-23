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
import org.xbib.oai.client.identify.IdentifyRequest;
import org.xbib.oai.client.identify.IdentifyResponse;
import org.xbib.oai.client.listrecords.ListRecordsRequest;
import org.xbib.oai.client.listrecords.ListRecordsResponse;
import org.xbib.oai.xml.SimpleMetadataHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

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

import static org.junit.Assert.assertTrue;

/**
 *
 */
public class DOAJClientTest {

    private static final Logger logger = LogManager.getLogger(DOAJClientTest.class.getName());

    @Test
    public void testListRecordsDOAJ() throws Exception {
        // will redirect to https://doaj.org/oai
        try (DefaultOAIClient oaiClient = new DefaultOAIClient().setURL(new URL("http://doaj.org/oai"), true)) {
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
            listRecordsRequest.setFrom(Instant.parse("2016-01-06T00:00:00Z"));
            listRecordsRequest.setUntil(Instant.parse("2016-11-07T00:00:00Z"));
            listRecordsRequest.setMetadataPrefix("oai_dc");
            final AtomicLong count = new AtomicLong(0L);
            SimpleMetadataHandler simpleMetadataHandler = new SimpleMetadataHandler() {
                @Override
                public void startDocument() throws SAXException {
                    logger.debug("start doc");
                }

                @Override
                public void endDocument() throws SAXException {
                    logger.debug("end doc");
                    count.incrementAndGet();
                }

                @Override
                public void startPrefixMapping(String prefix, String uri) throws SAXException {
                }

                @Override
                public void endPrefixMapping(String prefix) throws SAXException {
                }

                @Override
                public void startElement(String ns, String localname, String qname, Attributes atrbts) throws SAXException {
                }

                @Override
                public void endElement(String ns, String localname, String qname) throws SAXException {
                }

                @Override
                public void characters(char[] chars, int pos, int len) throws SAXException {
                }
            };
            File file = File.createTempFile("doaj.", ".xml");
            file.deleteOnExit();
            FileWriter fileWriter = new FileWriter(file);
            while (listRecordsRequest != null) {
                try {
                    listRecordsRequest.addHandler(simpleMetadataHandler);
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
                    ListRecordsResponse listRecordsResponse = new ListRecordsResponse(listRecordsRequest);
                    logger.debug("response = {}", response.headers());
                    listRecordsResponse.receivedResponse(response, fileWriter);
                    listRecordsRequest = oaiClient.resume(listRecordsRequest, listRecordsResponse.getResumptionToken());
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                    listRecordsRequest = null;
                }
            }
            fileWriter.close();
            logger.info("count={}", count.get());
            assertTrue(count.get() > 0L);
        } catch (ConnectException | ExecutionException e) {
            logger.warn("skipped, can not connect, exception is:", e);
        } catch (InterruptedException | IOException e) {
            throw e;
        }
    }

}
