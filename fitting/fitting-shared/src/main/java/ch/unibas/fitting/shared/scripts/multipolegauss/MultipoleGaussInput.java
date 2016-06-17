/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.scripts.multipolegauss;

import ch.unibas.fitting.shared.directories.MoleculesDir;
import ch.unibas.fitting.shared.xyz.XyzFile;

/**
 * User: mhelmer
 * Date: 28.11.13
 * Time: 14:29
 */
public class MultipoleGaussInput {
    private MoleculesDir moleculesDir;
    private XyzFile xyzFile;
    private final int netCharge;
    private final String quantChemDetails;
    private final int nCores;
    private final int state;

    public MultipoleGaussInput(MoleculesDir moleculesDir,
                               XyzFile xyzFile,
                               int netCharge,
                               String quantChemDetails,
                               int nCores,
                               int state) {
        this.moleculesDir = moleculesDir;
        this.xyzFile = xyzFile;
        this.netCharge = netCharge;
        this.quantChemDetails = quantChemDetails;
        this.nCores = nCores;
        this.state = state;
    }

    public XyzFile getXyzFile() {
        return xyzFile;
    }

    public int getNetCharge() {
        return netCharge;
    }

    public String getQuantChemDetails() {
        return quantChemDetails;
    }

    public int getnCores() {
        return nCores;
    }

    public int getState() {
        return state;
    }

    public MoleculesDir getMoleculesDir() {
        return moleculesDir;
    }

    public String getMoleculeName() {
        return xyzFile.getMoleculeName();
    }
}
