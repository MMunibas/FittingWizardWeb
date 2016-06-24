/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.gui.loadOutput;

import ch.unibas.charmmtools.gui.step3.showResults.CHARMM_GUI_ShowResults;
import ch.unibas.fitting.shared.charmm.generate.CHARMM_InOut;
import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Generator_DGHydr;
import ch.unibas.fitting.shared.charmm.generate.outputs.CHARMM_Output_GasPhase;
import ch.unibas.fitting.shared.charmm.generate.outputs.CHARMM_Output_PureLiquid;
import ch.unibas.fittingwizard.gaussian.base.WizardPage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
        
    private File gas_file=null, liquid_file=null;
    private File gas_vdw_file=null, gas_mtp_file=null;
    private File solvent_vdw_file=null, solvent_mtp_file=null;

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
        chooser.setInitialDirectory(new File("."));
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
        
        if(gas_file==null)
            gas_file = new File(text_gas.getText());
        
        if(liquid_file==null)
            liquid_file = new File(text_liquid.getText());
        
        if(gas_vdw_file==null)
            gas_vdw_file = new File(text_gas_vdw.getText());
        
        if(gas_mtp_file==null)
            gas_mtp_file = new File(text_gas_mtp.getText());
        
        if(solvent_vdw_file==null)
            solvent_vdw_file = new File(text_solvent_vdw.getText());
        
        if(solvent_mtp_file==null)
            solvent_mtp_file = new File(text_solvent_mtp.getText());
                
        ioList.add(new CHARMM_Output_GasPhase(gas_file));
        ioList.add(new CHARMM_Output_PureLiquid(liquid_file));
        
        ioList.add(new CHARMM_Generator_DGHydr(gas_vdw_file,"gas_vdw"));
        ioList.add(new CHARMM_Generator_DGHydr(gas_mtp_file,"gas_mtp"));
        ioList.add(new CHARMM_Generator_DGHydr(solvent_vdw_file,"solvent_vdw"));
        ioList.add(new CHARMM_Generator_DGHydr(solvent_mtp_file,"solvent_mtp"));
        
        this.navigateTo(CHARMM_GUI_ShowResults.class, ioList);
        
    }
    
    
}
