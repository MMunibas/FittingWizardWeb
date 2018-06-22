package ch.unibas.fitting.application.algorithms.mtpfit;

import ch.unibas.fitting.application.directories.IUserDirectory;
import ch.unibas.fitting.application.directories.MoleculesDir;
import ch.unibas.fitting.infrastructure.JsonSerializer;
import io.vavr.collection.List;
import io.vavr.control.Option;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;

@Singleton
public class MtpFitSessionRepository {
    @Inject
    private IUserDirectory userDirectory;
    @Inject
    private JsonSerializer serializer;
    @Inject
    private MtpResultsParser mtpResultsParser;

    public synchronized List<ChargeValue> loadLpunAtomTypes(
            String username,
            String moleculeName){
        MoleculesDir moleculesDir = userDirectory.getMtpFitDir(username)
                .getMoleculeDir();

         var json = moleculesDir.getResultsJson(moleculeName);

         return mtpResultsParser.getLpunAtomTypes(json);
    }

    public synchronized List<AtomCharge> loadUserCharges(
            String username,
            String moleculeName) {
        File charges = userDirectory.getMtpFitDir(username)
                .getMoleculeDir()
                .getUserChargesFile(moleculeName);

        if (!charges.exists())
            return List.empty();

        return serializer.readJsonFile(charges, UserCharges.class)
                .map(userCharges -> userCharges.charges)
                .getOrElse(List.empty());
    }

    public synchronized void saveUserCharges(
            String username,
            UserCharges userCharges) {
        File file = userDirectory.getMtpFitDir(username)
                .getMoleculeDir()
                .getUserChargesFile(userCharges.moleculeName);

        serializer.writeJsonFile(file, userCharges);
    }

    public List<Fit> loadAllFitResults(String username) {
        return userDirectory.getMtpFitDir(username)
                .listAllFitResultFiles()
                .flatMap(file -> serializer.readJsonFile(file, Fit.class));
    }

    public void saveFitResult(String username, Fit result) {
        File output = userDirectory.getMtpFitDir(username)
                .getFitResultFile(result.getId());
        serializer.writeJsonFile(output, result);
    }

    public String getAnyMoleculeOfUser(String username) {
        return userDirectory.getMtpFitDir(username)
                .getMoleculeDir()
                .getAnyMoleculeName();
    }

    public boolean sessionExists(String username) {
        return userDirectory.getMtpFitDir(username)
                .sessionExists();
    }

    public Option<Fit> loadFitResult(String username, int fitId) {
        File f = userDirectory.getMtpFitDir(username)
                .getFitResultFile(fitId);
        if (!f.exists())
            return Option.none();

        return serializer.readJsonFile(f, Fit.class);
    }
}
