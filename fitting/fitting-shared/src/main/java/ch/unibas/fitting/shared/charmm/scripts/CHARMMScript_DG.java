/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.charmm.scripts;

import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Input;

import ch.unibas.fitting.shared.charmm.generate.outputs.CHARMM_Output;
import ch.unibas.fitting.shared.charmm.generate.outputs.CHARMM_Output_DGHydr;
import ch.unibas.fitting.shared.config.Settings;
import ch.unibas.fitting.shared.directories.LjFitSessionDir;

import java.io.File;

/**
 *
 * @author hedin
 */
public abstract class CHARMMScript_DG extends CHARMMScript_Base implements ICHARMMScriptWithPython{

    protected static final String ScriptNameKey = "scripts.submitCHARMM_DG";
    //protected static final String OutputDirName = "test";
    
    public CHARMMScript_DG(LjFitSessionDir ljFitSessionDir, Settings _settings)
    {
        super(ljFitSessionDir,_settings,ScriptNameKey);
    }

    @Override
    public CHARMM_Output execute(CHARMM_Input input) {
        
        String FileName = "dg_hydr.out";
        File charmmout = new File(this.ljFitSessionDir.getDirectory(),FileName);

        runner.exec(this.ScriptFile, this.args, charmmout);
        
        CHARMM_Output out = new CHARMM_Output_DGHydr(charmmout);
        
        return out;
    }
    
//    @Override
//    public abstract void prepare_Python(CHARMM_Input input, File output);
    
//    @Override
//    public void prepare_Python(CHARMM_Input input, File output) {
////        String inpPath = input.getInp().getAbsolutePath();
////        String parPath = input.getPar();
////        String topPath = input.getTop();
////        String lpunPath = input.getLpun();   
//        
////        String outPath = output.getAbsolutePath();
//        
////        logger.info("Preparing PYTHON call with parameters : " + inpPath + " " + outPath + " " + parPath + " " + topPath + " " + lpunPath);
//        
//        CHARMM_Input_DGHydr input2 = (CHARMM_Input_DGHydr) input;
//        
//        runner.setWorkingDir(new File(OutputDirName));
//        
//        args.clear();
//        args.add("--ti");   args.add(input2.getTi_type());
//        args.add("--tps");   args.add(input2.getTop());
////        args.add("--top");   args.add(input2.getSolv_top());
//        args.add("--slu");   args.add(input2.getCrd());
////        args.add("--slv");   args.add(input2.getSolv_cor());
//        args.add("--par");   args.add(input2.getPar());
//        args.add("--lpun");  args.add(input2.getLpun());
//        args.add("--chm");  args.add("./charmm");
//        args.add("--lmb");  args.add(Double.toString(input2.getL_min()));
//        args.add(Double.toString(input2.getL_space()));
//        args.add(Double.toString(input2.getL_max()));
//    }
    
}
