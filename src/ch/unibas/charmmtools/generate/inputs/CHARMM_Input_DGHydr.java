///*
// * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
// * All rights reserved.
// *
// * The 3-clause BSD license is applied to this software.
// * see LICENSE.txt
// *
// */
//package ch.unibas.charmmtools.generate.inputs;
//
//import ch.unibas.fittingwizard.infrastructure.base.PythonScriptRunner;
//import java.io.CharArrayWriter;
//import java.io.File;
//
///**
// * DG of hydration charmm calculation ; extends the abstract CHARMM_Input class
// * it is also an abstract class before there are several types of possible simulations
// * and we want to have a dedicated class for each because the input files may vary a lot
// * @author hedin
// */
//public abstract class CHARMM_Input_DGHydr  extends CHARMM_Input {
//
//    protected String solv_cor, solv_top;
//    protected String ti_type;
//    protected double l_min, l_space, l_max;
//    
//    protected PythonScriptRunner runner;
//    
////    protected RunCHARMMWorkflow charmmWorkflow;
//    
//    /**
//     * A constructor to call when a DGHydr vdw or mtp simulation is required in solvent 
//     * @param _solu_cor solute coordinates file
//     * @param _solv_cor solvent coordinates file
//     * @param _solu_top solute topology file
//     * @param _solv_top solvent topology file
//     * @param _par FF parameters file
//     * @param _lpun MTPs lpun file
//     * @param _ti_type either "vdw" or "mtp" inidicating the type of simulation
//     * @param _l_min Minimal lambda value, usually 0.0
//     * @param _l_space spacing value for lambda grid, usually 0.1
//     * @param _l_max Maximal lambda value, usually 1.0
//     */
//    public CHARMM_Input_DGHydr(String _solu_cor, String _solv_cor,
//            String _solu_top, String _solv_top,
//            String _par, String _lpun,
//            String _ti_type, double _l_min, double _l_space, double _l_max) {
//        
//        super(_solu_cor, _solu_top, _par, _lpun, "DeltaG of Hydration (with solvent)");
//        
//        this.runner = new PythonScriptRunner();
//        this.runner.setWorkingDir(new File("./test"));
//        
//        this.solv_cor = _solv_cor;
//        this.solv_top = _solv_top;
//        this.ti_type = _ti_type;
//        this.l_min = _l_min;
//        this.l_space = _l_space;
//        this.l_max = _l_max;
////        this.charmmWorkflow = _cflow;
//        
//        writer = new CharArrayWriter();
//    }
//
//    /**
//     * A constructor to call when a DGHydr vdw or mtp simulation is required in gas phase 
//     * @param _solu_cor solute coordinates file
//     * @param _solu_top solute topology file
//     * @param _par FF parameters file
//     * @param _lpun MTPs lpun file
//     * @param _ti_type either "vdw" or "mtp" inidicating the type of simulation
//     * @param _l_min Minimal lambda value, usually 0.0
//     * @param _l_space spacing value for lambda grid, usually 0.1
//     * @param _l_max Maximal lambda value, usually 1.0
//     */
//    public CHARMM_Input_DGHydr(String _solu_cor, String _solu_top,
//            String _par, String _lpun,
//            String _ti_type, double _l_min, double _l_space, double _l_max) {
//        
//        super(_solu_cor, _solu_top, _par, _lpun, "DeltaG of Hydration (in gas phase)");
//        
//        this.runner = new PythonScriptRunner();
//        this.runner.setWorkingDir(new File("./test"));
//        
//        this.solv_cor = null;
//        this.solv_top = null;
//        this.ti_type = _ti_type;
//        this.l_min = _l_min;
//        this.l_space = _l_space;
//        this.l_max = _l_max;
////        this.charmmWorkflow = _cflow;
//        
//        writer = new CharArrayWriter();
//    }
//    
//    protected abstract void genInputFromPython();
//
//    /**
//     * @return the solv_cor
//     */
//    public String getSolv_cor() {
//        return solv_cor;
//    }
//
//    /**
//     * @return the solv_top
//     */
//    public String getSolv_top() {
//        return solv_top;
//    }
//
//    /**
//     * @return the ti_type
//     */
//    public String getTi_type() {
//        return ti_type;
//    }
//
//    /**
//     * @return the l_min
//     */
//    public double getL_min() {
//        return l_min;
//    }
//
//    /**
//     * @return the l_space
//     */
//    public double getL_space() {
//        return l_space;
//    }
//
//    /**
//     * @return the l_max
//     */
//    public double getL_max() {
//        return l_max;
//    }
//    
//}
