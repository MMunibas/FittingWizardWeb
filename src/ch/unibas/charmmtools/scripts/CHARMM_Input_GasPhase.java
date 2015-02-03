/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.scripts;

import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * gas_phase charmm calculation
 extends the abstract CHARMM_Input class
 * @author hedin
 */
public class CHARMM_Input_GasPhase extends CHARMM_Input {

    private final String expectedFormat=".pdb";

     /**
     * If content of the field has to be retrieved later on it is stored on an internal CharArrayWriter within this class
     *
     * @param _cor
     * @param _top
     * @param _par
     * @throws java.io.IOException
     */
    public CHARMM_Input_GasPhase(String _cor, String _top, String _par) throws IOException {
        
        super(_cor, _top, _par, "Gas Phase");
        
        writer = new CharArrayWriter();
        
        //prepare and print title
        this.print_title();

        //prepare and print io section
        this.print_ioSection();
        
        //if necessary convert coordinates file before
        this.convertCoordinates();
        this.print_corSection();

        //prepare non bonded parameters line and print it
        this.print_nbondsSection();

        this.print_ShakeSection();

        this.print_MiniSection();
        this.print_DynaSection();
        this.print_StopSection();

    }

    /**
     * If content of the field has to be directly written to a file we use a BufferedWriter type
     *
     * @param _cor
     * @param _top
     * @param _par
     * @param _outf
     * @throws java.io.IOException
     */
    public CHARMM_Input_GasPhase(String _cor, String _top, String _par, File _outf) throws IOException {
        
        super(_cor, _top, _par, _outf, "Gas Phase");
        
        writer = new BufferedWriter(new FileWriter(_outf));

        //if necessary convert coordinates file before
        this.convertCoordinates();
        
        //prepare and print title
        this.print_title();

        //prepare and print io section
        this.print_ioSection();

        //if necessary convert coordinates file before
        this.convertCoordinates();
        this.print_corSection();

        //prepare non bonded parameters line and print it
        this.print_nbondsSection();

        this.print_ShakeSection();

        this.print_MiniSection();
        this.print_DynaSection();
        this.print_StopSection();

        writer.close();

    }

    /**
     * If content of the field has to be retrieved later on it is stored on an internal CharArrayWriter within this class
     * Requires also a lpun file when MTP module is used
     * 
     * @param _cor
     * @param _top
     * @param _par
     * @param _lpun
     * @throws java.io.IOException
     */
    public CHARMM_Input_GasPhase(String _cor, String _top, String _par, String _lpun) throws IOException {

        super(_cor, _top, _par, _lpun, "Gas Phase");
        
        writer = new CharArrayWriter();

        //if necessary convert coordinates file before
        this.convertCoordinates();
        
        //prepare and print title
        this.print_title();

        //prepare and print io section
        this.print_ioSection();

        //if necessary convert coordinates file before
        this.convertCoordinates();
        this.print_corSection();

        //prepare non bonded parameters line and print it
        this.print_nbondsSection();

        this.print_ShakeSection();

        //add section with lpun file
        this.print_lpunfile();

        this.print_MiniSection();
        this.print_DynaSection();
        this.print_StopSection();

    }

    /**
     * If content of the field has to be directly written to a file we use a BufferedWriter type
     * Requires also a lpun file when MTP module is used
     *
     * @param _cor
     * @param _top
     * @param _par
     * @param _lpun
     * @param _outf
     * @throws java.io.IOException
     */
    public CHARMM_Input_GasPhase(String _cor, String _top, String _par, String _lpun, File _outf) throws IOException {

        super(_cor, _top, _par, _lpun, _outf, "Gas Phase");
        
        writer = new BufferedWriter(new FileWriter(_outf));

        //if necessary convert coordinates file before
        this.convertCoordinates();
        
        //prepare and print title
        this.print_title();

        //prepare and print io section
        this.print_ioSection();

        //if necessary convert coordinates file before
        this.convertCoordinates();
        this.print_corSection();

        //prepare non bonded parameters line and print it
        this.print_nbondsSection();

        this.print_ShakeSection();

        //add section with lpun file
        this.print_lpunfile();

        this.print_MiniSection();
        this.print_DynaSection();
        this.print_StopSection();

        writer.close();

    }
    
    /**
     * Creates the header part of charmm input file, i.e. containing a title and bomlev and prnlev parameters
     *
     * @throws java.io.IOException
     * @p    * @throws IOException
     */
    @Override
    protected void print_title() throws IOException {
        this.title += "* CHARMM input file for " + cor + "\n";
        this.title += "* Gas Phase simulation with MTPs \n";
        this.title += "* generated on " + d.toString() + "\n";
        this.title += "* by user " + System.getProperty("user.name") + " on machine " + System.getProperty("os.name") + " " + System.getProperty("os.arch") + " " + System.getProperty("os.version") + "\n";
        this.title += "*\n";
        //then print it
        writer.write(this.title + "\n");
        //print error level and print level
        writer.write("bomlev " + this.bomlev + "\n");
        writer.write("prnlev " + this.prnlev + "\n\n");
    }

    @Override
    protected void print_nbondsSection() throws IOException {
        //        nbonds_type nbtype = nbonds_type.ATOM;
        //        add_elec electype = add_elec.ELEC;
        //        add_vdw vdwtype = add_vdw.VDW;
        //        add_ewald ewaldtype = add_ewald.NOEWald;
        //        add_elec_opt elecopt = add_elec_opt.CDIElec;
        //        cut_type cuttype = cut_type.SHIFted;
        //        nbxmod_type nbxmod = nbxmod_type.PRESERVE;
        //        nbonds_params = new NBONDS(nbtype, electype, vdwtype, ewaldtype, elecopt, cuttype, nbxmod);
        writer.write("! Non bonded parameters" + "\n");
        //        writer.write(nbonds_params.getNB_params() + "\n\n");
        writer.write("NBONDS NBXMOD 5 ATOM CDIEL EPS 1.0 SHIFT VATOM VDISTANCE -" + "\n");
        writer.write("\t" + "VSWITCH CUTNB 99.0 CTOFNB 98.0 CTONNB 97. E14FAC 1.0" + "\n\n");
    }
    
    @Override
    protected void print_lpunfile() throws IOException {
        writer.write("OPEN UNIT 40 CARD READ NAME -" + "\n");
        writer.write(lpun + "\n");
        writer.write("MTPL MTPUNIT 40" + "\n");
        writer.write("CLOSE UNIT 40" + "\n\n");
    }
    
    @Override
    protected void print_MiniSection() throws IOException {
        writer.write("mini sd nstep 500 print 10" + "\n\n");
    }

    @Override
    protected void print_DynaSection() throws IOException {
        writer.write("DYNA LEAP STRT NSTEP 20000 TIMESTEP 0.001 -" + "\n");
        writer.write("\t" + "NTRFRQ 100 -" + "\n");
        writer.write("\t" + "IPRFRQ 0 INBFRQ -1 IMGFRQ 250 -" + "\n");
        writer.write("\t" + "TBATH 0. RBUF 0. ILBFRQ 10 FIRSTT 0. -" + "\n");
        writer.write("\t" + "NPRINT 1000 NSAVC -1" + "\n\n");
    }
    
    @Override
    protected void convertCoordinates(){
        
    }
        
}//end of class
