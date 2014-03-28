package ch.scs.unibas.fittingwizard.application.molecule;

import ch.scs.unibas.fittingwizard.application.base.MemoryRepository;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

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
