/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.workflows;

import ch.unibas.charmmtools.generate.CHARMM_Input;
import ch.unibas.charmmtools.generate.CHARMM_Output;
import ch.unibas.charmmtools.scripts.ICHARMMScript;
import ch.unibas.fittingwizard.application.workflows.base.Workflow;
import ch.unibas.fittingwizard.application.workflows.base.WorkflowContext;
import org.apache.log4j.Logger;

/**
 *
 * @author hedin
 */
public class RunCHARMMWorkflow extends Workflow<CHARMM_Input, CHARMM_Output> {
    
    private static final Logger logger = Logger.getLogger(RunCHARMMWorkflow.class);
    
    private final ICHARMMScript charmmScript;
    
    public RunCHARMMWorkflow(ICHARMMScript chScr){
        this.charmmScript = chScr;
    }
    
    @Override
    public CHARMM_Output execute(WorkflowContext<CHARMM_Input> status) {
        
        logger.info("Executing CHARMM workflow ...");
        status.setCurrentStatus("Executing " + charmmScript.getScriptFileName() + " ...");
        
        CHARMM_Output out = charmmScript.execute(status.getParameter());
        
        return out;
    }

}
