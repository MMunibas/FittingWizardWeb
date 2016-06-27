package ch.unibas.fitting.shared.charmm.web;

import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Generator_DGHydr;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tschmidt on 27.06.2016.
 */
public class CharmmResultParser {

    public static CharmmResultParserOutput parseOutput(CharmmResult result,
                                                       CHARMM_Generator_DGHydr gasVdw,
                                                       CHARMM_Generator_DGHydr gasMtp,
                                                       CHARMM_Generator_DGHydr solvVdw,
                                                       CHARMM_Generator_DGHydr solvMtp) {
        List<String> gasPhaseOut = splitOutFile(result.getGasPhaseOutput().getText().split("\n"));
        List<String> liquidPhaseOut = splitOutFile(result.getLiguidPhaseOutput().getText().split("\n"));

        Double gas_vdw = parseDeltaG(gasVdw);
        Double gas_mtp = parseDeltaG(gasMtp);
        Double solvent_vdw = parseDeltaG(solvVdw);
        Double solvent_mtp = parseDeltaG(solvMtp);

        return new CharmmResultParserOutput();
    }


    private static Double parseDeltaG(CHARMM_Generator_DGHydr deltaGOutput) {
        List<String> output = splitOutFile(deltaGOutput.getText().split("\n"));
        String line = findInArray(output, "kcal/mol").get(0);
        String value = line.split("\\s+")[4];
        return Double.valueOf(value);
    }

    private static List<String> splitOutFile(String[] Array) throws NullPointerException{
        List<String> arrList = new ArrayList<>();

        for (String st : Array) {
            arrList.add(st);
        }
        return arrList;
    }

    private static List<String> findInArray(List<String> arr, String pattern) throws NullPointerException{
        List<String> res = new ArrayList<>();

        for (String st : arr) {
            if (st.contains(pattern)) {
                res.add(st);
            }
        }
        return res;
    }
}
