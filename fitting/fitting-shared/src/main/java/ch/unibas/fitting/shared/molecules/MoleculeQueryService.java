/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.molecules;

import ch.unibas.fitting.shared.charges.ChargeTypes;
import ch.unibas.fitting.shared.fitting.ChargeValue;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Performs queries on a given list of molecules.
 * User: mhelmer
 * Date: 12.12.13
 * Time: 10:22
 */
public class MoleculeQueryService {

    private final ArrayList<Molecule> molecules;

    public MoleculeQueryService(List<Molecule> molecules) {
        this.molecules = new ArrayList<>(molecules);
    }

    public int getNumberOfMolecules() {
        return molecules.size();
    }

    public List<MoleculeId> getMoleculeIds() {
        ArrayList<MoleculeId> ids = molecules
                .stream()
                .map(Molecule::getId)
                .collect(Collectors.toCollection(ArrayList::new));
        return ids;
    }

    public List<Molecule> findMoleculesWithMissingUserCharges() {
        return findMoleculeWith(Molecule.UserChargesState.NoChargesDefined);
    }

    private List<Molecule> findMoleculeWith(Molecule.UserChargesState userChargesState) {
        ArrayList<Molecule> moleculeWithMissingUserCharges = molecules
                .stream()
                .filter(molecule -> molecule.getUserChargesState().equals(userChargesState))
                .collect(Collectors.toCollection(ArrayList::new));
        return moleculeWithMissingUserCharges;
    }

    public LinkedHashSet<ChargeValue> getUserChargesFromMoleculesWithCharges() {
        LinkedHashSet<ChargeValue> userCharges = new LinkedHashSet<>();

        for (Molecule molecule : findMoleculeWith(Molecule.UserChargesState.AllChargesDefined)) {
            userCharges.addAll(molecule.getAtomTypes()
                    .stream()
                    .map(atomType -> new ChargeValue(atomType.getId(), ChargeTypes.charge, atomType.getUserQ00()))
                    .collect(Collectors.toList()));
        }

        return userCharges;
    }

    public List<AtomTypeId> findUnequalAndDuplicateAtomTypes() {
        HashMap<AtomTypeId, ArrayList<Molecule>> duplicateAtomTypes = getAtomTypesMap(molecules);
        List<AtomTypeId> unequalAndDuplicates = getUnequalAndDuplicates(duplicateAtomTypes);

        return unequalAndDuplicates;
    }

    /**
     * Gets a map with all atom types and their corresponding molecule.
     * @param molecules
     * @return
     */
    private HashMap<AtomTypeId, ArrayList<Molecule>> getAtomTypesMap(ArrayList<Molecule> molecules) {
        HashMap<AtomTypeId, ArrayList<Molecule>> duplicateAtomTypes = new HashMap<>();
        for (Molecule molecule : molecules) {
            for (AtomType atomType : molecule.getAtomTypes()) {
                AtomTypeId atomTypeId = atomType.getId();
                ArrayList<Molecule> moleculesWithType = duplicateAtomTypes.get(atomTypeId);
                if (moleculesWithType == null) {
                    moleculesWithType = new ArrayList<>();
                    duplicateAtomTypes.put(atomTypeId, moleculesWithType);
                }

                if (!moleculesWithType.contains(molecule))
                    moleculesWithType.add(molecule);
            }
        }
        return duplicateAtomTypes;
    }

    /**
     * Gets all atom types which occur in multiple molecules and have different user charges defined.
     * @param duplicateAtomTypes
     * @return
     */
    private List<AtomTypeId> getUnequalAndDuplicates(HashMap<AtomTypeId, ArrayList<Molecule>> duplicateAtomTypes) {
        List<AtomTypeId> duplicates = new ArrayList<>();
        for (Map.Entry<AtomTypeId, ArrayList<Molecule>> entry : duplicateAtomTypes.entrySet()) {
            boolean isDuplicateAtomType = entry.getValue().size() > 1;
            if (isDuplicateAtomType && checkIfUserChargesAreDifferent(entry.getKey(), entry.getValue())) {
                duplicates.add(entry.getKey());
            }
        }
        return duplicates;
    }

    /**
     * Checks if the defined user charges of a given atom type is the same in all given molecules.
     * @param atomTypeId
     * @param moleculesWithType A list of molecules which contains the given atom type. Throws exception
     *                          if the atom type does not occur.
     * @return
     */
    public boolean checkIfUserChargesAreDifferent(AtomTypeId atomTypeId, List<Molecule> moleculesWithType) {
        if (moleculesWithType.size() < 2) {
            throw new RuntimeException("It makes no sense to check different charges for only one molecule.");
        }
        boolean chargesAreDifferent = false;
        boolean isReferenceInitialized = false;
        Double referenceValue = null;
        for (Molecule molecule : moleculesWithType) {
            Optional<AtomType> atomType = molecule.findAtomTypeById(atomTypeId);
            if (!atomType.isPresent()) {
                throw new RuntimeException("Atom type does not exist in molecule. Passed molecules are not filtered correctly.");
            }
            if (!isReferenceInitialized) {
                referenceValue = atomType.get().getUserQ00();
                isReferenceInitialized = true;
            } else {
                if (!Objects.equals(referenceValue, atomType.get().getUserQ00())) {
                    chargesAreDifferent = true;
                    break;
                }
            }
        }
        return chargesAreDifferent;
    }

    public List<Molecule> getMolecules() {
        return molecules.stream().collect(Collectors.toList());
    }
}
