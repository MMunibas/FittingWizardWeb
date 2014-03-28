package ch.scs.unibas.fittingwizard.application.workflows;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import ch.scs.unibas.fittingwizard.application.fitting.Fit;
import ch.scs.unibas.fittingwizard.application.molecule.MoleculeId;
import ch.scs.unibas.fittingwizard.application.scripts.export.ExportScriptInput;
import ch.scs.unibas.fittingwizard.application.scripts.export.ExportScriptOutput;
import ch.scs.unibas.fittingwizard.application.scripts.multipolegauss.MultipoleGaussInput;
import ch.scs.unibas.fittingwizard.application.scripts.vmd.IVmdDisplayScript;
import ch.scs.unibas.fittingwizard.application.scripts.vmd.VmdDisplayInput;
import ch.scs.unibas.fittingwizard.application.workflows.base.Workflow;
import ch.scs.unibas.fittingwizard.application.workflows.base.WorkflowContext;
import ch.scs.unibas.fittingwizard.infrastructure.RealFitScript;

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
