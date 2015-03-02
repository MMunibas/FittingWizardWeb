/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.application.tools;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: mhelmer
 * Date: 10.12.13
 * Time: 19:26
 */
public class FitOutputParser {
    public double parseRmseValue(File outputFile) {
        List<String> lines;
        try {
            lines = FileUtils.readLines(outputFile);
        } catch (IOException e) {
            throw new RuntimeException("Could not read output file.", e);
        }

        boolean lineWithRmseContained = false;
        Double rmse = null;
        for (String line : lines) {
            lineWithRmseContained = line.contains("RMSE:");
            if (lineWithRmseContained) {
                rmse = Double.parseDouble(getRmseString(line));
                break;
            }
        }

        if (!lineWithRmseContained) {
            throw new RuntimeException(String.format("The output file %s did not contain a RMSE line.", outputFile.getAbsoluteFile()));
        }

        return rmse;
    }

    private String getRmseString(String line) {
        Pattern pattern = Pattern.compile("RMSE:\\s*(.*)\\s*kcal/mol");
        Matcher matcher = pattern.matcher(line);
        String rmse = null;
        if (matcher.find()) {
            rmse = matcher.group(1);
        }
        return rmse;
    }
}
