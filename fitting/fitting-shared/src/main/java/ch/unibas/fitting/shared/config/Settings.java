/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 * A typed wrapper for the settings from the config_gui.ini file.
 * User: mhelmer
 * Date: 26.11.13
 * Time: 18:27
 */
public class Settings {
    private static final Logger logger = Logger.getLogger(Settings.class);

    public static final String ScriptPathKey = "scripts.path";
    public final static String ConfigFileName = "config_gui.ini";
    private final ConfigFile props;

    public Settings(ConfigFile props) {
        this.props = props;
    }

    /**
     * Loads the default config
     * @return
     */
    public static Settings loadConfig() {
        File f = new File(ConfigFileName);
        System.out.println("Loading config: " + f.getAbsolutePath());
        return loadConfig(f);
    }

    public static Settings loadConfig(File configFile) {
        ConfigFile cf = ConfigFile.loadConfig(ConfigFileName);
        return new Settings(cf);
    }

    public boolean isApplicationVerifcationEnabled() {
        return Boolean.parseBoolean(getValue("application.verify_setup"));
    }

    public File getMoleculeTestdataDir() {
        return new File(getTestdataDir(), "molecules");
    }

    public File getTestdataDir() {
        return new File(getDataDir(), "testdata");
    }

    public File getMoleculeDir() {
        return new File(getDataDir(), "molecules");
    }

    public File getDataDir() {
        return getFile("data.path");
    }

    public File getScriptsDir() {
        return new File(getValue(ScriptPathKey));
    }
    
    public File getPythonPath(){
        return new File(getValue("scripts.pythonpath"));
    }
    
    public File getLDLibraryPath(){
        return new File(getValue("scripts.ld_library_path"));
    }

    public String getValue(String key) {
        return props.valueOrDefault(key, "No default in config_gui.ini");
    }
    
    public boolean hasValue(String key) {
    	return props.containsKey(key);
    }

    public ConfigFile getProperties() {
        return props;
    }

    protected File getFile(String key) {
        return new File(getValue(key));
    }

    public boolean getUseGaussianMock() {
        return getValue("mocks.use_gaussian_mock").equals("true");
    }

    public boolean getMocksEnabled() {
        return getValue("mocks.enabled").equals("true");
    }
}
