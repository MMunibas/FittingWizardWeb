/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.gaussian.fitting;

import ch.unibas.fitting.shared.workflows.gaussian.fit.RunFitInput;
import ch.unibas.fitting.shared.workflows.gaussian.fit.RunFitWorkflow;
import ch.unibas.fitting.shared.workflows.base.WorkflowContext;
import ch.unibas.fittingwizard.gaussian.base.progress.Context;
import ch.unibas.fittingwizard.gaussian.base.progress.ProgressPage;

/**
 * User: mhelmer
 * Date: 29.11.13
 * Time: 16:09
 */
public class RunningFitPage extends ProgressPage {
    private final RunFitWorkflow runFitWorkflow;
    private final RunFitInput runFitInput;

    public RunningFitPage(RunFitWorkflow runFitWorkflow,
                          RunFitInput runFitInput) {
        super("Running calculation");
        this.runFitWorkflow = runFitWorkflow;
        this.runFitInput = runFitInput;
    }

    @Override
    protected boolean run(final Context ctx) throws Exception {

        runFitWorkflow.execute(new WorkflowContext<RunFitInput>() {
            @Override
            public void setCurrentStatus(String status) {
                ctx.setTitle(status);
            }

            @Override
            public RunFitInput getParameter() {
                return runFitInput;
            }
        });

        Thread.sleep(500);
        return true;
    }

    @Override
    protected void handleCanceled() {
        navigateTo(FittingParameterPage.class, runFitInput);
    }

    @Override
    protected void handleFinishedRun(boolean successful) {
        navigateTo(FitResultPage.class);
    }
}
