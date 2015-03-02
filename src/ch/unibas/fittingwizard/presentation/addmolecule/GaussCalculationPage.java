/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.presentation.addmolecule;

import java.io.File;

import ch.unibas.fittingwizard.application.scripts.multipolegauss.MultipoleGaussInput;
import ch.unibas.fittingwizard.application.workflows.RunGaussianResult;
import ch.unibas.fittingwizard.application.workflows.RunGaussianWorkflow;
import ch.unibas.fittingwizard.application.workflows.base.WorkflowContext;
import ch.unibas.fittingwizard.presentation.base.dialog.OverlayDialog;
import ch.unibas.fittingwizard.presentation.base.progress.Context;
import ch.unibas.fittingwizard.presentation.base.progress.ProgressPage;

/**
 * User: mhelmer
 * Date: 26.11.13
 * Time: 18:18
 */
public class GaussCalculationPage extends ProgressPage {
    private final GaussCalculationDto dto;
    private final RunGaussianWorkflow runGaussianWorkflow;

    public GaussCalculationPage(RunGaussianWorkflow runGaussianWorkflow,
                                GaussCalculationDto dto) {
        super("Running calculation");
        this.runGaussianWorkflow = runGaussianWorkflow;
        this.dto = dto;
    }

    @Override
    protected boolean run(final Context ctx) throws Exception {

        RunGaussianResult result = runGaussianWorkflow.execute(new WorkflowContext<MultipoleGaussInput>() {
            @Override
            public void setCurrentStatus(String status) {
                ctx.setTitle(status);
            }

            @Override
            public MultipoleGaussInput getParameter() {
                return dto.getMultipoleGaussInput();
            }
        });

        if (result.isInvalid()) {
            showErrorDialog(result.getLogFile());
        }

        return result.equals(RunGaussianResult.Success);
    }

    @Override
    protected void handleCanceled() {
        logger.info("Going back to input page.");

        navigateTo(MultipoleGaussParameterPage.class, new MultipoleGaussParameterDto(dto.getXyzFile()));
    }

    @Override
    protected void handleFinishedRun(boolean successful) {
        if (successful) {
            navigateTo(AtomTypeChargePage.class, new AtomChargesDto(dto.getXyzFile()));
        } else {
            navigateTo(MultipoleGaussParameterPage.class, new MultipoleGaussParameterDto(dto.getXyzFile()));
        }
    }

    private void showErrorDialog(File logFile) {
        OverlayDialog.showError("Error in the gaussian calculation",
                "There was an error in the Gaussian calculation. " +
                        "Please review the log file for more details. " + logFile.getAbsolutePath());
    }
}
