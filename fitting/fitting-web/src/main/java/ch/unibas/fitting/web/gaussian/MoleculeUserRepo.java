package ch.unibas.fitting.web.gaussian;

import ch.unibas.fitting.shared.molecules.Molecule;
import ch.unibas.fitting.shared.molecules.MoleculeId;
import ch.unibas.fitting.shared.molecules.MoleculeRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Created by mhelmer-mobile on 17.06.2016.
 */
public class MoleculeUserRepo {

    private final HashMap<String, MoleculeRepository> repos = new HashMap<>();

    public void save(String username, Molecule molecule) {
        getRepoFor(username).save(molecule);
    }

    public synchronized List<Molecule> loadAll(String username) {
        if (!repos.containsKey(username))
            return new ArrayList<>();
        return repos.get(username).loadAll();
    }

    public synchronized void remove(String username, String moleculeName) {
        MoleculeRepository rep = getRepoFor(username);
        if (rep == null)
            return;
        Molecule mol = rep.findById(new MoleculeId(moleculeName));
        rep.remove(mol);
    }

    public synchronized MoleculeRepository getRepoFor(String username) {
        if (!repos.containsKey(username))
            repos.put(username, new MoleculeRepository());
        return repos.get(username);
    }

    public synchronized Optional<Molecule> load(String username, String moleculeName) {
        return loadAll(username).stream()
                .filter(molecule -> molecule.getId().getName().equals(moleculeName))
                .findFirst();
    }
}
