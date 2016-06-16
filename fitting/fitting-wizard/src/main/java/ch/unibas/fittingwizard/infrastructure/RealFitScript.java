/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.infrastructure;

import ch.unibas.fitting.shared.config.Settings;
import ch.unibas.fitting.shared.molecules.MoleculeId;
import ch.unibas.fitting.shared.scripts.base.ScriptExecutionException;
import ch.unibas.fitting.shared.scripts.fitmtp.FitMtpInput;
import ch.unibas.fitting.shared.scripts.fitmtp.FitMtpOutput;
import ch.unibas.fitting.shared.scripts.fitmtp.IFitMtpScript;
import ch.unibas.fitting.shared.scripts.base.PythonScriptRunner;
import ch.unibas.fitting.shared.scripts.base.ResourceUtils;
import ch.unibas.fitting.shared.scripts.base.ScriptUtilities;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.log4j.Logger;

/**
 * User: mhelmer Date: 06.12.13 Time: 13:13
 */
public class RealFitScript implements IFitMtpScript {

    private final static Logger logger = Logger.getLogger(RealFitScript.class);

    public static final String MtpFitScriptNameKey = "scripts.fitting";

    public static final String FitNamePrefix = "fit_";
    public static final String OutputDirName = "output";
    public static final String ConsoleOutputFileName = "output.txt";
    public static final String FitResultFileName = "fit_results.txt";
    public static final String MtpfittabExtension = "_mtpfittab.txt";

    private File sessionDir;
    private final File moleculesDir;

    private final PythonScriptRunner runner;
    private File mtpFitScriptFile;

    public RealFitScript(File sessionDir, File moleculesDir, Settings settings) {
        this.sessionDir = sessionDir;
        this.moleculesDir = moleculesDir;
        runner = new PythonScriptRunner();
        mtpFitScriptFile = new File(settings.getScriptsDir(), settings.getValue(MtpFitScriptNameKey));
    }

    @Override
    public FitMtpOutput execute(FitMtpInput input) {
        File outputDir = new File(sessionDir, OutputDirName);
        outputDir.mkdir();

        runner.setWorkingDir(outputDir);

        List<String> args = new ArrayList<>();
        args.add("-rank");
        args.add(String.valueOf(input.getRank()));

        String relativeCharges = ResourceUtils.getRelativePath(input.getInitalChargesFile(), outputDir);
        args.add("-l");
        args.add(relativeCharges);

        String resultsFileName = getResultFileNameForFit(input.getFitId());
        args.add("-o");
        args.add(resultsFileName);

        args.add("-pen");
        args.add(String.valueOf(input.getConvergence()));

        if (input.isIgnoreHydrogen()) {
            args.add("-hyd");
        }

        // this must be at the end
        List<File> fitTabResults = getAllFitTabFiles(input.getMoleculesForFit());
        for (File fitTabResult : fitTabResults) {
            String relativePath = ResourceUtils.getRelativePath(fitTabResult, outputDir);
            args.add(relativePath);
        }

        File outputFile = new File(outputDir, getOutputFileNameForFit(input.getFitId()));
        ScriptUtilities.deleteFileIfExists(outputFile);
        File resultsFile = new File(outputDir, resultsFileName);
        ScriptUtilities.deleteFileIfExists(resultsFile);

        runner.exec(mtpFitScriptFile, args, outputFile);

        ScriptUtilities.verifyFileExistence(outputFile);
        ScriptUtilities.verifyFileExistence(resultsFile);

        FitMtpOutput output = new FitMtpOutput(outputFile, resultsFile);
        return output;
    }

    public static String getResultFileNameForFit(int fitId) {
        return getFileNameWithFitId(fitId, FitResultFileName);
    }

    public static String getOutputFileNameForFit(int fitId) {
        return getFileNameWithFitId(fitId, ConsoleOutputFileName);
    }

    public static String getFileNameWithFitId(int fitId, String baseName) {
        return FitNamePrefix + fitId + "_" + baseName;
    }

    private List<File> getAllFitTabFiles(List<MoleculeId> moleculesForFit) {
        List<File> allFitTabFiles = getAllFitTabFiles(moleculesDir);
        List<File> molecules = new ArrayList<>();

        for (File file : allFitTabFiles) {
            for (MoleculeId moleculeId : moleculesForFit) {
                if (file.getName().contains(moleculeId.getName())) {
                    molecules.add(file);
                }
            }
        }

        return molecules;
    }

    public static List<File> getAllFitTabFiles(File rootDir) {
        logger.info("getAllFitTabFiles");
        List<File> files = new ArrayList<>(FileUtils.listFiles(rootDir, new IOFileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(MtpfittabExtension);
            }

            @Override
            public boolean accept(File dir, String name) {
                return false;
            }
        }, TrueFileFilter.TRUE));

        if (files.size() == 0) {
            throw new ScriptExecutionException("Could not find any fit tab results file in "
                    + FilenameUtils.normalize(rootDir.getAbsolutePath()));
        }
        for (File file : files) {
            logger.info("Found fit tab file: " + FilenameUtils.normalize(file.getAbsolutePath()));
        }
        return files;
    }
}
