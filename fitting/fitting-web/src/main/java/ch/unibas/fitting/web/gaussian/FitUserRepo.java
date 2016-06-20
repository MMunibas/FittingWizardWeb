package ch.unibas.fitting.web.gaussian;

import ch.unibas.fitting.shared.fitting.Fit;
import ch.unibas.fitting.shared.fitting.FitRepository;
import ch.unibas.fitting.shared.fitting.InitialQ00;
import ch.unibas.fitting.shared.fitting.OutputAtomType;
import ch.unibas.fitting.shared.molecules.MoleculeRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Created by mhelmer-mobile on 19.06.2016.
 */
@Singleton
public class FitUserRepo {
    private final HashMap<String, FitRepository> repos = new HashMap<>();
    private MoleculeUserRepo moleculeRepo;

    public FitUserRepo() {}

    @Inject
    public FitUserRepo(MoleculeUserRepo moleculeRepo) {
        this.moleculeRepo = moleculeRepo;
    }

    public void save(String username, Fit fit) {
        getRepoFor(username).save(fit);
    }

    public List<Fit> loadAll(String username) {
        return getRepoFor(username).loadAll();
    }

    public void remove(String username, int fitId) {
        FitRepository rep = getRepoFor(username);
        if (rep == null)
            return;
        Optional<Fit> fit = rep.findById(fitId);
        if (fit.isPresent())
            rep.remove(fit.get());
    }

    public int getNextFitId(String username) {
        return getRepoFor(username).getNextFitId();
    }

    public FitRepository getRepoFor(String username) {
        if (!repos.containsKey(username)) {
            MoleculeRepository repo = moleculeRepo.getRepoFor(username);
            repos.put(username, new FitRepository(repo));
        }
        return repos.get(username);
    }

    public Fit createFit(String username, double rmse, int rank, List<OutputAtomType> atomTypes, InitialQ00 initialQs) {
        return getRepoFor(username).createAndSaveFit(rmse, rank, atomTypes, initialQs);
    }
}
