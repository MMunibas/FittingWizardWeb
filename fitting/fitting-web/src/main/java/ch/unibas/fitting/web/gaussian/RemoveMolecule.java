package ch.unibas.fitting.web.gaussian;

import ch.unibas.fitting.shared.directories.IUserDirectory;
import ch.unibas.fitting.web.application.IAmAUsercase;

import javax.inject.Inject;

/**
 * Created by mhelmer-mobile on 19.06.2016.
 */
public class RemoveMolecule implements IAmAUsercase {

    @Inject
    private IUserDirectory userDirectory;
    @Inject
    private MoleculeUserRepo moleculeRepo;

    public void remove(String username, String moleculeName) {
        userDirectory.getMoleculesDir(username).deleteMolecule(moleculeName);
        userDirectory.getXyzDir(username).deleteXyzFileFor(moleculeName);
        moleculeRepo.remove(username, moleculeName);
    }
}
