package ch.scs.unibas.fittingwizard.application.workflows;

import ch.scs.unibas.fittingwizard.application.fitting.Fit;

import java.io.File;

/**
 * User: mhelmer
 * Date: 16.12.13
 * Time: 10:14
 */
public class ExportFitInput {
    private final Fit fit;
    private final File destination;

    public ExportFitInput(Fit fit, File destination) {
        this.destination = destination;
        this.fit = fit;
    }

    public Fit getFit() {
        return fit;
    }

    public File getDestination() {
        return destination;
    }
}
