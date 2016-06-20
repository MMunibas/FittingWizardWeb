/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.scripts.export;

import ch.unibas.fitting.shared.directories.FitOutputDir;
import ch.unibas.fitting.shared.directories.MoleculesDir;
import ch.unibas.fitting.shared.molecules.MoleculeId;

/**
 * User: mhelmer
 * Date: 13.12.13
 * Time: 15:12
 */
public class ExportScriptInput {
    private FitOutputDir fitOutputDir;
    private MoleculesDir moleculesDir;
    private final int fitId;
    private final MoleculeId moleculeId;

    public ExportScriptInput(FitOutputDir fitOutputDir,
                             MoleculesDir moleculesDir,
                             int fitId,
                             MoleculeId moleculeId) {
        this.fitOutputDir = fitOutputDir;
        this.moleculesDir = moleculesDir;
        this.fitId = fitId;
        this.moleculeId = moleculeId;
    }

    public FitOutputDir getFitOutputDir() {
        return fitOutputDir;
    }

    public MoleculesDir getMoleculesDir() {
        return moleculesDir;
    }

    public int getFitId() {
        return fitId;
    }

    public MoleculeId getMoleculeId() {
        return moleculeId;
    }
}
