/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.gui;

import ch.unibas.charmmtools.scripts.CHARMM_input;
import ch.unibas.charmmtools.scripts.CHARMM_input_GasPhase;
import ch.unibas.charmmtools.scripts.CHARMM_input_PureLiquid;
import ch.unibas.charmmtools.workflows.RunCHARMMWorkflow;
import ch.unibas.fittingwizard.application.Visualization;
import ch.unibas.fittingwizard.application.workflows.base.WorkflowContext;
import ch.unibas.fittingwizard.infrastructure.base.ResourceUtils;
import ch.unibas.fittingwizard.presentation.base.WizardPageWithVisualization;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.log4j.Logger;

/**
 * FXML Controller class
 *
 * @author hedin
 */


public class CHARMM_Input_Assistant extends WizardPageWithVisualization{

    private static String title = "LJ fitting procedure : Step 1";
    private static final Logger logger = Logger.getLogger(CHARMM_Input_Assistant.class);

    /**
     * The TabPane in which several tabs are added
     */
//    @FXML
//    private TabPane Tab_Pane;
//    @FXML
//    private Tab Tab_Step1, Tab_Step2;

    /**
     * Everything related to the tab Step1
     */
    @FXML
    private CheckBox later_PAR, later_RTF, later_COR, later_LPUN;

    @FXML
    private ComboBox<String> coor_type;
    
    private ObservableList<String> avail_coor_types;

    @FXML
    private Button button_open_PAR, button_open_RTF, button_open_COR, button_open_LPUN;

    @FXML
    private TextField textfield_PAR, textfield_RTF, textfield_COR, textfield_LPUN;

    @FXML
    private Label RedLabel_Notice;

    @FXML
    private Button button_generate;

    @FXML
    private TextArea inpfile_TextArea;
    
    @FXML
    private RadioButton  radio_dens, radio_DHVap, radio_DG_hydration;
    @FXML
    private ToggleGroup toggle_radio;
    
    @FXML
    private Button button_reset, button_save_to_file, button_run_CHARMM;

    /**
     * Everything related to the tab Step2
     */
//    @FXML
//    private TextArea inpfile_TextArea_Step2;

    //@FXML
    //private Button button_back_Step2, button_next_Step2;
    /**
     * Internal variables
     */
    private boolean PAR_selected, RTF_selected, COR_selected, LPUN_selected;
    //type of simulation asked by user
    private boolean dens_required, DHVap_required, DG_hydration_required;
    
    private File CHARMM_saved_file;
    
    private final RunCHARMMWorkflow charmmWorkflow;

    public CHARMM_Input_Assistant(Visualization visualization, RunCHARMMWorkflow chWflow) {
        super(visualization, title);
        this.charmmWorkflow = chWflow;
    }

//    public CHARMM_Input_Assistant(String my_CHARMM_Title) {
//        super(my_CHARMM_Title);
//    }

    /**
     * Here we can add actions done just before showing the window
     */
    @Override
    public void initialize() {
        //Tab_Pane.getTabs().remove(Tab_Step2);
        //Tab_Step2.setDisable(true);

        later_PAR.setAllowIndeterminate(false);
        later_RTF.setAllowIndeterminate(false);
        later_COR.setAllowIndeterminate(false);
        later_LPUN.setAllowIndeterminate(false);

        avail_coor_types = FXCollections.observableArrayList();
        avail_coor_types.addAll(/*"*.xyz", "*.cor", */"*.pdb");
        coor_type.setItems(avail_coor_types);

        coor_type.setValue("*.pdb");
        
        // put in the same toggle group the radio buttons
        // this toggle groups manages the selection of only one radio button at a given time
        //radio_dens.setToggleGroup(toggle_radio);
        //radio_DHVap.setToggleGroup(toggle_radio);
        //radio_dens_DHVap.requestFocus();
        //radio_DG_hydration.setToggleGroup(toggle_radio);

        // set to false those booleans indicating if a file has been selected
        PAR_selected = false; RTF_selected = false; COR_selected = false; LPUN_selected = false;
        dens_required = radio_dens.isSelected();
        DHVap_required = radio_DHVap.isSelected();
        DG_hydration_required = radio_DG_hydration.isSelected();
    }

