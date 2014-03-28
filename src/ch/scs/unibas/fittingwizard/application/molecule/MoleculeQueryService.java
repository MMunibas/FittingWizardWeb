package ch.scs.unibas.fittingwizard.application.molecule;

import ch.scs.unibas.fittingwizard.application.fitting.ChargeValue;
import ch.scs.unibas.fittingwizard.application.tools.charges.ChargeTypes;

import java.util.*;

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
        ArrayList<MoleculeId> ids = new ArrayList<>();
        for (Molecule molecule : molecules) {
            ids.add(molecule.getId());
        }
        return ids;
    }

    public List<Molecule> findMoleculesWithMissingUserCharges() {
        return findMoleculeWith(Molecule.UserChargesState.NoChargesDefined);
    }

    private List<Molecule> findMoleculeWith(Molecule.UserChargesState userChargesState) {
        ArrayList<Molecule> moleculeWithMissingUserCharges = new ArrayList<>();
        for (Molecule molecule : molecules) {
            if (molecule.getUserChargesState().equals(userChargesState)) {
                moleculeWithMissingUserCharges.add(molecule);
            }
        }
        return moleculeWithMissingUserCharges;
    }

    public LinkedHashSet<ChargeValue> getUserChargesFromMoleculesWithCharges() {
        LinkedHashSet<ChargeValue> userCharges = new LinkedHashSet<>();

        for (Molecule molecule : findMoleculeWith(Molecule.UserChargesState.AllChargesDefined)) {
            for (AtomType atomType : molecule.getAtomTypes()) {
                userCharges.add(new ChargeValue(atomType.getId(), ChargeTypes.charge, atomType.getUserQ00()));
            }
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
            AtomType atomType = molecule.findAtomTypeById(atomTypeId);
            if (atomType == null) {
                throw new RuntimeException("Atom type does not exist in molecule. Passed molecules are not filtered correctly.");
            }
            if (!isReferenceInitialized) {
                referenceValue = atomType.getUserQ00();
                isReferenceInitialized = true;
            } else {
                if (!Objects.equals(referenceValue, atomType.getUserQ00())) {
                    chargesAreDifferent = true;
                    break;
                }
            }
        }
        return chargesAreDifferent;
    }
}
