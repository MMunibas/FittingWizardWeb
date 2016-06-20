/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.workflows;

import ch.unibas.fitting.shared.directories.FitOutputDir;
import ch.unibas.fitting.shared.directories.MoleculesDir;
import ch.unibas.fitting.shared.fitting.Fit;
import java.io.File;

/**
 * User: mhelmer
 * Date: 16.12.13
 * Time: 10:14
 */
public class ExportFitInput {
    private final Fit fit;
    private FitOutputDir fitOutputDir;
    private MoleculesDir moleculesDir;
    private final File destination;

    public ExportFitInput(FitOutputDir fitOutputDir,
                          MoleculesDir moleculesDir,
                          Fit fit,
                          File destination) {
        this.fitOutputDir = fitOutputDir;
        this.moleculesDir = moleculesDir;
        this.destination = destination;
        this.fit = fit;
    }

    public FitOutputDir getFitOutputDir() {
        return fitOutputDir;
    }

    public MoleculesDir getMoleculesDir() {
        return moleculesDir;
    }

    public Fit getFit() {
        return fit;
    }

    public File getDestination() {
        return destination;
    }
}
