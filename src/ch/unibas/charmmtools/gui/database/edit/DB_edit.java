/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.gui.database.edit;

import ch.unibas.charmmtools.gui.database.dataModel.DB_model;
import ch.unibas.fittingwizard.presentation.base.dialog.ModalDialog;
import ch.unibas.fittingwizard.presentation.base.ui.MainWindow;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.effect.BoxBlur;
import javafx.stage.Window;

/**
 * For editing a record of the compounds database
 *
 * @author hedin
 */
public class DB_edit extends ModalDialog {

    @FXML // fx:id="text_id"
    private TextField text_id; // Value injected by FXMLLoader

    @FXML // fx:id="text_idpubchem"
    private TextField text_idpubchem; // Value injected by FXMLLoader

    @FXML // fx:id="text_name"
    private TextField text_name; // Value injected by FXMLLoader

    @FXML // fx:id="text_formula"
    private TextField text_formula; // Value injected by FXMLLoader

    @FXML // fx:id="text_inchi"
    private TextField text_inchi; // Value injected by FXMLLoader

    @FXML // fx:id="text_smiles"
    private TextField text_smiles; // Value injected by FXMLLoader
    
    @FXML // fx:id="text_mass"
    private TextField text_mass; // Value injected by FXMLLoader

    @FXML // fx:id="text_density"
    private TextField text_density; // Value injected by FXMLLoader

    @FXML // fx:id="text_dh"
    private TextField text_dh; // Value injected by FXMLLoader

    @FXML // fx:id="text_dg"
    private TextField text_dg; // Value injected by FXMLLoader
    
    @FXML
    private TextField text_refdh;

    @FXML
    private TextField text_refdg;

    private final Window primary;

    private DB_model model = null;
    
    private boolean wasUpdated = false;

    public DB_edit(DB_model _mod) {
        super("Editing a compound from DB...");

        primary = MainWindow.getPrimaryStage().getScene().getWindow();
        primary.getScene().getRoot().setEffect(new BoxBlur());

        this.model = _mod;
    }

    public void edit() {
        
        /*missing terms*/
        text_id.setText(Integer.toString(model.getId()));
        text_idpubchem.setText(Integer.toString(model.getIdpubchem()));
        text_name.setText(model.getName());
        text_formula.setText(model.getFormula());
        text_inchi.setText(model.getInchi());
        text_smiles.setText(model.getSmiles());
        text_mass.setText(model.getMass());
        text_density.setText(model.getDensity());
        text_dh.setText(model.getDh());
        text_dg.setText(model.getDg());
        text_refdh.setText(model.getRefDh());
        text_refdg.setText(model.getRefDg());
        
        showAndWait();
        
        /* ... */
        
        primary.getScene().getRoot().setEffect(null);
    }

    @FXML
    protected void updateDB(ActionEvent event) {
        logger.info("Attempting an update of the DB");
        
        model.setName(text_name.getText());
        model.setFormula(text_formula.getText());
        model.setInchi(text_inchi.getText());
        model.setSmiles(text_smiles.getText());
        model.setMass(text_mass.getText());
        model.setDensity(text_density.getText());
        model.setDh(text_dh.getText());
        model.setDg(text_dg.getText());
        model.setRefDh(text_refdh.getText());
        model.setRefDg(text_refdg.getText());
        
        wasUpdated = true;
        
        close();
    }

    @FXML
    protected void cancel(ActionEvent event) {
        logger.info("Canceling ; no update of DB decided...");
        primary.getScene().getRoot().setEffect(null);
        wasUpdated = false;
        close();
    }

    /**
     * @return the wasUpdated
     */
    public boolean wasUpdated() {
        return wasUpdated;
    }

}
