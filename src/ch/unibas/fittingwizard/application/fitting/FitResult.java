/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.application.fitting;

import ch.unibas.fittingwizard.application.molecule.AtomTypeId;
import ch.unibas.fittingwizard.application.molecule.MoleculeId;
import ch.unibas.fittingwizard.application.tools.charges.ChargeTypes;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * This represents the fit result for on atom type.
 * User: mhelmer
 * Date: 03.12.13
 * Time: 15:21
 */
public class FitResult {
    private final List<MoleculeId> moleculeIds;
    private final List<ChargeValue> chargeValues;
    private final AtomTypeId atomTypeId;
    private final double initialQ;

    public FitResult(AtomTypeId atomTypeId, List<MoleculeId> moleculeIds, double initialQ, OutputAtomType outputAtomType) {
        if (!atomTypeId.equals(outputAtomType.getId())) {
             throw new IllegalArgumentException("output atom type's id does not match atom type id.");
        }
        this.atomTypeId = atomTypeId;
        this.moleculeIds = moleculeIds;
        this.initialQ = initialQ;
        this.chargeValues = outputAtomType.getChargeValues();
    }

    /**
     * Gets the list of molecule ids the atom type is contained in.
     * @return
     */
    public List<MoleculeId> getMoleculeIds() {
        return moleculeIds;
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
        List<Double> result = new ArrayList<>();
        for (ChargeValue chargeValue : chargeValues) {
            if (StringUtils.startsWithIgnoreCase(chargeValue.getType(), prefix)) {
                result.add(chargeValue.getValue());
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
}
