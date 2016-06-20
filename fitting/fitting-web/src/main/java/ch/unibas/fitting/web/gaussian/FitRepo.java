package ch.unibas.fitting.web.gaussian;

import ch.unibas.fitting.shared.fitting.Fit;
import ch.unibas.fitting.shared.fitting.FitRepository;
import ch.unibas.fitting.shared.molecules.MoleculeRepository;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Created by mhelmer-mobile on 19.06.2016.
 */
public class FitRepo {
    private final HashMap<String, FitRepository> repos = new HashMap<>();
    private MoleculeUserRepo moleculeRepo;

    @Inject
    public FitRepo(MoleculeUserRepo moleculeRepo) {
        this.moleculeRepo = moleculeRepo;
    }

    public void save(String username, Fit fit) {
        if (!repos.containsKey(username)) {
            MoleculeRepository repo = moleculeRepo.getRepoFor(username);
            repos.put(username, new FitRepository(repo));
        }
        repos.get(username).save(fit);
    }

    public List<Fit> loadAll(String username) {
        if (!repos.containsKey(username))
            return new ArrayList<>();
        return repos.get(username).loadAll();
    }

    public void remove(String username, int fitId) {
        FitRepository rep = repos.get(username);
        if (rep == null)
            return;
        Optional<Fit> fit = rep.findById(fitId);
        if (fit.isPresent())
            rep.remove(fit.get());
    }
}
