package ch.scs.unibas.fittingwizard.application.workflows;

import ch.scs.unibas.fittingwizard.application.fitting.FitRepository;
import ch.scs.unibas.fittingwizard.application.fitting.InitialQ00;
import ch.scs.unibas.fittingwizard.application.tools.FitOutputParser;
import ch.scs.unibas.fittingwizard.application.tools.charges.ChargesFileParser;
import ch.scs.unibas.fittingwizard.application.fitting.OutputAtomType;
import ch.scs.unibas.fittingwizard.application.scripts.fitmtp.FitMtpOutput;
import ch.scs.unibas.fittingwizard.application.scripts.fitmtp.FitMtpInput;
import ch.scs.unibas.fittingwizard.application.scripts.fitmtp.IFitMtpScript;
import ch.scs.unibas.fittingwizard.application.workflows.base.Workflow;
import ch.scs.unibas.fittingwizard.application.workflows.base.WorkflowContext;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.List;

/**
 * User: mhelmer
 * Date: 11.12.13
 * Time: 09:28
 */
public class RunFitWorkflow extends Workflow<FitMtpInput,Void> {

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

        fitRepository.createFit(fitMtpInput.getFitId(), rmse, fitMtpInput.getRank(), outputAtomTypes, initialQs);
        return null;
    }
}
