package ch.unibas.fitting.shared.directories;

import java.io.File;

/**
 * Created by tschmidt on 24.06.2016.
 */
public class CharmmOutputDir extends FittingDirectory {
    public CharmmOutputDir(File directory) {
        super(directory);
    }

    public File getInputDir() { return createAndGet(getDirectory(), "input"); }

    public File getDefaultExportDir() {
        return createAndGet(getDirectory(), "export");
    }

    public File getOutputDir() {return createAndGet(getDirectory(), "output");}

    public CharmmRunFileContainer createRunDir() {
        return new CharmmRunFileContainer(getOutputDir());
    }
}
