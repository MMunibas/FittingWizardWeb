/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.workflows;

import ch.unibas.charmmtools.scripts.CHARMM_input;
import ch.unibas.charmmtools.scripts.CHARMM_output;
import ch.unibas.charmmtools.scripts.ICHARMMScript;
import ch.unibas.fittingwizard.application.workflows.base.Workflow;
import ch.unibas.fittingwizard.application.workflows.base.WorkflowContext;
import org.apache.log4j.Logger;

/**
 *
 * @author hedin
 */
public class RunCHARMMWorkflow extends Workflow<CHARMM_input, CHARMM_output> {
    
    private static final Logger logger = Logger.getLogger(RunCHARMMWorkflow.class);
    
    private final ICHARMMScript charmmScript;
    
    public RunCHARMMWorkflow(ICHARMMScript chScr){
        this.charmmScript = chScr;
    }
    
    @Override
    public CHARMM_output execute(WorkflowContext<CHARMM_input> status) {
        
        logger.info("Executing CHARMM workflow ...");
        status.setCurrentStatus("Executing " + charmmScript.getScriptFileName() + " ...");
        
        CHARMM_output out = charmmScript.execute(status.getParameter());
        
        return out;
        
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
//    public void prepare_python_script(String inpPath, String parPath, String topPath, String lpunPath){
//        charmmScript.preparePython(inpPath, parPath, topPath, lpunPath);
//    }
//    
//     public void prepare_python_script(String inpPath, String outPath, String parPath, String topPath, String lpunPath){
//         charmmScript.preparePython(inpPath, outPath, parPath, topPath, lpunPath);
//     }
}
