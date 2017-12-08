package org.xbib.oai.server;

import org.xbib.oai.OAIResponse;

import javax.xml.stream.util.XMLEventConsumer;

/**
 * Default OAI response.
 */
public abstract class AbstractOAIResponse implements OAIResponse {

    private XMLEventConsumer consumer;

    public AbstractOAIResponse setConsumer(XMLEventConsumer consumer) {
        this.consumer = consumer;
        return this;
    }

    public XMLEventConsumer getConsumer() {
        return consumer;
    }

}
