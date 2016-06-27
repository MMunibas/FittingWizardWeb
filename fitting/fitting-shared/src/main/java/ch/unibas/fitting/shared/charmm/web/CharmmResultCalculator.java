package ch.unibas.fitting.shared.charmm.web;

/**
 * Created by tobias on 27.06.16.
 */
public class CharmmResultCalculator {

    public static ResultCalculatorOutput calculateResult(int nres, double mmass, double temp, CharmmResultParserOutput parserOutput) {
        Double density = mmass * nres / (PhysicalConstants.kBoltz * temp * parserOutput.getBox());
        Double deltaH = parserOutput.getEgas() - parserOutput.getEliq() + PhysicalConstants.kBoltz * temp;
        Double deltaG = (parserOutput.getSolvent_mtp() + parserOutput.getSolvent_vdw()) - (parserOutput.getGas_mtp() + parserOutput.getGas_vdw());

        return  new ResultCalculatorOutput(density, deltaH, deltaG);
    }
}
