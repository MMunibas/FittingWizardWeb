/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.gui.database.view;

import ch.unibas.charmmtools.gui.database.pubchem.DB_pubchemWebView;
import ch.unibas.charmmtools.gui.database.dataModel.DB_model;
import ch.unibas.fittingwizard.application.Visualization;
import ch.unibas.fittingwizard.presentation.base.dialog.ModalDialog;
import ch.unibas.fittingwizard.presentation.base.ui.MainWindow;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * For editing a record of the compounds database
 *
 * @author hedin
 */
public class DB_view extends ModalDialog {

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
    
    @FXML // fx:id="text_refdh"
    private TextField text_refdh; // Value injected by FXMLLoader

    @FXML // fx:id="text_refdg"
    private TextField text_refdg; // Value injected by FXMLLoader
    
    @FXML // fx:id="imageview_2d"
    private ImageView imageview_2d; // Value injected by FXMLLoader
        
    @FXML // fx:id="imageview_2d"
    private ImageView imageview_3d; // Value injected by FXMLLoader

    private final Stage stage;
    private final Window window;

    private DB_model model = null;
    
    private final String url;
    private final String url2d;
    private final String url3d;

    private final Visualization vis;
    
    public DB_view(DB_model _mod) {
        super("Viewing a compound from DB...");

        this.stage = MainWindow.getPrimaryStage();
        this.window = stage.getScene().getWindow();
        this.window.getScene().getRoot().setEffect(new BoxBlur());

        this.model = _mod;
        
        this.url = "http://pubchem.ncbi.nlm.nih.gov/compound/" + model.getIdpubchem() ;
        this.url2d = "http://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/" + model.getIdpubchem() + "/PNG?record_type=2d&image_size=large";
        this.url3d = "http://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/" + model.getIdpubchem() + "/PNG?record_type=3d&image_size=large";
        
        this.setResizable(false);
        
        this.vis = new Visualization(stage);

    }

    public void view() {
        
        /*missing terms*/
        text_id.setText(model.getId());
        text_idpubchem.setText(model.getIdpubchem());
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
        
        logger.info("Trying to load : " + url2d);
        logger.info("Trying to load : " + url3d);
        
        imageview_2d.setImage(new Image(url2d, true));
        imageview_3d.setImage(new Image(url3d, true));
        
        showAndWait();
        
        /* ... */
        
        window.getScene().getRoot().setEffect(null);
    }

    @FXML
    protected void done(ActionEvent event) {
        logger.info("Visualisation of DB record done...");
        window.getScene().getRoot().setEffect(null);
        close();
    }
    
    @FXML
    protected void showJMol(ActionEvent event)
    {
        vis.showSMILES(model.getSmiles());
    }
       
    @FXML
    protected void showPubChem(ActionEvent event)
    {  
        DB_pubchemWebView pubchem_browser = new DB_pubchemWebView(url,this.getScene());
        pubchem_browser.view(); 
    }
    
}
