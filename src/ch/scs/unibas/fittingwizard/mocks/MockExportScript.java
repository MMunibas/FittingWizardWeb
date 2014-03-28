package ch.scs.unibas.fittingwizard.mocks;

import ch.scs.unibas.fittingwizard.application.scripts.export.ExportScriptInput;
import ch.scs.unibas.fittingwizard.application.scripts.export.ExportScriptOutput;
import ch.scs.unibas.fittingwizard.application.scripts.export.IExportScript;
import ch.scs.unibas.fittingwizard.infrastructure.RealFitScript;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * User: mhelmer
 * Date: 13.12.13
 * Time: 15:12
 */
public class MockExportScript implements IExportScript {

    private final File sessionDir;
    private final File testdataDir;

    public MockExportScript(File sessionDir, File testdataDir) {
        this.testdataDir = testdataDir;
        this.sessionDir = sessionDir;
    }

    @Override
    public ExportScriptOutput execute(ExportScriptInput input) {
        File outputDir = new File(sessionDir, RealFitScript.OutputDirName);
        // working directory is output directory

        String testFile = "co2fit.lpun";
        File testdata = new File(new File(testdataDir, RealFitScript.OutputDirName), testFile);
        File outputFile = new File(outputDir, testFile);
        try {
            FileUtils.copyFile(testdata, outputFile);
        } catch (IOException e) {
            throw new RuntimeException("Could not copy testdata to ouptut dir.", e);
        }

        return new ExportScriptOutput(outputFile);
    }
}
