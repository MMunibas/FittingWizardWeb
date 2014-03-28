package ch.scs.unibas.fittingwizard.presentation.addmolecule;

import ch.scs.unibas.fittingwizard.application.molecule.Molecule;
import ch.scs.unibas.fittingwizard.application.xyz.XyzFile;

/**
 * User: mhelmer
 * Date: 06.12.13
 * Time: 16:43
 */
public class AtomChargesDto {

    private final XyzFile parsedXyzFile;
    private final Molecule molecule;

    public AtomChargesDto(Molecule molecule) {
        this.molecule = molecule;
        this.parsedXyzFile = molecule.getXyzFile();
    }

    public AtomChargesDto(XyzFile parsedXyzFile) {
        this.molecule = null;
        this.parsedXyzFile = parsedXyzFile;
    }

    public XyzFile getParsedXyzFile() {
        return parsedXyzFile;
    }

    public Molecule getMolecule() {
        return molecule;
    }
}
