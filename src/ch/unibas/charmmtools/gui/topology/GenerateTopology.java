/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.gui.topology;

import ch.unibas.charmmtools.files.coordinates.COR_generate;
import ch.unibas.charmmtools.files.coordinates.PDB_generate;
import ch.unibas.charmmtools.files.coordinates.coordinates_writer;
import ch.unibas.charmmtools.files.structure.PSF_generate;
import ch.unibas.charmmtools.files.topology.RTF_generate;
import ch.unibas.charmmtools.gui.CHARMM_GUI_base;
import ch.unibas.charmmtools.gui.MyTab;
import ch.unibas.charmmtools.workflows.RunCHARMMWorkflow;
import ch.unibas.fittingwizard.application.xyz.XyzFile;
import ch.unibas.fittingwizard.application.xyz.XyzFileParser;
import ch.unibas.fittingwizard.presentation.base.WizardPage;
import ch.unibas.fittingwizard.presentation.base.dialog.OverlayDialog;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/**
 *
 * @author hedin
 */
public class GenerateTopology extends CHARMM_GUI_base {

    private static final String title = "Generating custom PSF TOP PDB COR files using a XYZ file";
    private static final String csvName = "scripts/atomic_db.csv";

    // from FXML file
    @FXML // fx:id="xyzOpen"
    protected Button xyzOpen; // Value injected by FXMLLoader

    @FXML // fx:id="buttonGenerate"
    protected Button buttonGenerate; // Value injected by FXMLLoader

    @FXML // fx:id="buttonGenerate"
    protected Button buttonSave; // Value injected by FXMLLoader

    @FXML // fx:id="xyzPath"
    protected TextField xyzPath; // Value injected by FXMLLoader

    @FXML // fx:id="tabPane"
    private TabPane tabPane; // Value injected by FXMLLoader

    //private variables
    private XyzFile myXYZ;
    private boolean xyzSelected = false;
    private boolean canSaveFiles = false;

    private List<MyTab> tab_list = new ArrayList<>();
    private List<coordinates_writer> filesList = new ArrayList<>();

    public GenerateTopology(RunCHARMMWorkflow flow) {
        super(title, flow);
    }

    @Override
    public void initialize() {
        this.tabPane.getTabs().clear();
    }

    @FXML
    protected void openFile(ActionEvent event) {

        Window myParent = buttonGenerate.getScene().getWindow();
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File("."));

        chooser.setTitle("Open File");

        File fi = null;

        if (event.getSource().equals(xyzOpen)) {
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XYZ coordinates file (*.xyz)",
                    "*.xyz"));
            fi = chooser.showOpenDialog(myParent);
            if (fi != null) {
                xyzPath.setText(fi.getAbsolutePath());
                xyzSelected = true;
            }
        } else {
            throw new UnknownError("Unknown Event in OpenButtonPressed(ActionEvent event)");
        }

        myXYZ = XyzFileParser.parse(fi);

    }

    @FXML
    protected void GenerateFiles(ActionEvent event) {

        RTF_generate rtff = null;
        PSF_generate psff = null;
        PDB_generate pdbf = null;
        COR_generate corf = null;

        try {
            //generates a topology file
            rtff = new RTF_generate(myXYZ, csvName);

            //then a PSF file re-using data from PSF 
            psff = new PSF_generate(rtff);

            //then pdb file
            pdbf = new PDB_generate(psff);

            //and cor file
            corf = new COR_generate(psff);

            tab_list.add(new MyTab("RTF file", rtff.getTextContent()));
            filesList.add(rtff);

            tab_list.add(new MyTab("PSF file", psff.getTextContent()));
            filesList.add(psff);

            tab_list.add(new MyTab("PDB file", pdbf.getTextContent()));
            filesList.add(pdbf);

            tab_list.add(new MyTab("COR file", corf.getTextContent()));
            filesList.add(corf);

            tabPane.getTabs().addAll(tab_list);

        } catch (IOException ex) {
            logger.error("Exception while generating rtf and psf files : " + ex);
        }

        canSaveFiles = true;
        this.buttonSave.setDisable(false);

    }

    @FXML
    protected void saveFiles(ActionEvent event) {

        boolean failure = false;
        for (int i = 0; i < tab_list.size(); i++) {
            try {
                String s = tab_list.get(i).getContentText();
                filesList.get(i).setModifiedTextContent(s);
                filesList.get(i).writeFile(work_directory);
            } catch (IOException ex) {
                logger.error("Error while saving to file : " + ex);
                failure = true;
            }
        }

        if (failure) {
            OverlayDialog.showError("Error while saving files", "Error while saving your files in directory : " + this.work_directory.getAbsolutePath());
        } else {
            OverlayDialog.informUser("Files saved properly", "All your files were saved in directory : " + this.work_directory.getAbsolutePath());
        }

    }

    @Override
    protected void fillButtonBar() {

    }

}
