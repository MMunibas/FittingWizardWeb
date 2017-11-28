/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.scripts.export;

import ch.unibas.fitting.shared.config.Settings;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

import javax.inject.Inject;

/**
 * User: mhelmer
 * Date: 13.12.13
 * Time: 15:12
 */
public class MockExportScript extends RealExportScript {

    private final File testOutputDir;

    @Inject
    public MockExportScript(Settings settings) {
        super(settings);
        this.testOutputDir = settings.getTestdataFitOutput();
    }

    @Override
    public ExportScriptOutput execute(ExportScriptInput input) {
        File outputDir = input.getFitOutputDir().getFitMtpOutputDir();

        String testFile = "co2fit.lpun";
        File testdata = new File(testOutputDir, testFile);
        File outputFile = new File(outputDir, String.format("fit_%d_co2fit.lpun", input.getFitId()));
        try {
            FileUtils.copyFile(testdata, outputFile);
        } catch (IOException e) {
            throw new RuntimeException("Could not copy testdata to ouptut dir.", e);
        }

        return new ExportScriptOutput(outputFile);
    }
}
