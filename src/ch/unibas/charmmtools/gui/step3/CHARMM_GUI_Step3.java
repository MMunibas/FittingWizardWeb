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
import ch.unibas.charmmtools.scripts.CHARMM_Output_GasPhase;
import ch.unibas.charmmtools.scripts.CHARMM_Output_PureLiquid;
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
    
    /*
     * Those values are parsed from the output file as the may be useful later
     */
    private double box,density;
    private final static String find_natom = "Number of atoms";
    private final static String find_nconstr = "constraints will";
    private int natom,nconstr;
    private double egas, eliq;
    private double deltaH;
    // boltzmann constant in kcal/mol-1/k-1
    private final static double kBoltz = 0.0019872041;
    private final static String find_temp = "FINALT =";
    private double temp;
    private final static String find_nres = "Number of residues";
    private int nres;
    private double mmass;
    
    List<String> gasPhaseOut;
    List<String> pureLiqOut;
    
    @FXML
    private TextField temp_field,mmass_field,nres_field,dens_field,dhvap_field;
    
    @FXML
    private Button calculate_b;
    
    public CHARMM_GUI_Step3(RunCHARMMWorkflow flow, List<CHARMM_InOut> ioList) throws Exception {
        super(title, flow);
                
        for (CHARMM_InOut ioListIt : ioList) {
            
            Class c = ioListIt.getClass();
            Class sc = c.getSuperclass();
            
            if (sc == CHARMM_Input.class) {
                
                inp.add((CHARMM_Input) ioListIt);
                
            } else if (sc == CHARMM_Output.class) {
                
                out.add((CHARMM_Output) ioListIt);
                
                if(c==CHARMM_Output_GasPhase.class)
                    gasPhaseOut = splitOutFile(ioListIt.getText().split("\n"));
                else if (c==CHARMM_Output_PureLiquid.class)
                    pureLiqOut = splitOutFile(ioListIt.getText().split("\n"));
                
            } else {
                throw new UnknownError("Unknown type of object in List<CHARMM_InOut> : get "
                        + ioListIt.getClass() + " but expected types are " + CHARMM_Input.class +
                        " or " + CHARMM_Output.class);
            }
        }
        
        // get the output file for gas phase
//        gasPhaseOut = splitOutFile(out.get(0).getText().split("\n"));
        
        // get the output file for pure liquid
//        pureLiqOut = splitOutFile(out.get(1).getText().split("\n"));
        
        parse_required_data();

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
    
    private void parse_required_data(){
        
        List<String> nat = findInArray(gasPhaseOut,find_natom);
        String[] n = nat.get(nat.size()-1).split("\\s+");
        natom = Integer.valueOf(n[5]);
        
        List<String> Temptxt = findInArray(pureLiqOut,find_temp);
        String[] T = Temptxt.get(Temptxt.size()-1).split("\\s+");
        temp = Double.valueOf(T[3]);
        
        List<String> Restxt = findInArray(pureLiqOut,find_nres);
        String[] NR = Restxt.get(Restxt.size()-1).split("\\s+");
        nres = Integer.valueOf(NR[10]);
        
        // will contain number of constraints
        List<String> constr = findInArray(gasPhaseOut,find_nconstr);
        String[] cons = constr.get(constr.size()-1).split("\\s+");
        nconstr = Integer.valueOf(cons[1]);
//        logger.info("number of constraints : " + nconstr);
        
        //energy from gas phase
        List<String> averlist = findInArray(gasPhaseOut,"AVER>");
        String[] avL = averlist.get(averlist.size()-1).split("\\s+");
        egas = Double.valueOf(avL[5]) + 0.5*kBoltz*temp*(3.0*natom - 6 - nconstr);
        
        //energy from liquid phase
        List<String> averlist2 = findInArray(pureLiqOut,"AVER>");
        String[] avL2 = averlist2.get(averlist2.size()-1).split("\\s+");
        eliq = Double.valueOf(avL2[5]) / nres ;
        
        // get all lines containing "AVER PRESS>"
        List<String> boxLen = findInArray(pureLiqOut,"AVER PRESS>");
        //keep last column containing volume in cubic angstroems
        String[] L = boxLen.get(boxLen.size()-1).split("\\s+");
//        logger.info(L);
        box = Double.valueOf(L[6]);
//        box = Math.pow(box, 1.0/3.0);
//        logger.info("Box length [A] : " + box);
        
        
        
    }
    
    private static List<String> splitOutFile(String[] Array){
        
        List<String> arrList = new ArrayList<>();
        
        for(String st : Array)
        {
            arrList.add(st);
        }
        
        return arrList;

    }
    
    private static List<String> findInArray(List<String> arr, String pattern){
        
        List<String> res = new ArrayList<>();
        
        for(String st : arr)
        {
            if (st.contains(pattern))
                res.add(st);
        }
        
        return res;
    }
    
    private void calc_density(){
        
//        density = mmass * nres / (kBoltz*temp*box*box*box);
        density = mmass * nres / (kBoltz*temp*box);
//        logger.info("density : " + density);

    }
    
    private void calc_vapor(){

        //estimate deltaH in kcal/mol
        deltaH = egas - eliq + kBoltz*temp;
//        logger.info("DeltaH [kcal/mol] : " + deltaH); 
  
    }

    @Override
    public void initializeData() {
        temp_field.setText(Double.toString(temp));
        mmass_field.setText(Double.toString(mmass));
        nres_field.setText(Integer.toString(nres));
        
//        dens_field.setText(Double.toString(density));
//        dhvap_field.setText(Double.toString(deltaH));
    }

    @FXML
    protected void calculateValues(ActionEvent event)
    {
        
        temp = Double.valueOf(temp_field.getText());
        mmass = Double.valueOf(mmass_field.getText());
        nres = Integer.valueOf(nres_field.getText());
        
        calc_density();
        calc_vapor();
        
        dens_field.setText(Double.toString(density));
        dhvap_field.setText(Double.toString(deltaH));
    }
    
    
}//class
    

