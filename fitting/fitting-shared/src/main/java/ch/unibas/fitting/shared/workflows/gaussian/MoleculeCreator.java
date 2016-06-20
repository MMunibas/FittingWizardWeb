package ch.unibas.fitting.shared.workflows.gaussian;

import ch.unibas.fitting.shared.directories.MoleculesDir;
import ch.unibas.fitting.shared.directories.XyzDirectory;
import ch.unibas.fitting.shared.molecules.Atom;
import ch.unibas.fitting.shared.molecules.AtomType;
import ch.unibas.fitting.shared.molecules.Molecule;
import ch.unibas.fitting.shared.tools.LPunParser;
import ch.unibas.fitting.shared.xyz.XyzFile;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mhelmer on 20.06.2016.
 */
public class MoleculeCreator {
    private LPunParser lPunParser;

    @Inject
    public MoleculeCreator(LPunParser lPunParser) {
        this.lPunParser = lPunParser;
    }

    public Molecule createMolecule(MoleculesDir moleculesDir, XyzDirectory xyzDirectory, String moleculeName) {
        ArrayList<AtomType> atomTypes = lPunParser.parse(moleculesDir, moleculeName);

        XyzFile xyzFile = xyzDirectory.getXyzFile(moleculeName);

        List<Atom> atoms = xyzFile
                .getAtoms()
                .stream()
                .map(xyzAtom -> new Atom(xyzAtom.getName(), xyzAtom.getX(), xyzAtom.getY(), xyzAtom.getZ()))
                .collect(Collectors.toList());

        return new Molecule(xyzFile, atoms, atomTypes);
    }
}
