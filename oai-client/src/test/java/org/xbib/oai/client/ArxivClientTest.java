package org.xbib.oai.client;

import static org.junit.Assert.assertTrue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
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
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 */
public class ArxivClientTest {

    private static final Logger logger = LogManager.getLogger(ArxivClientTest.class.getName());

    @Test
    public void testListRecordsArxiv() throws Exception {
        try {
            OAIClient client = OAIClientFactory.newClient("http://export.arxiv.org/oai2");
            IdentifyRequest identifyRequest = client.newIdentifyRequest();
            HttpClient httpClient = client.getHttpClient();
            AggregatedHttpMessage response = httpClient.execute(HttpHeaders.of(HttpMethod.GET, identifyRequest.getPath())
                    .set(HttpHeaderNames.ACCEPT, "utf-8")).aggregate().get();
            IdentifyResponse identifyResponse = new IdentifyResponse();
            identifyResponse.receivedResponse(response, new StringWriter());
            String granularity = identifyResponse.getGranularity();
            logger.info("granularity = {}", granularity);
            DateTimeFormatter dateTimeFormatter = "YYYY-MM-DD".equals(granularity) ?
                    DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.of("GMT")) : null;
            // ArXiv wants us to wait 20 secs between *every* HTTP request, so we must wait here
            logger.info("waiting 20 seconds");
            Thread.sleep(20 * 1000L);
            ListRecordsRequest listRecordsRequest = client.newListRecordsRequest();
            listRecordsRequest.setDateTimeFormatter(dateTimeFormatter);
            listRecordsRequest.setFrom(Instant.parse("2016-11-01T00:00:00Z"));
            listRecordsRequest.setUntil(Instant.parse("2016-11-02T00:00:00Z"));
            listRecordsRequest.setMetadataPrefix("arXiv");
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
            File file = File.createTempFile("arxiv.", ".xml");
            file.deleteOnExit();
            FileWriter fileWriter = new FileWriter(file);
            while (listRecordsRequest != null) {
                try {
                    listRecordsRequest.addHandler(simpleMetadataHandler);
                    ListRecordsResponse listRecordsResponse = new ListRecordsResponse(listRecordsRequest);
                    logger.info("sending {}", listRecordsRequest.getPath());
                    response = httpClient.execute(HttpHeaders.of(HttpMethod.GET, listRecordsRequest.getPath())
                            .set(HttpHeaderNames.ACCEPT, "utf-8")).aggregate().get();
                    logger.debug("response headers = {} resumption-token = {}",
                            response.headers(), listRecordsResponse.getResumptionToken());
                    listRecordsResponse.receivedResponse(response, fileWriter);
                    listRecordsRequest = client.resume(listRecordsRequest, listRecordsResponse.getResumptionToken());
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                    listRecordsRequest = null;
                }
            }
            fileWriter.close();
            client.close();
            logger.info("count={}", count.get());
            assertTrue(count.get() > 0L);
        } catch (ConnectException | ExecutionException e) {
            logger.warn("skipped, can not connect", e);
        } catch (InterruptedException | IOException e) {
            throw e;
        }
    }
}
