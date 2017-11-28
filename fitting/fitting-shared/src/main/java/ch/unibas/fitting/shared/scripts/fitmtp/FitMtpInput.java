/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.scripts.fitmtp;

import ch.unibas.fitting.shared.directories.FitOutputDir;
import ch.unibas.fitting.shared.directories.MoleculesDir;
import io.vavr.collection.List;

import java.io.File;

/**
 * User: mhelmer
 * Date: 06.12.13
 * Time: 11:54
 */
public class FitMtpInput {
    private FitOutputDir fitOutputDir;
    private final double convergence;
    private final int rank;
    private final boolean ignoreHydrongen;
    private final File generatedCharges;
    private final List<File> mtpFitTabFiles;

    public FitMtpInput(FitOutputDir fitOutputDir,
                       double convergence,
                       int rank,
                       boolean ignoreHydrogen,
                       File generatedCharges,
                       List<File> mtpFitTabFiles) {
        this.fitOutputDir = fitOutputDir;
        this.convergence = convergence;
        this.rank = rank;
        this.ignoreHydrongen = ignoreHydrogen;
        this.generatedCharges = generatedCharges;
        this.mtpFitTabFiles = mtpFitTabFiles;
    }

    public FitOutputDir getFitOutputDir() {
        return fitOutputDir;
    }

    public double getConvergence() {
        return convergence;
    }

    public int getRank() {
        return rank;
    }

    public boolean isIgnoreHydrogen() {
        return ignoreHydrongen;
    }

    public File getGeneratedCharges() {
        return generatedCharges;
    }

    public List<File> getMtpFitTabFiles() {
        return mtpFitTabFiles;
    }
}
