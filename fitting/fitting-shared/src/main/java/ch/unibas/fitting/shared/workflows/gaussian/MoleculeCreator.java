package ch.unibas.fitting.shared.workflows.gaussian;

import ch.unibas.fitting.shared.directories.MoleculesDir;
import ch.unibas.fitting.shared.directories.XyzDirectory;
import ch.unibas.fitting.shared.molecules.Atom;
import ch.unibas.fitting.shared.molecules.AtomType;
import ch.unibas.fitting.shared.molecules.Molecule;
import ch.unibas.fitting.shared.tools.LPunParser;
import ch.unibas.fitting.shared.xyz.XyzFile;

import javax.inject.Inject;
import java.io.File;
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

    public Molecule createMolecule(MoleculesDir moleculesDir,
                                   XyzDirectory xyzDirectory,
                                   String moleculeName) {
        File lpun = moleculesDir.findLPunFileFor(moleculeName);
        File mtpFit = moleculesDir.findFitTabFileFor(moleculeName);
        ArrayList<AtomType> atomTypes = lPunParser.parse(lpun);

        XyzFile xyzFile = xyzDirectory.getXyzFile(moleculeName);

        List<Atom> atoms = xyzFile
                .getAtoms()
                .stream()
                .map(xyzAtom -> new Atom(xyzAtom.getName(), xyzAtom.getX(), xyzAtom.getY(), xyzAtom.getZ()))
                .collect(Collectors.toList());

        return new Molecule(xyzFile, lpun, mtpFit, atoms, atomTypes);
    }
}
