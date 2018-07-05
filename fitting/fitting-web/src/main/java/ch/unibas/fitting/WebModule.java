package ch.unibas.fitting;

import ch.unibas.fitting.application.directories.IUserDirectory;
import ch.unibas.fitting.application.directories.UserDirectory;
import com.google.inject.AbstractModule;

public class WebModule extends AbstractModule {

    private Settings settings;

    public WebModule(Settings settings) {
        this.settings = settings;
    }

    @Override
    protected void configure() {
        bind(Settings.class).toInstance(settings);
        bind(IUserDirectory.class).to(UserDirectory.class);
    }
}
