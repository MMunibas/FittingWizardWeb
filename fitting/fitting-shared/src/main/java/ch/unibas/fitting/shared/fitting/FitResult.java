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
import ch.unibas.fitting.shared.tools.AtomTypeId;
import io.vavr.collection.List;
import org.apache.commons.lang.StringUtils;

public class FitResult {
    private final List<String> moleculeNames;
    private final List<ChargeValue> chargeValues;
    private final AtomTypeId atomTypeId;
    private final double initialQ;

    public FitResult(
            AtomTypeId atomTypeId,
            List<String> moleculeNames,
            double initialQ,
            OutputAtomType outputAtomType) {
        if (!atomTypeId.equals(outputAtomType.getId())) {
             throw new IllegalArgumentException("output atom type's id does not match atom type id.");
        }
        this.atomTypeId = atomTypeId;
        this.moleculeNames = moleculeNames;
        this.initialQ = initialQ;
        this.chargeValues = outputAtomType.getChargeValues();
    }

    /**
     * Gets the list of molecule ids the atom type is contained in.
     * @return
     */
    public List<String> getMoleculeNames() {
        return moleculeNames;
    }

    public AtomTypeId getAtomTypeId() {
        return atomTypeId;
    }

    /**
     * Gets the name of the atom type this fit results is for.
     * @return
     */
    public String getAtomTypeName() {
        return atomTypeId.getName();
    }

    public double getQ00() {
        return findValueByType(ChargeTypes.charge);
    }

    public double getInitialQ() {
        return initialQ;
    }

    public double getAbsDeviationOfQ() {
        return Math.abs(Math.abs(initialQ) - Math.abs(getQ00()));
    }

    public Double getChargeValue(String chargeType) {
        if (chargeType.equalsIgnoreCase(ChargeTypes.charge)) {
            return getQ00();
        }
        return findValueByType(chargeType);
    }

    public List<Double> findValuesByPrefix(String prefix) {
        prefix = getPrefix(prefix);
        List<Double> result = List.empty();
        for (ChargeValue chargeValue : chargeValues) {
            if (StringUtils.startsWithIgnoreCase(chargeValue.getType(), prefix)) {
                result = result.append(chargeValue.getValue());
            }
        }
        return result;
    }

    private String getPrefix(String chargeType) {
        return chargeType.substring(0, 2);
    }

    public Double findValueByType(String type) {
        ChargeValue result = null;
        for (ChargeValue chargeValue : chargeValues) {
            if (chargeValue.getType().equalsIgnoreCase(type)) {
                result = chargeValue;
                break;
            }
        }
        return result != null ? result.getValue() : null;
    }

    public List<ChargeValue> getChargeValues() {
        return chargeValues;
    }
}
