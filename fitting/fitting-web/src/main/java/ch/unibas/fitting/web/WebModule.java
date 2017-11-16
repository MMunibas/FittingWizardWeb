package ch.unibas.fitting.web;

import ch.unibas.fitting.shared.charmm.web.IRunCharmmWorkflowNew;
import ch.unibas.fitting.shared.charmm.web.MockRunCharmmWorkflowNew;
import ch.unibas.fitting.shared.charmm.web.RunCharmmWorkflowNew;
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
import ch.unibas.fitting.shared.workflows.charmm.IGenerateInputWorkflow;
import ch.unibas.fitting.shared.workflows.charmm.MockGenerateInputWorkflow;
import ch.unibas.fitting.shared.workflows.charmm.RealGenerateInputWorkflow;
import ch.unibas.fitting.web.application.BackgroundTaskService;
import ch.unibas.fitting.web.application.IBackgroundTasks;
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
        bind(Settings.class).to(WebSettings.class);
        bind(IBackgroundTasks.class).to(BackgroundTaskService.class).in(Scopes.SINGLETON);
        bind(IUserDirectory.class).to(UserDirectory.class);

        if (settings.getUseGaussianMock()) {
            bind(IMultipoleGaussScript.class).to(MockMultipoleGaussScript.class);
        } else {
            bind(IMultipoleGaussScript.class).to(RealMultipoleGaussScript.class);
        }

        if (settings.getMocksEnabled()) {
            bind(IBabelScript.class).to(MockBabelScript.class);
            bind(ILRAScript.class).to(MockLRAScript.class);
            bind(IFittabScript.class).to(MockFittabMarkerScript.class);

            bind(IGenerateInputWorkflow.class).to(MockGenerateInputWorkflow.class);
            bind(IRunCharmmWorkflowNew.class).to(MockRunCharmmWorkflowNew.class);
        } else {
            bind(IBabelScript.class).to(RealBabelScript.class);
            bind(ILRAScript.class).to(RealLRAScript.class);
            bind(IFittabScript.class).to(RealFittabMarkerScript.class);

            bind(IGenerateInputWorkflow.class).to(RealGenerateInputWorkflow.class);
            bind(IRunCharmmWorkflowNew.class).to(RunCharmmWorkflowNew.class);
        }

        if (settings.getMocksEnabled()) {
            bind(IFitMtpScript.class).to(MockFitMtpScript.class);
            bind(IExportScript.class).to(MockExportScript.class);
        } else {
            bind(IFitMtpScript.class).to(RealFitMtpScript.class);
            bind(IExportScript.class).to(RealExportScript.class);
        }
    }
}
