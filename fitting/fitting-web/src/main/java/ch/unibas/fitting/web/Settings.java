/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.web;

import java.io.File;

import org.apache.log4j.Logger;

/**
 * A typed wrapper for the settings from the config_gui.ini file.
 * User: mhelmer
 * Date: 26.11.13
 * Time: 18:27
 */
public class Settings {
    private static final Logger LOGGER = Logger.getLogger(Settings.class);

    private final ConfigFile props;

    public Settings(ConfigFile props) {
        this.props = props;
    }

    public static Settings loadConfig() {
        ConfigFile cf = ConfigFile.loadDefaultConfig();
        return new Settings(cf);
    }

    public File getJavaScriptDir() {
        return getFile("javascript.path");
    }

    public int getServerPort() {
        return getProperties().getInteger("server.port");
    }


    public File getTestdataDir() {
        return getFile("testdata.path");
    }

    public File getDataDir() {
        return getFile("data.path");
    }

    public File getLogDir() {
        return getFile("log.path");
    }

    public String getValue(String key) {
        if (!hasValue(key)) {
            throw new RuntimeException("Key " + key + "not found in config.");
        }
        return props.valueOrDefault(key, null);
    }
    
    private boolean hasValue(String key) {
    	return props.containsKey(key);
    }

    public ConfigFile getProperties() {
        return props;
    }

    protected File getFile(String key) {
        return new File(props.value(key));
    }

    public boolean isDebuggingMode() {
        return props.booleanOrDefault("debugging_enabled", false);
    }

    public int getNumberOfBackgroundTaskThreads() {
        return props.getInteger("server.background_threads");
    }

    public String getScriptServiceUri() {
        return getValue("calculation.service");
    }
}
