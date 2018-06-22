package ch.unibas.fitting.application.directories;

import io.vavr.control.Option;

import java.io.File;

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

    File getUserBaseDir(String username);
}
