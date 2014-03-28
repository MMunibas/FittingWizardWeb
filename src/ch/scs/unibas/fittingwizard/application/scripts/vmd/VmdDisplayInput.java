package ch.scs.unibas.fittingwizard.application.scripts.vmd;

import ch.scs.unibas.fittingwizard.application.molecule.MoleculeId;

public class VmdDisplayInput {
    private final int fitRank;
    private final MoleculeId moleculeId;
    private final int fitId;

    public VmdDisplayInput(MoleculeId moleculeId, int fitRank, int fitId) {
        this.moleculeId = moleculeId;
        this.fitRank = fitRank;
        this.fitId = fitId;
    }

    public MoleculeId getMoleculeId() {
        return moleculeId;
    }

	public int getFitRank() {
		return fitRank;
	}
    
    public int getFitId() {
        return fitId;
    }
    
}
