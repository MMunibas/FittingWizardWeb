package ch.unibas.fitting.web.web;

import ch.unibas.fitting.web.application.task.IBackgroundTasks;
import ch.unibas.fitting.web.web.progress.ProgressPage;
import ch.unibas.fitting.web.welcome.NewSessionPage;
import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.WebPage;

import javax.inject.Inject;

/**
 * Created by martin on 04.06.2016.
 */
public abstract class WizardPage extends WebPage {

    protected final Logger LOGGER;

    @Inject
    private IBackgroundTasks tasks;

    protected WizardPage() {
        LOGGER = org.apache.log4j.Logger.getLogger(this.getClass());
    }

    public UserSession session() {
        return UserSession.current();
    }

    public String getCurrentUsername() {
        return session().getUsername();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        if (    !session().isDebuggingMode() &&
                !session().hasUserName() &&
                !this.getClass().equals(NewSessionPage.class)) {
            LOGGER.debug("No user name defined. Redirecting to NewSessionPage");
            setResponsePage(NewSessionPage.class);
            return;
        }

        if (!this.getClass().equals(ProgressPage.class)) {
            tasks.getHandleForUser(getCurrentUsername())
                    .peek(th -> PageNavigation.ToProgressForTask(th));
        }
    }
}
