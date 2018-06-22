/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.application.algorithms.mtpfit;

import io.vavr.collection.List;
import io.vavr.collection.Set;
import org.joda.time.DateTime;

public class Fit {
    private int id;
    private double rmse;
    private int rank;
    private DateTime created;
    private List<FitResult> fitResults;

    public Fit(int id,
               double rmse,
               int rank,
               List<FitResult> fitResults,
               DateTime created) {
        this.id = id;
        this.rmse = rmse;
        this.fitResults = fitResults;
        this.rank = rank;
        this.created = created;
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

    public Set<String> getAllMoleculeIds() {
        return fitResults.map(r -> r.getMoleculeNames())
                .flatMap(strings -> strings)
                .toSet();
    }

    public List<FitResult> getFitResults() {
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
        return fitResults.map(r -> r.getAbsDeviationOfQ());
    }

    private List<Double> getAllValuesWithPrefix(String chargeType) {
        return fitResults
                .flatMap(r -> r.findValuesByPrefix(chargeType));
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
