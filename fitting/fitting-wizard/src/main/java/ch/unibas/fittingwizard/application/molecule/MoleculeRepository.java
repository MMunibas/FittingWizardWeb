/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.application.molecule;

import ch.unibas.fittingwizard.application.base.MemoryRepository;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * User: mhelmer
 * Date: 25.11.13
 * Time: 15:13
 */
public class MoleculeRepository extends MemoryRepository<Molecule> {

    private static final Logger logger = Logger.getLogger(MoleculeRepository.class);

    public List<Molecule> findMoleculesWithAtomType(AtomTypeId atomTypeId) {
        ArrayList<Molecule> molecules = new ArrayList<>();
        for (Molecule molecule : loadAll()) {
            if (molecule.containsAtomType(atomTypeId)) {
                molecules.add(molecule);
            }
        }
        return molecules;
    }

    public Molecule findById(MoleculeId id) {
        for (Molecule molecule : loadAll()) {
            if (molecule.getId().equals(id)) {
                return molecule;
            }
        }
        return null;
    }

    public MoleculeQueryService getQueryServiceForAllMolecules() {
        return new MoleculeQueryService(loadAll());
    }

    public boolean checkIfExists(MoleculeId id) {
        return findById(id) != null;
    }
}
