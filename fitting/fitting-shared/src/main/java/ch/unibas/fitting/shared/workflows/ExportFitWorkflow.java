/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.workflows;

import ch.unibas.fitting.shared.directories.FitOutputDir;
import ch.unibas.fitting.shared.fitting.Fit;
import ch.unibas.fitting.shared.molecules.MoleculeId;
import ch.unibas.fitting.shared.scripts.export.ExportScriptInput;
import ch.unibas.fitting.shared.scripts.export.ExportScriptOutput;
import ch.unibas.fitting.shared.scripts.export.IExportScript;
import ch.unibas.fitting.shared.workflows.base.Workflow;
import ch.unibas.fitting.shared.workflows.base.WorkflowContext;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import javax.inject.Inject;

/**
 * User: mhelmer Date: 13.12.13 Time: 15:39
 */
public class ExportFitWorkflow {

    private static final Logger logger = Logger.getLogger(ExportFitWorkflow.class);

    private final IExportScript exportScript;

    @Inject
    public ExportFitWorkflow(IExportScript exportScript) {
        this.exportScript = exportScript;
    }

    public Void execute(WorkflowContext<ExportFitInput> input) {
        logger.info("Executing export workflow.");
        Fit fit = input.getParameter().getFit();
        File destination = input.getParameter().getDestination();
        FitOutputDir fitOutputDir = input.getParameter().getFitOutputDir();

        if (destination == null) {
            destination = fitOutputDir.getDefaultExportDir();
            logger.info("No destination passed. Using default destination: "
                    + FilenameUtils.normalize(destination.getAbsolutePath()));
        }
        List<File> exportedFiles = new ArrayList<>();
        for (MoleculeId moleculeId : fit.getAllMoleculeIds()) {
            ExportScriptOutput output = exportScript.execute(new ExportScriptInput(
                    input.getParameter().getFitOutputDir(),
                    input.getParameter().getMoleculesDir(),
                    fit.getId(),
                    moleculeId));
            exportedFiles.add(output.getExportFile());
        }
        copyToDestinationIfNecessary(exportedFiles, destination);
        return null;
    }

    public List<File> execute(WorkflowContext<ExportFitInput> input, boolean charmm_export) {
        logger.info("Executing export workflow with return of List<File> for CHARMM");
        Fit fit = input.getParameter().getFit();
        File destination = input.getParameter().getDestination();
        FitOutputDir fitOutputDir = input.getParameter().getFitOutputDir();
        if (destination == null) {
            destination = fitOutputDir.getDefaultExportDir();
            logger.info("No destination passed. Using default destination: "
                    + FilenameUtils.normalize(destination.getAbsolutePath()));
        }
        List<File> exportedFiles = new ArrayList<>();
        for (MoleculeId moleculeId : fit.getAllMoleculeIds()) {
            ExportScriptOutput output = exportScript.execute(new ExportScriptInput(
                    fitOutputDir,
                    input.getParameter().getMoleculesDir(),
                    fit.getId(),
                    moleculeId));
            exportedFiles.add(output.getExportFile());
        }
        copyToDestinationIfNecessary(exportedFiles, destination);
        
        return exportedFiles;
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
}
