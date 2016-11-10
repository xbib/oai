package org.xbib.oai.server;

import org.xbib.oai.OAIResponse;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.util.XMLEventConsumer;

/**
 * Default OAI response.
 */
public class ServerOAIResponse implements OAIResponse {

    private String format;

    private XMLEventConsumer consumer;

    public String getOutputFormat() {
        return format;
    }

    @Override
    public void to(Writer writer) throws IOException {
    }


    public ServerOAIResponse setConsumer(XMLEventConsumer consumer) {
        this.consumer = consumer;
        return this;
    }

    public XMLEventConsumer getConsumer() {
        return consumer;
    }

}
