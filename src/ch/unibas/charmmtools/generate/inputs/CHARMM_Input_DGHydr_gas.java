/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.generate.inputs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * DG of hydration charmm calculation ; gas system
 * @author hedin
 */
public abstract class CHARMM_Input_DGHydr_gas  extends CHARMM_Input_DGHydr {

    public CHARMM_Input_DGHydr_gas(String _solu_cor, String _solu_top,
            String _par, String _lpun,
            String _ti_type, double _l_min, double _l_space, double _l_max) throws IOException {
        
        super(_solu_cor, _solu_top, _par, _lpun, _ti_type, _l_min, _l_space, _l_max);
        
//        build();
    }

    @Override
    protected void build() throws IOException {
//        genInputFromPython();
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
      
}
