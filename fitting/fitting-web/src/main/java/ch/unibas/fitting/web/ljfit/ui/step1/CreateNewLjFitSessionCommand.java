package ch.unibas.fitting.web.ljfit.ui.step1;

import ch.unibas.fitting.web.ljfit.ui.commands.OpenLjFitSessionCommand;

import javax.inject.Inject;

public class CreateNewLjFitSessionCommand {

    @Inject
    private OpenLjFitSessionCommand openLjFitSession;

    public void execute(String username) {
        // remove old
        // bootstrap new
        // do whatever it takes...

        openLjFitSession.execute(username);
    }
}
