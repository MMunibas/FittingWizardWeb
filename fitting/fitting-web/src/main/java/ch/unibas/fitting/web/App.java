package ch.unibas.fitting.web;

import ch.unibas.fitting.web.welcome.WelcomePage;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;

/**
 * Created by martin on 29.05.2016.
 */
public class App extends WebApplication {
    @Override
    public Class<? extends Page> getHomePage() {
        return WelcomePage.class;
    }
}
