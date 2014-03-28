package ch.unibas.fittingwizard.application.scripts.export;

import ch.unibas.fittingwizard.application.molecule.MoleculeId;

/**
 * User: mhelmer
 * Date: 13.12.13
 * Time: 15:12
 */
public class ExportScriptInput {
    private final int fitId;
    private final MoleculeId moleculeId;

    public ExportScriptInput(int fitId, MoleculeId moleculeId) {
        this.fitId = fitId;
        this.moleculeId = moleculeId;
    }

    public int getFitId() {
        return fitId;
    }

    public MoleculeId getMoleculeId() {
        return moleculeId;
    }
}
