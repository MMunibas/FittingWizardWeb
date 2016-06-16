/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.tools;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 *
 * User: mhelmer
 * Date: 16.12.13
 * Time: 10:30
 */
public class GaussianLogModifier {

    private static final Logger logger = Logger.getLogger(GaussianLogModifier.class);

    public static final String BackupFileSuffix = "~";
    public static final String EnteringLineString = "Entering Gaussian System";
    public static final String TerminationLineString = "Normal termination of Gaussian";

    public File removeHeadersFromCluster(File fileWithHeader) {
        logger.info("Removing headers from cluster from file " + fileWithHeader.getAbsolutePath());

        List<String> lines;
        try {
            lines = FileUtils.readLines(fileWithHeader);
        } catch (IOException e) {
            throw new RuntimeException("Could not read input file " + fileWithHeader.getAbsolutePath());
        }

        int enteringLine = getEnteringLine(lines);
        int terminationLine = getTerminationLine(lines);

        if (enteringLine > 0 && terminationLine > 0 && terminationLine != lines.size() - 1) {
            createBackup(fileWithHeader);
            List<String> linesWithoutHeader = lines.subList(enteringLine, terminationLine + 1);
            try {
                FileUtils.writeLines(fileWithHeader, linesWithoutHeader);
            } catch (IOException e) {
                throw new RuntimeException("Could not write file without headers. " + fileWithHeader.getAbsolutePath());
            }
        }

        return fileWithHeader;
    }

    public int getEnteringLine(List<String> lines) {
        return findLineWithContent(lines, EnteringLineString);
    }

    public int getTerminationLine(List<String> lines) {
        return findLineWithContent(lines, TerminationLineString);
    }

    public int findLineWithContent(List<String> lines, String content) {
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.contains(content))
                return i;
        }
        return -1;
    }

    public void createBackup(File file) {
        File bak = getBackupFileName(file);
        try {
            FileUtils.copyFile(file, bak);
        } catch (IOException e) {
            throw new RuntimeException("Could not create backup file " + bak.getAbsolutePath());
        }
    }

    private File getBackupFileName(File file) {
        return new File(file.getParentFile(), file.getName() + BackupFileSuffix);
    }
}
