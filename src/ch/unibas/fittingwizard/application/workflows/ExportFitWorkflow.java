/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.application.workflows;

import ch.unibas.fittingwizard.application.fitting.Fit;
import ch.unibas.fittingwizard.application.molecule.MoleculeId;
import ch.unibas.fittingwizard.application.scripts.export.ExportScriptInput;
import ch.unibas.fittingwizard.application.scripts.export.ExportScriptOutput;
import ch.unibas.fittingwizard.application.scripts.export.IExportScript;
import ch.unibas.fittingwizard.application.workflows.base.Workflow;
import ch.unibas.fittingwizard.application.workflows.base.WorkflowContext;
import ch.unibas.fittingwizard.infrastructure.RealFitScript;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: mhelmer
 * Date: 13.12.13
 * Time: 15:39
 */
public class ExportFitWorkflow extends Workflow<ExportFitInput,Void> {

    private static final Logger logger = Logger.getLogger(ExportFitWorkflow.class);

    private final IExportScript exportScript;
    private final File sessionDir;

    public ExportFitWorkflow(IExportScript exportScript, File sessionDir) {
        this.exportScript = exportScript;
        this.sessionDir = sessionDir;
    }

    public Void execute(WorkflowContext<ExportFitInput> input) {
        logger.info("Executing export workflow.");
        Fit fit = input.getParameter().getFit();
        File destination = input.getParameter().getDestination();

        if (destination == null) {
            destination = getDefaultExportDir();
            logger.info("No destination passed. Using default destination: "
                    + FilenameUtils.normalize(destination.getAbsolutePath()));
        }
        List<File> exportedFiles = new ArrayList<>();
        for (MoleculeId moleculeId : fit.getAllMoleculeIds()) {
            ExportScriptOutput output = exportScript.execute(new ExportScriptInput(fit.getId(), moleculeId));
            exportedFiles.add(output.getExportFile());
        }
        copyToDestinationIfNecessary(exportedFiles, destination);
        return null;
    }

    private void copyToDestinationIfNecessary(List<File> exportedFiles, File destination) {
        destination.mkdir();
        logger.info("copyToDestinationIfNecessary");
        for (File exported : exportedFiles) {
            boolean alreadyInDestination = FilenameUtils.equalsNormalized(exported.getParentFile().getAbsolutePath(), destination.getAbsolutePath());
            if (!alreadyInDestination) {
                try {
                    FileUtils.copyFileToDirectory(exported, destination);
                } catch (IOException e) {
                    throw new RuntimeException("Could not copy file to destination " + destination.getAbsolutePath(), e);
                }
                if (!exported.delete()) {
                    logger.error("Exported file was not deleted.");
                }
            } else {
                logger.info("Skipping copying to destination, since export file is already in destination.");
            }
        }
    }

    public File getDefaultExportDir() {
        return new File(new File(sessionDir, RealFitScript.OutputDirName), "export");
    }
}
