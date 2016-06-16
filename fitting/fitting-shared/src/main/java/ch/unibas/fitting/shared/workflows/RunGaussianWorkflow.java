/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.workflows;

import ch.unibas.fitting.shared.scripts.babel.BabelInput;
import ch.unibas.fitting.shared.scripts.babel.BabelOutput;
import ch.unibas.fitting.shared.scripts.babel.IBabelScript;
import ch.unibas.fitting.shared.scripts.fittab.FittabScriptInput;
import ch.unibas.fitting.shared.scripts.fittab.IFittabScript;
import ch.unibas.fitting.shared.scripts.lra.ILRAScript;
import ch.unibas.fitting.shared.scripts.lra.LRAScriptInput;
import ch.unibas.fitting.shared.scripts.lra.LRAScriptOutput;
import ch.unibas.fitting.shared.scripts.multipolegauss.IMultipoleGaussScript;
import ch.unibas.fitting.shared.scripts.multipolegauss.MultipoleGaussInput;
import ch.unibas.fitting.shared.scripts.multipolegauss.MultipoleGaussOutput;
import ch.unibas.fitting.shared.tools.GaussianLogModifier;
import ch.unibas.fitting.shared.tools.Notifications;
import ch.unibas.fitting.shared.workflows.base.Workflow;
import ch.unibas.fitting.shared.workflows.base.WorkflowContext;
import org.apache.log4j.Logger;

/**
 * User: mhelmer
 * Date: 16.12.13
 * Time: 09:28
 */
public class RunGaussianWorkflow extends Workflow<MultipoleGaussInput,RunGaussianResult> {

    private static final Logger logger = Logger.getLogger(RunGaussianWorkflow.class);

    private final IMultipoleGaussScript gaussScript;
    private final IBabelScript babelScript;
    private final ILRAScript lraScript;
    private final IFittabScript fittabMarkerScript;
    private final GaussianLogModifier gaussianLogModifier;
    private final Notifications notifications;

    public RunGaussianWorkflow(IMultipoleGaussScript gaussScript,
                               IBabelScript babelScript,
                               ILRAScript lraScript,
                               IFittabScript fittabMarkerScript,
                               GaussianLogModifier gaussianLogModifier,
                               Notifications notifications) {
        this.gaussScript = gaussScript;
        this.babelScript = babelScript;
        this.lraScript = lraScript;
        this.fittabMarkerScript = fittabMarkerScript;
        this.gaussianLogModifier = gaussianLogModifier;
        this.notifications = notifications;
    }

    public RunGaussianResult execute(WorkflowContext<MultipoleGaussInput> status) {
        logger.info("Executing gaussian workflow.");

        MultipoleGaussOutput gaussOutput = executeGaussScript(status);

        RunGaussianResult result;
        if (!gaussOutput.isLogFileValid()) {
            result = RunGaussianResult.createInvalid(gaussOutput.getLogFile());
        } else {
            status.setCurrentStatus("Removing header and footer of cluster submission from gaussian log file.");
            gaussianLogModifier.removeHeadersFromCluster(gaussOutput.getLogFile());

            status.setCurrentStatus("Converting log file to sdf file with babel...");
            BabelOutput babelOutput = babelScript.execute(new BabelInput(gaussOutput.getLogFile()));

            status.setCurrentStatus("Executing calc_LRA.py ...");
            LRAScriptOutput lraScriptOutput = lraScript.execute(new LRAScriptInput(babelOutput.getSdfFile()));

            status.setCurrentStatus("Executing mtp_fittab_maker.py ...");
            fittabMarkerScript.execute(new FittabScriptInput(gaussOutput.getCubeFile(), gaussOutput.getVdwFile(), lraScriptOutput.getLPunFile()));
            result = RunGaussianResult.Success;
        }
        return result;
    }

    private MultipoleGaussOutput executeGaussScript(WorkflowContext<MultipoleGaussInput> status) {
        MultipoleGaussOutput gaussOutput;
        boolean logIsValid = false;
        try {
            status.setCurrentStatus("Executing calc_Multipole_Gauss_MEP_comp.py ...");
            gaussOutput = gaussScript.execute(status.getParameter());
            status.setCurrentStatus("Verifying generated gauss log \n" + gaussOutput.getLogFile().getName());
            logIsValid = gaussOutput.isLogFileValid();
        } finally {
            sendNotification(logIsValid);
        }
        return gaussOutput;
    }

    private void sendNotification(boolean logIsValid) {
        try {
            notifications.sendGaussianDoneNotification(logIsValid);
        } catch (Exception e) {
            logger.warn("Could not send notification mail.", e);
        }
    }

}
