/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.gui.step4;

import ch.unibas.charmmtools.gui.CHARMM_GUI_base;
import ch.unibas.charmmtools.gui.RunningCHARMM;
import ch.unibas.charmmtools.generate.CHARMM_InOut;
import ch.unibas.charmmtools.generate.inputs.CHARMM_Generator_DGHydr;
import ch.unibas.charmmtools.workflows.RunCHARMMWorkflow;
import ch.unibas.fittingwizard.presentation.base.ButtonFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public class CHARMM_GUI_Step4 extends CHARMM_GUI_base {
    
    private static final String title = "LJ fitting procedure Step 4 : preparing CHARMM files for Therm. Integration";

    /**
     * All FXML variables
     */
    @FXML
    private Button button_open_PAR, button_open_RTF, button_open_COR_solu, button_open_COR_solv, button_open_LPUN;
    
    @FXML
    private TextField textfield_PAR, textfield_RTF, textfield_COR_solu, textfield_COR_solv, textfield_LPUN;
    
    @FXML
    private Button button_generate;

    //where the generated input files are added
    @FXML
    private TabPane tab_pane_gas, tab_pane_solv;
    
    @FXML
    private TextField lambda_space;
    
    @FXML
    private CheckBox check_autogen_corsolv;

    // those buttons are NOT exposed to FXML but handled locally with fillbuttonbar
    private Button button_reset;
//    private Button button_save_to_file;
    private Button button_run_CHARMM;

    /**
     * Internal variables
     */
    private boolean PAR_selected, RTF_selected, COR_selected_solu,
            COR_selected_solv, LPUN_selected;
    
    private List<MyTab> tab_list_gas = new ArrayList<>();
    private List<MyTab> tab_list_solv = new ArrayList<>();
    
    private CHARMM_Generator_DGHydr in_gas_vdw = null, in_gas_mtp = null, in_solv_vdw = null, in_solv_mtp = null;
    
    public CHARMM_GUI_Step4(RunCHARMMWorkflow chWflow) {
        super(title, chWflow);
    }
    
    public CHARMM_GUI_Step4(RunCHARMMWorkflow chWflow, List<CHARMM_InOut> ioList) {
        
        super(title, chWflow);

//        for (CHARMM_InOut ioListIt : ioList) {
//
//            if (ioList.getClass().isInstance(CHARMM_Input.class)) {
//                
//                inp.add(ioListIt);
//                
//                tabsList.add(
//                        new MyTab(
//                                ioListIt.getType(), ioListIt.getText()
//                        )
//                );
//            } else if (sc == CHARMM_Output.class) {
//                out.add((CHARMM_Output) ioListIt);
//                tabsList.add(
//                        new MyTab(
//                                ioListIt.getType(), ioListIt.getText()
//                        )
//                );
//            } else {
//                throw new UnknownError("Unknown type of object in List<CHARMM_InOut> : get "
//                        + ioListIt.getClass() + " but expected types are " + CHARMM_Input.class
//                        + " or " + CHARMM_Output.class);
//            }
//            tab_pane.getTabs().addAll(tabsList);
//        }
//        button_save_to_file.setDisable(false);
//        textfield_PAR.setDisable(true);
//        textfield_RTF.setDisable(true);
//        textfield_COR_solu.setDisable(true);
//        textfield_COR_solv.setDisable(true);
//        textfield_LPUN.setDisable(true);
//
//        button_generate.setDisable(true);
//
//        button_open_PAR.setDisable(true);
//        button_open_RTF.setDisable(true);
//        button_open_COR_solu.setDisable(true);
//        button_open_COR_solv.setDisable(true);
//        button_open_LPUN.setDisable(true);
    }

    /**
     * Here we can add actions done just before showing the window
     */
    @Override
    public void initialize() {

//        avail_coor_types = FXCollections.observableArrayList();
//        avail_coor_types.addAll(/*"*.xyz", "*.cor", */"*.pdb");
//        coor_type_solu.setItems(avail_coor_types);
//        coor_type_solu.setValue("*.pdb");
//
//        coor_type_solv.setItems(avail_coor_types);
//        coor_type_solv.setValue("*.pdb");
        // set to false those booleans indicating if a file has been selected
        PAR_selected = false;
        RTF_selected = false;
        COR_selected_solu = false;
        COR_selected_solv = false;
        LPUN_selected = false;

//        lambda_space.textProperty().addListener(new ChangeListener<String>() {
//            @Override
//            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
//                if (Double.valueOf(newValue) >= Double.valueOf(lambda_max.getText())) {
//                    Alert alert = new Alert(AlertType.ERROR);
//                    alert.setTitle("Error with λ space value !");
//                    alert.setHeaderText(null);
//                    alert.setContentText("Please choose a λ space value smaller than λ max !");
//                    alert.showAndWait();
//                    lambda_space.setText("0.1");
//                }
//            }
//        });
        this.tab_pane_gas.getTabs().clear();
        this.tab_pane_solv.getTabs().clear();
    }

//    @Override
//    public void initializeData(){
//    }
    /**
     * Enable or Disable the button_generate if required
     */
    private void validateButtonGenerate() {
        button_generate.setDisable(true);
        
        if (PAR_selected == true && RTF_selected == true
                && COR_selected_solu == true && COR_selected_solv == true && LPUN_selected == true) {
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
        chooser.setInitialDirectory(new File("test"));
        File selectedFile = null;
        
        chooser.setTitle("Open File");
        
        if (event.getSource().equals(button_open_PAR)) {
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CHARMM FF parameters file (*.par,*.prm)", "*.par", "*.prm"));
            selectedFile = chooser.showOpenDialog(myParent);
            if (selectedFile != null) {
                textfield_PAR.setText(selectedFile.getAbsolutePath());
                PAR_selected = true;
            }
        } else if (event.getSource().equals(button_open_RTF)) {
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CHARMM FF topology file (*.top,*.rtf)", "*.top", "*.rtf"));
            selectedFile = chooser.showOpenDialog(myParent);
            if (selectedFile != null) {
                textfield_RTF.setText(selectedFile.getAbsolutePath());
                RTF_selected = true;
            }
        } else if (event.getSource().equals(button_open_COR_solu)) {
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Coordinates file (*.pdb,*.ent)", "*.pdb", "*.ent"));
            selectedFile = chooser.showOpenDialog(myParent);
            if (selectedFile != null) {
                textfield_COR_solu.setText(selectedFile.getAbsolutePath());
                COR_selected_solu = true;
            }
        } else if (event.getSource().equals(button_open_COR_solv)) {
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Coordinates file (*.pdb,*.ent)", "*.pdb", "*.ent"));
            selectedFile = chooser.showOpenDialog(myParent);
            if (selectedFile != null) {
                textfield_COR_solv.setText(selectedFile.getAbsolutePath());
                COR_selected_solv = true;
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
    protected void GenerateInputFiles() {

        // get filenames
        String corname_solu = textfield_COR_solu.getText();
        String corname_solv = textfield_COR_solv.getText();
        String rtfname = textfield_RTF.getText();
        String parname = textfield_PAR.getText();
        String lpunname = textfield_LPUN.getText();

        //transform it to relative path instead as we have to send data to clusters later
//        String folderPath = new File("test").getAbsolutePath();
//        corname_solu = ResourceUtils.getRelativePath(corname_solu, folderPath);
//        corname_solv = ResourceUtils.getRelativePath(corname_solv, folderPath);
//        rtfname = ResourceUtils.getRelativePath(rtfname, folderPath);
//        parname = ResourceUtils.getRelativePath(parname, folderPath);
//        lpunname = ResourceUtils.getRelativePath(lpunname, folderPath);
        double lamb_spacing_val = Double.valueOf(lambda_space.getText());

        /**
         * TODO : get a list of input files from python script
         */

//        try {
        in_gas_vdw = new CHARMM_Generator_DGHydr(corname_solu, rtfname, parname, lpunname, "vdw",
                0.0, lamb_spacing_val, 1.0);
//            CHARMM_inFile.addAll(in_gas_vdw.getMyFiles());
//
        in_gas_mtp = new CHARMM_Generator_DGHydr(corname_solu, rtfname, parname, lpunname, "mtp",
                0.0, lamb_spacing_val, 1.0);
//            CHARMM_inFile.addAll(in_gas_mtp.getMyFiles());
//
        in_solv_vdw = new CHARMM_Generator_DGHydr(corname_solu, corname_solv, rtfname, rtfname,
                parname, lpunname, "vdw", 0.0, lamb_spacing_val, 1.0);
//            CHARMM_inFile.addAll(in_solv_vdw.getMyFiles());
//
        in_solv_mtp = new CHARMM_Generator_DGHydr(corname_solu, corname_solv, rtfname, rtfname,
                parname, lpunname, "mtp", 0.0, lamb_spacing_val, 1.0);
//            CHARMM_inFile.addAll(in_solv_mtp.getMyFiles());

//            tab_list_gas.add(new MyTab(in_gas_vdw.getType(), in_gas_vdw.getText()));
//            tab_list_gas.add(new MyTab(in_gas_mtp.getType(), in_gas_mtp.getText()));
//
//            tab_list_solv.add(new MyTab(in_solv_vdw.getType(), in_solv_vdw.getText()));
//            tab_list_solv.add(new MyTab(in_solv_mtp.getType(), in_solv_mtp.getText()));
        try {
            for (File fi : in_gas_vdw.getMyFiles()) {
                tab_list_gas.add(new MyTab(
                        "Gas & VDW", new String(Files.readAllBytes(Paths.get(fi.getAbsolutePath())))
                ));
            }
            
            for (File fi : in_gas_mtp.getMyFiles()) {
                tab_list_gas.add(new MyTab(
                        "Gas & MTP", new String(Files.readAllBytes(Paths.get(fi.getAbsolutePath())))
                ));
            }
            
            for (File fi : in_solv_vdw.getMyFiles()) {
                tab_list_solv.add(new MyTab(
                        "Solv & VDW", new String(Files.readAllBytes(Paths.get(fi.getAbsolutePath())))
                ));
            }
            
            for (File fi : in_solv_mtp.getMyFiles()) {
                tab_list_solv.add(new MyTab(
                        "Solv & MTP", new String(Files.readAllBytes(Paths.get(fi.getAbsolutePath())))
                ));
            }
        } catch (IOException ex) {
            logger.error("Error while loading file in a tab");
        }
        
        tab_pane_gas.getTabs().addAll(tab_list_gas);
        tab_pane_solv.getTabs().addAll(tab_list_solv);
        
        button_run_CHARMM.setDisable(false);

//            logger.debug(in_gas_vdw.getText());
//            logger.debug(in_gas_mtp.getText());
//            logger.debug(in_solv_vdw.getText());
//            logger.debug(in_solv_mtp.getText());
//
//        } catch (IOException ex) {
//            logger.error(ex);
//        }

        /*
         * TODO : display input files without running CHARMM (might go with previous TODO ?? )
         */
    }

    /**
     * Handles the event when a checkBox is selected.
     *
     * @param event
     */
    @FXML
    protected void CheckBoxActions(ActionEvent event) {
        
        if (event.getSource().equals(check_autogen_corsolv)) {
            boolean l = check_autogen_corsolv.isSelected();
            textfield_COR_solv.setDisable(l);
            button_open_COR_solv.setDisable(l);
            COR_selected_solv = l;
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

//        later_PAR.setDisable(false);
//        later_RTF.setDisable(false);
//        later_COR_solu.setDisable(false);
//        later_COR_solv.setDisable(false);
//        later_LPUN.setDisable(false);
        textfield_PAR.clear();
        textfield_RTF.clear();
        textfield_COR_solu.clear();
        textfield_COR_solv.clear();
        textfield_LPUN.clear();

        //disable some elements 
        PAR_selected = false;
        RTF_selected = false;
        COR_selected_solu = false;
        COR_selected_solv = false;
        LPUN_selected = false;

//        later_PAR.setSelected(false);
        button_open_PAR.setDisable(false);
        textfield_PAR.setDisable(false);

//        later_RTF.setSelected(false);
        button_open_RTF.setDisable(false);
        textfield_RTF.setDisable(false);

//        later_COR_solu.setSelected(false);
        button_open_COR_solu.setDisable(false);
        textfield_COR_solu.setDisable(false);
////        coor_type_solu.setDisable(later_COR_solu.isSelected());
//
//        later_COR_solv.setSelected(false);
        button_open_COR_solv.setDisable(false);
        textfield_COR_solv.setDisable(false);
        check_autogen_corsolv.setSelected(false);
//        coor_type_solv.setDisable(later_COR_solv.isSelected());

//        later_LPUN.setSelected(false);
        button_open_LPUN.setDisable(false);
        textfield_LPUN.setDisable(false);
        
        button_generate.setDisable(true);
//        button_save_to_file.setDisable(true);
        button_run_CHARMM.setDisable(true);

//        button_save_to_file.setText("Click to save");
//        button_run_CHARMM.setText("Run CHARMM");
        inp.clear();
        out.clear();
        
        CHARMM_inFile.clear();
        CHARMM_outFile.clear();
        
        tab_list_gas.clear();
        tab_list_solv.clear();
        tab_pane_gas.getTabs().clear();
        tab_pane_solv.getTabs().clear();

//        lambda_min.setText("0.0");
        lambda_space.setText("0.1");
//        lambda_max.setText("1.0");

//        ti_toggle_group.selectToggle(ti_mtp);
    }

//    protected void SaveToFile(ActionEvent event) {
//        Window myParent = button_save_to_file.getScene().getWindow();
//        FileChooser chooser = new FileChooser();
////        chooser.setInitialDirectory(new File("test"));
//
//        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CHARMM input file", "*.inp"));
//
//        for (CHARMM_Input ip : inp) {
//            
//            if (ip.getClass() == CHARMM_Input_GasPhase.class) {
//                chooser.setInitialFileName("gas_phase.inp");
//                chooser.setTitle("Please save the file for Gas Phase calculation");
//            } else if (ip.getClass() == CHARMM_Input_PureLiquid.class) {
//                chooser.setInitialFileName("pure_liquid.inp");
//                chooser.setTitle("Please save the file for Pure Liquid calculation");
//            }
//
//            File selectedFile = chooser.showSaveDialog(myParent);
//            CHARMM_inFile.add(selectedFile);
//
//            BufferedWriter buffw = null;
//
//            if (selectedFile != null) {
//                try {
//                    buffw = new BufferedWriter(new FileWriter(selectedFile));
//                    buffw.write(ip.getText());
//                    buffw.close();
//                } catch (IOException ex) {
//                    logger.error("IOException raised whene generating CHARMM inputfile : " + ex.getMessage());
//                }
//            } else {
//                logger.error("Error while setting file name or save path for CHARMM input file.");
//            }
//            
//            ip.setInp(selectedFile);
//            
//        }
//
//        //now that it is saved it may be runned
//
//        /**
//         * Allow running charmm script
//         */
//        button_run_CHARMM.setDisable(false);
//
//    }
    protected void runCHARMM(ActionEvent event) {
        
//        List<CHARMM_InOut> myList = new ArrayList<>();
//        myList.addAll(inp);
//        myList.addAll(out);
//        navigateTo(RunningCHARMM.class, myList);
        
        logger.info("Now running in_gas_vdw");
        this.in_gas_vdw.run();
        
        logger.info("Now running in_gas_mtp");
        this.in_gas_mtp.run();
        
        logger.info("Now running in_solv_vdw");
        this.in_solv_vdw.run();
        
        logger.info("Now running in_solv_mtp");
        this.in_solv_mtp.run();
        
    }
    
    @Override
    protected void fillButtonBar() {
        
        button_reset = ButtonFactory.createButtonBarButton("Reset", (ActionEvent actionEvent) -> {
            logger.info("Resetting all fields.");
            ResetFields(actionEvent);
        });
        addButtonToButtonBar(button_reset);

//        button_save_to_file = ButtonFactory.createButtonBarButton("Click to save", new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent actionEvent) {
//                logger.info("Saving CHARMM input script to a file.");
//                SaveToFile(actionEvent);
//            }
//        });
//        addButtonToButtonBar(button_save_to_file);
//        button_save_to_file.setDisable(true);
        button_run_CHARMM = ButtonFactory.createButtonBarButton("Run CHARMM", (ActionEvent actionEvent) -> {
            logger.info("Running CHARMM input script.");
            runCHARMM(actionEvent);
        });
        addButtonToButtonBar(button_run_CHARMM);
        button_run_CHARMM.setDisable(true);
    }
    
}//end of controller class
