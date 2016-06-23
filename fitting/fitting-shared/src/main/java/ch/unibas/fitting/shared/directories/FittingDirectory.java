package ch.unibas.fitting.shared.directories;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * Created by mhelmer-mobile on 19.06.2016.
 */
abstract class FittingDirectory {
    protected final Logger LOGGER;
    private final File directory;

    protected FittingDirectory(File directory) {
        LOGGER = Logger.getLogger(getClass());
        directory.mkdirs();
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Given directory does not exist " + directory.getAbsolutePath());
        }
        this.directory = new File(FilenameUtils.normalize(directory.getAbsolutePath()));
    }

    public File getDirectory() {
        return directory;
    }

    protected File createAndGet(File file, String dir) {
        File f =  new File(file, dir);
        f.mkdirs();
        return f;
    }
}
