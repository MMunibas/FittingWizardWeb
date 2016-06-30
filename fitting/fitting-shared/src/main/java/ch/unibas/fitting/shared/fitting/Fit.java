/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.fitting;

import ch.unibas.fitting.shared.charges.ChargeTypes;
import ch.unibas.fitting.shared.molecules.MoleculeId;
import org.joda.time.DateTime;

import java.io.File;
import java.util.*;

/**
 * User: mhelmer
 * Date: 03.12.13
 * Time: 14:48
 */
public class Fit {
    private int id;
    private double rmse;
    private ArrayList<FitResult> fitResults;
    private int rank;
    private final File resultsFile;
    private final File outputFile;
    private DateTime created;

    public Fit(int id,
               double rmse,
               int rank,
               ArrayList<FitResult> fitResults,
               File resultsFile,
               File outputFile) {
        this.id = id;
        this.rmse = rmse;
        this.fitResults = fitResults;
        this.rank = rank;
        this.resultsFile = resultsFile;
        this.outputFile = outputFile;
        this.created = DateTime.now();
    }

    public int getId() {
        return id;
    }

    public double getRmse() {
        return rmse;
    }
    
    public int getRank() {
    	return rank;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public File getResultsFile() {
        return resultsFile;
    }

    public LinkedHashSet<MoleculeId> getAllMoleculeIds() {
        LinkedHashSet<MoleculeId> ids = new LinkedHashSet<>();
        for (FitResult fitResult : fitResults) {
            ids.addAll(fitResult.getMoleculeIds());
        }
        return ids;
    }

    public ArrayList<FitResult> getFitResults() {
        return fitResults;
    }

    public double getMinAbsoluteDeviationFromInitialQ() {
        return findAbsoluteMinOf(getAllAbsDeviationOfQ());
    }

    public double getMaxAbsoluteDeviationFromInitialQ() {
        return findAbsoluteMaxOf(getAllAbsDeviationOfQ());
    }

    @Override
    public String toString() {
        return "Fit{" +
                "id=" + id +
                ", rmse=" + rmse +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Fit fit = (Fit) o;

        if (id != fit.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public double getAbsoluteMinValue(String chargeType) {
        if (chargeType.equalsIgnoreCase(ChargeTypes.charge)) {
            return getMinAbsoluteDeviationFromInitialQ();
        } else {
            return findAbsoluteMinOf(getAllValuesWithPrefix(chargeType));
        }
    }

    public double getAbsoluteMaxValue(String chargeType) {
        if (chargeType.equalsIgnoreCase(ChargeTypes.charge)) {
            return getMaxAbsoluteDeviationFromInitialQ();
        } else {
           return findAbsoluteMaxOf(getAllValuesWithPrefix(chargeType));
        }
    }

    private List<Double> getAllAbsDeviationOfQ() {
        List<Double> values = new ArrayList<>();
        for (FitResult fitResult : fitResults) {
            values.add(fitResult.getAbsDeviationOfQ());
        }
        return values;
    }

    private List<Double> getAllValuesWithPrefix(String chargeType) {
        List<Double> values = new ArrayList<>();
        for (FitResult fitResult : fitResults) {
            values.addAll(fitResult.findValuesByPrefix(chargeType));
        }
        return values;
    }

    private static double findAbsoluteMinOf(List<Double> values) {
        double minValue = Double.MAX_VALUE;
        for (double fitResult : values) {
            double absoluteValue = Math.abs(fitResult);
            if (absoluteValue < minValue) {
                minValue = absoluteValue;
            }
        }
        return minValue;
    }

    private static double findAbsoluteMaxOf(List<Double> values) {
        double maxValue = 0.0;
        for (double fitResult : values) {
            double absoluteValue = Math.abs(fitResult);
            if (absoluteValue > maxValue) {
                maxValue = absoluteValue;
            }
        }
        return maxValue;
    }

    public DateTime getCreated() {
        return created;
    }
}
