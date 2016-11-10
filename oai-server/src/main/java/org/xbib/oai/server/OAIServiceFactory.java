package org.xbib.oai.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;

/**
 *
 */
public class OAIServiceFactory {

    private static final Map<URL, OAIServer> services = new HashMap<>();

    private static final OAIServiceFactory instance = new OAIServiceFactory();

    private OAIServiceFactory() {
        ServiceLoader<OAIServer> loader = ServiceLoader.load(OAIServer.class);
        for (OAIServer service : loader) {
            if (!services.containsKey(service.getURL())) {
                services.put(service.getURL(), service);
            }
        }
    }

    public static OAIServiceFactory getInstance() {
        return instance;
    }

    public static OAIServer getDefaultService() {
        return services.isEmpty() ? null : services.entrySet().iterator().next().getValue();
    }

    public static OAIServer getService(URL url) {
        if (services.containsKey(url)) {
            return services.get(url);
        }
        throw new IllegalArgumentException("OAI service " + url + " not found in " + services);
    }

    public static OAIServer getService(String name) {
        Properties properties = new Properties();
        InputStream in = instance.getClass().getResourceAsStream("/org/xbib/oai/service/" + name + ".properties");
        if (in != null) {
            try {
                properties.load(in);
            } catch (IOException ex) {
                // ignore
            }
        } else {
            throw new IllegalArgumentException("service " + name + " not found");
        }
        return new PropertiesOAIServer(properties);
    }
}
