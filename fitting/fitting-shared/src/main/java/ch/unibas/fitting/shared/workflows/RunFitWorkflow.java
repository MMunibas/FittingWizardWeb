/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.workflows;

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
public class RunFitWorkflow implements Workflow<FitMtpInput,Void> {

    private static final Logger logger = Logger.getLogger(RunFitWorkflow.class);

    private final IFitMtpScript fitMtpScript;
    private final FitRepository fitRepository;
    private final ChargesFileParser chargesFileParser;
    private final FitOutputParser fitOutputParser;

    public RunFitWorkflow(IFitMtpScript fitMtpScript,
                          FitRepository fitRepository,
                          ChargesFileParser chargesFileParser,
                          FitOutputParser fitOutputParser) {

        this.fitMtpScript = fitMtpScript;
        this.fitRepository = fitRepository;
        this.chargesFileParser = chargesFileParser;
        this.fitOutputParser = fitOutputParser;
    }

    @Override
    public Void execute(WorkflowContext<FitMtpInput> status) {
        logger.info("Executing fit workflow.");

        status.setCurrentStatus("Executing fit.mtp.py ...");

        FitMtpInput fitMtpInput = status.getParameter();
        FitMtpOutput fitOutput = fitMtpScript.execute(fitMtpInput);

        double rmse = fitOutputParser.parseRmseValue(fitOutput.getOutputFile());

        List<OutputAtomType> outputAtomTypes = chargesFileParser.parseOutputFile(fitOutput.getResultsFile());
        File intialQsFile = fitMtpInput.getInitalChargesFile();

        InitialQ00 initialQs = new ChargesFileParser().parseInitalCharges(intialQsFile);
        // todo verify initial charges vs generated output

        fitRepository.createAndSaveFit(rmse, fitMtpInput.getRank(), outputAtomTypes, initialQs);
        return null;
    }
}
