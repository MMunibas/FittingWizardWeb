/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.scripts;

import ch.unibas.fittingwizard.Settings;
import ch.unibas.fittingwizard.infrastructure.base.PythonScriptRunner;
import ch.unibas.fittingwizard.infrastructure.base.ResourceUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;


public class RealCHARMMScript implements ICHARMMScript {
    
    private final static Logger logger = Logger.getLogger(RealCHARMMScript.class);

    public static final String ScriptNameKey = "scripts.submitCHARMM";
    public static final String OutputDirName = "test";
    public static final String OutputFileName = "CHARMM_script_output.txt";

    private File sessionDir;
    private Settings settings;
    private PythonScriptRunner runner;
    private File ScriptFile;
    
    List<String> args = new ArrayList<>();
    
    public RealCHARMMScript(File _sessionDir, Settings _settings)
    {
        this.sessionDir = _sessionDir;
        this.settings = _settings;
        this.runner = new PythonScriptRunner();
        this.ScriptFile = new File(settings.getScriptsDir(), settings.getValue(ScriptNameKey));
    }
    
    /**
     * @param input
     * @return CHARMM_output
     */
    @Override
    public CHARMM_output execute(CHARMM_input input) {

        runner.exec(this.ScriptFile, this.args, new File(OutputDirName,OutputFileName));
        
        CHARMM_output out = new CHARMM_output();

        return out;
    }

    @Override
    public void prepareData() {
    }

    /**
     * prepare python calls
     * @param inpPath
     * @param parPath
     * @param topPath
     * @param lpunPath
     */
    @Override
    public void preparePython(String inpPath, String parPath, String topPath, String lpunPath) {
        runner.setWorkingDir(new File(OutputDirName));
        
        args.add("-inp");   args.add(ResourceUtils.getRelativePath(inpPath, runner.getWorkingDir().getAbsolutePath()));
        args.add("-par");   args.add(ResourceUtils.getRelativePath(parPath, runner.getWorkingDir().getAbsolutePath()));
        args.add("-top");   args.add(ResourceUtils.getRelativePath(topPath, runner.getWorkingDir().getAbsolutePath()));
        args.add("-lpun");  args.add(ResourceUtils.getRelativePath(lpunPath, runner.getWorkingDir().getAbsolutePath()));
        args.add("-np");    args.add(Integer.toString(1));
    }
    
}
