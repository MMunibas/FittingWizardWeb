package ch.unibas.fitting.web.web;

import ch.unibas.fitting.web.welcome.NewSessionPage;
import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.WebPage;

/**
 * Created by martin on 04.06.2016.
 */
public abstract class WizardPage extends WebPage {

    protected final Logger Logger;

    protected WizardPage() {
        Logger = org.apache.log4j.Logger.getLogger(this.getClass());
    }

    public UserSession session() {
        return UserSession.current();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        if (!session().hasUserName() && !this.getClass().equals(NewSessionPage.class)) {
            Logger.debug("No user name defined. Redirecting to NewSessionPage");
            setResponsePage(NewSessionPage.class);
        }
    }
}
