package ch.unibas.fitting.shared.scripts.base;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * Created by mhelmer on 23.06.2016.
 */
public abstract class MockScriptBase {
    protected final Logger LOGGER;

    private final File testFileBase;

    protected MockScriptBase(File testFileBase) {
        this.testFileBase = testFileBase;
        LOGGER = Logger.getLogger(getClass());
    }

    protected File getTestFileBase() {
        return testFileBase;
    }

    protected void copyTestFile(String srcFile, File destination) {
        File testFile = new File(getTestFileBase(), srcFile);
        copyTestFile(testFile, destination);
    }

    protected void copyTestFile(File srcFile, File destinationFile) {
        LOGGER.debug(String.format("copyTestFile %s to %s.",
                FilenameUtils.normalize(srcFile.getAbsolutePath()),
                FilenameUtils.normalize(destinationFile.getAbsolutePath())));
        try {
            FileUtils.copyFile(srcFile, destinationFile);
        } catch (IOException e) {
            throw new RuntimeException("Could not copy mock data to output directory.");
        }
    }
}
