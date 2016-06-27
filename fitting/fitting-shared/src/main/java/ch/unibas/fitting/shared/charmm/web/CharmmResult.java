package ch.unibas.fitting.shared.charmm.web;

import ch.unibas.fitting.shared.charmm.generate.outputs.CHARMM_Output_GasPhase;
import ch.unibas.fitting.shared.charmm.generate.outputs.CHARMM_Output_PureLiquid;

import java.io.File;

/**
 * Created by mhelmer on 27.06.2016.
 */
public class CharmmResult {
    private final CHARMM_Output_GasPhase gasOut;
    private final CHARMM_Output_PureLiquid pureLiquidOut;

    public CharmmResult(CHARMM_Output_GasPhase gasOut, CHARMM_Output_PureLiquid pureLiquidOut) {
        this.gasOut = gasOut;
        this.pureLiquidOut = pureLiquidOut;
    }

    public boolean hasGasPhaseError() {
        return gasOut.getErrorOccured();
    }

    public boolean hasLiquidPhaseError() {
        return pureLiquidOut.getErrorOccured();
    }

    public File getGasPhaseOutput() {
        return gasOut.getFile();
    }

    public File getLiquirdPhaseOutput() {
        return pureLiquidOut.getFile();
    }
}
