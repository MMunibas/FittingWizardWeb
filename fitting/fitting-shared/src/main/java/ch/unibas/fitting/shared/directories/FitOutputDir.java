package ch.unibas.fitting.shared.directories;

import java.io.File;

/**
 * Created by mhelmer-mobile on 19.06.2016.
 */
public class FitOutputDir extends FittingDirectory {

    public FitOutputDir(String username, File directory) {
        super(username, directory);
    }

    public File getFitMtpOutputDir() {
        return createAndGet(getDirectory(), "output");
    }

    public File getDefaultExportDir() {
        return createAndGet(getDirectory(), "export");
    }

    public File getFitOutputFileRef(int index) {
        return new File(getFitMtpOutputDir(), String.format("fit_%s_output.txt", index));
    }

    public void removeFitResult(int index) {
        for (File f: getFitMtpOutputDir().listFiles())
            if (f.getName().startsWith("fit_" + index)) {
                LOGGER.debug("Removing fit result " + f.getName());
                f.delete();
            }
    }
}
