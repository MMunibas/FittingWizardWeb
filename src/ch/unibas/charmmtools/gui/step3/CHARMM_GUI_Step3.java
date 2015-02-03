/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.gui.step3;

import ch.unibas.charmmtools.gui.CHARMM_GUI_base;
import ch.unibas.charmmtools.gui.step2.CHARMM_GUI_Step2;
import ch.unibas.charmmtools.gui.step1.CHARMM_GUI_Step1;
import ch.unibas.charmmtools.scripts.CHARMM_InOut;
import ch.unibas.charmmtools.scripts.CHARMM_Input;
import ch.unibas.charmmtools.scripts.CHARMM_Input_GasPhase;
import ch.unibas.charmmtools.scripts.CHARMM_Input_PureLiquid;
import ch.unibas.charmmtools.scripts.CHARMM_Output;
import ch.unibas.charmmtools.workflows.RunCHARMMWorkflow;
import ch.unibas.fittingwizard.presentation.base.ButtonFactory;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

/**
 *
 * @author hedin
 */
public class CHARMM_GUI_Step3 extends CHARMM_GUI_base{

    private static final String title = "LJ fitting procedure Step 3 : Results";
    
    private Button backStep1,backStep2;
     
    private String[] arraystrings;
    
    public CHARMM_GUI_Step3(RunCHARMMWorkflow flow, List<CHARMM_InOut> ioList) throws Exception {
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
        
        calc_density();
        calc_vapor();

    }

//    @Override
//    public void initializeData() {
//    }

    @Override
    protected void fillButtonBar() {
        backStep1 = ButtonFactory.createButtonBarButton("Back to INPUT file build", new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent actionEvent) {
                List<CHARMM_InOut> myList = new ArrayList<CHARMM_InOut>();
                myList.addAll(inp);
                myList.addAll(out);
                logger.info("Going back to CHARMM input assistant Step1.");
                navigateTo(CHARMM_GUI_Step1.class,myList);
            }
        });
        addButtonToButtonBar(backStep1);
        
        backStep2 = ButtonFactory.createButtonBarButton("Back to OUTPUT file ", new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent actionEvent) {
                List<CHARMM_InOut> myList = new ArrayList<CHARMM_InOut>();
                myList.addAll(inp);
                myList.addAll(out);
                logger.info("Going to Step2 Results.");
                navigateTo(CHARMM_GUI_Step2.class,myList);
            }
        });
        addButtonToButtonBar(backStep2);

    }
    
    private void splitOutFile(){
//        arraystrings = out.getTextOut().split("\n");
    }
    
    private String findInArray(String pattern){
        
        for(String st : arraystrings)
        {
            if (st.contains(pattern))
                return st;
        }
        
        return "";
    }
    
    private void calc_density(){
        
        this.splitOutFile();
        
        String constr = this.findInArray("constraints will");
        String aver = this.findInArray("AVER>");
    }
    
    private void calc_vapor(){

    }

    
}//class
    

