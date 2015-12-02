/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.application.base;

import ch.unibas.fittingwizard.application.fitting.ChargeValue;
import ch.unibas.fittingwizard.application.fitting.Fit;
import ch.unibas.fittingwizard.application.fitting.FitResult;
import ch.unibas.fittingwizard.application.fitting.OutputAtomType;
import ch.unibas.fittingwizard.application.molecule.*;
import ch.unibas.fittingwizard.application.xyz.XyzAtom;
import ch.unibas.fittingwizard.application.xyz.XyzFile;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * User: mhelmer
 * Date: 05.12.13
 * Time: 11:16
 */
public class DataGenerator {

    public static ArrayList<Atom> createCo2Atoms() {
        ArrayList<Atom> atoms = new ArrayList<>();
        atoms.add(new Atom("O", 1.09470, 0.02223, 0.00068));
        atoms.add(new Atom("C", 2.29155, 0.02223, 0.00068));
        atoms.add(new Atom("O", 3.48840, 0.02223, 0.00068));
        return atoms;
    }

    public static ArrayList<AtomType> createAtomCharges() {
        ArrayList<AtomType> charges = new ArrayList<>();

        charges.add(new AtomType("O1C2O1", new int[] { 0, 2 }));
        charges.add(new AtomType("C2O1O1", new int[] {1}));

        return charges;
    }

    public static XyzFile createXyzFile() {
        return new XyzFile(new File("co2.xyz"), 0, new ArrayList<XyzAtom>());
    }

    public static Molecule createCO2Molecule() {
        return new Molecule(createXyzFile(), createCo2Atoms(), createAtomCharges());
    }

    public static ArrayList<Molecule> createMolecules() {
        ArrayList<Molecule> molecules = new ArrayList<>();
        molecules.add(createCO2Molecule());
        return molecules;
    }

    public static OutputAtomType createC2O1O1OutputAtomType() {
        AtomTypeId atomTypeId = new AtomTypeId("C2O1O1");
        return new OutputAtomType(atomTypeId, Arrays.asList(
                new ChargeValue(atomTypeId, "Q00", 0.383238664493689),
                new ChargeValue(atomTypeId, "Q10", -0.000093497350474),
                new ChargeValue(atomTypeId, "Q20", -0.738538369568741)));
    }

    public static OutputAtomType createO1C2O1OutputAtomType() {
        AtomTypeId atomTypeId = new AtomTypeId("O1C2O1");
        return new OutputAtomType(atomTypeId, Arrays.asList(
                new ChargeValue(atomTypeId, "Q00", -0.191619332263909),
                new ChargeValue(atomTypeId, "Q10", -0.232693781160765),
                new ChargeValue(atomTypeId, "Q20", -0.825108549611139)));
    }

    public static FitResult createC2O1O1AtomType() {
        return new FitResult(new AtomTypeId("C2O1O1"), Arrays.asList(new MoleculeId("CO2")), 0.374621844024330, createC2O1O1OutputAtomType());
    }

    public static FitResult createO1C2O1AtomType() {
        return new FitResult(new AtomTypeId("O1C2O1"), Arrays.asList(new MoleculeId("CO2")), -0.187310922006895, createO1C2O1OutputAtomType());
    }

    public static Fit createEmptyFit() {
        ArrayList<FitResult> atoms = new ArrayList<>();
        ArrayList<Molecule> molecules = new ArrayList<>();
        return new Fit(0, 0.2840, 0, atoms);
    }

    public static Fit createCo2Fit(int id) {
        ArrayList<FitResult> atoms = new ArrayList<>();
        atoms.add(createC2O1O1AtomType());
        atoms.add(createO1C2O1AtomType());
        return new Fit(id, 0.2840, 2, atoms);
    }
}
