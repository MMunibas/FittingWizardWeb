/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.presentation.fitting;

import ch.unibas.fittingwizard.application.fitting.FitResult;
import ch.unibas.fittingwizard.application.fitting.Fit;
import ch.unibas.fittingwizard.application.molecule.AtomTypeId;
import ch.unibas.fittingwizard.application.molecule.MoleculeId;
import ch.unibas.fittingwizard.application.tools.charges.ChargeTypes;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Color;

/**
 * Represents a row in the fit results table.
 * User: mhelmer
 * Date: 03.12.13
 * Time: 15:24
 */
public class FitResultViewModel {
    private final ColorCoder coder;
    private FitResult fitResult;
    private final Fit fit;

    public FitResultViewModel(ColorCoder coder, FitResult fitResult, Fit fit) {
        this.coder = coder;
        this.fitResult = fitResult;
        this.fit = fit;
    }

    public boolean hasMolecule(MoleculeId moleculeId) {
        return fitResult.getMoleculeIds().contains(moleculeId);
    }

    public String getAtomTypeName() {
        return fitResult.getAtomTypeName();
    }

    public AtomTypeId getAtomTypeId() {
        return fitResult.getAtomTypeId();
    }

    public ObservableValue<FitValue> getChargeValueFor(String chargeType) {
        return new SimpleObjectProperty<>(new FitValue(chargeType));
    }

    public class FitValue {

        private final String chargeType;

        public FitValue(String chargeType) {
            this.chargeType = chargeType;
        }

        public String getValue() {
            Double value = fitResult.getChargeValue(chargeType);
            return getFormattedValue(value);
        }
        public Color getColor() {

            Color color = null;
            Double value = null;
            if (chargeType.equalsIgnoreCase(ChargeTypes.charge)) {
                value = fitResult.getAbsDeviationOfQ();
            } else {
                value = fitResult.getChargeValue(chargeType);
            }
            if (value != null) {
                double min = fit.getAbsoluteMinValue(chargeType);
                double max = fit.getAbsoluteMaxValue(chargeType);
                color = coder.getColor(min, max, Math.abs(value));
            }

            return color;
        }

        public String getToolTip() {
            String tooltip = null;
            if (chargeType.equalsIgnoreCase(ChargeTypes.charge)) {
                tooltip = "Deviation from initial charge: " + getFormattedValue(fitResult.getAbsDeviationOfQ()) + "\n" +
                        "Initial charge: " + getFormattedValue(fitResult.getInitialQ());
            } else {
                double min = fit.getAbsoluteMinValue(chargeType);
                double max = fit.getAbsoluteMaxValue(chargeType);
                tooltip = "Min absolute value: " + getFormattedValue(min) + "\n" +
                        "Max absolute value: " + getFormattedValue(max);
            }
            return tooltip;
        }

        private String getFormattedValue(Double value) {
            String formatted = value == null ? "" : String.format("%7.4f", value);
            return formatted;
        }
    }
}
