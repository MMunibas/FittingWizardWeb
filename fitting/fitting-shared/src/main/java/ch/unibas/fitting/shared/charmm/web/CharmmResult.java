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
    private final CharmmResultParserOutput output;

    public CharmmResult(CHARMM_Output_GasPhase gasOut,
                        CHARMM_Output_PureLiquid pureLiquidOut,
                        CharmmResultParserOutput output) {
        this.gasOut = gasOut;
        this.pureLiquidOut = pureLiquidOut;
        this.output = output;
    }

    public CharmmResultParserOutput getOutput() {
        return output;
    }

    public boolean hasGasPhaseError() {
        return gasOut.getErrorOccured();
    }

    public boolean hasLiquidPhaseError() {
        return pureLiquidOut.getErrorOccured();
    }

    public File getGasPhaseOutputFile() {
        return gasOut.getFile();
    }

    public File getLiquidPhaseOutputFile() {
        return pureLiquidOut.getFile();
    }

    public CHARMM_Output_GasPhase getGasPhaseOutput() { return gasOut; }

    public CHARMM_Output_PureLiquid getLiguidPhaseOutput() { return pureLiquidOut; }

}
