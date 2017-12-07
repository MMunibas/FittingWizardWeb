/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.tools;

import io.vavr.collection.List;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * User: mhelmer
 * Date: 05.12.13
 * Time: 17:42
 */
public class LPunParser {

    public List<LPunAtomType> parse(File lpunFile) {
        if (!lpunFile.isFile())
            throw new RuntimeException("Could not finde file: " + lpunFile);

        List<String> lines;
        try {
            lines = List.ofAll(FileUtils.readLines(lpunFile));
        } catch (IOException e) {
            throw new RuntimeException("Could not read file " + lpunFile.getAbsolutePath(), e);
        }

        lines = removeHeader(lines);
        lines = removeFooter(lines);

        HashMap<String, ArrayList<Integer>> atomTypePositions = new HashMap<>();
        HashMap<String, Double> atomTypeCharge = new HashMap<>();

        int idxCharge = 0;
        for (int i = 0; i + 2 < lines.size(); i += 3) {
            String line = lines.get(i);
            String[] parts = line.trim().split("\\s+");
            String type = parts[0].trim();
            String nextLine = lines.get(i+1).trim();
            Double charge = Double.parseDouble(nextLine);

            ArrayList<Integer> indices = atomTypePositions.get(type);
            if (indices == null) {
                indices = new ArrayList<>();
                atomTypePositions.put(type, indices);
                atomTypeCharge.put(type, charge);
            }
            indices.add(idxCharge);

            idxCharge++;
        }

        List<LPunAtomType> charges = List.empty();
        for (Map.Entry<String, ArrayList<Integer>> entry : atomTypePositions.entrySet()) {
            ArrayList<Integer> indices = entry.getValue();
            int[] array = new int[indices.size()];
            for(int i = 0; i < indices.size(); i++)
                array[i] = indices.get(i);
            Double charge = atomTypeCharge.get(entry.getKey());
            LPunAtomType lPunAtomType = new LPunAtomType(
                    entry.getKey(),
                    charge,
                    array);
            charges = charges.append(lPunAtomType);
        }

        return charges;
    }

    private List<String> removeHeader(List<String> lines) {
        return lines.subSequence(3, lines.size());
    }

    private List<String> removeFooter(List<String> lines) {
        int lraIndex = -1;
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).trim().contains("LRA:"))
                lraIndex = i;
        }
        if (lraIndex == -1)
            throw new LPunParserException("Could not find line containing 'LRA:'.");
        return lines.subSequence(0, lraIndex);
    }
}
