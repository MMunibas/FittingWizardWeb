/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.charmm.generate.inputs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * gas_phase charmm calculation
 extends the abstract CHARMM_Input class
 * @author hedin
 */
public class CHARMM_Input_GasPhase extends CHARMM_Input {

    /**
     * If content of the field has to be directly written to a file we use a BufferedWriter type
     *
     * @param _cor
     * @param _top
     * @param _par
     * @param _outf
     * @throws java.io.IOException
     */
    public CHARMM_Input_GasPhase(File _cor,
                                 File _top,
                                 File _par,
                                 File _outf) throws IOException {
        super(_cor, _top, _par, _outf, "Gas Phase");
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
    public CHARMM_Input_GasPhase(File _cor,
                                 File _top,
                                 File _par,
                                 File _lpun,
                                 File _outf) {
        super(_cor, _top, _par, _lpun, _outf, "Gas Phase");
    }

    public void generate() {
        try {
            writer = new BufferedWriter(new FileWriter(out));
            //build the input file by calling all the print_* sections
            this.build();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException("CHARMM_Input_GasPhase ctor failed", e);
        }
    }

    @Override
    protected void build() throws IOException {
        //prepare and print title
        this.print_title();

        //prepare and print io section
        this.print_ioSection();
        this.print_corSection();

        //prepare non bonded parameters line and print it
        this.print_nbondsSection();

        this.print_ShakeSection();

        this.print_MiniSection();
        this.print_DynaSection();
        this.print_StopSection();
    }
    
    /**
     * Creates the header part of charmm input file, i.e. containing a title and bomlev and prnlev parameters
     *
     * @throws java.io.IOException
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
        writer.write("! Non bonded parameters" + "\n");
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
        writer.write("mini sd nstep 1000  " + "\n\n");
        writer.write("mini abnr nstep 100 " + "\n\n");
    }

    @Override
    protected void print_DynaSection() throws IOException {
        writer.write("DYNA LEAP STRT NSTEP 20000 TIMESTEP 0.001 -" + "\n");
        writer.write("\t" + "NTRFRQ 100 -" + "\n");
        writer.write("\t" + "IPRFRQ 0 INBFRQ -1 IMGFRQ 250 -" + "\n");
        writer.write("\t" + "TBATH 0. RBUF 0. ILBFRQ 10 FIRSTT 0. -" + "\n");
        writer.write("\t" + "NPRINT 1000 NSAVC -1" + "\n\n");
    }
}
