/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.generate.inputs;

import ch.unibas.fittingwizard.infrastructure.base.PythonScriptRunner;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * DG of hydration charmm calculation ; gas system
 * @author hedin
 */
public class CHARMM_Generator_DGHydr{
    
    protected String solu_cor, solu_top;
    protected String solv_cor, solv_top;
    protected String par, lpun;
    protected String ti_type;
    protected double l_min, l_space, l_max;
    
    protected PythonScriptRunner runner=null;
//    protected final File currDir = new File(".");
    protected File myDir=null;

    public CHARMM_Generator_DGHydr(String _solu_cor, String _solu_top,
            String _par, String _lpun,
            String _ti_type, double _l_min, double _l_space, double _l_max) {

        this.solu_cor = _solu_cor;
        this.solv_cor = "";
        this.solu_top = _solu_top;
        this.solv_top = "";
        this.par = _par;
        this.lpun = _lpun; 
        this.ti_type = _ti_type;
        this.l_min = _l_min;
        this.l_space = _l_space;
        this.l_max = _l_max;
        
        this.myDir = new File("./test/gas_" + ti_type);
        this.myDir.mkdirs();
        
        this.runner = new PythonScriptRunner();
        this.runner.setWorkingDir(this.myDir);
        
        genInputPythonGas();
    }
    
    public CHARMM_Generator_DGHydr(String _solu_cor, String _solv_cor,
            String _solu_top, String _solv_top,
            String _par, String _lpun,
            String _ti_type, double _l_min, double _l_space, double _l_max) {

        this.solu_cor = _solu_cor;
        this.solv_cor = _solv_cor;
        this.solu_top = _solu_top;
        this.solv_top = _solv_top;
        this.par = _par;
        this.lpun = _lpun; 
        this.ti_type = _ti_type;
        this.l_min = _l_min;
        this.l_space = _l_space;
        this.l_max = _l_max;
        
        this.myDir = new File("./test/solvent_" + ti_type);
        this.myDir.mkdirs();
        
        this.runner = new PythonScriptRunner();
        this.runner.setWorkingDir(this.myDir);
        
        genInputPythonSolvent();
    }
    
    protected void genInputPythonGas() {
        List<String> args = new ArrayList<>();
        args.clear();
        
//        String extraPath = "../";
        args.add("--ti");   args.add(this.ti_type);
        args.add("--tps");   args.add(this.solu_top);
        args.add("--slu");   args.add(this.solu_cor);
        args.add("--par");   args.add(this.par);
        args.add("--lpun");  args.add(this.lpun);
        args.add("--chm");  args.add("../../scripts/charmm");
        args.add("--lmb");  
        args.add(Double.toString(this.l_min));
        args.add(Double.toString(this.l_space));
        args.add(Double.toString(this.l_max));
        args.add("--generate");
        
        File script = new File("scripts/charmm-ti/perform-ti.py");
        File output = new File("test/gen_dg_inp_" + this.ti_type + "_gas" + ".out");
        
        int returnCode = runner.exec(script, args, output);
    }

    protected void genInputPythonSolvent() {
        List<String> args = new ArrayList<>();
        args.clear();
        
//        String extraPath = "../";
        args.add("--ti");   args.add(this.ti_type);
        args.add("--tps");   args.add(this.solu_top);
        args.add("--top");   args.add(this.solv_top);
        args.add("--slu");   args.add(this.solu_cor);
        args.add("--slv");   args.add(this.solv_cor);
        args.add("--par");   args.add(this.par);
        args.add("--lpun");  args.add(this.lpun);
        args.add("--chm");  args.add("../../scripts/charmm");
        args.add("--lmb");  
        args.add(Double.toString(this.l_min));
        args.add(Double.toString(this.l_space));
        args.add(Double.toString(this.l_max));
        args.add("--generate");
        
        File script = new File("scripts/charmm-ti/perform-ti.py");
        File output = new File("test/gen_dg_inp_" + this.ti_type + "_solvent" + ".out");
        
        int returnCode = runner.exec(script, args, output);
    }
  
    
}
