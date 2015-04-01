/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.presentation.base;

import ch.unibas.charmmtools.gui.database.CHARMM_GUI_db;
import ch.unibas.charmmtools.gui.step1.CHARMM_GUI_InputAssistant;
import ch.unibas.fittingwizard.presentation.MoleculeListPage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

/**
 *
 * @author hedin
 */
public class WhereToGo extends WizardPage{

    private static final String title = "Please choose what to do : ";
    
    @FXML // fx:id="Choices"
    private ToggleGroup Choices; // Value injected by FXMLLoader

    @FXML // fx:id="button_go"
    private Button button_go; // Value injected by FXMLLoader

    @FXML // fx:id="goDB"
    private RadioButton goDB; // Value injected by FXMLLoader

    @FXML // fx:id="goCHARMM"
    private RadioButton goCHARMM; // Value injected by FXMLLoader

    @FXML // fx:id="goMTP"
    private RadioButton goMTP; // Value injected by FXMLLoader

    
    public WhereToGo() {
        super(title);
        logger.info("Choosing where to go and what to do");
    }

    @Override
    protected void fillButtonBar() {

    }
    
    @FXML
    protected void goToScreen(ActionEvent e){
        RadioButton selected = (RadioButton) Choices.getSelectedToggle();
        
        if (selected.equals(goMTP))
            navigateTo(MoleculeListPage.class, null);
        else if(selected.equals(goCHARMM))
            navigateTo(CHARMM_GUI_InputAssistant.class, null);
        else if (selected.equals(goDB))
            navigateTo(CHARMM_GUI_db.class, null);
    }
    
}
