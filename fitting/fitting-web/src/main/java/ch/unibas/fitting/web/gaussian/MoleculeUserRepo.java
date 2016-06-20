package ch.unibas.fitting.web.gaussian;

import ch.unibas.fitting.shared.molecules.Molecule;
import ch.unibas.fitting.shared.molecules.MoleculeId;
import ch.unibas.fitting.shared.molecules.MoleculeRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mhelmer-mobile on 17.06.2016.
 */
public class MoleculeUserRepo {

    private final HashMap<String, MoleculeRepository> repos = new HashMap<>();

    public void save(String username, Molecule molecule) {
        getRepoFor(username).save(molecule);
    }

    public List<Molecule> loadAllI(String username) {
        if (!repos.containsKey(username))
            return new ArrayList<>();
        return repos.get(username).loadAll();
    }

    public void remove(String username, String moleculeName) {
        MoleculeRepository rep = getRepoFor(username);
        if (rep == null)
            return;
        Molecule mol = rep.findById(new MoleculeId(moleculeName));
        rep.remove(mol);
    }

    public MoleculeRepository getRepoFor(String username) {
        if (!repos.containsKey(username))
            repos.put(username, new MoleculeRepository());
        return repos.get(username);
    }
}
