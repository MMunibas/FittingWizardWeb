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
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 *
 * @author hedin
 */
public class CHARMM_GUI_Step3 extends CHARMM_GUI_base{

    private static final String title = "LJ fitting procedure Step 3 : Results";
    
    private Button backStep1,backStep2;
    
//    private class CHARMMdata{
//        private StringProperty str;
//        public void setStr(String value){ strProperty().set(value); }
//        public String getStr() { return strProperty().get(); }
//        public StringProperty strProperty(){
//            if (str == null) str = new SimpleStringProperty(this, "str");
//            return str; 
//        }
//    }
    
    @FXML
    private TableView<String> tabView;
    @FXML
    private TableColumn<String,String> tabCol;
    
    private ObservableList<String> oblist;
//    private String[] arraystrings;
    
    public CHARMM_GUI_Step3(RunCHARMMWorkflow flow, List<CHARMM_InOut> ioList) throws Exception {
        super(title, flow);
        inp = (CHARMM_Input)  ioList.get(0);
        out = (CHARMM_Output) ioList.get(1);
        
        if(inp.getClass()==CHARMM_Input_GasPhase.class)
        {
            calc_density();
        }
        else if(inp.getClass()==CHARMM_Input_PureLiquid.class)
        {
            calc_vapor();
        }
        else
        {
            throw new Exception("Unsupported type of class when parsing CHARMM output in constructor of CHARMM_GUI_Step3 : " + inp.getClass());
        }
    }

    @Override
    public void initializeData() {
        oblist = FXCollections.observableArrayList();
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
        
        backStep2 = ButtonFactory.createButtonBarButton("Back to OUTPUT file ", new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent actionEvent) {
                List<CHARMM_InOut> myList = new ArrayList<CHARMM_InOut>();
                myList.add(0, inp);
                myList.add(1, out);
                logger.info("Going to Step2 Results.");
                navigateTo(CHARMM_GUI_Step2.class,myList);
            }
        });
        addButtonToButtonBar(backStep2);

    }
    
    private void calc_density(){

    }
    
    private void calc_vapor(){

    }

    
}//class
    

