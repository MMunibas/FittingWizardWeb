/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.gui.step4;

import ch.unibas.charmmtools.gui.CHARMM_GUI_base;
import ch.unibas.charmmtools.workflows.RunningCHARMM;
import ch.unibas.charmmtools.generate.CHARMM_InOut;
import ch.unibas.charmmtools.generate.CHARMM_Input;
import ch.unibas.charmmtools.generate.CHARMM_Output;
import ch.unibas.charmmtools.workflows.RunCHARMMWorkflow;
import ch.unibas.fittingwizard.infrastructure.base.ResourceUtils;
import ch.unibas.fittingwizard.presentation.base.ButtonFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public class CHARMM_GUI_Step4 extends CHARMM_GUI_base {

    private static final String title = "LJ fitting procedure Step 1 : preparing CHARMM input file";

    /**
     * All FXML variables
     */
    @FXML
    private CheckBox later_PAR, later_RTF, later_COR_solu, later_COR_solv, later_LPUN;

    @FXML
    private ComboBox<String> coor_type_solu, coor_type_solv;

    private ObservableList<String> avail_coor_types;

    @FXML
    private Button button_open_PAR, button_open_RTF, button_open_COR_solu, button_open_COR_solv, button_open_LPUN;

    @FXML
    private TextField textfield_PAR, textfield_RTF, textfield_COR_solu, textfield_COR_solv, textfield_LPUN;

    @FXML
    private Button button_generate;

    //where the generated input files are added
    @FXML
    private TabPane tab_pane;

    @FXML
    private TextField lambda_min, lambda_space, lambda_max;

    // those buttons are NOT exposed to FXML but handled locally with fillbuttonbar
    private Button button_reset;
//    private Button button_save_to_file;
    private Button button_run_CHARMM;

    /**
     * Internal variables
     */
    private boolean PAR_selected, RTF_selected, COR_selected_solu,
            COR_selected_solv, LPUN_selected;

    private List<MyTab> tabsList = new ArrayList<>();

    public CHARMM_GUI_Step4(RunCHARMMWorkflow chWflow) {
        super(title, chWflow);
    }

    public CHARMM_GUI_Step4(RunCHARMMWorkflow chWflow, List<CHARMM_InOut> ioList) {

        super(title, chWflow);

        for (CHARMM_InOut ioListIt : ioList) {

            Class c = ioListIt.getClass();
            Class sc = c.getSuperclass();

            if (sc == CHARMM_Input.class) {
                inp.add((CHARMM_Input) ioListIt);
                tabsList.add(
                        new MyTab(
                                ioListIt.getType(), ioListIt.getText()
                        )
                );
            } else if (sc == CHARMM_Output.class) {
                out.add((CHARMM_Output) ioListIt);
            } else {
                throw new UnknownError("Unknown type of object in List<CHARMM_InOut> : get "
                        + ioListIt.getClass() + " but expected types are " + CHARMM_Input.class
                        + " or " + CHARMM_Output.class);
            }
            tab_pane.getTabs().addAll(tabsList);
        }

//        button_save_to_file.setDisable(false);
        textfield_PAR.setDisable(true);
        textfield_RTF.setDisable(true);
        textfield_COR_solu.setDisable(true);
        textfield_COR_solv.setDisable(true);
        textfield_LPUN.setDisable(true);

        button_generate.setDisable(true);

        button_open_PAR.setDisable(true);
        button_open_RTF.setDisable(true);
        button_open_COR_solu.setDisable(true);
        button_open_COR_solv.setDisable(true);
        button_open_LPUN.setDisable(true);

        coor_type_solu.setDisable(true);

        later_PAR.setDisable(true);
        later_RTF.setDisable(true);
        later_COR_solu.setDisable(true);
        later_COR_solv.setDisable(true);
        later_LPUN.setDisable(true);
    }

    /**
     * Here we can add actions done just before showing the window
     */
    @Override
    public void initialize() {

        later_PAR.setAllowIndeterminate(false);
        later_RTF.setAllowIndeterminate(false);
        later_COR_solu.setAllowIndeterminate(false);
        later_COR_solv.setAllowIndeterminate(false);
        later_LPUN.setAllowIndeterminate(false);

        avail_coor_types = FXCollections.observableArrayList();
        avail_coor_types.addAll(/*"*.xyz", "*.cor", */"*.pdb");

        coor_type_solu.setItems(avail_coor_types);
        coor_type_solu.setValue("*.pdb");

        coor_type_solv.setItems(avail_coor_types);
        coor_type_solv.setValue("*.pdb");

        // set to false those booleans indicating if a file has been selected
        PAR_selected = false;
        RTF_selected = false;
        COR_selected_solu = false;
        COR_selected_solv = false;
        LPUN_selected = false;

        lambda_min.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (Double.valueOf(newValue) < 0.0) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error with λ min value !");
                    alert.setHeaderText(null);
                    alert.setContentText("Please choose a λ min not less than 0.0 !");
                    alert.showAndWait();
                    lambda_min.setText("0.0");
                }
            }
        });

        lambda_max.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (Double.valueOf(newValue) > 1.0) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error with λ max value !");
                    alert.setHeaderText(null);
                    alert.setContentText("Please choose a λ max not larger than 1.0 !");
                    alert.showAndWait();
                    lambda_max.setText("1.0");
                }
            }
        });

        lambda_space.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (Double.valueOf(newValue) >= Double.valueOf(lambda_max.getText())) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error with λ space value !");
                    alert.setHeaderText(null);
                    alert.setContentText("Please choose a λ space value smaller than λ max !");
                    alert.showAndWait();
                    lambda_space.setText("0.1");
                }
            }
        });

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
        chooser.setInitialDirectory(new File("."));
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
        } else if (event.getSource().equals(button_open_COR_solu)) {
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Coordinates file " + coor_type_solu.getValue(), coor_type_solu.getValue()));
            selectedFile = chooser.showOpenDialog(myParent);
            if (selectedFile != null) {
                textfield_COR_solu.setText(selectedFile.getAbsolutePath());
                COR_selected_solu = true;
            }
        } else if (event.getSource().equals(button_open_COR_solv)) {
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Coordinates file " + coor_type_solv.getValue(), coor_type_solv.getValue()));
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
    protected void GenerateInputFile() {

//        try {
        // get filenames
        String corname_solu = textfield_COR_solu.getText();
        String corname_solv = textfield_COR_solv.getText();
        String rtfname = textfield_RTF.getText();
        String parname = textfield_PAR.getText();
        String lpunname = textfield_LPUN.getText();

        // if empty filenames print a pattern user should modify
        //transform it to relative path instead as we have to send data to clusters later
        String folderPath = new File(".").getAbsolutePath();
        corname_solu = corname_solu.length() == 0 ? "ADD_HERE_PATH_TO_COORDINATES_LIQUID_FILE" : ResourceUtils.getRelativePath(corname_solu, folderPath);
        corname_solv = corname_solv.length() == 0 ? "ADD_HERE_PATH_TO_COORDINATES_SOLVENT_FILE" : ResourceUtils.getRelativePath(corname_solv, folderPath);
        rtfname = rtfname.length() == 0 ? "ADD_HERE_PATH_TO_TOPOLOGY_FILE" : ResourceUtils.getRelativePath(rtfname, folderPath);
        parname = parname.length() == 0 ? "ADD_HERE_PATH_TO_PARAMETERS_FILE" : ResourceUtils.getRelativePath(parname, folderPath);
        lpunname = lpunname.length() == 0 ? "ADD_HERE_PATH_TO_LPUN_FILE" : ResourceUtils.getRelativePath(lpunname, folderPath);

        tabsList.add(new MyTab(corname_solu, corname_solu));
        tabsList.add(new MyTab(corname_solv, corname_solv));
        tabsList.add(new MyTab(rtfname, rtfname));
        tabsList.add(new MyTab(parname, parname));
        tabsList.add(new MyTab(lpunname, lpunname));

        boolean addTabsSuccess = tab_pane.getTabs().addAll(tabsList);
        if (!addTabsSuccess) {
            logger.error("Problem while adding tabs to current window ; try again ... ");
        } else {
            button_generate.setDisable(addTabsSuccess);
        }

        /**
         * If success enable button for saving
         */
//        button_save_to_file.setDisable(false);
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
        } else if (event.getSource().equals(later_COR_solu)) {
            COR_selected_solu = later_COR_solu.isSelected();
            button_open_COR_solu.setDisable(later_COR_solu.isSelected());
            textfield_COR_solu.setDisable(later_COR_solu.isSelected());
            coor_type_solu.setDisable(later_COR_solu.isSelected());
        } else if (event.getSource().equals(later_COR_solv)) {
            COR_selected_solv = later_COR_solv.isSelected();
            button_open_COR_solv.setDisable(later_COR_solv.isSelected());
            textfield_COR_solv.setDisable(later_COR_solv.isSelected());
            coor_type_solv.setDisable(later_COR_solv.isSelected());
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
        later_COR_solu.setDisable(false);
        later_COR_solv.setDisable(false);
        later_LPUN.setDisable(false);

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

        later_PAR.setSelected(false);
        button_open_PAR.setDisable(later_PAR.isSelected());
        textfield_PAR.setDisable(later_PAR.isSelected());

        later_RTF.setSelected(false);
        button_open_RTF.setDisable(later_RTF.isSelected());
        textfield_RTF.setDisable(later_RTF.isSelected());

        later_COR_solu.setSelected(false);
        button_open_COR_solu.setDisable(later_COR_solu.isSelected());
        textfield_COR_solu.setDisable(later_COR_solu.isSelected());
        coor_type_solu.setDisable(later_COR_solu.isSelected());

        later_COR_solv.setSelected(false);
        button_open_COR_solv.setDisable(later_COR_solv.isSelected());
        textfield_COR_solv.setDisable(later_COR_solv.isSelected());
        coor_type_solv.setDisable(later_COR_solv.isSelected());

        later_LPUN.setSelected(false);
        button_open_LPUN.setDisable(later_LPUN.isSelected());
        textfield_LPUN.setDisable(later_LPUN.isSelected());

        button_generate.setDisable(true);
//        button_save_to_file.setDisable(true);
        button_run_CHARMM.setDisable(true);

//        button_save_to_file.setText("Click to save");
        button_run_CHARMM.setText("Run CHARMM");

        inp.clear();
        out.clear();
        CHARMM_inFile.clear();
        CHARMM_outFile.clear();

        tabsList.clear();
        tab_pane.getTabs().clear();

        lambda_min.setText("0.0");
        lambda_space.setText("0.1");
        lambda_max.setText("1.0");
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
//        this.button_run_CHARMM.setDisable(false);
//
//        /**
//         * Allow running charmm script
//         */
//        button_run_CHARMM.setDisable(false);
//
//    }
    protected void runCHARMM(ActionEvent event) {

        List<CHARMM_InOut> myList = new ArrayList<>();
        myList.addAll(inp);
        myList.addAll(out);
        navigateTo(RunningCHARMM.class, myList);

    }

    @Override
    protected void fillButtonBar() {

        button_reset = ButtonFactory.createButtonBarButton("Reset", new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                logger.info("Resetting all fields.");
                ResetFields(actionEvent);
            }
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
        button_run_CHARMM = ButtonFactory.createButtonBarButton("Run CHARMM", new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                logger.info("Running CHARMM input script.");
                runCHARMM(actionEvent);
            }
        });
        addButtonToButtonBar(button_run_CHARMM);
        button_run_CHARMM.setDisable(true);
    }

}//end of controller class
