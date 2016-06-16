/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.mocks;

import ch.unibas.fitting.shared.scripts.fitmtp.FitMtpInput;
import ch.unibas.fitting.shared.scripts.fitmtp.FitMtpOutput;
import ch.unibas.fitting.shared.scripts.fitmtp.IFitMtpScript;
import ch.unibas.fittingwizard.infrastructure.RealFitScript;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

/**
 * User: mhelmer
 * Date: 06.12.13
 * Time: 13:13
 */
public class MockFitMtpScript implements IFitMtpScript {
    private final static Logger logger = Logger.getLogger(MockFitMtpScript.class);

    private final File sessionDir;
    private final File testdataDir;

	public MockFitMtpScript(File sessionDir, File testdataDir) {
    	this.sessionDir = sessionDir;
        this.testdataDir = testdataDir;
	}

	@Override
    public FitMtpOutput execute(FitMtpInput input) {
        File outputDir = new File(sessionDir, RealFitScript.OutputDirName);
        outputDir.mkdir();

        File testdataOutput = new File(testdataDir, RealFitScript.OutputDirName);
        File outputMockData = new File(testdataOutput, RealFitScript.ConsoleOutputFileName);
        File resultMockData = new File(testdataOutput, RealFitScript.FitResultFileName);

        File outputFile = new File(outputDir, RealFitScript.getOutputFileNameForFit(input.getFitId()));
        File resultsFile = new File(outputDir, RealFitScript.getResultFileNameForFit(input.getFitId()));

        try {
            logger.info(String.format("Copying mock data from %s to %s.",
                            FilenameUtils.normalize(testdataOutput.getAbsolutePath()),
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
