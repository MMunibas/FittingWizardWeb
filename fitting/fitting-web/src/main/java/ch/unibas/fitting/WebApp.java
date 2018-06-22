package ch.unibas.fitting;

import ch.unibas.fitting.web.misc.SessionCounter;
import ch.unibas.fitting.web.misc.UserSession;
import ch.unibas.fitting.web.welcome.WelcomePage;
import com.google.inject.Injector;
import de.agilecoders.wicket.core.Bootstrap;
import de.agilecoders.wicket.core.settings.*;
import de.agilecoders.wicket.themes.markup.html.bootswatch.BootswatchTheme;
import de.agilecoders.wicket.themes.markup.html.bootswatch.BootswatchThemeProvider;
import org.apache.log4j.Logger;
import org.apache.wicket.Page;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.Session;
import org.apache.wicket.guice.GuiceComponentInjector;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;

import javax.inject.Inject;
import java.util.Locale;

/**
 * Created by martin on 29.05.2016.
 */
public class WebApp extends WebApplication {

    private static final Logger LOGGER = Logger.getLogger(WebApp.class);

    private final Injector injector;
    private final SessionCounter counter;
    private final Settings settings;

    @Inject
    public WebApp(Injector injector, SessionCounter counter, Settings settings) {
        this.injector = injector;
        this.counter = counter;
        this.settings = settings;
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return WelcomePage.class;
    }

    @Override
    protected void init() {
        super.init();
        IBootstrapSettings set = new BootstrapSettings();
        set.setThemeProvider(new BootswatchThemeProvider(BootswatchTheme.Yeti));
        Bootstrap.install(this, set);
        getComponentInstantiationListeners().add(new GuiceComponentInjector(this, injector));
    }

    @Override
    public Session newSession(Request request, Response response) {
        UserSession s = new UserSession(request);
        s.setLocale(Locale.ENGLISH);
        if (settings.isDebuggingMode()) {
            s.setDebuggingMode(true);
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
        if (settings.isDebuggingMode())
            return RuntimeConfigurationType.DEVELOPMENT;
        return RuntimeConfigurationType.DEPLOYMENT;
    }
}
