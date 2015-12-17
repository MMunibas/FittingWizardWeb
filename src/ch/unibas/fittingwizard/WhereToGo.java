/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard;

import ch.unibas.charmmtools.gui.database.DB_View_Edit_add;
import ch.unibas.charmmtools.gui.loadOutput.CHARMM_GUI_LoadOutput;
import ch.unibas.charmmtools.gui.step1.mdAssistant.CHARMM_GUI_InputAssistant;
import ch.unibas.charmmtools.gui.step4.ParGrid.CHARMM_GUI_Fitgrid;
import ch.unibas.charmmtools.gui.topology.GenerateTopology;
import ch.unibas.fittingwizard.presentation.MoleculeListPage;
import ch.unibas.fittingwizard.presentation.base.WizardPage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

/**
 *
 * @author hedin
 */
public class WhereToGo extends WizardPage {

    private static final String title = "Please choose what to do : ";

    @FXML // fx:id="Choices"
    private ToggleGroup Choices; // Value injected by FXMLLoader

    @FXML // fx:id="button_go"
    private Button button_go; // Value injected by FXMLLoader

    @FXML // fx:id="goDB"
    private RadioButton goDB; // Value injected by FXMLLoader

    @FXML // fx:id="goCHARMM"
    private RadioButton goCHARMM; // Value injected by FXMLLoader

    @FXML // fx:id="goCHARMM"
    private RadioButton goAnalysis; // Value injected by FXMLLoader

    @FXML // fx:id="goMTP"
    private RadioButton goMTP; // Value injected by FXMLLoader

    @FXML // fx:id="goGridScale"
    private RadioButton goGridScale; // Value injected by FXMLLoader

    @FXML // fx:id="goGridScale"
    private RadioButton goTopology; // Value injected by FXMLLoader

    public WhereToGo() {
        super(title);
        removeButtonFromButtonBar(button_initialSelection);
        logger.info("Choosing where to go and what to do");
    }

    @Override
    protected void fillButtonBar() {

    }

    @FXML
    protected void goToScreen(ActionEvent e) {
        RadioButton selected = (RadioButton) Choices.getSelectedToggle();

        if (selected.equals(goMTP)) {

            navigateTo(MoleculeListPage.class, null);

        } else if (selected.equals(goCHARMM)) {
            
            navigateTo(CHARMM_GUI_InputAssistant.class);

        } else if (selected.equals(goAnalysis)) {

            navigateTo(CHARMM_GUI_LoadOutput.class);
            
        } else if (selected.equals(goDB)) {

            navigateTo(DB_View_Edit_add.class, null);

        } else if (selected.equals(goGridScale)) {

            navigateTo(CHARMM_GUI_Fitgrid.class, null);

        } else if (selected.equals(goTopology)) {

            logger.info("Going to topology generate page");
            navigateTo(GenerateTopology.class, null);

        }

    }

}
