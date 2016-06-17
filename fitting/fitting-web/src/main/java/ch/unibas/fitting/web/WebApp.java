package ch.unibas.fitting.web;

import ch.unibas.fitting.shared.config.Settings;
import ch.unibas.fitting.web.application.*;
import ch.unibas.fitting.web.gaussian.addmolecule.step4.ParameterPage;
import ch.unibas.fitting.web.web.SessionCounter;
import ch.unibas.fitting.web.web.UserSession;
import ch.unibas.fitting.web.welcome.WelcomePage;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import de.agilecoders.wicket.core.Bootstrap;
import org.apache.log4j.Logger;
import org.apache.wicket.Page;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.Session;
import org.apache.wicket.guice.GuiceComponentInjector;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;

import java.util.Locale;

/**
 * Created by martin on 29.05.2016.
 */
public class WebApp extends WebApplication {

    private static final Logger LOGGER = Logger.getLogger(WebApp.class);

    private SessionCounter counter = new SessionCounter();

    @Override
    public Class<? extends Page> getHomePage() {
        return WelcomePage.class;
    }

    @Override
    protected void init() {
        super.init();

        Bootstrap.install(this);

        getComponentInstantiationListeners().add(new GuiceComponentInjector(this, new AbstractModule() {
            @Override
            protected void configure() {

                bind(IBackgroundTasks.class).to(BackgroundTaskService.class).in(Scopes.SINGLETON);
                bind(IUserDirectory.class).to(UserDirectory.class);
                bind(WebSettings.class).toInstance(WebSettings.load());
                bind(SessionCounter.class).toInstance(counter);
            }
        }));
    }

    @Override
    public Session newSession(Request request, Response response) {
        UserSession s = new UserSession(request);
        s.setLocale(Locale.ENGLISH);
        if (Constants.IsDebuggingMode) {
            s.setDebuggingMode(Constants.IsDebuggingMode);
            s.setUsername("debugging-mode");
        }
        counter.track(s);
        return s;
    }

    @Override
    public void sessionUnbound(String sessionId) {
        counter.untrack(sessionId);
        super.sessionUnbound(sessionId);
    }

    @Override
    public RuntimeConfigurationType getConfigurationType() {
        return super.getConfigurationType();
    }
}
