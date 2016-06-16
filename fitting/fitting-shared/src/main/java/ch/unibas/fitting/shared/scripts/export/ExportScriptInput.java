/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.scripts.export;

import ch.unibas.fitting.shared.molecules.MoleculeId;

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
