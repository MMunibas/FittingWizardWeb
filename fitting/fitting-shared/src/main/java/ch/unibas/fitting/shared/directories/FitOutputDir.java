package ch.unibas.fitting.shared.directories;

import java.io.File;

/**
 * Created by mhelmer-mobile on 19.06.2016.
 */
public class FitOutputDir extends FittingDirectory {

    public FitOutputDir(File directory) {
        super(directory);
    }

    public File getFitMtpOutputDir() {
        return new File(getDirectory(), "output");
    }

    public File getCharmmOutputDir() {
        return new File(getDirectory(), "charmm");
    }

    public File getDefaultExportDir() {
        return new File(getDirectory(), "export");
    }
}
