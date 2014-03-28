package ch.unibas.fittingwizard.application.scripts.fitmtp;

import java.io.File;

/**
 * User: mhelmer
 * Date: 10.12.13
 * Time: 19:37
 */
public class FitMtpOutput {
    private final File outputFile;
    private final File reusltsFile;

    public FitMtpOutput(File outputFile, File resultsFile) {
        this.outputFile = outputFile;
        this.reusltsFile = resultsFile;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public File getResultsFile() {
        return reusltsFile;
    }
}
