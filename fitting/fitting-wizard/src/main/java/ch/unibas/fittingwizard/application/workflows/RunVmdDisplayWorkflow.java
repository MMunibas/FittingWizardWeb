/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.application.workflows;

import ch.unibas.fitting.shared.scripts.vmd.IVmdDisplayScript;
import ch.unibas.fitting.shared.scripts.vmd.VmdDisplayInput;
import ch.unibas.fittingwizard.application.workflows.base.Workflow;
import ch.unibas.fittingwizard.application.workflows.base.WorkflowContext;
import java.io.File;
import org.apache.log4j.Logger;

public class RunVmdDisplayWorkflow extends Workflow<VmdDisplayInput,Void> {

    private static final Logger logger = Logger.getLogger(RunVmdDisplayWorkflow.class);

    private final IVmdDisplayScript vmdScript;
    private final File sessionDir;

    public RunVmdDisplayWorkflow(IVmdDisplayScript vmdScript, File sessionDir) {
        this.vmdScript = vmdScript;
        this.sessionDir = sessionDir;
    }

    public Void execute(WorkflowContext<VmdDisplayInput> input) {
    	
        logger.info("Executing vmd display workflow.");

        vmdScript.execute(input.getParameter());
        
        return null;
    }

}
