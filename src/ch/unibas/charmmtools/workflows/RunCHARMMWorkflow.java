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

/**
 *
 * @author hedin
 */
public class RunCHARMMWorkflow extends Workflow<CHARMM_input, CHARMM_output> {
    
    private final ICHARMMScript charmmScript;
    
    public RunCHARMMWorkflow(ICHARMMScript chScr){
        this.charmmScript = chScr;
    }
    
    @Override
    public CHARMM_output execute(WorkflowContext<CHARMM_input> status) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
