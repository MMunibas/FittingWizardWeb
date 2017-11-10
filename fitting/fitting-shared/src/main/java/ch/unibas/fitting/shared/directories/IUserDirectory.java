package ch.unibas.fitting.shared.directories;

import java.util.List;

/**
 * Created by mhelmer-mobile on 15.06.2016.
 */
public interface IUserDirectory {
    MoleculesDir getMoleculesDir(String username);
    FitOutputDir getFitOutputDir(String username);
    XyzDirectory getXyzDir(String username);

    LjFitSessionDir getLjFitSessionDir(String username);

    List<String> listAllUserDirs();

    boolean ljFitSessionDirectoryExists(String username);

    void deleteLjFitSession(String username);
}
