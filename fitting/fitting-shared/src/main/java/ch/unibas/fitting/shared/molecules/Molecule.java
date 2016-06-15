/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.molecules;

import ch.unibas.fitting.shared.xyz.XyzFile;
import java.util.ArrayList;
import java.util.LinkedHashSet;

/**
 * User: mhelmer
 * Date: 25.11.13
 * Time: 14:39
 */
public class Molecule {
    private final XyzFile xyzFile;
    private final MoleculeId id;
    private final ArrayList<Atom> atoms;
    private final ArrayList<AtomType> atomTypes;

    public Molecule(XyzFile xyzFile, ArrayList<Atom> atoms, ArrayList<AtomType> atomTypes) {
        this.xyzFile = xyzFile;
        this.id = new MoleculeId(xyzFile.getMoleculeName());
        this.atoms = atoms;
        this.atomTypes = atomTypes;
        verifyUserChargeState();
        verifyIndices();
    }

    private void verifyUserChargeState() {
        UserChargesState userChargesState = checkChargesState(atomTypes);
        if (userChargesState == UserChargesState.Invalid)
            throw new IllegalArgumentException("Either all or no user charges must be defined.");
    }

    private void verifyIndices() {
        int indicesCount = 0;
        for (AtomType atomType : atomTypes) {
            indicesCount += atomType.getIndices().length;
        }
        if (atoms.size() != indicesCount)
            throw new IllegalArgumentException("Atom count does not match atom charge count.");
    }

    public static UserChargesState checkChargesState(ArrayList<AtomType> charges) {
        UserChargesState state = UserChargesState.Invalid;

        int userChargesDefinedCount = 0;
        for (int i = 0; i < charges.size(); i++) {
            AtomType charge = charges.get(i);
            if (charge.getUserQ00() != null)
                userChargesDefinedCount++;
        }
        if (userChargesDefinedCount == 0)
            state = UserChargesState.NoChargesDefined;
        else if (userChargesDefinedCount == charges.size())
            state = UserChargesState.AllChargesDefined;

        return state;
    }

    public void setUserCharge(AtomTypeId atomTypeId, Double newCharge) {
        AtomType type = findAtomTypeById(atomTypeId);
        type.setUserQ0(newCharge);
    }

    public String getDescription() {
        return id.getDescription();
    }

    public boolean containsAtomType(AtomTypeId atomTypeId) {
        return findAtomTypeById(atomTypeId) != null;
    }

    public AtomType findAtomTypeById(AtomTypeId id) {
        for (AtomType atomType : atomTypes) {
            if (atomType.getId().equals(id))
                return atomType;
        }
        return null;
    }

    public UserChargesState getUserChargesState() {
        return checkChargesState(atomTypes);
    }

    public MoleculeId getId() {
        return id;
    }

    public XyzFile getXyzFile() {
        return xyzFile;
    }

    public ArrayList<Atom> getAtoms() {
        return atoms;
    }

    public ArrayList<AtomType> getAtomTypes() {
        return atomTypes;
    }

    @Override
    public String toString() {
        return "Molecule{" +
                "id=" + id +
                ", atoms=" + atoms +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Molecule molecule = (Molecule) o;

        if (!id.equals(molecule.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public LinkedHashSet<AtomTypeId> getAllAtomTypeIds() {
        LinkedHashSet<AtomTypeId> all = new LinkedHashSet<>();
        for (AtomType atomType : atomTypes) {
            all.add(atomType.getId());
        }
        return all;
    }

    public enum UserChargesState {
        Invalid,
        AllChargesDefined,
        NoChargesDefined
    }
}
