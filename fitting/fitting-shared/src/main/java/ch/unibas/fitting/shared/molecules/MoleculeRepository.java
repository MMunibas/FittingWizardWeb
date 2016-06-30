/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.molecules;

import ch.unibas.fitting.shared.base.MemoryRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

/**
 * User: mhelmer
 * Date: 25.11.13
 * Time: 15:13
 */
public class MoleculeRepository extends MemoryRepository<Molecule> {

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
