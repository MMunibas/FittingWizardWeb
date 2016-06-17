package ch.unibas.fitting.web;

import ch.unibas.fitting.shared.config.Settings;
import ch.unibas.fitting.shared.directories.IUserDirectory;
import ch.unibas.fitting.shared.directories.UserDirectory;
import ch.unibas.fitting.shared.scripts.babel.IBabelScript;
import ch.unibas.fitting.shared.scripts.babel.MockBabelScript;
import ch.unibas.fitting.shared.scripts.babel.RealBabelScript;
import ch.unibas.fitting.shared.scripts.fittab.IFittabScript;
import ch.unibas.fitting.shared.scripts.fittab.MockFittabMarkerScript;
import ch.unibas.fitting.shared.scripts.fittab.RealFittabMarkerScript;
import ch.unibas.fitting.shared.scripts.lra.ILRAScript;
import ch.unibas.fitting.shared.scripts.lra.MockLRAScript;
import ch.unibas.fitting.shared.scripts.lra.RealLRAScript;
import ch.unibas.fitting.shared.scripts.multipolegauss.IMultipoleGaussScript;
import ch.unibas.fitting.shared.scripts.multipolegauss.MockMultipoleGaussScript;
import ch.unibas.fitting.shared.scripts.multipolegauss.RealMultipoleGaussScript;
import ch.unibas.fitting.shared.tools.GaussianLogModifier;
import ch.unibas.fitting.shared.tools.LPunParser;
import ch.unibas.fitting.shared.tools.Notifications;
import ch.unibas.fitting.shared.workflows.gaussian.GaussianWorkflow;
import ch.unibas.fitting.shared.workflows.gaussian.RunGaussianWorkflow;
import ch.unibas.fitting.web.application.*;
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

    private final SessionCounter counter = new SessionCounter();
    private WebSettings settings;

    public WebApp(WebSettings settings) {
        this.settings = settings;
    }

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

                // web app dependencies
                bind(IBackgroundTasks.class).to(BackgroundTaskService.class).in(Scopes.SINGLETON);
                bind(IUserDirectory.class).to(UserDirectory.class);

                bind(WebSettings.class).toInstance(settings);
                bind(Settings.class).to(WebSettings.class);

                bind(SessionCounter.class).toInstance(counter);

                // gaussian dependencies
                bind(GaussianWorkflow.class).to(RunGaussianWorkflow.class).asEagerSingleton();

                if (settings.getUseGaussianMock()) {
                    bind(IMultipoleGaussScript.class).to(MockMultipoleGaussScript.class).in(Scopes.SINGLETON);
                } else {
                    bind(IMultipoleGaussScript.class).to(RealMultipoleGaussScript.class).in(Scopes.SINGLETON);
                }

                if (settings.getMocksEnabled()) {
                    bind(IBabelScript.class).to(MockBabelScript.class).in(Scopes.SINGLETON);
                    bind(ILRAScript.class).to(MockLRAScript.class).in(Scopes.SINGLETON);
                    bind(IFittabScript.class).to(MockFittabMarkerScript.class).in(Scopes.SINGLETON);
                } else {
                    bind(IBabelScript.class).to(RealBabelScript.class).in(Scopes.SINGLETON);
                    bind(ILRAScript.class).to(RealLRAScript.class).in(Scopes.SINGLETON);
                    bind(IFittabScript.class).to(RealFittabMarkerScript.class).in(Scopes.SINGLETON);
                }

                bind(LPunParser.class).in(Scopes.SINGLETON);
                bind(GaussianLogModifier.class).in(Scopes.SINGLETON);
                bind(Notifications.class).in(Scopes.SINGLETON);
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
