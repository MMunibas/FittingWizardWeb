/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.workflows.gaussian.fit;

import ch.unibas.fitting.shared.fitting.Fit;
import ch.unibas.fitting.shared.fitting.FitRepository;
import ch.unibas.fitting.shared.fitting.InitialQ00;
import ch.unibas.fitting.shared.fitting.OutputAtomType;
import ch.unibas.fitting.shared.scripts.fitmtp.FitMtpInput;
import ch.unibas.fitting.shared.scripts.fitmtp.FitMtpOutput;
import ch.unibas.fitting.shared.scripts.fitmtp.IFitMtpScript;
import ch.unibas.fitting.shared.tools.FitOutputParser;
import ch.unibas.fitting.shared.charges.ChargesFileParser;
import ch.unibas.fitting.shared.workflows.base.Workflow;
import ch.unibas.fitting.shared.workflows.base.WorkflowContext;
import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * User: mhelmer
 * Date: 11.12.13
 * Time: 09:28
 */
public class RunFitWorkflow implements Workflow<RunFitInput,Void> {

    private static final Logger logger = Logger.getLogger(RunFitWorkflow.class);

    private final IFitMtpScript fitMtpScript;
    private final FitRepository fitRepository;
    private final CreateFit createFit;

    public RunFitWorkflow(CreateFit createFit,
                          IFitMtpScript fitMtpScript,
                          FitRepository fitRepository) {
        this.createFit = createFit;
        this.fitMtpScript = fitMtpScript;
        this.fitRepository = fitRepository;
    }

    @Override
    public Void execute(WorkflowContext<RunFitInput> status) {
        logger.info("Executing fit workflow.");

        status.setCurrentStatus("Executing fit.mtp.py ...");

        RunFitInput runFit = status.getParameter();
        FitMtpInput fitMtpInput = runFit.getFitMtpInput();
        FitMtpOutput fitOutput = fitMtpScript.execute(fitMtpInput);

        Fit fit = createFit.createFit(fitMtpInput.getFitId(),
                fitMtpInput.getRank(),
                fitOutput.getResultsFile(),
                fitOutput.getOutputFile(),
                runFit.getInitialQ00(),
                fitMtpInput.getMoleculesForFit());

        fitRepository.save(fit);
        return null;
    }
}
