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
import ch.unibas.charmmtools.scripts.CHARMM_Output;
import ch.unibas.charmmtools.workflows.RunCHARMMWorkflow;
import ch.unibas.fittingwizard.presentation.base.ButtonFactory;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 *
 * @author hedin
 */
public class CHARMM_GUI_Step3 extends CHARMM_GUI_base{

    private static final String title = "LJ fitting procedure Step 3 : Results";
    
    private Button backStep1,backStep2;
     
    private double box,density;
    private int nconstr;
    private double egas, eliq;
    private double deltaH;
    
    @FXML
    private TextField dens_field, dhvap_field;
    
    public CHARMM_GUI_Step3(RunCHARMMWorkflow flow, List<CHARMM_InOut> ioList) throws Exception {
        super(title, flow);
                
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
    
    private List<String> splitOutFile(String[] Array){
        
        List<String> arrList = new ArrayList<>();
        
        for(String st : Array)
        {
            arrList.add(st);
        }
        
        return arrList;

    }
    
    private List<String> findInArray(List<String> arr, String pattern){
        
        List<String> res = new ArrayList<>();
        
        for(String st : arr)
        {
            if (st.contains(pattern))
                res.add(st);
        }
        
        return res;
    }
    
    private void calc_density(){
        
        // get the output file for pure liquid
        List<String> pureLiqOut = splitOutFile(out.get(1).getTextOut().split("\n"));
        // get all lines containing "AVER PRESS>"
        List<String> boxLen = findInArray(pureLiqOut,"AVER PRESS>");
        //keep last column containing volume in cubic angstroems
        String[] L = boxLen.get(boxLen.size()-1).split("\\s+");
        
//        logger.info(L);
        box = Double.valueOf(L[6]);
        box = Math.pow(box, 1.0/3.0);
        logger.info("Box length [A] : " + box);
        
        density = 94.12 * 150 / (0.602 * box*box*box);
        logger.info("density : " + density);

    }
    
    private void calc_vapor(){

        // get the output file for gas phase
        List<String> gasPhaseOut = splitOutFile(out.get(0).getTextOut().split("\n"));
        
        // get the output file for pure liquid
        List<String> pureLiqOut = splitOutFile(out.get(1).getTextOut().split("\n"));
        
        // will contain number of constraints
        List<String> constr = findInArray(gasPhaseOut,"constraints will");
        String[] cons = constr.get(constr.size()-1).split("\\s+");
        nconstr = Integer.valueOf(cons[1]);
        logger.info("number of constraints : " + nconstr);
        
        //energy from gas phase
        List<String> averlist = findInArray(gasPhaseOut,"AVER>");
        String[] avL = averlist.get(averlist.size()-1).split("\\s+");
        egas = Double.valueOf(avL[6]) + 0.5*0.59*(3.0*13.0 - 6 - nconstr);
        
        //energy from liquid phase
        List<String> averlist2 = findInArray(pureLiqOut,"AVER>");
        String[] avL2 = averlist2.get(averlist2.size()-1).split("\\s+");
        eliq = Double.valueOf(avL2[6]) / 150.0 ;
        
        //estimate deltaH in kcal/mol
        deltaH = egas - eliq + 0.59;
        logger.info("DeltaH [kcal/mol] : " + deltaH);
        
        
    }

    @Override
    public void initializeData() {
        dens_field.setText(Double.toString(density));
        dhvap_field.setText(Double.toString(deltaH));
    }

    
    
    
}//class
    

