package org.xbib.oai.client;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;
import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.xbib.helianthus.client.http.HttpClient;
import org.xbib.helianthus.common.http.AggregatedHttpMessage;
import org.xbib.helianthus.common.http.HttpHeaderNames;
import org.xbib.helianthus.common.http.HttpHeaders;
import org.xbib.helianthus.common.http.HttpMethod;
import org.xbib.oai.client.identify.IdentifyRequest;
import org.xbib.oai.client.listrecords.ListRecordsRequest;
import org.xbib.oai.client.listrecords.ListRecordsResponse;
import org.xbib.oai.xml.SimpleMetadataHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class DNBClientTest {

    private static final Logger logger = LogManager.getLogger(DNBClientTest.class.getName());

    @Test
    public void testIdentify() throws Exception {
        OAIClient client = new DefaultOAIClient().setURL(new URL("http://services.dnb.de/oai/repository"));
        IdentifyRequest request = client.newIdentifyRequest();
        HttpClient httpClient = client.getHttpClient();
        assertEquals("/oai/repository?verb=Identify", request.getPath());
        AggregatedHttpMessage response = httpClient.get(request.getPath()).aggregate().get();
        logger.info("{}", response.content().toStringUtf8());
    }

    @Test
    public void testListRecordsDNB() throws Exception {
        try {
            OAIClient client = new DefaultOAIClient().setURL(new URL("http://services.dnb.de/oai/repository"));
            ListRecordsRequest listRecordsRequest = client.newListRecordsRequest();
            listRecordsRequest.setFrom(Instant.parse("2016-01-01T00:00:00Z"));
            listRecordsRequest.setUntil(Instant.parse("2016-01-10T00:00:00Z"));
            listRecordsRequest.setSet("bib");
            listRecordsRequest.setMetadataPrefix("PicaPlus-xml");
            final AtomicLong count = new AtomicLong(0L);
            SimpleMetadataHandler simpleMetadataHandler = new SimpleMetadataHandler() {
                @Override
                public void startDocument() throws SAXException {
                    logger.debug("startDocument");
                }

                @Override
                public void endDocument() throws SAXException {
                    count.incrementAndGet();
                    logger.debug("endDocument");
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
            File file = File.createTempFile("dnb-bib-pica.", ".xml");
            file.deleteOnExit();
            FileWriter sw = new FileWriter(file);
            while (listRecordsRequest != null) {
                try {
                    ListRecordsResponse listRecordsResponse = new ListRecordsResponse(listRecordsRequest);
                    listRecordsRequest.addHandler(simpleMetadataHandler);
                    HttpClient httpClient = client.getHttpClient();
                    AggregatedHttpMessage response = httpClient.execute(HttpHeaders.of(HttpMethod.GET, listRecordsRequest.getPath())
                            .set(HttpHeaderNames.ACCEPT, "utf-8")).aggregate().get();
                    String content = response.content().toStringUtf8();
                    listRecordsResponse.receivedResponse(response, sw);
                    listRecordsRequest = client.resume(listRecordsRequest, listRecordsResponse.getResumptionToken());
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                    listRecordsRequest = null;
                }
            }
            sw.close();
            client.close();
            logger.info("count={}", count.get());
        } catch (ConnectException | ExecutionException e) {
            logger.warn("skipped, can not connect");
        } catch (IOException e) {
            logger.warn("skipped, HTTP exception");
        }
    }
}
