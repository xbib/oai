package org.xbib.oai.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;


/**
 *  Factory for OAI clients
 *
 */
public class OAIClientFactory {

    private final static OAIClientFactory instance = new OAIClientFactory();

    private OAIClientFactory() {
    }

    public static OAIClientFactory getInstance() {
        return instance;
    }

    public static OAIClient newClient() {
        return new DefaultOAIClient();
    }

    public static OAIClient newClient(String spec) {
        return newClient(spec, false);
    }

    public static OAIClient newClient(String spec, boolean trustAll) {
        Properties properties = new Properties();
        InputStream in = instance.getClass().getResourceAsStream("/org/xbib/oai/client/" + spec + ".properties");
        if (in != null) {
            try {
                properties.load(in);
            } catch (IOException ex) {
                // ignore
            }
            DefaultOAIClient client = new DefaultOAIClient();
            try {
                client.setURL(new URL(properties.getProperty("uri")), trustAll);
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
            return client;
        } else {
            DefaultOAIClient client = new DefaultOAIClient();
            try {
                client.setURL(new URL(spec));
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
            return client;
        }
    }
}
