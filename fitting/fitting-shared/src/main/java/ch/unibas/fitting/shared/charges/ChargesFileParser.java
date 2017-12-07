/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.charges;

import ch.unibas.fitting.shared.fitting.ChargeValue;
import ch.unibas.fitting.shared.fitting.InitialQ00;
import ch.unibas.fitting.shared.fitting.OutputAtomType;
import ch.unibas.fitting.shared.tools.AtomTypeId;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import io.vavr.collection.List;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class ChargesFileParser {

    private static final Logger LOGGER = Logger.getLogger(ChargesFileParser.class);

    public InitialQ00 parseInitalCharges(File chargesFile) {
        LOGGER.debug("parseInitalCharges chargesFile=" + chargesFile.getAbsolutePath());
        List<ChargeValue> parsedChargeLines = parseFile(chargesFile);
        LinkedHashSet<ChargeValue> initalCharges =
                parsedChargeLines
                        .filter(line -> line.getType().equalsIgnoreCase(ChargeTypes.charge))
                        .collect(Collectors.toCollection(LinkedHashSet::new));
        return new InitialQ00(initalCharges);
    }

    public List<OutputAtomType> parseOutputFile(File outputFile) {
        List<ChargeValue> parsedChargeLines = parseFile(outputFile);
        List<OutputAtomType> types = createTypes(parsedChargeLines);

        return types;
    }

    private List<ChargeValue> parseFile(File chargesFile) {
        List<String> lines;
        try {
            lines = List.ofAll(FileUtils.readLines(chargesFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return parseLines(lines);
    }

    private List<ChargeValue> parseLines(List<String> lines) {
        return lines.map(s -> parseLine(s));
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
                atomType = new OutputAtomType(chargeLine.getAtomTypeId(),
                        List.of(chargeValue));
                typeValues.put(chargeLine.getAtomTypeId(), atomType);
            } else {
                atomType.add(chargeValue);
            }
        }
        return List.ofAll(typeValues.values());
    }
}
