package ch.unibas.fitting;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Properties;

/**
 * Created by mhelmer-mobile on 15.06.2016.
 */
public class ConfigFile {
    private static final Logger logger = Logger.getLogger(ConfigFile.class);

    private Properties props;

    public ConfigFile(Properties props) {
        this.props = props;
    }

    public static ConfigFile loadConfig(String configFile) {
        return loadConfig(new File(configFile));
    }

    public static ConfigFile loadConfig(File configFile) {
        Properties props = new Properties();
        FileInputStream inStream = null;
        try {
            inStream = new FileInputStream(configFile);
            props.load(inStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    logger.error("Could not close input stream.", e);
                }
            }
        }
        logger.info("Loaded properties\n" + props);
        return new ConfigFile(props);
    }

    public String valueOrDefault(String key, String defVal) {
        return props.getProperty(key, defVal);
    }

    public String value(String key) {
        if (!containsKey(key))
            throw new NoSuchElementException("no value for key '" + key + "'");
        return props.getProperty(key);
    }

    public boolean containsKey(String key) {
        return props.containsKey(key);
    }

    public Properties getProperties() {
        return props;
    }

    public int getInteger(String key) {
        return Integer.parseInt(value(key));
    }

    public static ConfigFile loadDefaultConfig() {
        return loadConfig("config.ini");
    }

    public boolean booleanOrDefault(String key, boolean defaultValue) {
        if (containsKey(key))
            return Boolean.valueOf(value(key));
        return defaultValue;
    }
}
