/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.scripts;

import ch.unibas.charmmtools.generate.inputs.CHARMM_Input;
import ch.unibas.charmmtools.generate.outputs.CHARMM_Output;
import ch.unibas.charmmtools.generate.outputs.CHARMM_Output_solvating;
import ch.unibas.fittingwizard.Settings;
import java.io.File;

/**
 *
 * @author hedin
 */
public class CHARMMScript_solvating extends CHARMMScript_Base implements ICHARMMScriptWithBash{

    private static final String ScriptNameKey = "scripts.solvate";
    //private static final String OutputDirName = "test";
    
    public CHARMMScript_solvating(File _sessionDir, Settings _settings)
    {
        super(_sessionDir,_settings,ScriptNameKey);
    }

    @Override
    public CHARMM_Output execute(CHARMM_Input input) {
        
        String FileName = "solvating.out";
        File charmmout = new File(this.sessionDir,FileName);
        
        this.prepare_Bash(input, null);
        
        runner.exec(this.ScriptFile, this.args, charmmout);
        
        CHARMM_Output out = new CHARMM_Output_solvating(charmmout);
        
        return out;
    }
    
    @Override
    public void prepare_Bash(CHARMM_Input input, File output) {
//        String inpPath = input.getInp().getAbsolutePath();
//        String parPath = input.getPar();
//        String topPath = input.getTop();
//        String lpunPath = input.getLpun();   
        
//        String outPath = output.getAbsolutePath();
        
//        logger.info("Preparing PYTHON call with parameters : " + inpPath + " " + outPath + " " + parPath + " " + topPath + " " + lpunPath);
        
//        CHARMM_Input_DGHydr input2 = (CHARMM_Input_DGHydr) input;
//        
//        runner.setWorkingDir(new File(OutputDirName));
//        
//        args.clear();
//        args.add("--ti");   args.add(input2.getTi_type());
//        args.add("--tps");   args.add(input2.getTop());
//        args.add("--top");   args.add(input2.getSolv_top());
//        args.add("--slu");   args.add(input2.getCrd());
//        args.add("--slv");   args.add(input2.getSolv_cor());
//        args.add("--par");   args.add(input2.getPar());
//        args.add("--lpun");  args.add(input2.getLpun());
//        args.add("--chm");  args.add("./charmm");
//        args.add("--lmb");  args.add(Double.toString(input2.getL_min()));
//        args.add(Double.toString(input2.getL_space()));
//        args.add(Double.toString(input2.getL_max()));
    }
}
