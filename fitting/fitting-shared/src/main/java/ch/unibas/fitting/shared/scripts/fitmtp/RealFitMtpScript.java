/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.scripts.fitmtp;

import ch.unibas.fitting.shared.config.Settings;
import ch.unibas.fitting.shared.scripts.base.PythonScriptRunner;
import ch.unibas.fitting.shared.scripts.base.ResourceUtils;
import ch.unibas.fitting.shared.scripts.base.ScriptUtilities;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import javax.inject.Inject;

/**
 * User: mhelmer Date: 06.12.13 Time: 13:13
 */
public class RealFitMtpScript implements IFitMtpScript {

    private final static Logger LOGGER = Logger.getLogger(RealFitMtpScript.class);

    public static final String FitNamePrefix = "fit_";
    public static final String ConsoleOutputFileName = "output.txt";
    public static final String FitResultFileName = "fit_results.txt";

    private File mtpFitScriptFile;

    @Inject
    public RealFitMtpScript(Settings settings) {
        mtpFitScriptFile = new File(settings.getScriptsDir(), "fit.mtp.py");
    }

    @Override
    public FitMtpOutput execute(FitMtpInput input) {
        File outputDir = input.getFitOutputDir().getDirectory();
        LOGGER.debug("Executing in " + outputDir);

        List<String> args = new ArrayList<>();
        args.add("-rank");
        args.add(String.valueOf(input.getRank()));

        String relativeCharges = ResourceUtils.getRelativePath(input.getGeneratedCharges(), outputDir);
        args.add("-l");
        args.add(relativeCharges);

        String resultsFileName = getResultFileNameForFit();
        args.add("-o");
        args.add(resultsFileName);

        args.add("-pen");
        args.add(String.valueOf(input.getConvergence()));

        if (input.isIgnoreHydrogen()) {
            args.add("-hyd");
        }

        // this must be at the end
        for (File mol : input.getMtpFitTabFiles()) {
            String relativePath = ResourceUtils.getRelativePath(mol, outputDir);
            args.add(relativePath);
        }

        File outputFile = new File(outputDir, getOutputFileNameForFit());
        ScriptUtilities.deleteFileIfExists(outputFile);
        File resultsFile = new File(outputDir, resultsFileName);
        ScriptUtilities.deleteFileIfExists(resultsFile);

        PythonScriptRunner runner = new PythonScriptRunner();
        runner.setWorkingDir(outputDir);
        runner.exec(mtpFitScriptFile, args, outputFile);

        ScriptUtilities.verifyFileExistence(outputFile);
        ScriptUtilities.verifyFileExistence(resultsFile);

        return new FitMtpOutput(outputFile, resultsFile);
    }

    public static String getResultFileNameForFit() {

        return FitResultFileName;
    }

    public static String getOutputFileNameForFit() {
        return ConsoleOutputFileName;
    }
}
