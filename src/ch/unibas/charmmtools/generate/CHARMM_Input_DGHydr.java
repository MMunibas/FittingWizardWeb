/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.generate;

import java.io.IOException;

/**
 * DG of hydration charmm calculation ; extends the abstract CHARMM_Input class
 * @author hedin
 */
public class CHARMM_Input_DGHydr  extends CHARMM_Input {

    private final String expectedFormat=".pdb";
    
    protected String solv_cor, solv_top;
            
    public CHARMM_Input_DGHydr(String _solu_cor, String _solv_cor,
            String _solu_top, String _solv_top,
            String _par, String _lpun) {
        super(_solu_cor, _solu_top, _par, _lpun, "DeltaG of Hydration");
        this.solv_cor = _solv_cor;
        this.solv_top = _solv_top;
        
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
