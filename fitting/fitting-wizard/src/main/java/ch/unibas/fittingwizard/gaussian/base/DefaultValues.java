/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.gaussian.base;

import ch.unibas.fitting.shared.config.Settings;

/**
 * User: mhelmer
 * Date: 28.11.13
 * Time: 14:55
 */
public class DefaultValues {

    private int netCharge;
    private String quantumChemicalDetails;
    private int numberOfCores;
    private String state;

    private double monopoleConvergence;
    private boolean ignoreHydrogen;
    private int rank;

    public DefaultValues(Settings settings) {
        initializeValues(settings);
    }

    private void initializeValues(Settings settings) {
        netCharge = Integer.parseInt(settings.getValue("default.net_charge"));
        quantumChemicalDetails = settings.getValue("default.quantum_chemical_details");
        numberOfCores = Integer.parseInt(settings.getValue("default.number_of_cores"));
        state = settings.getValue("default.state");

        monopoleConvergence = Double.parseDouble(settings.getValue("default.monopole_convergence"));
        ignoreHydrogen = Boolean.parseBoolean(settings.getValue("default.ignore_hydrogen"));
        rank = Integer.parseInt(settings.getValue("default.rank"));
    }

    public int getNetCharge() {
        return netCharge;
    }

    public String getQuantumChemicalDetails() {
        return quantumChemicalDetails;
    }

    public int getNumberOfCores() {
        return numberOfCores;
    }

    public String getState() {
        return state;
    }

    public double getMonopoleConvergence() {
        return monopoleConvergence;
    }

    public boolean getIgnoreHydrogen() {
        return ignoreHydrogen;
    }

    public int getRank() {
        return rank;
    }
}
