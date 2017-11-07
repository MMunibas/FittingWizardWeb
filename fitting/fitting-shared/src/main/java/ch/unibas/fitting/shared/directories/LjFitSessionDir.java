package ch.unibas.fitting.shared.directories;

import java.io.File;

/**
 * Created by tschmidt on 24.06.2016.
 */
public class LjFitSessionDir extends FittingDirectory {

    public LjFitSessionDir(String username, File directory) {
        super(username, directory);
    }

    public File getInputDir() { return createAndGet(getDirectory(), "input"); }

    public File getDefaultExportDir() {
        return createAndGet(getDirectory(), "export");
    }

    public File getOutputDir() {return createAndGet(getDirectory(), "output");}

    public File getSessionJsonFile() {return  new File(getDirectory(), "session.json");}

    public CharmmRunFileContainer createRunDir() {
        return new CharmmRunFileContainer(username, getOutputDir());
    }

    public File getUploadDir() {
        return createAndGet(getDirectory(), "uploaded");
    }
}
