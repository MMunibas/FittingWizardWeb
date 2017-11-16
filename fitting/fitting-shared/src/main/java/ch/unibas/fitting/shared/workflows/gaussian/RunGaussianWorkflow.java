/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.workflows.gaussian;

import ch.unibas.fitting.shared.directories.MoleculesDir;
import ch.unibas.fitting.shared.molecules.Molecule;
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

import javax.inject.Inject;

/**
 * User: mhelmer
 * Date: 16.12.13
 * Time: 09:28
 */
public class RunGaussianWorkflow implements Workflow<MultipoleGaussInput,RunGaussianResult> {

    private static final Logger logger = Logger.getLogger(RunGaussianWorkflow.class);

    private IMultipoleGaussScript gaussScript;
    private IBabelScript babelScript;
    private ILRAScript lraScript;
    private IFittabScript fittabMarkerScript;
    private GaussianLogModifier gaussianLogModifier;
    private Notifications notifications;
    private MoleculeCreator moleculeCreator;

    @Inject
    public RunGaussianWorkflow(IMultipoleGaussScript gaussScript,
                               IBabelScript babelScript,
                               ILRAScript lraScript,
                               IFittabScript fittabMarkerScript,
                               GaussianLogModifier gaussianLogModifier,
                               Notifications notifications,
                               MoleculeCreator moleculeCreator) {
        this.gaussScript = gaussScript;
        this.babelScript = babelScript;
        this.lraScript = lraScript;
        this.fittabMarkerScript = fittabMarkerScript;
        this.gaussianLogModifier = gaussianLogModifier;
        this.notifications = notifications;
        this.moleculeCreator = moleculeCreator;
    }

    public RunGaussianResult execute(WorkflowContext<MultipoleGaussInput> status) {
        logger.info("Executing gaussian workflow.");

        MultipoleGaussInput input = status.getParameter();
        MultipoleGaussOutput gaussOutput = executeGaussScript(status);
        MoleculesDir moleculesDir = input.getMoleculesDir();

        RunGaussianResult result;
        if (gaussOutput.isLogFileValid()) {
            status.setCurrentStatus("Removing header and footer of cluster submission from gaussian log file.");
            gaussianLogModifier.removeHeadersFromCluster(gaussOutput.getLogFile());

            status.setCurrentStatus("Converting log file to sdf file with babel...");
            BabelOutput babelOutput = babelScript.execute(new BabelInput(moleculesDir, gaussOutput.getLogFile()));

            status.setCurrentStatus("Executing calc_LRA.py ...");
            LRAScriptOutput lraScriptOutput = lraScript.execute(new LRAScriptInput(moleculesDir, babelOutput.getSdfFile()));

            status.setCurrentStatus("Executing mtp_fittab_maker.py ...");
            fittabMarkerScript.execute(new FittabScriptInput(
                    moleculesDir,
                    gaussOutput.getCubeFile(),
                    gaussOutput.getVdwFile(),
                    lraScriptOutput.getLPunFile()));

            Molecule molecule = moleculeCreator.createMolecule(moleculesDir, input.getXyzDirectory(), input.getMoleculeName());

            result = new RunGaussianResult(molecule, gaussOutput.getLogFile());
        } else {
            result = new RunGaussianResult(gaussOutput.getLogFile());
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
