package ch.unibas.fitting.web.application.directories;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * Created by mhelmer-mobile on 19.06.2016.
 */
abstract class FittingDirectory {
    protected final Logger LOGGER;

    protected final String username;
    private final File directory;

    protected FittingDirectory(String username,
                               File directory) {
        this.username = username;
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

    protected File createSubDir(String name) {
        File sub = new File(directory, name);
        sub.mkdirs();
        return sub;
    }

    public boolean exists() {
        return directory.exists();
    }

    public String getUsername() {
        return username;
    }
}
