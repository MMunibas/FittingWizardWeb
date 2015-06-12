/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.gui.loadOutput;

import ch.unibas.charmmtools.generate.CHARMM_InOut;
import ch.unibas.charmmtools.generate.outputs.CHARMM_Output_GasPhase;
import ch.unibas.charmmtools.generate.outputs.CHARMM_Output_PureLiquid;
import ch.unibas.charmmtools.gui.step3.CHARMM_GUI_ShowResults;
import ch.unibas.fittingwizard.presentation.base.WizardPage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/**
 *
 * @author hedin
 */
public class CHARMM_GUI_LoadOutput extends WizardPage {

    private static final String title = "Extracting Thermodynamic properties from Output files";

    @FXML // fx:id="text_liquid"
    private TextField text_liquid; // Value injected by FXMLLoader

    @FXML // fx:id="open_gas_vdw"
    private Button open_gas_vdw; // Value injected by FXMLLoader

    @FXML // fx:id="open_gas_mtp"
    private Button open_gas_mtp; // Value injected by FXMLLoader

    @FXML // fx:id="text_gas_vdw"
    private TextField text_gas_vdw; // Value injected by FXMLLoader

    @FXML // fx:id="text_gas_mtp"
    private TextField text_gas_mtp; // Value injected by FXMLLoader

    @FXML // fx:id="open_gas"
    private Button open_gas; // Value injected by FXMLLoader

    @FXML // fx:id="text_gas"
    private TextField text_gas; // Value injected by FXMLLoader

    @FXML // fx:id="open_liquid"
    private Button open_liquid; // Value injected by FXMLLoader

    @FXML // fx:id="open_solvent_mtp"
    private Button open_solvent_mtp; // Value injected by FXMLLoader

    @FXML // fx:id="open_solvent_vdw"
    private Button open_solvent_vdw; // Value injected by FXMLLoader

    @FXML // fx:id="text_solvent_vdw"
    private TextField text_solvent_vdw; // Value injected by FXMLLoader

    @FXML // fx:id="text_solvent_mtp"
    private TextField text_solvent_mtp; // Value injected by FXMLLoader

    @FXML // fx:id="button_process"
    private Button button_process; // Value injected by FXMLLoader
        
    private File gas_file, liquid_file;
    private File gas_vdw_file, gas_mtp_file;
    private File solvent_vdw_file, solvent_mtp_file;

    private boolean gas_selected = false;
    private boolean liquid_selected = false;

    private boolean gas_vdw_selected = false;
    private boolean gas_mtp_selected = false;

    private boolean solvent_vdw_selected = false;
    private boolean solvent_mtp_selected = false;

    public CHARMM_GUI_LoadOutput() {
        super(title);
    }

    @Override
    protected void fillButtonBar() {

    }

    @FXML
    void openFile(ActionEvent event) {
        Window myParent = open_gas.getScene().getWindow();
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File("./test"));
        File selectedFile = null;

        chooser.setTitle("Open File");

        if (event.getSource().equals(open_gas)) {

            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CHARMM output file (*.out,*.log)",
                    "*.out", "*.log"));
            selectedFile = chooser.showOpenDialog(myParent);
            if (selectedFile != null) {
                text_gas.setText(selectedFile.getAbsolutePath());
                gas_file = new File(selectedFile.getAbsolutePath());
                gas_selected = true;
            }

        } else if (event.getSource().equals(open_liquid)) {

            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CHARMM output file (*.out,*.log)",
                    "*.out", "*.log"));
            selectedFile = chooser.showOpenDialog(myParent);
            if (selectedFile != null) {
                text_liquid.setText(selectedFile.getAbsolutePath());
                liquid_file = new File(selectedFile.getAbsolutePath());
                liquid_selected = true;
            }

        } else if (event.getSource().equals(open_gas_vdw)) {

            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CHARMM output file (*.out,*.log)",
                    "*.out", "*.log"));
            selectedFile = chooser.showOpenDialog(myParent);
            if (selectedFile != null) {
                text_gas_vdw.setText(selectedFile.getAbsolutePath());
                gas_vdw_file = new File(selectedFile.getAbsolutePath());
                gas_vdw_selected = true;
            }

        } else if (event.getSource().equals(open_gas_mtp)) {

            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CHARMM output file (*.out,*.log)",
                    "*.out", "*.log"));
            selectedFile = chooser.showOpenDialog(myParent);
            if (selectedFile != null) {
                text_gas_mtp.setText(selectedFile.getAbsolutePath());
                gas_mtp_file = new File(selectedFile.getAbsolutePath());
                gas_mtp_selected = true;
            }

        } else if (event.getSource().equals(open_solvent_vdw)) {

            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CHARMM output file (*.out,*.log)",
                    "*.out", "*.log"));
            selectedFile = chooser.showOpenDialog(myParent);
            if (selectedFile != null) {
                text_solvent_vdw.setText(selectedFile.getAbsolutePath());
                solvent_vdw_file = new File(selectedFile.getAbsolutePath());
                solvent_vdw_selected = true;
            }

        } else if (event.getSource().equals(open_solvent_mtp)) {

            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CHARMM output file (*.out,*.log)",
                    "*.out", "*.log"));
            selectedFile = chooser.showOpenDialog(myParent);
            if (selectedFile != null) {
                text_solvent_mtp.setText(selectedFile.getAbsolutePath());
                solvent_mtp_file = new File(selectedFile.getAbsolutePath());
                solvent_mtp_selected = true;
            }

        }
        
//        if (gas_selected && liquid_selected && gas_vdw_selected && gas_mtp_selected && solvent_vdw_selected && solvent_mtp_selected) {
//            
//            this.button_process.setDisable(false);
//            
//        }

    } //end openFile()

    
    @FXML
    void parseFiles(ActionEvent event) {
        
        List<CHARMM_InOut> ioList = new ArrayList<>();
        ioList.add(new CHARMM_Output_GasPhase(gas_file));
        ioList.add(new CHARMM_Output_PureLiquid(liquid_file));
                
        
        this.navigateTo(CHARMM_GUI_ShowResults.class, ioList);
        
    }
    
    
}
