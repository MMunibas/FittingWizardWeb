/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.generate.inputs;

import java.io.CharArrayWriter;
import java.io.IOException;

/**
 * DG of hydration charmm calculation ; gas system
 * @author hedin
 */
public class CHARMM_Input_DGHydr_gas  extends CHARMM_Input_DGHydr {

    public CHARMM_Input_DGHydr_gas(String _solu_cor, String _solu_top,
            String _par, String _lpun,
            String _ti_type, double _l_min, double _l_space, double _l_max) {
        
        super(_solu_cor, _solu_top, _par, _lpun, _ti_type, _l_min, _l_space, _l_max);

        writer = new CharArrayWriter();
        
        this.build();
    }

    @Override
    protected void build(){
        
    }
    
    @Override
    protected void print_title() throws IOException {
    }
    
    @Override
    protected void print_ioSection() throws IOException {}
    
    @Override
    protected void print_corSection() throws IOException {}
    
    @Override
    protected void print_crystalSection() throws IOException{}
    
    @Override
    protected void print_nbondsSection() throws IOException {

    }
    
    @Override
    protected void print_ShakeSection() throws IOException {
    }

    @Override
    protected void print_lpunfile() throws IOException {

    }

    @Override
    protected void print_MiniSection() throws IOException {

    }

    @Override
    protected void print_DynaSection() throws IOException {

    }
    
}
