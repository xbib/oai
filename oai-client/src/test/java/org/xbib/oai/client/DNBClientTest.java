package org.xbib.oai.client;

import static org.junit.Assert.assertEquals;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Ignore;
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;
import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 */
@Ignore
public class DNBClientTest {

    private static final Logger logger = LogManager.getLogger(DNBClientTest.class.getName());

    @Test
    public void testIdentify() throws Exception {
        OAIClient client = new OAIClient().setURL(new URL("http://services.dnb.de/oai/repository"));
        IdentifyRequest request = client.newIdentifyRequest();
        HttpClient httpClient = client.getHttpClient();
        assertEquals("/oai/repository?verb=Identify", request.getPath());
        AggregatedHttpMessage response = httpClient.get(request.getPath()).aggregate().get();
        logger.info("{}", response.content().toStringUtf8());
    }

    @Test
    public void testListRecordsDNB() throws Exception {
        try (OAIClient client = new OAIClient().setURL(new URL("http://services.dnb.de/oai/repository"))){
            ListRecordsRequest listRecordsRequest = client.newListRecordsRequest();
            listRecordsRequest.setFrom(Instant.parse("2016-01-01T00:00:00Z"));
            listRecordsRequest.setUntil(Instant.parse("2016-01-10T00:00:00Z"));
            listRecordsRequest.setSet("bib");
            listRecordsRequest.setMetadataPrefix("PicaPlus-xml");
            Handler handler = new Handler();
            File file = File.createTempFile("dnb-bib-pica.", ".xml");
            file.deleteOnExit();
            FileWriter sw = new FileWriter(file);
            while (listRecordsRequest != null) {
                try {
                    ListRecordsResponse listRecordsResponse = new ListRecordsResponse(listRecordsRequest);
                    listRecordsRequest.addHandler(handler);
                    HttpClient httpClient = client.getHttpClient();
                    AggregatedHttpMessage response =
                            httpClient.execute(HttpHeaders.of(HttpMethod.GET, listRecordsRequest.getPath())
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
            logger.info("count={}", handler.count());
        } catch (ConnectException | ExecutionException e) {
            logger.warn("skipped, can not connect");
        } catch (IOException e) {
            logger.warn("skipped, HTTP exception");
        }
    }

    class Handler extends SimpleMetadataHandler {

        final AtomicLong count = new AtomicLong(0L);

        @Override
        public void startDocument() {
            logger.debug("start doc");
        }

        @Override
        public void endDocument() {
            logger.debug("end doc");
            count.incrementAndGet();
        }

        long count() {
            return count.get();
        }
    }
}
