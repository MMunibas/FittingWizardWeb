/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.scripts;

import ch.unibas.charmmtools.generate.CHARMM_Output_PureLiquid;
import ch.unibas.charmmtools.generate.CHARMM_Output;
import ch.unibas.charmmtools.generate.CHARMM_Output_GasPhase;
import ch.unibas.charmmtools.generate.CHARMM_Input;
import ch.unibas.charmmtools.generate.CHARMM_Input_PureLiquid;
import ch.unibas.charmmtools.generate.CHARMM_Input_GasPhase;
import ch.unibas.fittingwizard.Settings;
import java.io.File;


public class CHARMMScript_Den_Vap extends CHARMMScript_Base {
    
    private static final String ScriptNameKey = "scripts.submitCHARMM_Den_Vap";
    private static final String OutputDirName = "test";
    
    public CHARMMScript_Den_Vap(File _sessionDir, Settings _settings)
    {
        super(_sessionDir,_settings,ScriptNameKey);
    }
    
    /**
     * @param input
     * @return CHARMM_Output
     */
    @Override
    public CHARMM_Output execute(CHARMM_Input input) {

        String FileName = "generic.out";
        File charmmout = null;
        
        Class c = input.getClass();
        if(c==CHARMM_Input_GasPhase.class)
        {
            FileName = "gas_phase.out";
            charmmout = new File(OutputDirName,FileName);
            this.prepare_Python(input,charmmout);
        }
        else if(c==CHARMM_Input_PureLiquid.class)
        {
            FileName = "pure_liquid.out";
            charmmout = new File(OutputDirName,FileName);
            this.prepare_Python(input,charmmout);
        }
        else
            throw new UnknownError("Unknown type of object in CHARMM_Input : got " + c + " but expected types are " + CHARMM_Input_GasPhase.class + " or " + CHARMM_Output_PureLiquid.class);
        
        logger.info("Saving to output file " + FileName);

//        runner.exec(this.ScriptFile, this.args, new File(OutputDirName,OutputFileName));
        runner.exec(this.ScriptFile, this.args);
        
        // Object representation of the charmm output file
        CHARMM_Output out = null;
        
        if(c==CHARMM_Input_GasPhase.class)
            out = new CHARMM_Output_GasPhase(charmmout);
        else if(c==CHARMM_Input_PureLiquid.class)
            out = new CHARMM_Output_PureLiquid(charmmout);
        else
             throw new UnknownError("Unknown type of object in List<CHARMM_InOut> : got " + input.getClass() + " but expected types are " + CHARMM_Input_GasPhase.class + " or " + CHARMM_Output_PureLiquid.class);

        return out;
    }


    
    /**
     * prepare python call for density or delta h vap simulations
     * @param input
     * @param output
     */
    @Override
    public void prepare_Python(CHARMM_Input input, File output) {
        
        String inpPath = input.getInp().getAbsolutePath();
        String parPath = input.getPar();
        String topPath = input.getTop();
        String lpunPath = input.getLpun();   
        
        String outPath = output.getAbsolutePath();
        
        logger.info("Preparing PYTHON call with parameters : " + inpPath + " " + outPath + " " + parPath + " " + topPath + " " + lpunPath);
        
        runner.setWorkingDir(new File(OutputDirName));
        
        args.clear();
        args.add("-inp");   args.add(inpPath);
        args.add("-out");   args.add(outPath);
        args.add("-par");   args.add(parPath);
        args.add("-top");   args.add(topPath);
        args.add("-lpun");  args.add(lpunPath);
        args.add("-np");    args.add(Integer.toString(1));
    }  
    
}