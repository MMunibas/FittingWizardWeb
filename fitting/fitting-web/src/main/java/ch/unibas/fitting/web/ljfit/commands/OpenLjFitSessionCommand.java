package ch.unibas.fitting.web.ljfit.commands;

import ch.unibas.fitting.application.algorithms.ljfit.LjFitRepository;
import ch.unibas.fitting.web.ljfit.session.CreateNewSessionPage;
import ch.unibas.fitting.web.ljfit.fitting.step2.LjSessionPage;
import ch.unibas.fitting.web.misc.PageNavigation;

import javax.inject.Inject;

public class OpenLjFitSessionCommand {

    @Inject
    private LjFitRepository fitRepository;

    public void execute(String username) {
        boolean sessionExists = fitRepository.sessionExists(username);
        Class nextPage = sessionExists ? LjSessionPage.class : CreateNewSessionPage.class;
        PageNavigation.ToPage(nextPage);
    }
}
