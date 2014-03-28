package ch.scs.unibas.fittingwizard.application.scripts.fitmtp;

import ch.scs.unibas.fittingwizard.application.fitting.InitialQ00;
import ch.scs.unibas.fittingwizard.application.molecule.MoleculeId;

import java.io.File;
import java.util.List;

/**
 * User: mhelmer
 * Date: 06.12.13
 * Time: 11:54
 */
public class FitMtpInput {
    private final int fitId;
    private final double convergence;
    private final int rank;
    private final boolean ignoreHydrongen;
    private final File initalCharges;
    private final List<MoleculeId> moleculesForFit;

    public FitMtpInput(int fitId,
                       double convergence,
                       int rank,
                       boolean ignoreHydrogen,
                       File initalChargesFile,
                       List<MoleculeId> moleculesForFit) {
        this.fitId = fitId;
        this.convergence = convergence;
        this.rank = rank;
        this.ignoreHydrongen = ignoreHydrogen;
        this.initalCharges = initalChargesFile;
        this.moleculesForFit = moleculesForFit;
    }

    public int getFitId() {
        return fitId;
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

    public File getInitalChargesFile() {
        return initalCharges;
    }

    public List<MoleculeId> getMoleculesForFit() {
        return moleculesForFit;
    }
}
