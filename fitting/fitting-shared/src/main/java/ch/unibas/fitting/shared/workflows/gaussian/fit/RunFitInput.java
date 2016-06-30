package ch.unibas.fitting.shared.workflows.gaussian.fit;

import ch.unibas.fitting.shared.fitting.InitialQ00;
import ch.unibas.fitting.shared.scripts.fitmtp.FitMtpInput;

/**
 * Created by mhelmer on 30.06.2016.
 */
public class RunFitInput {
    private FitMtpInput fitMtpInput;
    private InitialQ00 initialQ00;

    public RunFitInput(InitialQ00 initialQ00, FitMtpInput fitMtpInput) {
        this.initialQ00 = initialQ00;
        this.fitMtpInput = fitMtpInput;
    }

    public FitMtpInput getFitMtpInput() {
        return fitMtpInput;
    }

    public InitialQ00 getInitialQ00() {
        return initialQ00;
    }
}
