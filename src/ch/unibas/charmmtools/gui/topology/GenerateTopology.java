/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.gui.topology;

import ch.unibas.charmmtools.files.coordinates.PDB;
import ch.unibas.charmmtools.files.coordinates.PDB_generate;
import ch.unibas.charmmtools.files.structure.PSF;
import ch.unibas.charmmtools.files.structure.PSF_generate;
import ch.unibas.charmmtools.files.topology.RTF;
import ch.unibas.charmmtools.files.topology.RTF_generate;
import ch.unibas.charmmtools.gui.MyTab;
import ch.unibas.fittingwizard.application.xyz.XyzFile;
import ch.unibas.fittingwizard.application.xyz.XyzFileParser;
import ch.unibas.fittingwizard.presentation.base.WizardPage;
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
public class GenerateTopology extends WizardPage {

    private static final String title = "Generating custom PSF and TOP files using a XYZ file";
    private static final String csvName = "test/atomic_db.csv";

    // from FXML file
    @FXML // fx:id="xyzOpen"
    protected Button xyzOpen; // Value injected by FXMLLoader

    @FXML // fx:id="buttonGenerate"
    protected Button buttonGenerate; // Value injected by FXMLLoader

    @FXML // fx:id="xyzPath"
    protected TextField xyzPath; // Value injected by FXMLLoader

    @FXML // fx:id="tabPane"
    private TabPane tabPane; // Value injected by FXMLLoader

    //private variables
    private XyzFile myXYZ;
    private boolean xyzSelected;

    private List<MyTab> tab_list = new ArrayList<>();

    public GenerateTopology() {
        super(title);
    }

    @Override
    public void initialize() {
        this.tabPane.getTabs().clear();
    }

    @FXML
    protected void openFile(ActionEvent event) {

        Window myParent = buttonGenerate.getScene().getWindow();
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File("./test"));

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

        RTF rtff = null;
        PSF psff = null;
        PDB pdbf = null;

        try {
            //generates a topology file
            rtff = new RTF_generate(myXYZ, csvName);
            //then a PSF file re-using data from PSF 
            psff = new PSF_generate(rtff);
            //then pdb file
            pdbf = new PDB_generate(psff);

            tab_list.add(new MyTab("RTF file", rtff.getTextContent()));
            tab_list.add(new MyTab("PSF file", psff.getTextContent()));
            tab_list.add(new MyTab("PDB file", pdbf.getTextContent()));
            
            tabPane.getTabs().addAll(tab_list);

        } catch (IOException ex) {
            logger.error("Exception while generating rtf and psf files : " + ex);
        }

    }

    @Override
    protected void fillButtonBar() {

    }

}
