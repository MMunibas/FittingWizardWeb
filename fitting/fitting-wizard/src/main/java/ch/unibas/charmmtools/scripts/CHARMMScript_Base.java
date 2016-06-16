/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.scripts;

import ch.unibas.fitting.shared.config.Settings;
import ch.unibas.fitting.shared.scripts.base.PythonScriptRunner;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author hedin
 */
public abstract class CHARMMScript_Base implements ICHARMMScript{

    protected final static Logger logger = Logger.getLogger(CHARMMScript_Base.class);

    protected File sessionDir;
    protected Settings settings;
    protected PythonScriptRunner runner;
    protected File ScriptFile;
    protected final File configFile = new File("scripts","config.ini");
    
    protected List<String> args = new ArrayList<>();
    
    public CHARMMScript_Base(File _sessionDir, Settings _settings, String _scriptKey)
    {
        this.sessionDir = _sessionDir;
        this.settings = _settings;
        this.runner = new PythonScriptRunner();
        this.ScriptFile = new File(settings.getScriptsDir(), settings.getValue(_scriptKey));
    }
    
    /**
     * if some more data modification is required
     */
    @Override
    public  void prepareData() {
    }
    
    /**
     * @return the ScriptFile name
     */
    @Override
    public String getScriptFileName() {
        return ScriptFile.getName();
    }
    
    @Override
    public File getWorkingDirectory(){
        return sessionDir;
    }
    
}
