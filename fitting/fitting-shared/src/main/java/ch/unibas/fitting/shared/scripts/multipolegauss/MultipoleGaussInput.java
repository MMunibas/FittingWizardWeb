/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.scripts.multipolegauss;

import ch.unibas.fitting.shared.directories.MtpFitDir;
import org.apache.commons.lang.StringUtils;

/**
 * User: mhelmer
 * Date: 28.11.13
 * Time: 14:29
 */
public class MultipoleGaussInput {
    private final MtpFitDir mtpFitDir;
    private final String moleculeName;
    private final int netCharge;
    private final String quantChemDetails;
    private final int nCores;
    private final int state;

    public MultipoleGaussInput(MtpFitDir mtpFitDir,
                               String moleculeName,
                               int netCharge,
                               String quantChemDetails,
                               int nCores,
                               int state) {
        this.mtpFitDir = mtpFitDir;
        if (StringUtils.isEmpty(moleculeName))
            throw new IllegalArgumentException("moleculeName is empty");
        if (StringUtils.isEmpty(quantChemDetails))
            throw new IllegalArgumentException("quantChemDetails is empty");

        this.moleculeName = moleculeName;
        this.netCharge = netCharge;
        this.quantChemDetails = quantChemDetails;
        this.nCores = nCores;
        this.state = state;
    }

    public String getMoleculeName() {
        return moleculeName;
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

    public MtpFitDir getMtpFitDir() {
        return mtpFitDir;
    }
}
