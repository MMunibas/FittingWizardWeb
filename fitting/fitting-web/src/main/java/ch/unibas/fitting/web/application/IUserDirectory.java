package ch.unibas.fitting.web.application;

import java.io.File;
import java.util.function.Consumer;

/**
 * Created by mhelmer-mobile on 15.06.2016.
 */
public interface IUserDirectory {
    File getXyzFileName(String username, String name);

    void createUserdir(String username);
}
