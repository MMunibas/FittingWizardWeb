/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.scripts.vmd;

import ch.unibas.fitting.shared.molecules.MoleculeId;

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
