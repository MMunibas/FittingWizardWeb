package ch.unibas.fitting.shared.directories;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

/**
 * Created by mhelmer-mobile on 19.06.2016.
 */
abstract class FittingDirectory {
    private final File directory;

    protected FittingDirectory(File directory) {
        directory.mkdirs();
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Given directory does not exist " + directory.getAbsolutePath());
        }
        this.directory = new File(FilenameUtils.normalize(directory.getAbsolutePath()));
    }

    public File getDirectory() {
        return directory;
    }
}
