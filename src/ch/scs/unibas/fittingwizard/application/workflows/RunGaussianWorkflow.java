package ch.scs.unibas.fittingwizard.application.workflows;

import ch.scs.unibas.fittingwizard.application.scripts.babel.BabelInput;
import ch.scs.unibas.fittingwizard.application.scripts.babel.BabelOutput;
import ch.scs.unibas.fittingwizard.application.scripts.babel.IBabelScript;
import ch.scs.unibas.fittingwizard.application.scripts.fittab.FittabScriptInput;
import ch.scs.unibas.fittingwizard.application.scripts.fittab.IFittabScript;
import ch.scs.unibas.fittingwizard.application.scripts.lra.ILRAScript;
import ch.scs.unibas.fittingwizard.application.scripts.lra.LRAScriptInput;
import ch.scs.unibas.fittingwizard.application.scripts.lra.LRAScriptOutput;
import ch.scs.unibas.fittingwizard.application.scripts.multipolegauss.IMultipoleGaussScript;
import ch.scs.unibas.fittingwizard.application.scripts.multipolegauss.MultipoleGaussInput;
import ch.scs.unibas.fittingwizard.application.scripts.multipolegauss.MultipoleGaussOutput;
import ch.scs.unibas.fittingwizard.application.tools.GaussianLogModifier;
import ch.scs.unibas.fittingwizard.application.tools.Notifications;
import ch.scs.unibas.fittingwizard.application.workflows.base.Workflow;
import ch.scs.unibas.fittingwizard.application.workflows.base.WorkflowContext;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.IOException;

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
