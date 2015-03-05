/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.generate.inputs;

import ch.unibas.charmmtools.workflows.RunCHARMMWorkflow;
import java.io.IOException;

/**
 * DG of hydration charmm calculation ; solvated system
 * @author hedin
 */
public class CHARMM_Input_DGHydr_solvent  extends CHARMM_Input_DGHydr {

    public CHARMM_Input_DGHydr_solvent(String _solu_cor, String _solv_cor,
            String _solu_top, String _solv_top,
            String _par, String _lpun,
            String _ti_type, double _l_min, double _l_space, double _l_max,
            RunCHARMMWorkflow _cflow) throws IOException {
        
        super(_solu_cor, _solv_cor, _solu_top, _solv_top, _par, _lpun, 
                _ti_type, _l_min, _l_space, _l_max, _cflow);

//        writer = new CharArrayWriter();
        
        build();
    }

    @Override
    protected void build() throws IOException {
        genInputFromPython();
    }

    @Override
    protected void print_nbondsSection() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void print_lpunfile() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void print_MiniSection() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void print_DynaSection() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void genInputFromPython() {
        
    }


    
}
