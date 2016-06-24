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
    private static final Logger LOGGER = Logger.getLogger(Settings.class);

    public static final String ScriptPathKey = "scripts.path";
    private final ConfigFile props;
    private boolean get;

    public Settings(ConfigFile props) {
        this.props = props;
    }

    public static Settings loadConfig() {
        ConfigFile cf = ConfigFile.loadDefaultConfig();
        return new Settings(cf);
    }

    public boolean isApplicationVerifcationEnabled() {
        return Boolean.parseBoolean(getValue("application.verify_setup"));
    }

    public File getMoleculeTestdataDir() {
        return new File(getTestdataDir(), "molecules");
    }

    public File getTestdataDir() {
        return getFile("testdata.path");
    }

    public File getMoleculeDir() {
        return new File(getDataDir(), "molecules");
    }

    public File getDataDir() {
        return getFile("data.path");
    }

    public File getLogDir() {
        return getFile("log.path");
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
        return new File(props.value(key));
    }

    public boolean getUseGaussianMock() {
        return getValue("mocks.use_gaussian_mock").equals("true");
    }

    public boolean getMocksEnabled() {
        return getValue("mocks.enabled").equals("true");
    }

    public File getTestdataFitOutput() {
        return new File(getTestdataDir(), "output");
    }

    public File getTestdataCharmmGenerateInputOutputDir() {
        return new File(new File(getTestdataDir().getAbsoluteFile(), "charmm"), "generated_input");
    }

    public boolean isDebuggingMode() {
        return props.booleanOrDefault("debugging_enabled", false);
    }

    public File getCharmmConfig() {
        return new File(getScriptsDir(), "charmm_config.ini");
    }
}
