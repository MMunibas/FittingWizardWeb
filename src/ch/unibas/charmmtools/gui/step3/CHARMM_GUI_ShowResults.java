/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.gui.step3;

import au.com.bytecode.opencsv.CSVWriter;
import ch.unibas.charmmtools.gui.CHARMM_GUI_base;
import ch.unibas.charmmtools.gui.step2.CHARMM_GUI_ShowOutput;
import ch.unibas.charmmtools.gui.step1.CHARMM_GUI_InputAssistant;
import ch.unibas.charmmtools.generate.CHARMM_InOut;
import ch.unibas.charmmtools.generate.inputs.CHARMM_Generator_DGHydr;
import ch.unibas.charmmtools.generate.inputs.CHARMM_Input;
import ch.unibas.charmmtools.generate.outputs.CHARMM_Output;
import ch.unibas.charmmtools.generate.outputs.CHARMM_Output_GasPhase;
import ch.unibas.charmmtools.generate.outputs.CHARMM_Output_PureLiquid;
import ch.unibas.charmmtools.gui.step4.CHARMM_GUI_Fitgrid;
import ch.unibas.charmmtools.workflows.RunCHARMMWorkflow;
import ch.unibas.fittingwizard.presentation.base.ButtonFactory;
import ch.unibas.fittingwizard.presentation.base.dialog.OverlayDialog;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/**
 *
 * @author hedin
 */
public class CHARMM_GUI_ShowResults extends CHARMM_GUI_base {

    private static final String title = "LJ fitting procedure : Results";

    private Button backInputAssistant, backShowOutput, saveToFile, gotofitgrid;

    /*
     * Those values are parsed from the output file as the may be useful later
     */
    private double box, density;
    private final static String find_natom = "Number of atoms";
    private final static String find_nconstr = "constraints will";
    private int natom, nconstr;
    private double egas, eliq;
    private double deltaH;

    // boltzmann constant in kcal/mol-1/k-1
    private final static double kBoltz = 0.0019872041;
    private final static String find_temp = "FINALT =";
    private double temp;
    private final static String find_nres = "Number of residues";
    private int nres;
    private double mmass;

    private double dg;
    private double gas_mtp;
    private double gas_vdw;
    private double solvent_mtp;
    private double solvent_vdw;

    List<String> gasPhaseOut;
    List<String> pureLiqOut;

    @FXML
    private TextField temp_field, mmass_field, nres_field, dens_field, dhvap_field, dghydr_field;

    @FXML
    private Button calculate_b;

    public CHARMM_GUI_ShowResults(RunCHARMMWorkflow flow, List<CHARMM_InOut> ioList) throws Exception {
        super(title, flow);

        for (CHARMM_InOut ioListIt : ioList) {

            Class c = ioListIt.getClass();
            Class sc = c.getSuperclass();

            if (sc == CHARMM_Input.class) {

                inp.add((CHARMM_Input) ioListIt);

            } else if (sc == CHARMM_Output.class) {

                out.add((CHARMM_Output) ioListIt);

                if (c == CHARMM_Output_GasPhase.class) {
                    gasPhaseOut = splitOutFile(ioListIt.getText().split("\n"));
                } else if (c == CHARMM_Output_PureLiquid.class) {
                    pureLiqOut = splitOutFile(ioListIt.getText().split("\n"));
                }

            } else if (c == CHARMM_Generator_DGHydr.class) {

                try {
                    List<String> myDgString = splitOutFile(ioListIt.getText().split("\n"));
                    String myDgType = ioListIt.getType();
                    String line = findInArray(myDgString, "kcal/mol").get(0);
                    String value = line.split("\\s+")[4];

                    switch (myDgType) {
                        case "gas_vdw":
                            gas_vdw = Double.valueOf(value);
                            break;

                        case "gas_mtp":
                            gas_mtp = Double.valueOf(value);
                            break;

                        case "solvent_vdw":
                            solvent_vdw = Double.valueOf(value);
                            break;

                        case "solvent_mtp":
                            solvent_mtp = Double.valueOf(value);
                            break;

                        default:
                            break;
                    }
                } catch (IndexOutOfBoundsException ex) {
                    logger.info("Skipping object " + c.toString() + " : data missing : " + ex.getMessage());
                }

            } else {
                throw new UnknownError("Unknown type of object in List<CHARMM_InOut> : get "
                        + ioListIt.getClass() + " but expected types are " + CHARMM_Input.class
                        + " or " + CHARMM_Output.class);
            }
        }

        // get the output file for gas phase
//        gasPhaseOut = splitOutFile(out.get(0).getText().split("\n"));
        // get the output file for pure liquid
//        pureLiqOut = splitOutFile(out.get(1).getText().split("\n"));
        try{
            parse_required_data();
        }catch (NullPointerException e)
        {
            OverlayDialog.showError("Error while parsing CHARMM output files","Error while reading some terms  : " + this.work_directory.getAbsolutePath());
        }

    }

