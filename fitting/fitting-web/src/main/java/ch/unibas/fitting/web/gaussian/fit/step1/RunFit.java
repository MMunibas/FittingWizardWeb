package ch.unibas.fitting.web.gaussian.fit.step1;

import ch.unibas.fitting.shared.scripts.fitmtp.FitMtpInput;
import ch.unibas.fitting.shared.scripts.fitmtp.FitMtpOutput;
import ch.unibas.fitting.shared.scripts.fitmtp.IFitMtpScript;
import ch.unibas.fitting.web.application.IAmAUsercase;
import ch.unibas.fitting.web.application.IBackgroundTasks;
import ch.unibas.fitting.web.application.TaskHandle;

import javax.inject.Inject;
import java.util.UUID;

/**
 * Created by mhelmer-mobile on 19.06.2016.
 */
public class RunFit implements IAmAUsercase {
    @Inject
    private IBackgroundTasks tasks;

    @Inject
    private IFitMtpScript fitScript;

    public UUID runFit(String username, FitMtpInput input) {
        TaskHandle<FitMtpOutput> output = tasks.execute(username, "", () -> {
            return fitScript.execute(input);
        });
        return output.getId();
    }
}
