/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard;

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

    private final static String ConfigFileName = "config_gui.ini";
    private final Properties props;

    public Settings(Properties props) {
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
        return new Settings(props);
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
        return new File(getValue("data.path"));
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
        return props.getProperty(key, "No default in config_gui.ini");
    }
    
    public boolean hasValue(String key) {
    	return props.containsKey(key);
    }

    public Properties getProperties() {
        return this.props;
    }
}
