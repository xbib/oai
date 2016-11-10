package org.xbib.oai.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class OAIServiceFactory {

    private static final Logger logger = Logger.getLogger(OAIServiceFactory.class.getName());

    private static final Map<URI, OAIServer> services = new HashMap<>();

    private static final OAIServiceFactory instance = new OAIServiceFactory();

    private OAIServiceFactory() {
        ServiceLoader<OAIServer> loader = ServiceLoader.load(OAIServer.class);
        for (OAIServer service : loader) {
            try {
                URI uri = service.getURL().toURI();
                if (!services.containsKey(uri)) {
                    services.put(uri, service);
                }
            } catch (URISyntaxException e) {
                logger.log(Level.FINE, e.getMessage(), e);
            }
        }
    }

    public static OAIServiceFactory getInstance() {
        return instance;
    }

    public static OAIServer getDefaultService() {
        return services.isEmpty() ? null : services.entrySet().iterator().next().getValue();
    }

    public static OAIServer getService(URI uri) {
        if (services.containsKey(uri)) {
            return services.get(uri);
        }
        throw new IllegalArgumentException("OAI service " + uri + " not found in " + services);
    }

    public static OAIServer getService(String name) {
        Properties properties = new Properties();
        InputStream in = instance.getClass().getResourceAsStream("/org/xbib/oai/service/" + name + ".properties");
        if (in != null) {
            try {
                properties.load(in);
            } catch (IOException e) {
                logger.log(Level.FINE, e.getMessage(), e);
            }
        } else {
            throw new IllegalArgumentException("service " + name + " not found");
        }
        return new PropertiesOAIServer(properties);
    }
}
