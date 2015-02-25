/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.generate;

import java.io.CharArrayWriter;
import java.io.IOException;

/**
 * DG of hydration charmm calculation ; extends the abstract CHARMM_Input class
 * @author hedin
 */
public class CHARMM_Input_DGHydr  extends CHARMM_Input {

    private final String expectedFormat=".pdb";
    
    protected String solv_cor, solv_top;
    protected String ti_type;
    protected double l_min, l_space, l_max;
    
    public CHARMM_Input_DGHydr(String _solu_cor, String _solv_cor,
            String _solu_top, String _solv_top,
            String _par, String _lpun,
            String _ti_type, double _l_min, double _l_space, double _l_max) {
        super(_solu_cor, _solu_top, _par, _lpun, "DeltaG of Hydration");
        this.solv_cor = _solv_cor;
        this.solv_top = _solv_top;
        this.ti_type = _ti_type;
        this.l_min = _l_min;
        this.l_space = _l_space;
        this.l_max = _l_max;
        
        writer = new CharArrayWriter();
        
//        build();
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

    /**
     * @return the solv_cor
     */
    public String getSolv_cor() {
        return solv_cor;
    }

    /**
     * @return the solv_top
     */
    public String getSolv_top() {
        return solv_top;
    }

    /**
     * @return the ti_type
     */
    public String getTi_type() {
        return ti_type;
    }

    /**
     * @return the l_min
     */
    public double getL_min() {
        return l_min;
    }

    /**
     * @return the l_space
     */
    public double getL_space() {
        return l_space;
    }

    /**
     * @return the l_max
     */
    public double getL_max() {
        return l_max;
    }
    
}
