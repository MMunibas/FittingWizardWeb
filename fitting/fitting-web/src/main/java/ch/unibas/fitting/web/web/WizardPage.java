package ch.unibas.fitting.web.web;

import ch.unibas.fitting.web.application.IBackgroundTasks;
import ch.unibas.fitting.web.application.TaskHandle;
import ch.unibas.fitting.web.web.progress.ProgressPage;
import ch.unibas.fitting.web.welcome.NewSessionPage;
import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import javax.inject.Inject;
import java.util.Optional;

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

        Optional<TaskHandle> th = tasks.getHandleForUser(getCurrentUsername());
        if (    th.isPresent() &&
                !this.getClass().equals(ProgressPage.class)) {
            PageParameters pp = new PageParameters();
            pp.add("task_id", th.get().getId());
            setResponsePage(ProgressPage.class, pp);
            LOGGER.debug("User "+ getCurrentUsername() + " has running task. Redirecting to ProgressPage");
            return;
        }
    }
}
