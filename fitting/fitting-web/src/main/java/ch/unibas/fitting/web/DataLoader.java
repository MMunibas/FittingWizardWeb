package ch.unibas.fitting.web;

import ch.unibas.fitting.shared.config.Settings;
import ch.unibas.fitting.shared.directories.IUserDirectory;
import ch.unibas.fitting.shared.directories.MoleculesDir;
import ch.unibas.fitting.shared.directories.XyzDirectory;
import ch.unibas.fitting.shared.molecules.Molecule;
import ch.unibas.fitting.shared.workflows.gaussian.MoleculeCreator;
import ch.unibas.fitting.web.gaussian.MoleculeUserRepo;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.io.File;

/**
 * Created by mhelmer on 20.06.2016.
 */
public class DataLoader {
    private static final Logger LOGGER = Logger.getLogger(DataLoader.class);
    private File dataDir;
    private final Settings settings;
    private IUserDirectory userDirectory;
    private MoleculeCreator creator;
    private final MoleculeUserRepo repo;

    @Inject
    public DataLoader(Settings settings,
                      IUserDirectory userDirectory,
                      MoleculeCreator creator,
                      MoleculeUserRepo repo) {
        dataDir = settings.getDataDir();
        this.settings = settings;
        this.userDirectory = userDirectory;
        this.creator = creator;
        this.repo = repo;
    }

    public void loadExistingData() {
        LOGGER.info("Loading existing user data from " + dataDir);

        userDirectory.listAllUserDirs().forEach(username -> {

            MoleculesDir moleculesDir = userDirectory.getMoleculesDir(username);
            XyzDirectory xyzDirectory = userDirectory.getXyzDir(username);

            moleculesDir.listAllMolecules().forEach(moleculeName -> {

                LOGGER.info("Found " + moleculeName + " for user " + username);
                Molecule mol = creator.createMolecule(moleculesDir, xyzDirectory, moleculeName);
                repo.save(username, mol);
            });
        });

        LOGGER.info("Loading existing user data DONE.");
    }
}
