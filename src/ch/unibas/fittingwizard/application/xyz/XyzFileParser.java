/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.application.xyz;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * User: mhelmer
 * Date: 26.11.13
 * Time: 17:46
 */
public class XyzFileParser {

    private static final Logger logger = Logger.getLogger(XyzFileParser.class);

    private static final int HeaderLine = 0;
    private static final int FirstAtomLine = 2;

    private final File file;
    private List<String> content;

    public static XyzFile parse(File file) {
        return new XyzFileParser(file).parse();
    }

    private XyzFileParser(File file) {
        logger.setLevel(Level.INFO);
        logger.info(String.format("Initializing parser for file %s", file.getAbsoluteFile()));
        this.file = file;
    }

    public XyzFile parse() {
        readLinesFromFile();

        XyzFile xyzFile = new XyzFile(file,
                parseCount(),
                parseAtoms());

        return xyzFile;
    }

    private void readLinesFromFile() {
        logger.info("Reading lines from file.");
        try {
            content = FileUtils.readLines(file);
        } catch (IOException e) {
            throw new RuntimeException("Could not read coordinates file.");
        }
    }

    private List<XyzAtom> parseAtoms() {
        logger.info("Parsing atoms.");
        ArrayList<XyzAtom> atoms = new ArrayList();

        for (int lineIdx = FirstAtomLine; lineIdx < content.size(); lineIdx++) {
            String line = content.get(lineIdx);

            boolean lineIsEmpty = line.trim().isEmpty();
            if (lineIsEmpty) break;

            String[] parts = line.split("\\s+");
            boolean lineMissesColumns = parts.length < 4;
            if (lineMissesColumns) throw new RuntimeException(String.format("Malformed line %d in file %s", line, file));

            XyzAtom atom = new XyzAtom(parts[0],
                            lineIdx-FirstAtomLine,
                            Double.parseDouble(parts[1]),
                            Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
            logger.debug("Found atom: " + atom);
            atoms.add(atom);
        }

        return atoms;
    }

    private int parseCount() {
        logger.info("Prasing atom count from file.");
        int count = Integer.parseInt(content.get(HeaderLine).trim());
        logger.info(String.format("File contains %d atoms.", count));
        return count;
    }
}
