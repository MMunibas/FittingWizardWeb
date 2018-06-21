package ch.unibas.fitting.web;

import ch.unibas.fitting.web.application.directories.IUserDirectory;
import ch.unibas.fitting.web.application.directories.UserDirectory;
import ch.unibas.fitting.web.application.task.BackgroundTaskService;
import ch.unibas.fitting.web.application.task.IBackgroundTasks;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class WebModule extends AbstractModule {

    private Settings settings;

    public WebModule(Settings settings) {
        this.settings = settings;
    }

    @Override
    protected void configure() {
        bind(Settings.class).toInstance(settings);
        bind(IBackgroundTasks.class).to(BackgroundTaskService.class).in(Scopes.SINGLETON);
        bind(IUserDirectory.class).to(UserDirectory.class);
    }
}
