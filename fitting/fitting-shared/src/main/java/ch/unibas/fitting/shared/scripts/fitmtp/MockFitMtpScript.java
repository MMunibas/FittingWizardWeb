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
import ch.unibas.fitting.shared.scripts.base.MockScriptBase;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import javax.inject.Inject;

/**
 * User: mhelmer
 * Date: 06.12.13
 * Time: 13:13
 */
public class MockFitMtpScript extends MockScriptBase implements IFitMtpScript {

    @Inject
	public MockFitMtpScript(Settings settings) {
        super(settings.getTestdataFitOutput());
	}

	@Override
    public FitMtpOutput execute(FitMtpInput input) {
        File outputDir = input.getFitOutputDir().getFitMtpOutputDir();

        File outputFile = new File(outputDir, RealFitMtpScript.getOutputFileNameForFit(input.getFitId()));
        copyTestFile(RealFitMtpScript.ConsoleOutputFileName, outputFile);

        File resultsFile = new File(outputDir, RealFitMtpScript.getResultFileNameForFit(input.getFitId()));
        copyTestFile(RealFitMtpScript.FitResultFileName, resultsFile);

        return new FitMtpOutput(outputFile, resultsFile);
    }
}
