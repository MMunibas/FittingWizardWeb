/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.scripts.fitmtp;

import java.io.File;
import java.io.IOException;

import ch.unibas.fitting.shared.config.Settings;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import javax.inject.Inject;

/**
 * User: mhelmer
 * Date: 06.12.13
 * Time: 13:13
 */
public class MockFitMtpScript implements IFitMtpScript {
    private final static Logger logger = Logger.getLogger(MockFitMtpScript.class);

    private final File testOuptputDir;

    @Inject
	public MockFitMtpScript(Settings settings) {
        this.testOuptputDir = settings.getTestdataFitOutput();
	}

	@Override
    public FitMtpOutput execute(FitMtpInput input) {
        File outputDir = input.getFitOutputDir().getFitMtpOutputDir();

        File outputMockData = new File(testOuptputDir, RealFitMtpScript.ConsoleOutputFileName);
        File resultMockData = new File(testOuptputDir, RealFitMtpScript.FitResultFileName);

        File outputFile = new File(outputDir, RealFitMtpScript.getOutputFileNameForFit(input.getFitId()));
        File resultsFile = new File(outputDir, RealFitMtpScript.getResultFileNameForFit(input.getFitId()));

        try {
            logger.info(String.format("Copying mock data from %s to %s.",
                            FilenameUtils.normalize(testOuptputDir.getAbsolutePath()),
                            FilenameUtils.normalize(outputDir.getAbsolutePath())));
            FileUtils.copyFile(resultMockData, resultsFile);
            FileUtils.copyFile(outputMockData, outputFile);
        } catch (IOException e) {
            throw new RuntimeException("Could not copy mock data to output directory.");
        }

        FitMtpOutput output = new FitMtpOutput(outputFile, resultsFile);
        return output;
    }
}