    @Override
    protected void fillButtonBar() {
        backInputAssistant = ButtonFactory.createButtonBarButton("Back to ρ and ΔH input assistant", new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                List<CHARMM_InOut> myList = new ArrayList<CHARMM_InOut>();
                myList.addAll(inp);
                myList.addAll(out);
                logger.info("Going back to CHARMM input assistant Step1.");
                navigateTo(CHARMM_GUI_InputAssistant.class, myList);
            }
        });
        addButtonToButtonBar(backInputAssistant);

        backShowOutput = ButtonFactory.createButtonBarButton("Back to ρ and ΔH output", new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                List<CHARMM_InOut> myList = new ArrayList<CHARMM_InOut>();
                myList.addAll(inp);
                myList.addAll(out);
                logger.info("Going to Step2 Results.");
                navigateTo(CHARMM_GUI_ShowOutput.class, myList);
            }
        });
        addButtonToButtonBar(backShowOutput);

//        backStep4 = ButtonFactory.createButtonBarButton("Back to ΔG solvation", new EventHandler<ActionEvent>(){
//            @Override
//            public void handle(ActionEvent actionEvent) {
//                List<CHARMM_InOut> myList = new ArrayList<CHARMM_InOut>();
//                myList.addAll(inp);
//                myList.addAll(out);
//                logger.info("Back to ΔG of solvation input assistant");
//                navigateTo(CHARMM_GUI_Step4.class,myList);
//            }
//        });
//        addButtonToButtonBar(backStep4);
        saveToFile = ButtonFactory.createButtonBarButton("Save to file", new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                logger.info("Saving to *.csv file");
                exportResultsCSV();
            }
        });
        addButtonToButtonBar(saveToFile);

        gotofitgrid = ButtonFactory.createButtonBarButton("Run using different LJ parameters", new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                logger.info("Going to grid of parameters");
                navigateTo(CHARMM_GUI_Fitgrid.class, null);
            }
        });
        addButtonToButtonBar(gotofitgrid);

    }

    private void parse_required_data() {

        List<String> nat = findInArray(gasPhaseOut, find_natom);
        String[] n = nat.get(nat.size() - 1).split("\\s+");
        natom = Integer.valueOf(n[5]);

        List<String> Temptxt = findInArray(pureLiqOut, find_temp);
        String[] T = Temptxt.get(Temptxt.size() - 1).split("\\s+");
        temp = Double.valueOf(T[3]);

        List<String> Restxt = findInArray(pureLiqOut, find_nres);
        String[] NR = Restxt.get(Restxt.size() - 1).split("\\s+");
        nres = Integer.valueOf(NR[10]);

        // will contain number of constraints
        List<String> constr = findInArray(gasPhaseOut, find_nconstr);
        String[] cons = constr.get(constr.size() - 1).split("\\s+");
        nconstr = Integer.valueOf(cons[1]);
//        logger.info("number of constraints : " + nconstr);

        //energy from gas phase
        List<String> averlist = findInArray(gasPhaseOut, "AVER>");
        String[] avL = averlist.get(averlist.size() - 1).split("\\s+");
        egas = Double.valueOf(avL[5]) + 0.5 * kBoltz * temp * (3.0 * natom - 6 - nconstr);

        //energy from liquid phase
        List<String> averlist2 = findInArray(pureLiqOut, "AVER>");
        String[] avL2 = averlist2.get(averlist2.size() - 1).split("\\s+");
        eliq = Double.valueOf(avL2[5]) / nres;

        // get all lines containing "AVER PRESS>"
        List<String> boxLen = findInArray(pureLiqOut, "AVER PRESS>");
        //keep last column containing volume in cubic angstroems
        String[] L = boxLen.get(boxLen.size() - 1).split("\\s+");
//        logger.info(L);
        box = Double.valueOf(L[6]);
//        box = Math.pow(box, 1.0/3.0);
//        logger.info("Box length [A] : " + box);

        //get data for delta g
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

    private void calc_density() {

//        density = mmass * nres / (kBoltz*temp*box*box*box);
        density = mmass * nres / (kBoltz * temp * box);
//        logger.info("density : " + density);

    }

    private void calc_vapor() {

        //estimate deltaH in kcal/mol
        deltaH = egas - eliq + kBoltz * temp;
//        logger.info("DeltaH [kcal/mol] : " + deltaH); 

    }

    @Override
    public void initializeData() {
        temp_field.setText(Double.toString(temp));
        mmass_field.setText(Double.toString(mmass));
        nres_field.setText(Integer.toString(nres));

        //calc_dg();
        //dghydr_field.setText(Double.toString(dg));
//        dens_field.setText(Double.toString(density));
//        dhvap_field.setText(Double.toString(deltaH));
    }

    @FXML
    protected void calculateValues(ActionEvent event) {

        temp = Double.valueOf(temp_field.getText());
        mmass = Double.valueOf(mmass_field.getText());
        nres = Integer.valueOf(nres_field.getText());

        calc_density();
        calc_vapor();
        calc_dg();

        dens_field.setText(Double.toString(density));
        dhvap_field.setText(Double.toString(deltaH));
        dghydr_field.setText(Double.toString(dg));

    }

    @FXML
    protected void calc_dg() {
//        double gas_mtp = -13.09403;
//        double gas_vdw = 11.43775;
//        double solvent_mtp = -21.11868;
//        double solvent_vdw = 12.96885;
        dg = (solvent_mtp + solvent_vdw) - (gas_mtp + gas_vdw);
    }

    private void exportResultsCSV() {
        String fname = "density_and_DHVap.csv";
        File outf = null;

        Window myParent = calculate_b.getScene().getWindow();
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Comma-separated values file (*.csv)", "*.csv"));
        chooser.setInitialFileName("density_and_DHVap.csv");
        chooser.setTitle("Exporting data for Density and DHVap calculations ...");

//        chooser.setInitialDirectory(new File("test"));
        outf = chooser.showSaveDialog(myParent);

        Writer wr = null;
        try {
            wr = new BufferedWriter(new FileWriter(outf));
        } catch (IOException ex) {
            this.logger.error("IOException while trying to create file " + outf.getName() + " : " + ex.getMessage());
        }

        CSVWriter csvw = new CSVWriter(wr);

        try {
            final String[] array1 = {"e_gas(kcal/mol)", "e_liquid(kcal/mol)", "temperature(K)", "molar_mass(g/mol)",
                "density(g/cm^3)", "delta_H_vap(kcal/mol)"};
            csvw.writeNext(array1);

            final String[] array2 = {Double.toString(egas), Double.toString(eliq), Double.toString(temp),
                Double.toString(mmass), Double.toString(density), Double.toString(deltaH)};
            csvw.writeNext(array2);

            csvw.close();
        } catch (IOException ex) {
            this.logger.error("IOException while exporting csv file " + outf.getName() + " : " + ex.getMessage());
        }

    }

}//class

