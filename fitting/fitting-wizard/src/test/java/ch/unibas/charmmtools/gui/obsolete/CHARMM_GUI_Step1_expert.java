/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.gui.obsolete;

import ch.unibas.charmmtools.gui.CHARMM_GUI_base;
import ch.unibas.charmmtools.gui.RunningCHARMM_DenVap;
import ch.unibas.fitting.shared.charmm.generate.CHARMM_InOut;
import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Input;
import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Input_GasPhase;
import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Input_PureLiquid;

import ch.unibas.fitting.shared.charmm.RunCHARMMWorkflow;
import ch.unibas.fitting.shared.charmm.generate.outputs.CHARMM_Output;
import ch.unibas.fitting.shared.scripts.base.ResourceUtils;
import ch.unibas.fittingwizard.gaussian.base.ButtonFactory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public class CHARMM_GUI_Step1_expert extends CHARMM_GUI_base {

    private static final String title = "LJ fitting procedure : preparing CHARMM input files";

    /**
     * All FXML variables
     */
    @FXML
    private CheckBox later_PAR, later_RTF, later_COR_gas, later_COR_liquid, later_LPUN;

    @FXML
    private ComboBox<String> coor_type_gas, coor_type_liquid;

    private ObservableList<String> avail_coor_types;

    @FXML
    private Button button_open_PAR, button_open_RTF, button_open_COR_gas, 
            button_open_COR_liquid, button_open_LPUN;

    @FXML
    private TextField textfield_PAR, textfield_RTF, textfield_COR_gas, textfield_COR_liquid, textfield_LPUN;

    @FXML
    private Label RedLabel_Notice;

    @FXML
    private Button button_generate;

    @FXML
    private TextArea textarea_left, textarea_right;

    // those buttons are NOT exposed to FXML but handles locally with fillbuttonbar
    private Button button_reset, button_save_to_file, button_run_CHARMM;

    /**
     * Internal variables
     */
    private boolean PAR_selected, RTF_selected, COR_selected_gas, COR_selected_liquid, LPUN_selected;

    public CHARMM_GUI_Step1_expert(RunCHARMMWorkflow chWflow) {
        super(title, chWflow);
    }

    public CHARMM_GUI_Step1_expert(RunCHARMMWorkflow chWflow, List<CHARMM_InOut> ioList) {
        
        super(title, chWflow);
        
        for (CHARMM_InOut ioListIt : ioList) {
            
            Class c = ioListIt.getClass();
            Class sc = c.getSuperclass();
            
            if (sc == CHARMM_Input.class) {
                inp.add((CHARMM_Input) ioListIt);
            } else if (sc == CHARMM_Output.class) {
                out.add((CHARMM_Output) ioListIt);
            } else {
                throw new UnknownError("Unknown type of object in List<CHARMM_InOut> : get " +
                        ioListIt.getClass() + " but expected types are " + CHARMM_Input.class +
                        " or " + CHARMM_Output.class);
            }
        }
        
        textarea_left.setText(inp.get(0).getText());
        textarea_left.setEditable(true);

        textarea_right.setText(inp.get(1).getText());
        textarea_right.setEditable(true);

        RedLabel_Notice.setText("Error while running CHARMM ! Please modify input file(s) !");
        RedLabel_Notice.setVisible(true);

        button_save_to_file.setDisable(false);

        textfield_PAR.setDisable(true);
        textfield_RTF.setDisable(true);
        textfield_COR_gas.setDisable(true);
        textfield_COR_liquid.setDisable(true);
        textfield_LPUN.setDisable(true);

        button_generate.setDisable(true);

        button_open_PAR.setDisable(true);
        button_open_RTF.setDisable(true);
        button_open_COR_gas.setDisable(true);
        button_open_COR_liquid.setDisable(true);
        button_open_LPUN.setDisable(true);

        coor_type_gas.setDisable(true);
        coor_type_liquid.setDisable(true);
        
        later_PAR.setDisable(true);
        later_RTF.setDisable(true);
        later_COR_gas.setDisable(true);
        later_COR_liquid.setDisable(true);
        later_LPUN.setDisable(true);
    }

    /**
     * Here we can add actions done just before showing the window
     */
    @Override
    public void initialize() {

        later_PAR.setAllowIndeterminate(false);
        later_RTF.setAllowIndeterminate(false);
        later_COR_gas.setAllowIndeterminate(false);
        later_COR_liquid.setAllowIndeterminate(false);
        later_LPUN.setAllowIndeterminate(false);

        avail_coor_types = FXCollections.observableArrayList();
        avail_coor_types.addAll(/*"*.xyz", "*.cor", */"*.pdb");
        
        coor_type_gas.setItems(avail_coor_types);
        coor_type_gas.setValue("*.pdb");
        
        coor_type_liquid.setItems(avail_coor_types);
        coor_type_liquid.setValue("*.pdb");

        // set to false those booleans indicating if a file has been selected
        PAR_selected = false;
        RTF_selected = false;
        COR_selected_gas = false;
        COR_selected_liquid = false;
        LPUN_selected = false;
    }

    /**
     * Enable or Disable the button_generate if required
     */
    private void validateButtonGenerate() {
        button_generate.setDisable(true);

        if (PAR_selected == true && RTF_selected == true && COR_selected_gas == true &&
                COR_selected_liquid == true && LPUN_selected == true) {
            button_generate.setDisable(false);
        }
    }
 
    /**
     * Handles the event when one of the 3 button_open_XXX is pressed button_generate is enabled only when the 3 files
     * have been loaded
     *
     * @param event
     */
    @FXML
    protected void OpenButtonPressed(ActionEvent event) {

        Window myParent = button_generate.getScene().getWindow();
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File("./test"));
        File selectedFile = null;

        chooser.setTitle("Open File");

        if (event.getSource().equals(button_open_PAR)) {
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CHARMM FF parameters file", /*"*.inp",*/ "*.par", "*.prm"));
            selectedFile = chooser.showOpenDialog(myParent);
            if (selectedFile != null) {
                textfield_PAR.setText(selectedFile.getAbsolutePath());
                PAR_selected = true;
            }
        } else if (event.getSource().equals(button_open_RTF)) {
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CHARMM FF topology file", "*.top", "*.rtf"));
            selectedFile = chooser.showOpenDialog(myParent);
            if (selectedFile != null) {
                textfield_RTF.setText(selectedFile.getAbsolutePath());
                RTF_selected = true;
            }
        } else if (event.getSource().equals(button_open_COR_gas)) {
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Coordinates file " + coor_type_gas.getValue(), coor_type_gas.getValue()));
            selectedFile = chooser.showOpenDialog(myParent);
            if (selectedFile != null) {
                textfield_COR_gas.setText(selectedFile.getAbsolutePath());
                COR_selected_gas = true;
            }
        } else if (event.getSource().equals(button_open_COR_liquid)) {
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Coordinates file " + coor_type_liquid.getValue(), coor_type_liquid.getValue()));
            selectedFile = chooser.showOpenDialog(myParent);
            if (selectedFile != null) {
                textfield_COR_liquid.setText(selectedFile.getAbsolutePath());
                COR_selected_liquid = true;
            }
        } else if (event.getSource().equals(button_open_LPUN)) {
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("LPUN file", "*.lpun"));
            selectedFile = chooser.showOpenDialog(myParent);
            if (selectedFile != null) {
                textfield_LPUN.setText(selectedFile.getAbsolutePath());
                LPUN_selected = true;
            }
        } else {
            throw new UnknownError("Unknown Event in OpenButtonPressed(ActionEvent event)");
        }

        this.validateButtonGenerate();

    }//end of OpenButtonPressed action

    /**
     * Try to generate an input file with standard parameters, it can be edited later
     */
    @FXML
    protected void GenerateInputFile() {

        try {

            // get filenames
            String corname_gas = textfield_COR_gas.getText();
            String corname_liquid = textfield_COR_liquid.getText();
            String rtfname = textfield_RTF.getText();
            String parname = textfield_PAR.getText();
            String lpunname = textfield_LPUN.getText();

            // if empty filenames print a pattern user should modify
            //transform it to relative path instead as we have to send data to clusters later
            String folderPath = new File(".").getAbsolutePath();
            corname_gas = corname_gas.length() == 0 ? "ADD_HERE_PATH_TO_COORDINATES_GAS_FILE" : ResourceUtils.getRelativePath(corname_gas, folderPath);
            corname_liquid = corname_liquid.length() == 0 ? "ADD_HERE_PATH_TO_COORDINATES_LIQUID_FILE" : ResourceUtils.getRelativePath(corname_liquid, folderPath);
            rtfname = rtfname.length() == 0 ? "ADD_HERE_PATH_TO_TOPOLOGY_FILE" : ResourceUtils.getRelativePath(rtfname, folderPath);
            parname = parname.length() == 0 ? "ADD_HERE_PATH_TO_PARAMETERS_FILE" : ResourceUtils.getRelativePath(parname, folderPath);
            lpunname = lpunname.length() == 0 ? "ADD_HERE_PATH_TO_LPUN_FILE" : ResourceUtils.getRelativePath(lpunname, folderPath);

            CHARMM_Input_GasPhase gas = new CHARMM_Input_GasPhase(
                    new File(corname_gas),
                    new File(rtfname),
                    new File(parname),
                    new File(lpunname));
            gas.generate();
            inp.add(0, gas);

            CHARMM_Input_PureLiquid liquid = new CHARMM_Input_PureLiquid(
                    new File(corname_liquid),
                    new File(rtfname),
                    new File(parname),
                    new File(lpunname));
            liquid.generate();
            inp.add(1, liquid);
            textarea_left.setText(inp.get(0).getText());
            textarea_right.setText(inp.get(1).getText());

            RedLabel_Notice.setVisible(true);

        } catch (IOException ex) {
            logger.error(ex);
        }

        /**
         * If success enable button for saving
         */
        button_save_to_file.setDisable(false);

    }

    /**
     * Handles the event when one of the 4 checkBox is selected. If the 4 are set to true button_generate is enabled as
     * the 4 required files will be chosen later
     *
     * @param event
     */
    @FXML
    protected void CheckBoxActions(ActionEvent event) {

        if (event.getSource().equals(later_PAR)) {
            PAR_selected = later_PAR.isSelected();
            button_open_PAR.setDisable(later_PAR.isSelected());
            textfield_PAR.setDisable(later_PAR.isSelected());
        } else if (event.getSource().equals(later_RTF)) {
            RTF_selected = later_RTF.isSelected();
            button_open_RTF.setDisable(later_RTF.isSelected());
            textfield_RTF.setDisable(later_RTF.isSelected());
        } else if (event.getSource().equals(later_COR_gas)) {
            COR_selected_gas = later_COR_gas.isSelected();
            button_open_COR_gas.setDisable(later_COR_gas.isSelected());
            textfield_COR_gas.setDisable(later_COR_gas.isSelected());
            coor_type_gas.setDisable(later_COR_gas.isSelected());
        } else if (event.getSource().equals(later_COR_liquid)) {
            COR_selected_liquid = later_COR_liquid.isSelected();
            button_open_COR_liquid.setDisable(later_COR_liquid.isSelected());
            textfield_COR_liquid.setDisable(later_COR_liquid.isSelected());
            coor_type_liquid.setDisable(later_COR_liquid.isSelected());
        } else if (event.getSource().equals(later_LPUN)) {
            LPUN_selected = later_LPUN.isSelected();
            button_open_LPUN.setDisable(later_LPUN.isSelected());
            textfield_LPUN.setDisable(later_LPUN.isSelected());
        } else {
            throw new UnknownError("Unknown Event");
        }

        this.validateButtonGenerate();
    }

    /**
     *
     * @param event
     */
    protected void ResetFields(ActionEvent event) {

        later_PAR.setDisable(false);
        later_RTF.setDisable(false);
        later_COR_gas.setDisable(false);
        later_COR_liquid.setDisable(false);
        later_LPUN.setDisable(false);

        //clear textcontent
        textarea_left.clear();
        textarea_right.clear();
        textfield_PAR.clear();
        textfield_RTF.clear();
        textfield_COR_gas.clear();
        textfield_COR_liquid.clear();
        textfield_LPUN.clear();

        //disable some elements 
        PAR_selected = false;
        RTF_selected = false;
        COR_selected_gas = false;
        COR_selected_liquid = false;
        LPUN_selected = false;

        later_PAR.setSelected(false);
        button_open_PAR.setDisable(later_PAR.isSelected());
        textfield_PAR.setDisable(later_PAR.isSelected());

        later_RTF.setSelected(false);
        button_open_RTF.setDisable(later_RTF.isSelected());
        textfield_RTF.setDisable(later_RTF.isSelected());

        later_COR_gas.setSelected(false);
        button_open_COR_gas.setDisable(later_COR_gas.isSelected());
        textfield_COR_gas.setDisable(later_COR_gas.isSelected());
        coor_type_gas.setDisable(later_COR_gas.isSelected());
        
        later_COR_liquid.setSelected(false);
        button_open_COR_liquid.setDisable(later_COR_liquid.isSelected());
        textfield_COR_liquid.setDisable(later_COR_liquid.isSelected());
        coor_type_liquid.setDisable(later_COR_liquid.isSelected());

        later_LPUN.setSelected(false);
        button_open_LPUN.setDisable(later_LPUN.isSelected());
        textfield_LPUN.setDisable(later_LPUN.isSelected());

        RedLabel_Notice.setVisible(false);
        button_generate.setDisable(true);
        button_save_to_file.setDisable(true);
        button_run_CHARMM.setDisable(true);

        button_save_to_file.setText("Click to save");
        button_run_CHARMM.setText("Run CHARMM");

        inp.clear();
        out.clear();
        CHARMM_inFile.clear();
        CHARMM_outFile.clear();

    }

    protected void SaveToFile(ActionEvent event) {
        Window myParent = button_save_to_file.getScene().getWindow();
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File("./test"));

        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CHARMM input file", "*.inp"));

        for (CHARMM_Input ip : inp) {
            
            if (ip.getClass() == CHARMM_Input_GasPhase.class) {
                chooser.setInitialFileName("gas_phase.inp");
                chooser.setTitle("Please save the file for Gas Phase calculation");
            } else if (ip.getClass() == CHARMM_Input_PureLiquid.class) {
                chooser.setInitialFileName("pure_liquid.inp");
                chooser.setTitle("Please save the file for Pure Liquid calculation");
            }

            File selectedFile = chooser.showSaveDialog(myParent);
            CHARMM_inFile.add(selectedFile);

            BufferedWriter buffw = null;

            if (selectedFile != null) {
                try {
                    buffw = new BufferedWriter(new FileWriter(selectedFile));
                    buffw.write(ip.getText());
                    buffw.close();
                } catch (IOException ex) {
                    logger.error("IOException raised whene generating CHARMM inputfile : " + ex.getMessage());
                }
            } else {
                logger.error("Error while setting file name or save path for CHARMM input file.");
            }
            
//            ip.setInp(selectedFile);
            
        }

        //now that it is saved it may be runned
        this.RedLabel_Notice.setText("You can now try to run the simulation(s)");
        this.button_run_CHARMM.setDisable(false);

        /**
         * Allow running charmm script
         */
        button_run_CHARMM.setDisable(false);

    }

    protected void runCHARMM(ActionEvent event) {

        List<CHARMM_InOut> myList = new ArrayList<>();
        myList.addAll(inp);
        myList.addAll(out);
        navigateTo(RunningCHARMM_DenVap.class, myList);

    }

    @Override
    protected void fillButtonBar() {

        button_reset = ButtonFactory.createButtonBarButton("Reset", actionEvent -> {
            logger.info("Resetting all fields.");
            ResetFields(actionEvent);
        });
        addButtonToButtonBar(button_reset);

        button_save_to_file = ButtonFactory.createButtonBarButton("Click to save files ", actionEvent -> {
            logger.info("Saving CHARMM input script(s) to file(s).");
            SaveToFile(actionEvent);
        });
        addButtonToButtonBar(button_save_to_file);
        button_save_to_file.setDisable(true);

        button_run_CHARMM = ButtonFactory.createButtonBarButton("Run CHARMM ρ and ΔH simulations", actionEvent -> {
            logger.info("Running CHARMM input script.");
            runCHARMM(actionEvent);
        });
        addButtonToButtonBar(button_run_CHARMM);
        button_run_CHARMM.setDisable(true);
    }

}//end of controller class
