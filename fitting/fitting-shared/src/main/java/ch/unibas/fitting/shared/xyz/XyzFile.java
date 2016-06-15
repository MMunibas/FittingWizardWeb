/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.xyz;

import java.io.File;
import java.util.List;
import org.apache.commons.io.FilenameUtils;

/**
 * User: mhelmer
 * Date: 26.11.13
 * Time: 17:47
 */
public class XyzFile {

    private final String name;
    private final int atomCount;
    private final List<XyzAtom> atoms;
    private final File source;

    public XyzFile(File source, int atomCount, List<XyzAtom> atoms) {
        this.source = source;
        this.name = FilenameUtils.getBaseName(source.getAbsolutePath());
        this.atomCount = atomCount;
        this.atoms = atoms;
    }

    public XyzFile(List<XyzAtom> atoms) {
        this.atomCount = atoms.size();
        this.atoms = atoms;
        name = null;
        source = null;
    }

    public String getMoleculeName() {
        return name;
    }

    public File getSource() {
        return source;
    }

    public int getAtomCount() {
        return atomCount;
    }

    public List<XyzAtom> getAtoms() {
        return atoms;
    }

    @Override
    public String toString() {
        return "XyzFile{" +
                "atomCount=" + atomCount +
                ", atoms=" + atoms +
                '}';
    }
}
