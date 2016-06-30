package ch.unibas.fitting.web.gaussian.fit;

import ch.unibas.fitting.shared.directories.IUserDirectory;
import ch.unibas.fitting.web.application.IAmACommand;
import ch.unibas.fitting.web.gaussian.FitUserRepo;
import com.google.inject.Inject;

/**
 * Created by mhelmer on 30.06.2016.
 */
public class RemoveFitCommand implements IAmACommand {
    @Inject
    private FitUserRepo fitUserRepo;
    @Inject
    private IUserDirectory userDirectory;

    public void remove(String username, int index) {
        fitUserRepo.getRepoFor(username).remove(index);
        userDirectory.getFitOutputDir(username).removeFitResult(index);
    }
}
