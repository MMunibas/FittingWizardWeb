package ch.scs.unibas.fittingwizard.infrastructure.base;

import ch.scs.unibas.fittingwizard.application.scripts.base.ScriptExecutionException;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * User: mhelmer
 * Date: 13.12.13
 * Time: 16:17
 */
public class ScriptUtilities {

    private final static Logger logger = Logger.getLogger(ScriptUtilities.class);

    public static void deleteFileIfExists(File resultsFile) {
        if (resultsFile.exists()) {
            logger.info("Deleting existing file. " + FilenameUtils.normalize(resultsFile.getAbsolutePath()));
            if (resultsFile.delete()) {
                logger.info("File deleted.");
            } else {
                logger.error("Could not delete file.");
            }
        }
    }

    public static void verifyFileExistence(File file) {
        if (!file.exists()) {
            throw new ScriptExecutionException(
                    "Script did not create expected " + FilenameUtils.normalize(file.getAbsolutePath()) + " file.");
        }
    }
}
