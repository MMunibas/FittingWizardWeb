package ch.unibas.fitting.shared.charmm.web;

import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Generator_DGHydr;
import ch.unibas.fitting.shared.charmm.generate.outputs.CHARMM_Output_GasPhase;
import ch.unibas.fitting.shared.charmm.generate.outputs.CHARMM_Output_PureLiquid;
import ch.unibas.fitting.shared.directories.LjFitRunDir;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tschmidt on 27.06.2016.
 */
public class CharmmResultParser {

    private final static String find_natom = "Number of atoms";
    private final static String find_nres = "Number of residues";
    private final static String find_nconstr = "constraints will";
    private final static String find_temp = "FINALT =";

    private static Logger LOGGER = Logger.getLogger(CharmmResultParser.class);

    public static CharmmResultParserOutput parseOutput(LjFitRunDir dir) {
        return parseOutput(
                dir.getGasOutputFile(),
                dir.getSolventOutputFile(),
                dir.getGasVdwOutputFile(),
                dir.getGasMtpOutputFile(),
                dir.getSolvVdwOutputFile(),
                dir.getSolvMtpOutputFile()
        );
    }

    public static CharmmResultParserOutput parseOutput(
            File gasOutput,
            File solventOutput,
            File gasVdw,
            File gasMtp,
            File solvVdw,
            File solvMtp) {

        String gasText;
        String solventText;
        String gasVdwText;
        String gasMtpText;
        String solvVdwText;
        String solvMtpText;
        try {
            gasText = FileUtils.readFileToString(gasOutput);
            solventText = FileUtils.readFileToString(solventOutput);

            gasVdwText = FileUtils.readFileToString(gasVdw);
            gasMtpText = FileUtils.readFileToString(gasMtp);
            solvVdwText = FileUtils.readFileToString(solvVdw);
            solvMtpText = FileUtils.readFileToString(solvMtp);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse CHARMM output. File(s) missing", e);
        }

        CharmmResultParserOutput output = parseCharmmResult(gasText, solventText);

        output.setGas_vdw(parseDeltaG(gasVdwText));
        output.setGas_mtp(parseDeltaG(gasMtpText));
        output.setSolvent_vdw(parseDeltaG(solvVdwText));
        output.setSolvent_mtp(parseDeltaG(solvMtpText));

        LOGGER.debug("Parsed value: " +
                " nres: " + output.getNres() +
                " natoms: " + output.getNatom() +
                " nconstr: " + output.getNconstr() +
                " temp: " + output.getTemp() +
                " box: " + output.getBox() +
                " egas: " + output.getEgas() +
                " eliq: " + output.getEliq() +
                " gas_vdw: " + output.getGas_vdw() +
                " gas_mtp: " + output.getGas_mtp() +
                " solvent_mtp: " + output.getSolvent_mtp() +
                " solvent_vdw: " + output.getSolvent_mtp()
        );

        return output;
    }

    private static CharmmResultParserOutput parseCharmmResult(String gasOutput,
                                                              String solventOutput) {
        var output = new CharmmResultParserOutput();

        List<String> solventLines = splitOutFile(solventOutput.split("\n"));
        List<String> gasPhaseOut = splitOutFile(gasOutput.split("\n"));

        List<String> nat = findInArray(gasPhaseOut, find_natom);
        String[] n = nat.get(nat.size() - 1).split("\\s+");
        output.setNatom(Integer.valueOf(n[5]));

        List<String> Temptxt = findInArray(solventLines, find_temp);
        String[] T = Temptxt.get(Temptxt.size() - 1).split("\\s+");
        output.setTemp(Double.valueOf(T[3]));

        List<String> Restxt = findInArray(solventLines, find_nres);
        String[] NR = Restxt.get(Restxt.size() - 1).split("\\s+");
        output.setNres(Integer.valueOf(NR[10]));

        // will contain number of constraints
        List<String> constr = findInArray(gasPhaseOut, find_nconstr);
        String[] cons = constr.get(constr.size() - 1).split("\\s+");
        output.setNconstr(Integer.valueOf(cons[1]));

        //energy from gas phase
        List<String> averlist = findInArray(gasPhaseOut, "AVER>");
        String[] avL = averlist.get(averlist.size() - 1).split("\\s+");
        output.setEgas(Double.valueOf(avL[5]) + 0.5 * PhysicalConstants.kBoltz * output.getTemp() *
                (3.0 * output.getNatom() - 6 - output.getNconstr()));

        //energy from liquid phase
        List<String> averlist2 = findInArray(solventLines, "AVER>");
        String[] avL2 = averlist2.get(averlist2.size() - 1).split("\\s+");
        output.setEliq(Double.valueOf(avL2[5]) / output.getNres());

        // get all lines containing "AVER PRESS>"
        List<String> boxLen = findInArray(solventLines, "AVER PRESS>");
        //keep last column containing volume in cubic angstroems
        String[] L = boxLen.get(boxLen.size() - 1).split("\\s+");
        output.setBox(Double.valueOf(L[6]));

        return output;
    }

    private static Double parseDeltaG(String deltaGOutput) {
        List<String> output = splitOutFile(deltaGOutput.split("\n"));
        String line = findInArray(output, "kcal/mol").get(0);
        String text = line.split("\\s+")[4];
        Double value = Double.valueOf(text);
        return value;
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
