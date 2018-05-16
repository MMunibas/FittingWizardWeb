package ch.unibas.fitting.shared.directories;

import io.vavr.control.Option;

import java.util.List;
import java.util.Optional;

/**
 * Created by mhelmer-mobile on 15.06.2016.
 */
public interface IUserDirectory {

    MtpFitDir getMtpFitDir(String username);
    void deleteMtpFitDir(String username);

    LjFitSessionDir createLjFitSessionDir(String username);
    Option<LjFitSessionDir> getLjFitSessionDir(String username);
    boolean ljFitSessionDirectoryExists(String username);
    void deleteLjFitSession(String username);
}
