/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.fitting;

import ch.unibas.fitting.shared.base.MemoryRepository;
import ch.unibas.fitting.shared.molecules.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * User: mhelmer
 * Date: 03.12.13
 * Time: 14:45
 */
public class FitRepository extends MemoryRepository<Fit> {

    private final MoleculeRepository moleculeRepository;

    public FitRepository(MoleculeRepository moleculeRepository) {
        this.moleculeRepository = moleculeRepository;
    }

    public int getFitCount() {
        return loadAll().size();
    }

    public int getNextFitId() {
        return loadAll().size();
    }

    public Fit createAndSaveFit(double rmse, int rank, List<OutputAtomType> outputAtomTypes, InitialQ00 initialValues) {
        int fitId = getNextFitId();
        Fit fit = new Fit(fitId, rmse, rank, createFitResults(outputAtomTypes, initialValues));
        save(fit);
        return fit;
    }

    private ArrayList<FitResult> createFitResults(List<OutputAtomType> outputAtomTypes, InitialQ00 initialValues) {
        ArrayList<FitResult> results = new ArrayList<>();
        for (OutputAtomType outputAtomType : outputAtomTypes) {
            AtomTypeId atomTypeId = outputAtomType.getId();
            List<Molecule> molecules = moleculeRepository.findMoleculesWithAtomType(atomTypeId);

            double initialQ = getInialQ00(initialValues, atomTypeId);

            FitResult fitResult = new FitResult(atomTypeId,
                    getMoleculeIds(molecules),
                    initialQ,
                    outputAtomType);
            results.add(fitResult);
        }

        return results;
    }

    private Double getInialQ00(InitialQ00 initialValues, AtomTypeId atomTypeId) {
        for (ChargeValue initialValue : initialValues.getChargeValues()) {
            if (initialValue.getAtomTypeId().equals(atomTypeId))
                return initialValue.getValue();
        }
        throw new RuntimeException("Missing inital Q for atom type " + atomTypeId.getName());
    }

    private List<MoleculeId> getMoleculeIds(List<Molecule> molecules) {
        ArrayList<MoleculeId> ids = molecules.stream()
                .map(Molecule::getId)
                .collect(Collectors.toCollection(ArrayList::new));
        return ids;
    }

    private Double getVerifiedInitialQ(List<Molecule> molecules, AtomTypeId atomTypeId) {
        boolean isReferenceValueInitialized = false;
        Double referenceValue = 0.0;
        for (int i = 0; i < molecules.size(); i++) {
            Molecule molecule = molecules.get(i);
            Optional<AtomType> atomTypesInMolecule = molecule.findAtomTypeById(atomTypeId);
            boolean moleculeIsCandidate = atomTypesInMolecule != null;
            if (moleculeIsCandidate) {
                Double userCharge = atomTypesInMolecule.get().getUserQ00();
                if (!isReferenceValueInitialized) {
                    referenceValue = userCharge;
                    isReferenceValueInitialized = true;
                } else if (userCharge == null) {
                    throw new RuntimeException("Atom types occuring in multiple molecules must have charge defined by user.");
                } else if (userCharge != referenceValue) {
                    throw new RuntimeException("Atom types occuring in multiple molecules must have same charge defined by user.");
                }
            }
        }

        return referenceValue;
    }

    public Optional<Fit> findById(int fitId) {
        return loadAll().stream()
                .filter(fit -> fit.getId() == fitId)
                .findFirst();
    }
}
