package MDfromLogQueries.SPARQLSyntacticalValidation;

import org.apache.jena.iri.impl.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

public class Resources {

    private static Logger getLazyLogger() {
        return LoggerFactory.getLogger(Resources.class);
    }

    private static final ThreadLocal<ClassLoader> cl = new ThreadLocal<>() {
        @Override
        protected ClassLoader initialValue() {
            return Thread.currentThread().getContextClassLoader();
        }
    };

    public static ClassLoader currentClassLoader() {
        return cl.get();
    }


    private static final Properties properties = System.getProperties();

    static {
        if (!loadHostProperties()) {
            if (loadProperties(properties, "local.prop")) {
                getLazyLogger().debug("Loaded {}", "local.prop");
            }
            if (loadProperties(properties, "local.properties")) {
                getLazyLogger().debug("Loaded {}", "local.properties");
            }
        } else {
            getLazyLogger().debug("Loaded host specific properties");
        }
    }

    private static boolean loadHostProperties() {
        String hostname = getHostname();
        if (hostname != null) {
            String filename = String.format("local-%s.prop", hostname);
            return loadProperties(properties, filename);
        }
        return false;
    }

    public static String getHostname() {
        String hostname = System.getenv("HOSTNAME");
        if (hostname == null) {
            hostname = System.getenv("COMPUTERNAME");
        }
        if (hostname == null) {
            try {
                hostname = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException ignored) {
            }
        }
        if (hostname != null) {
            return hostname;
        } else {


            return null;
        }
    }

    public static boolean loadProperties(Properties properties, String resource) {
        resource = String.format("%s/%s", Main.class.getPackage().getName().replace('.', '/'), resource);
        try (InputStream stream = currentClassLoader().getResourceAsStream(resource)) {
            if (stream == null) {
                throw new IOException("null stream");
            }
            properties.load(stream);

            return true;
        } catch (IOException | Error e) {

            return false;
        }
    }


    public static InputStream getResourceAsStream(String resourceName) {
        return currentClassLoader().getResourceAsStream(resourceName);
    }


}