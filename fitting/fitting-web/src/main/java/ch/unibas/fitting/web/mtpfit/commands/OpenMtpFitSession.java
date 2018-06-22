package ch.unibas.fitting.web.mtpfit.commands;

import ch.unibas.fitting.application.IAmACommand;
import ch.unibas.fitting.web.mtpfit.session.step2.UploadPage;
import ch.unibas.fitting.web.mtpfit.fitting.step1.MtpFitSessionPage;
import ch.unibas.fitting.application.algorithms.mtpfit.MtpFitSessionRepository;
import ch.unibas.fitting.web.misc.PageNavigation;

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
