package ch.unibas.fitting.web;

import ch.unibas.fitting.shared.config.ConfigFile;
import ch.unibas.fitting.shared.config.Settings;

import java.io.File;
import java.util.Properties;

/**
 * Created by mhelmer-mobile on 16.06.2016.
 */
public class WebSettings extends Settings {
    public WebSettings(ConfigFile props) {
        super(props);
    }

    public static WebSettings load() {
        ConfigFile cf = ConfigFile.loadConfig(Settings.ConfigFileName);
        return new WebSettings(cf);
    }


    public File getJavaScriptDir() {
        return getFile("javascript.path");
    }
}
