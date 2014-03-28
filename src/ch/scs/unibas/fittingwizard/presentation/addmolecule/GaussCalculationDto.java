package ch.scs.unibas.fittingwizard.presentation.addmolecule;

import ch.scs.unibas.fittingwizard.application.scripts.multipolegauss.MultipoleGaussInput;
import ch.scs.unibas.fittingwizard.application.xyz.XyzFile;

/**
 * Created with IntelliJ IDEA.
 * User: mhelmer
 * Date: 07.12.13
 * Time: 11:49
 * To change this template use File | Settings | File Templates.
 */
public class GaussCalculationDto {
    private final MultipoleGaussInput multipoleGaussInput;
    private final XyzFile xyzFile;

    public GaussCalculationDto(MultipoleGaussInput multipoleGaussInput, XyzFile xyzFile) {
        this.multipoleGaussInput = multipoleGaussInput;
        this.xyzFile = xyzFile;
    }

    public MultipoleGaussInput getMultipoleGaussInput() {
        return multipoleGaussInput;
    }

    public XyzFile getXyzFile() {
        return xyzFile;
    }
}
