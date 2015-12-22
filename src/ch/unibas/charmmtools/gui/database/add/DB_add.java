/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.gui.database.add;

import ch.unibas.charmmtools.gui.database.dataModel.DB_model;
import ch.unibas.charmmtools.gui.database.pubchem.PubChemQuery;
import ch.unibas.fittingwizard.presentation.base.dialog.ModalDialog;
import ch.unibas.fittingwizard.presentation.base.ui.MainWindow;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.KeyEvent;
import javafx.stage.Window;

/**
 * For editing a record of the compounds database
 *
 * @author hedin
 */
public class DB_add extends ModalDialog {

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
    
    @FXML // fx:id="button_insert"
    private Button button_insert; // Value injected by FXMLLoader
    
    @FXML
    private Button b_idpubchem;
        
    @FXML
    private Button b_name;

    @FXML
    private Button b_formula;
    
    @FXML
    private Button b_inchi;

    @FXML
    private Button b_smiles;

    private final Window primary;

    private DB_model model = null;

    private boolean required_idpubchem = false;
    private boolean required_name = false;
    private boolean required_formula = false;
    private boolean required_inchi = false;
    private boolean required_smiles = false;

    private boolean readyToAdd = false;

    public DB_add(DB_model _mod) {
        super("Adding a compound to DB...");

        primary = MainWindow.getPrimaryStage().getScene().getWindow();
        primary.getScene().getRoot().setEffect(new BoxBlur());

        this.model = _mod;
        
        this.setResizable(false);
    }

    public void add() {

        showAndWait();

        readyToAdd = false;

        primary.getScene().getRoot().setEffect(null);
    }

    @FXML
    protected void checkNotEmptyTextField(KeyEvent event) {
        
        TextField field = (TextField) event.getSource();
        
        if (field.getText().length() == 0) {
            
            if (field == text_idpubchem) {
                required_idpubchem = false;
            } else if (field == text_name) {
                required_name = false;
            } else if (field == text_formula) {
                required_formula = false;
            } else if (field == text_inchi) {
                required_inchi = false;
            } else if (field == text_smiles) {
                required_smiles = false;
            }
                        
        } else {

            if (field == text_idpubchem) {
                required_idpubchem = true;
            } else if (field == text_name) {
                required_name = true;
            } else if (field == text_formula) {
                required_formula = true;
            } else if (field == text_inchi) {
                required_inchi = true;
            } else if (field == text_smiles) {
                required_smiles = true;
            }
            
        }
        
        setFieldStyle(field);
        
        validateAllFieldsOK();

    }
    
    private void setFieldStyle(TextField field)
    {
        
        if (field.getText().length() == 0) {
                    field.setStyle("-fx-border-color:red ;"
                    + " -fx-border-width:2 ;"
                    + " -fx-border-radius:3");
        } else {
                        field.setStyle("-fx-border-color:green ;"
                    + " -fx-border-width:2 ;"
                    + " -fx-border-radius:3");
        }
        
    }
    
    
    
    private void validateAllFieldsOK()
    {
        if(required_idpubchem && required_name && required_formula && required_inchi && required_smiles)
            button_insert.setDisable(false);
        else
            button_insert.setDisable(true);
    }

    public boolean addingOK()
    {
        return readyToAdd;
    }
    
    @FXML
    protected void insert(ActionEvent event) {
        logger.info("Attempting compound insert in DB...");
        primary.getScene().getRoot().setEffect(null);
        readyToAdd = true;
        close();
    }

    @FXML
    protected void cancel(ActionEvent event) {
        logger.info("Cancelled compound insertion in DB...");
        primary.getScene().getRoot().setEffect(null);
        readyToAdd = false;
        close();
    }
   
    /**
     * Query pubchem for more details concerning a compound
     * 
     * @param event 
     */
    @FXML
    protected void pubchemQuery(ActionEvent event) {
        Button button = (Button) event.getSource();
        logger.info("Performing PubChem query for field " + button.getId());
        
        PubChemQuery query = new PubChemQuery(this.getScene());
        
        if(button==b_idpubchem)
        {
            model = query.byPubChemId(text_idpubchem.getText());
        }
        else if(button==b_name)
        {
            model = query.byName(text_name.getText());
        }
        else if(button==b_formula)
        {
            model = query.byFormula(text_formula.getText());
        }
        else if(button==b_inchi)
        {
            model = query.byInchi(text_inchi.getText());
        }
        else if(button==b_smiles)
        {
            model = query.bySmiles(text_smiles.getText());
        }
        
        //attempt automatic filling of fields from pubchem
        autoFill();
    }
    
    private void autoFill()
    {
        //fill each of the text field with pubchem content
        text_idpubchem.setText( Integer.toString(model.getIdpubchem()));
        required_idpubchem = (text_idpubchem.getLength() != 0);
        setFieldStyle(text_idpubchem);
        
        text_name.setText( model.getName() );
        required_name = (text_name.getLength() != 0);
        setFieldStyle(text_name);
        
        text_formula.setText( model.getFormula() );
        required_formula = (text_formula.getLength() != 0);
        setFieldStyle(text_formula);
        
        text_inchi.setText( model.getInchi() );
        required_inchi = (text_inchi.getLength() != 0);
        setFieldStyle(text_inchi);
        
        text_smiles.setText( model.getSmiles());
        required_smiles = (text_smiles.getLength() != 0);
        setFieldStyle(text_smiles);
        
        text_mass.setText( model.getMass() );
        
        validateAllFieldsOK();
    }
    
}
