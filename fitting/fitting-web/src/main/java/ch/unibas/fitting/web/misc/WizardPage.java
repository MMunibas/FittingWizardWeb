package ch.unibas.fitting.web.misc;

import ch.unibas.fitting.application.calculation.CalculationManagementClient;
import ch.unibas.fitting.web.welcome.NewSessionPage;
import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.WebPage;

import javax.inject.Inject;

/**
 * Created by martin on 04.06.2016.
 */
public abstract class WizardPage extends WebPage {

    protected final static Logger LOGGER = Logger.getLogger(WizardPage.class);

    @Inject
    private CalculationManagementClient managementClient;

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
            managementClient.getUsersTask(getCurrentUsername())
                    .peek(th -> PageNavigation.ToProgressForCalculation(th));
        }
    }
}
