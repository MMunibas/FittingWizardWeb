package ch.unibas.fitting.shared.utilities;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by mhelmer-mobile on 15.06.2016.
 */
public class ConfigFile {
    private static final Logger logger = Logger.getLogger(ConfigFile.class);

    private Properties _props;

    public ConfigFile(Properties props) {
        _props = props;
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
        return _props.getProperty(key, defVal);
    }
}
