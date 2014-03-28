package ch.unibas.fittingwizard.presentation.fitting;

import ch.unibas.fittingwizard.application.scripts.fitmtp.FitMtpInput;
import ch.unibas.fittingwizard.application.workflows.RunFitWorkflow;
import ch.unibas.fittingwizard.application.workflows.base.WorkflowContext;
import ch.unibas.fittingwizard.presentation.base.progress.Context;
import ch.unibas.fittingwizard.presentation.base.progress.ProgressPage;

/**
 * User: mhelmer
 * Date: 29.11.13
 * Time: 16:09
 */
public class RunningFitPage extends ProgressPage {
    private final RunFitWorkflow runFitWorkflow;
    private final FitMtpInput fitMtpInput;

    public RunningFitPage(RunFitWorkflow runFitWorkflow,
                          FitMtpInput fitMtpInput) {
        super("Running calculation");
        this.runFitWorkflow = runFitWorkflow;
        this.fitMtpInput = fitMtpInput;
    }

    @Override
    protected boolean run(final Context ctx) throws Exception {

        runFitWorkflow.execute(new WorkflowContext<FitMtpInput>() {
            @Override
            public void setCurrentStatus(String status) {
                ctx.setTitle(status);
            }

            @Override
            public FitMtpInput getParameter() {
                return fitMtpInput;
            }
        });

        Thread.sleep(500);
        return true;
    }

    @Override
    protected void handleCanceled() {
        navigateTo(FittingParameterPage.class, fitMtpInput);
    }

    @Override
    protected void handleFinishedRun(boolean successful) {
        navigateTo(FitResultPage.class);
    }
}
