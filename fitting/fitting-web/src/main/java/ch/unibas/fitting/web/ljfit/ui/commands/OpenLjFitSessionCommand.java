package ch.unibas.fitting.web.ljfit.ui.commands;

import ch.unibas.fitting.web.ljfit.services.LjFitRepository;
import ch.unibas.fitting.web.ljfit.ui.step1.InputAssistantPage;
import ch.unibas.fitting.web.ljfit.ui.step2.LjSessionPage;
import org.apache.wicket.request.cycle.RequestCycle;

import javax.inject.Inject;

public class OpenLjFitSessionCommand {

    @Inject
    private LjFitRepository fitRepository;

    public void execute(String username) {
        boolean sessionExists = fitRepository.sessionExists(username);
        Class nextPage = sessionExists ? LjSessionPage.class : InputAssistantPage.class;
        RequestCycle.get().setResponsePage(nextPage);
    }
}
