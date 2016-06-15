package ch.unibas.fitting.web;

import ch.unibas.fitting.shared.utilities.ConfigFile;

import javax.inject.Singleton;
import java.io.File;

/**
 * Created by mhelmer-mobile on 15.06.2016.
 */
@Singleton
public class WebConfig {

    private ConfigFile _config;

    public WebConfig() {
        this("web.ini");
    }

    public WebConfig(String filename) {
        _config = ConfigFile.loadConfig(filename);
    }

    public File getDataDir() {
        return new File(valueOrDefault("data.dir", "datastore"));
    }

    private String valueOrDefault(String key, String defVal) {
        return _config.valueOrDefault(key, defVal);
    }
}
