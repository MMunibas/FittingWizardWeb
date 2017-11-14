package ch.unibas.fitting.web.ljfit.ui.step2.run;

import ch.unibas.fitting.shared.charmm.scripts.ClusterParameter;
import ch.unibas.fitting.shared.charmm.web.CharmmResult;
import ch.unibas.fitting.shared.charmm.web.CharmmResultCalculator;
import ch.unibas.fitting.shared.charmm.web.IRunCharmmWorkflowNew;
import ch.unibas.fitting.shared.charmm.web.ResultCalculatorOutput;
import ch.unibas.fitting.shared.directories.IUserDirectory;
import ch.unibas.fitting.shared.directories.LjFitRunDir;
import ch.unibas.fitting.shared.directories.LjFitSessionDir;
import ch.unibas.fitting.shared.infrastructure.JsonSerializer;
import ch.unibas.fitting.shared.workflows.base.WorkflowContext;
import ch.unibas.fitting.shared.workflows.charmm.CharmmInputContainer;
import ch.unibas.fitting.shared.workflows.charmm.GenerateInputWorkflowInput;
import ch.unibas.fitting.shared.workflows.charmm.IGenerateInputWorkflow;
import ch.unibas.fitting.shared.workflows.charmm.UploadedFiles;
import ch.unibas.fitting.shared.workflows.ljfit.LjFitRun;
import ch.unibas.fitting.shared.workflows.ljfit.LjFitRunInput;
import ch.unibas.fitting.shared.workflows.ljfit.LjFitRunResult;
import ch.unibas.fitting.shared.workflows.ljfit.LjFitSession;
import ch.unibas.fitting.web.application.IBackgroundTasks;
import ch.unibas.fitting.web.application.ProgressPageTaskHandle;
import ch.unibas.fitting.web.ljfit.services.LjFitRepository;
import ch.unibas.fitting.web.ljfit.ui.commands.OpenLjFitSessionCommand;
import ch.unibas.fitting.web.ljfit.ui.step2.LjSessionPage;
import ch.unibas.fitting.web.web.PageNavigation;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by mhelmer on 24.06.2016.
 */
public class RunLjFitsCommand {
    @Inject
    private IGenerateInputWorkflow generateInputWorkflow;

    @Inject
    private IRunCharmmWorkflowNew runCharmmWorkflowNew;

    @Inject
    private IBackgroundTasks backgroundTasks;

    @Inject
    private OpenLjFitSessionCommand openLjFitSessionCommand;

    @Inject
    private LjFitRepository ljFitRepository;

    @Inject
    private IUserDirectory userDirectory;

    @Inject
    private JsonSerializer serializer;


    public void execute(String username, RunFromPage runs) {

        ProgressPageTaskHandle th = backgroundTasks.execute(username, "Running LJ Fits ...",
                () -> {
                    for (RunPair pair : runs.runPairs) {
                        LjFitRunInput input = new LjFitRunInput(
                                pair.lambda_epsiolon,
                                pair.lambda_sigma,
                                runs.lambda_spacing);
                        runSingleFit(username,
                                input,
                                new ClusterParameter(runs.ncpus,runs.clusterName));
                    }

                    return null;
                },
                (t, pageParameters) -> {
                    openLjFitSessionCommand.execute(username);
                    return null;
                },
                LjSessionPage.class);

        PageNavigation.ToProgressForTask(th);
    }

    private void runSingleFit(
            String username,
            LjFitRunInput in,
            ClusterParameter clusterParameter) {
        LjFitSession session = ljFitRepository.loadSessionForUser(username).get();
        LjFitSessionDir sessionDir = userDirectory.getLjFitSessionDir(username).get();
        UploadedFiles files = sessionDir.lookupUploadedFiles(session.getUploadedFileNames());
        LjFitRunDir runDir = sessionDir.createRunDir(in.lambdaSigma, in.lambdaEpsilon);

        writeToJson(runDir.getRunInputJson(), in);

        GenerateInputWorkflowInput workflowInput = new GenerateInputWorkflowInput(
                runDir,
                in,
                files);

        CharmmInputContainer output = generateInputWorkflow.execute(WorkflowContext.withInput(workflowInput));

        CharmmResult charmmResult = runCharmmWorkflowNew.executeCharmm(output, clusterParameter);
        LjFitRunResult runResult = createResult(session, in, charmmResult);
        writeToJson(runDir.getRunOutputJson(), runResult);
    }

    private void writeToJson(File file, Object object) {
        String json = serializer.toJson(object);
        try {
            FileUtils.write(file, json, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException("Failed to write result json "+ file);
        }
    }

    private LjFitRunResult createResult(
            LjFitSession session,
            LjFitRunInput in,
            CharmmResult result) {

        double gasTotal = result.getOutput().getGas_vdw() + result.getOutput().getGas_mtp();
        double solTotal = result.getOutput().getSolvent_mtp() + result.getOutput().getSolvent_vdw();

        ResultCalculatorOutput calculatedResult = CharmmResultCalculator.calculateResult(
                session.getSessionParameter().numberOfResidues,
                session.getSessionParameter().molarMass,
                session.getSessionParameter().temperature,
                result.getOutput());

        double score_deltaG = Math.pow(calculatedResult.getDeltaG() - session.getSessionParameter().expectedDeltaG, 2);
        double score_deltaH = Math.pow(calculatedResult.getDeltaH() - session.getSessionParameter().expectedDeltaH, 2);
        double score_density = Math.pow(calculatedResult.getDensity() - session.getSessionParameter().expectedDensity, 2);
        double score_total = score_density + (3* score_deltaH) + (5*score_deltaG);

        return new LjFitRunResult(
                in.lambdaEpsilon,
                in.lambdaSigma,
                result.getOutput().getGas_vdw(),
                result.getOutput().getGas_mtp(),
                result.getOutput().getSolvent_mtp(),
                result.getOutput().getSolvent_vdw(),
                gasTotal,
                solTotal,
                calculatedResult.getDeltaG(),
                session.getSessionParameter().expectedDeltaG,
                calculatedResult.getDeltaH(),
                session.getSessionParameter().expectedDeltaH,
                calculatedResult.getDensity(),
                session.getSessionParameter().expectedDensity,
                score_deltaG,
                score_deltaH,
                score_density,
                score_total
                );
    }
}
