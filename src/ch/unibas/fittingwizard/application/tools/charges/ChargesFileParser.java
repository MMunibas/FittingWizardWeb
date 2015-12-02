/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.application.tools.charges;

import ch.unibas.fittingwizard.application.fitting.ChargeValue;
import ch.unibas.fittingwizard.application.fitting.InitialQ00;
import ch.unibas.fittingwizard.application.fitting.OutputAtomType;
import ch.unibas.fittingwizard.application.molecule.AtomTypeId;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * User: mhelmer
 * Date: 10.12.13
 * Time: 17:42
 */
public class ChargesFileParser {

    private static final Logger logger = Logger.getLogger(ChargesFileParser.class);

    public InitialQ00 parseInitalCharges(File chargesFile) {
        logger.info("parseInitalCharges chargesFile=" + chargesFile.getAbsolutePath());
        List<ChargeValue> parsedChargeLines = parseFile(chargesFile);
        List<ChargeValue> initalCharges = new ArrayList<>();
        for (ChargeValue line : parsedChargeLines) {
            if (line.getType().equalsIgnoreCase(ChargeTypes.charge)) {
                initalCharges.add(line);
            }
        }
        InitialQ00 initialQ00 = new InitialQ00(initalCharges);
        return initialQ00;
    }

    public List<OutputAtomType> parseOutputFile(File outputFile) {
        List<ChargeValue> parsedChargeLines = parseFile(outputFile);
        List<OutputAtomType> types = createTypes(parsedChargeLines);

        return types;
    }

    private List<ChargeValue> parseFile(File chargesFile) {
        List<String> lines;
        try {
            lines = FileUtils.readLines(chargesFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return parseLines(lines);
    }

    private List<ChargeValue> parseLines(List<String> lines) {
        ArrayList<ChargeValue> parsedChargeLines = new ArrayList<>();
        for (String line : lines) {
            parsedChargeLines.add(parseLine(line));
        }
        return parsedChargeLines;
    }

    private ChargeValue parseLine(String line) {
        String[] split = line.trim().split("_|\\s+");
        return new ChargeValue(new AtomTypeId(split[0]), split[1], Double.parseDouble(split[2]));
    }

    private List<OutputAtomType> createTypes(List<ChargeValue> chargeLines) {
        HashMap<AtomTypeId, OutputAtomType> typeValues = new HashMap<>();
        for (ChargeValue chargeLine : chargeLines) {
            OutputAtomType atomType = typeValues.get(chargeLine.getAtomTypeId());

            ChargeValue chargeValue = new ChargeValue(chargeLine.getAtomTypeId(), chargeLine.getType(), chargeLine.getValue());
            if (atomType == null) {
                atomType = new OutputAtomType(chargeLine.getAtomTypeId(), Arrays.asList(chargeValue));
                typeValues.put(chargeLine.getAtomTypeId(), atomType);
            } else {
                atomType.getChargeValues().add(chargeValue);
            }
        }
        return new ArrayList<>(typeValues.values());
    }
}
