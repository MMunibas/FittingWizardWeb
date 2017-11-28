package ch.unibas.fitting.web.gaussian.services;

import ch.unibas.fitting.shared.fitting.Fit;
import ch.unibas.fitting.shared.fitting.FitResult;
import ch.unibas.fitting.shared.molecules.AtomType;
import ch.unibas.fitting.shared.presentation.gaussian.ColorCoder;
import ch.unibas.fitting.web.gaussian.addmolecule.step6.ChargesViewModel;
import ch.unibas.fitting.web.gaussian.fit.step1.FitViewModel;
import ch.unibas.fitting.web.gaussian.fit.step2.FitResultViewModel;
import io.vavr.collection.List;
import io.vavr.collection.Stream;
import io.vavr.control.Option;

import javax.inject.Inject;

public class ViewModelMapper {

    @Inject
    private ColorCoder colorCoder;
    @Inject
    private MtpFitSessionRepository repo;

    public List<FitViewModel> loadFits(String username) {
        return repo.loadAllFitResults(username)
                .map(fit -> new FitViewModel(fit))
                .toList();
    }

    public List<ChargesViewModel> loadUserCharges(String username) {
        String moleculeName = repo.getAnyMoleculeOfUser(username);
        return loadUserCharges(username, moleculeName);
    }

    public List<ChargesViewModel> loadUserCharges(String username, String moleculeName) {
        List<AtomCharge> userCharges = repo.loadUserCharges(username, moleculeName);

        return repo.loadLpunAtomTypes(username, moleculeName)
                .map(atomType -> new ChargesViewModel(
                        moleculeName,
                        atomType.getId().getName(),
                        atomType.getIndices(),
                        findUserCharge(userCharges, atomType)));
    }

    private Double findUserCharge(List<AtomCharge> userCharges, AtomType atomType) {
        return userCharges
                .find(atomCharge -> atomCharge.atomLabel.equals(atomType.getId().getName()))
                .map(atomCharge -> atomCharge.charge)
                .getOrElse((Double) null);
    }

    public List<FitResultViewModel> loadFitResults(String username, int fitId) {
        Option<Fit> fit = repo.loadFitResult(username, fitId);
        if (fit.isEmpty())
            return List.empty();

        return Stream.ofAll(fit.get().getFitResults())
                .map(fr -> new FitResultViewModel(colorCoder, fit.get(), fr))
                .toList();
    }
}
