/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.gui.step2;

import ch.unibas.charmmtools.gui.step3.CHARMM_GUI_Step3;
import ch.unibas.charmmtools.gui.CHARMM_GUI_base;
import ch.unibas.charmmtools.gui.step1.CHARMM_GUI_Step1;
import ch.unibas.charmmtools.scripts.CHARMM_InOut;
import ch.unibas.charmmtools.scripts.CHARMM_Input;
import ch.unibas.charmmtools.scripts.CHARMM_Output;
import ch.unibas.charmmtools.workflows.RunCHARMMWorkflow;
import ch.unibas.fittingwizard.presentation.base.ButtonFactory;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

/**
 * FXML Controller class
 *
 * @author hedin
 */
public class CHARMM_GUI_Step2 extends CHARMM_GUI_base {
    
    private static final String title = "LJ fitting procedure Step 2 : visualising output file";

    @FXML
    private TextArea inpArea, outArea;
    
    @FXML
    private Label errorLabel;
    
    private Button backStep1,gotoStep3;
    
    private final boolean errorOccured;
    
    public CHARMM_GUI_Step2(RunCHARMMWorkflow flow, List<CHARMM_InOut> ioList)
    {
        super(title, flow);
        this.inp = (CHARMM_Input)  ioList.get(0);
        this.out = (CHARMM_Output) ioList.get(1);
//        logger.info("CHARMM_Input object  : " + inp.toString() + inp.getContentOfInputFile());
//        logger.info("CHARMM_Output object : " + out.toString() + out.getTextOut());
        
        errorOccured = out.getErrorOccured();
        
        if (errorOccured)
        {
            errorLabel.setVisible(true);
//            gotoStep3.setDisable(true);
        }
    }
    
    @Override
    public void initializeData() {
        inpArea.setText(inp.getContentOfInputFile());
        outArea.setText(out.getTextOut());
        inpArea.setEditable(false);
        outArea.setEditable(false);
    }
    
    @Override
    protected void fillButtonBar() {
        
        backStep1 = ButtonFactory.createButtonBarButton("Back to INPUT file build", new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent actionEvent) {
                List<CHARMM_InOut> myList = new ArrayList<CHARMM_InOut>();
                myList.add(0, inp);
                myList.add(1, out);
                logger.info("Going back to CHARMM input assistant Step1.");
                navigateTo(CHARMM_GUI_Step1.class,myList);
            }
        });
        addButtonToButtonBar(backStep1);
        
        gotoStep3 = ButtonFactory.createButtonBarButton("Proceed to Results panel", new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent actionEvent) {
                List<CHARMM_InOut> myList = new ArrayList<CHARMM_InOut>();
                myList.add(0, inp);
                myList.add(1, out);
                logger.info("Going to Step3 Results.");
                navigateTo(CHARMM_GUI_Step3.class,myList);
            }
        });
        addButtonToButtonBar(gotoStep3);

    }

   
    
}
