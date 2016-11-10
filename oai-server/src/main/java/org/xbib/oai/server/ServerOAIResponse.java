package org.xbib.oai.server;

import org.xbib.oai.OAIResponse;

import javax.xml.stream.util.XMLEventConsumer;

/**
 * Default OAI response.
 */
public class ServerOAIResponse implements OAIResponse {

    private XMLEventConsumer consumer;

    public ServerOAIResponse setConsumer(XMLEventConsumer consumer) {
        this.consumer = consumer;
        return this;
    }

    public XMLEventConsumer getConsumer() {
        return consumer;
    }

}
