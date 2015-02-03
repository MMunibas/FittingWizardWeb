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
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;


public class RealCHARMMScript implements ICHARMMScript {
    
    private final static Logger logger = Logger.getLogger(RealCHARMMScript.class);

    public static final String ScriptNameKey = "scripts.submitCHARMM";
    public static final String OutputDirName = "test";

    private final File sessionDir;
    private final Settings settings;
    private final PythonScriptRunner runner;
    private final File ScriptFile;
    
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
     * @return CHARMM_Output
     */
    @Override
    public CHARMM_Output execute(CHARMM_Input input) {

        String FileName = "";
        
        if(input instanceof CHARMM_Input_GasPhase)
            FileName = "gas_phase.out";
        else if(input instanceof CHARMM_Input_PureLiquid)
            FileName = "pure_liquid.out";
        
        File charmmout = new File(OutputDirName,FileName);
        
        this.preparePython(input.getInp().getAbsolutePath(),charmmout.getAbsolutePath(),
                input.getPar(), input.getTop(), input.getLpun());
        
//        runner.exec(this.ScriptFile, this.args, new File(OutputDirName,OutputFileName));
        runner.exec(this.ScriptFile, this.args);
        
        // Object representation of the charmm output file
        CHARMM_Output out = null;
        
        if(input instanceof CHARMM_Input_GasPhase)
            out = new CHARMM_Output_GasPhase(charmmout);
        else if(input instanceof CHARMM_Input_PureLiquid)
            out = new CHARMM_Output_PureLiquid(charmmout);
        else
             throw new UnknownError("Unknown type of object in List<CHARMM_InOut> : got " + input.getClass() + " but expected types are " + CHARMM_Input_GasPhase.class + " or " + CHARMM_Output_PureLiquid.class);

        return out;
    }

    @Override
    public void prepareData() {
    }

    /**
     * prepare python calls, output file has default name decided in python script
     * @param inpPath
     * @param parPath
     * @param topPath
     * @param lpunPath
     */
    @Override
    public void preparePython(String inpPath, String parPath, String topPath, String lpunPath) {
        
        runner.setWorkingDir(new File(OutputDirName));
        
        args.add("-inp");   args.add(inpPath);
        args.add("-par");   args.add(parPath);
        args.add("-top");   args.add(topPath);
        args.add("-lpun");  args.add(lpunPath);
        args.add("-np");    args.add(Integer.toString(1));
    }
    
    /**
     * prepare python calls,output file name chosen from interface
     * @param inpPath
     * @param outPath
     * @param parPath
     * @param topPath
     * @param lpunPath
     */
    @Override
    public void preparePython(String inpPath, String outPath, String parPath, String topPath, String lpunPath) {
        
        runner.setWorkingDir(new File(OutputDirName));
        
        args.add("-inp");   args.add(inpPath);
        args.add("-out");   args.add(outPath);
        args.add("-par");   args.add(parPath);
        args.add("-top");   args.add(topPath);
        args.add("-lpun");  args.add(lpunPath);
        args.add("-np");    args.add(Integer.toString(1));
    }

    /**
     * @return the ScriptFile name
     */
    @Override
    public String getScriptFileName() {
        return ScriptFile.getName();
    }
    
}