    /**
     * Enable or Disable the button_generate if required
     */
    private void validateButtonGenerate() {
        button_generate.setDisable(true);

        if (PAR_selected == true && RTF_selected == true && COR_selected == true && LPUN_selected == true) {
            button_generate.setDisable(false);
        }
    }

    /**
     * Handles the event when one of the 3 button_open_XXX is pressed
     * button_generate is enabled only when the 3 files have been loaded
     *
     * @param event
     */
    @FXML
    protected void OpenButtonPressed(ActionEvent event) {

        Window myParent = inpfile_TextArea.getScene().getWindow();
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File("."));
        File selectedFile = null;

        chooser.setTitle("Open File");

        if (event.getSource().equals(button_open_PAR)) {
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CHARMM FF parameters file", "*.inp", "*.par", "*.prm"));
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
        } else if (event.getSource().equals(button_open_COR)) {
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Coordinates file " + coor_type.getValue(), coor_type.getValue()));
            selectedFile = chooser.showOpenDialog(myParent);
            if (selectedFile != null) {
                textfield_COR.setText(selectedFile.getAbsolutePath());
                COR_selected = true;
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

        CHARMM_input inp = null;
        try {
            
            // get filenames
            String corname = textfield_COR.getText();
            String rtfname = textfield_RTF.getText();
            String parname = textfield_PAR.getText();
            String lpunname = textfield_LPUN.getText();
            
            // if empty filenames print a pattern user should modify
            //transform it to relative path instead as we have to send data to clusters later
            String folderPath = new File(".").getAbsolutePath();
            corname = corname.length()==0?"ADD_HERE_PATH_TO_COORDINATES_FILE":ResourceUtils.getRelativePath(corname,folderPath);
            rtfname = rtfname.length()==0?"ADD_HERE_PATH_TO_TOPOLOGY_FILE":ResourceUtils.getRelativePath(rtfname,folderPath);
            parname = parname.length()==0?"ADD_HERE_PATH_TO_PARAMETERS_FILE":ResourceUtils.getRelativePath(parname,folderPath);
            lpunname = lpunname.length()==0?"ADD_HERE_PATH_TO_LPUN_FILE":ResourceUtils.getRelativePath(lpunname,folderPath);
            
            dens_required   = toggle_radio.getSelectedToggle().equals(radio_dens);
            DHVap_required   = toggle_radio.getSelectedToggle().equals(radio_DHVap);
            DG_hydration_required = toggle_radio.getSelectedToggle().equals(radio_DG_hydration);
            
            if(dens_required)
                inp = new CHARMM_input_GasPhase(corname, rtfname, parname, lpunname);
            else if(DHVap_required)
                inp = new CHARMM_input_PureLiquid(corname, rtfname, parname, lpunname);
            else{
                logger.error("The impossible happened : unable to determine which radio button was selected !");
                throw new UnknownError("Unknown error related to selection of radio buttons.");
            }
                
            inpfile_TextArea.setText(inp.getContentOfInputFile());
            //System.err.println(inpfile_TextArea.getText());
            RedLabel_Notice.setVisible(true);
            
        } catch (IOException ex) {
            logger.error(ex);
        }

        /**
         * If success enable button for going to step2 tab
         */
        button_save_to_file.setDisable(false);
        
        /**
         * Allow running charmm script
         */
        button_run_CHARMM.setDisable(false);

        RadioButton selected = (RadioButton) toggle_radio.getSelectedToggle();
        String selText = selected.getText();
        
        button_save_to_file.setText("Click to save (" + selText + ")");
        button_run_CHARMM.setText("Run CHARMM (" + selText + ")");
    }

    /**
     * Handles the event when one of the 3 checkBox is selected. If 3 check box are set to true button_generate is enabled as the 3 required files will be chosen later
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
        } else if (event.getSource().equals(later_COR)) {
            COR_selected = later_COR.isSelected();
            button_open_COR.setDisable(later_COR.isSelected());
            textfield_COR.setDisable(later_COR.isSelected());
            coor_type.setDisable(later_COR.isSelected());
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
    @FXML
    protected void ResetFields(ActionEvent event) {
        //clear textcontent
        inpfile_TextArea.clear();
        textfield_PAR.clear();
        textfield_RTF.clear();
        textfield_COR.clear();
        textfield_LPUN.clear();

        //disable elements of tab 1
        PAR_selected = false;
        RTF_selected = false;
        COR_selected = false;
        LPUN_selected = false;

        later_PAR.setSelected(false);
        button_open_PAR.setDisable(later_PAR.isSelected());
        textfield_PAR.setDisable(later_PAR.isSelected());

        later_RTF.setSelected(false);
        button_open_RTF.setDisable(later_RTF.isSelected());
        textfield_RTF.setDisable(later_RTF.isSelected());

        later_COR.setSelected(false);
        button_open_COR.setDisable(later_COR.isSelected());
        textfield_COR.setDisable(later_COR.isSelected());
        coor_type.setDisable(later_COR.isSelected());

        later_LPUN.setSelected(false);
        button_open_LPUN.setDisable(later_LPUN.isSelected());
        textfield_LPUN.setDisable(later_LPUN.isSelected());

        RedLabel_Notice.setVisible(false);
        button_generate.setDisable(true);
        button_save_to_file.setDisable(true);
        button_run_CHARMM.setDisable(true);
        
        button_save_to_file.setText("Click to save");
        button_run_CHARMM.setText("Run CHARMM");
        
        // related to tab2
//        Tab_Step2.setDisable(true);
//        Tab_Pane.getTabs().removeAll(Tab_Step2);
    }

    /**
     *
     * @param event
     */
//    @FXML
//    protected void GoToStep1(ActionEvent event) {
//        Tab_Pane.getSelectionModel().select(Tab_Step1);
//        Tab_Step2.setDisable(true);
//    }

    /**
     *
     * @param event
     */
//    @FXML
//    protected void GoToStep2(ActionEvent event) {
//        Tab_Step2.setDisable(false);
//        Tab_Pane.getTabs().addAll(Tab_Step2);
//        Tab_Pane.getSelectionModel().select(Tab_Step2);
//        Tab_Step2.inpfile_TextArea_Step2 = new TextArea("HELLO");
//        inpfile_TextArea_Step2.setText("HELLO");
//        inpfile_TextArea_Step2.setEditable(true);

//    }

    @FXML
    protected void SaveToFile(ActionEvent event) {
        Window myParent = button_save_to_file.getScene().getWindow();
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File("test"));
        File selectedFile = null;

        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CHARMM input file", "*.inp"));

        selectedFile = chooser.showSaveDialog(myParent);

        BufferedWriter buffw = null;

        if (selectedFile != null) {
            try {
                buffw = new BufferedWriter(new FileWriter(selectedFile));
                buffw.write(inpfile_TextArea.getText());
                buffw.close();
            } catch (IOException ex) {
                logger.error("IOException raised whene generating CHARMM inputfile : " + ex.getMessage());
            }
        } else {
            logger.error("Error while setting file name or save path for CHARMM input file.");
        }
        
        //now that it is saved it may be runned
        this.RedLabel_Notice.setText("You can now try to run the simulation");
        this.button_run_CHARMM.setDisable(false);
        
        this.CHARMM_saved_file = selectedFile;
    }

    // returns null if file isn't relative to folder
//    public static String getRelativePath(String filePath, String folderPath) {
//        //logger.info(filePath + "\t" + folderPath );
//        if (filePath.startsWith(folderPath)) {
//            return filePath.substring(folderPath.length() + 1);
//        } else {
//            return "";
//        }
//    }
    
    @FXML
    protected void runCHARMM(ActionEvent event) {
        charmmWorkflow.execute(WorkflowContext.withInput(null));
    }

    @Override
    protected void fillButtonBar() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}//end of controller class
