package ch.unibas.fitting.web;

import ch.unibas.fitting.shared.charges.ChargesFileGenerator;
import ch.unibas.fitting.shared.config.Settings;
import ch.unibas.fitting.shared.directories.IUserDirectory;
import ch.unibas.fitting.shared.directories.UserDirectory;
import ch.unibas.fitting.shared.scripts.babel.IBabelScript;
import ch.unibas.fitting.shared.scripts.babel.MockBabelScript;
import ch.unibas.fitting.shared.scripts.babel.RealBabelScript;
import ch.unibas.fitting.shared.scripts.export.IExportScript;
import ch.unibas.fitting.shared.scripts.export.MockExportScript;
import ch.unibas.fitting.shared.scripts.export.RealExportScript;
import ch.unibas.fitting.shared.scripts.fitmtp.IFitMtpScript;
import ch.unibas.fitting.shared.scripts.fitmtp.MockFitMtpScript;
import ch.unibas.fitting.shared.scripts.fitmtp.RealFitMtpScript;
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
import ch.unibas.fitting.shared.workflows.charmm.IGenerateInputWorkflow;
import ch.unibas.fitting.shared.workflows.charmm.MockGenerateInputWorkflow;
import ch.unibas.fitting.shared.workflows.charmm.RealGenerateInputWorkflow;
import ch.unibas.fitting.shared.workflows.gaussian.GaussianWorkflow;
import ch.unibas.fitting.shared.workflows.gaussian.MoleculeCreator;
import ch.unibas.fitting.shared.workflows.gaussian.RunGaussianWorkflow;
import ch.unibas.fitting.web.application.BackgroundTaskService;
import ch.unibas.fitting.web.application.IBackgroundTasks;
import ch.unibas.fitting.web.gaussian.MoleculeUserRepo;
import ch.unibas.fitting.web.gaussian.RemoveMoleculeCommand;
import ch.unibas.fitting.web.gaussian.fit.step1.RunFitCommand;
import ch.unibas.fitting.web.web.SessionCounter;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

/**
 * Created by mhelmer on 20.06.2016.
 */
public class WebModule extends AbstractModule {

    private WebSettings settings;

    public WebModule(WebSettings settings) {
        this.settings = settings;
    }

    @Override
    protected void configure() {
        bind(WebSettings.class).toInstance(settings);
        bind(DataLoader.class).in(Scopes.SINGLETON);
        // web app dependencies
        bind(IBackgroundTasks.class).to(BackgroundTaskService.class).in(Scopes.SINGLETON);
        bind(IUserDirectory.class).to(UserDirectory.class);

        bind(Settings.class).to(WebSettings.class);

        bind(SessionCounter.class).in(Scopes.SINGLETON);

        bind(MoleculeUserRepo.class).in(Scopes.SINGLETON);
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

            bind(IGenerateInputWorkflow.class).to(MockGenerateInputWorkflow.class).in(Scopes.SINGLETON);
        } else {
            bind(IBabelScript.class).to(RealBabelScript.class).in(Scopes.SINGLETON);
            bind(ILRAScript.class).to(RealLRAScript.class).in(Scopes.SINGLETON);
            bind(IFittabScript.class).to(RealFittabMarkerScript.class).in(Scopes.SINGLETON);

            bind(IGenerateInputWorkflow.class).to(RealGenerateInputWorkflow.class).in(Scopes.SINGLETON);
        }

        bind(LPunParser.class).in(Scopes.SINGLETON);
        bind(MoleculeCreator.class).in(Scopes.SINGLETON);
        bind(GaussianLogModifier.class).in(Scopes.SINGLETON);
        bind(Notifications.class).in(Scopes.SINGLETON);
        bind(ChargesFileGenerator.class).in(Scopes.SINGLETON);

        bind(RemoveMoleculeCommand.class).in(Scopes.SINGLETON);
        // fitting dependencies
        bind(RunFitCommand.class).in(Scopes.SINGLETON);

        if (settings.getMocksEnabled()) {
            bind(IFitMtpScript.class).to(MockFitMtpScript.class).in(Scopes.SINGLETON);
            bind(IExportScript.class).to(MockExportScript.class).in(Scopes.SINGLETON);
        } else {
            bind(IFitMtpScript.class).to(RealFitMtpScript.class).in(Scopes.SINGLETON);
            bind(IExportScript.class).to(RealExportScript.class).in(Scopes.SINGLETON);
        }
    }
}
