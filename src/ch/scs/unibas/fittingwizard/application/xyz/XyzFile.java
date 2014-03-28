package ch.scs.unibas.fittingwizard.application.xyz;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
