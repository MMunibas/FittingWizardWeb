/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.scripts.vmd;


public class VmdDisplayInput {
    private final int fitRank;
    private final String moleculeName;
    private final int fitId;

    public VmdDisplayInput(String moleculeName, int fitRank, int fitId) {
        this.moleculeName = moleculeName;
        this.fitRank = fitRank;
        this.fitId = fitId;
    }

    public String getMoleculeName() {
        return moleculeName;
    }

	public int getFitRank() {
		return fitRank;
	}
    
    public int getFitId() {
        return fitId;
    }
    
}
