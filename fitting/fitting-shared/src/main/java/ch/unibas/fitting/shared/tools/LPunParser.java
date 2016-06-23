/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.tools;

import ch.unibas.fitting.shared.directories.MoleculesDir;
import ch.unibas.fitting.shared.molecules.AtomType;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;

/**
 * User: mhelmer
 * Date: 05.12.13
 * Time: 17:42
 */
public class LPunParser {

    public ArrayList<AtomType> parse(File lpunFile) {
        if (!lpunFile.isFile())
            throw new RuntimeException("Could not finde file: " + lpunFile);

        List<String> lines;
        try {
            lines = FileUtils.readLines(lpunFile);
        } catch (IOException e) {
            throw new RuntimeException("Could not read file " + lpunFile.getAbsolutePath(), e);
        }

        lines = removeHeader(lines);
        lines = removeFooter(lines);

        HashMap<String, ArrayList<Integer>> atomTypePositions = new HashMap<>();

        int idxCharge = 0;
        for (int i = 0; i + 2 < lines.size(); i += 3) {
            String type = lines.get(i).trim().split("\\s+")[0].trim();

            ArrayList<Integer> indices = atomTypePositions.get(type);
            if (indices == null) {
                indices = new ArrayList<>();
                atomTypePositions.put(type, indices);
            }
            indices.add(idxCharge);

            idxCharge++;
        }

        ArrayList<AtomType> charges = new ArrayList<>();
        for (Map.Entry<String, ArrayList<Integer>> entry : atomTypePositions.entrySet()) {
            ArrayList<Integer> indices = entry.getValue();
            int[] array = new int[indices.size()];
            for(int i = 0; i < indices.size(); i++) array[i] = indices.get(i);
            AtomType atomType = new AtomType(entry.getKey(), array);
            charges.add(atomType);
        }

        return charges;
    }

    private List<String> removeHeader(List<String> lines) {
        return lines.subList(3, lines.size());
    }

    private List<String> removeFooter(List<String> lines) {
        int lraIndex = -1;
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).trim().contains("LRA:"))
                lraIndex = i;
        }
        if (lraIndex == -1)
            throw new LPunParserException("Could not find line containing 'LRA:'.");
        return lines.subList(0, lraIndex);
    }
}
