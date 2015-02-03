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
import javafx.scene.paint.Paint;

/**
 * FXML Controller class
 *
 * @author hedin
 */
public class CHARMM_GUI_Step2 extends CHARMM_GUI_base {
    
    private static final String title = "LJ fitting procedure Step 2 : visualising output file";

    @FXML
    private TextArea out_left, out_right;
    
    @FXML
    private Label LabelLeft,LabelRight,errorLabelRight,errorLabelLeft;
    
    private Button backStep1,gotoStep3;
    
//    private final boolean errorOccured;
    
    public CHARMM_GUI_Step2(RunCHARMMWorkflow flow, List<CHARMM_InOut> ioList)
    {
        super(title, flow);
        
//        for (CHARMM_InOut ioListIt : ioList) {
//            if (ioListIt instanceof CHARMM_Input) {
//                inp.add((CHARMM_Input) ioListIt);
//            } else if (ioListIt instanceof CHARMM_Output) {
//                out.add((CHARMM_Output) ioListIt);
//            } else {
//                throw new UnknownError("Unknown type of object in List<CHARMM_InOut> : got " + ioListIt.getClass() + " but expected types are " + CHARMM_Input.class + " or " + CHARMM_Output.class);
//            }
//        }
                
        for (CHARMM_InOut ioListIt : ioList) {
            
            Class c = ioListIt.getClass();
            Class sc = c.getSuperclass();
            
            if (sc == CHARMM_Input.class) {
                inp.add((CHARMM_Input) ioListIt);
            } else if (sc == CHARMM_Output.class) {
                out.add((CHARMM_Output) ioListIt);
            } else {
                throw new UnknownError("Unknown type of object in List<CHARMM_InOut> : get " + ioListIt.getClass() + " but expected types are " + CHARMM_Input.class + " or " + CHARMM_Output.class);
            }
            
        }
        
    }//ctor
    
    @Override
    public void initializeData() {
        
//        logger.info(out.get(0).getTextOut());
//        logger.info(out.get(1).getTextOut());
        
        out_left.setText(out.get(0).getTextOut());
        out_right.setText(out.get(1).getTextOut());
        
        LabelLeft.setText(LabelLeft.getText() + " " + out.get(0).getType() );
        LabelRight.setText(LabelRight.getText() + " " + out.get(1).getType() );
        
//        out_left.setEditable(false);
//        out_right.setEditable(false);
        
        if (out.get(0).getErrorOccured())
        {
            errorLabelLeft.setVisible(true);
            errorLabelLeft.setText("Error while running CHARMM calculation ! Please check output :");
            errorLabelLeft.setTextFill(Paint.valueOf("RED"));
            gotoStep3.setDisable(true);
        }

        if (out.get(1).getErrorOccured())
        {
            errorLabelRight.setVisible(true);
            errorLabelRight.setText("Error while running CHARMM calculation ! Please check output :");
            errorLabelRight.setTextFill(Paint.valueOf("RED"));
            gotoStep3.setDisable(true);
        }
    }
    
    @Override
    protected void fillButtonBar() {
        
        backStep1 = ButtonFactory.createButtonBarButton("Back to INPUT file build", new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent actionEvent) {
                List<CHARMM_InOut> myList = new ArrayList<>();
                myList.addAll(inp);
                myList.addAll(out);
                logger.info("Going back to CHARMM input assistant Step1.");
                navigateTo(CHARMM_GUI_Step1.class,myList);
            }
        });
        addButtonToButtonBar(backStep1);
        
        gotoStep3 = ButtonFactory.createButtonBarButton("Proceed to Results panel", new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent actionEvent) {
                List<CHARMM_InOut> myList = new ArrayList<>();
                myList.addAll(inp);
                myList.addAll(out);
                logger.info("Going to Step3 Results.");
                navigateTo(CHARMM_GUI_Step3.class,myList);
            }
        });
        addButtonToButtonBar(gotoStep3);

    }

   
    
}
