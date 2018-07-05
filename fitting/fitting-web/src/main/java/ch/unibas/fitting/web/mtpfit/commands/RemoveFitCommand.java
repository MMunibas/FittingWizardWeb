package ch.unibas.fitting.web.mtpfit.commands;

import ch.unibas.fitting.application.directories.IUserDirectory;
import ch.unibas.fitting.application.IAmACommand;
import com.google.inject.Inject;

/**
 * Created by mhelmer on 30.06.2016.
 */
public class RemoveFitCommand implements IAmACommand {

    @Inject
    private IUserDirectory userDirectory;

    public void remove(String username, int index) {
        userDirectory.getMtpFitDir(username).removeFitResult(index);
    }
}
