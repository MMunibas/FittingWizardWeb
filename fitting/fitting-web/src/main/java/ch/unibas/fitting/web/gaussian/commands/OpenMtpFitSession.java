package ch.unibas.fitting.web.gaussian.commands;

import ch.unibas.fitting.web.application.IAmACommand;
import ch.unibas.fitting.web.gaussian.addmolecule.step2.UploadPage;
import ch.unibas.fitting.web.gaussian.fit.step1.MtpFitSessionPage;
import ch.unibas.fitting.web.gaussian.services.MtpFitSessionRepository;
import ch.unibas.fitting.web.web.PageNavigation;

import javax.inject.Inject;

public class OpenMtpFitSession implements IAmACommand {

    @Inject
    private MtpFitSessionRepository mtpFitSessionRepository;

    public void execute(String username) {
        boolean sessionExists = mtpFitSessionRepository.sessionExists(username);
        Class nextPage = sessionExists ? MtpFitSessionPage.class : UploadPage.class;
        PageNavigation.ToPage(nextPage);
    }
}
