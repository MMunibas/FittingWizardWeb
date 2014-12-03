/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.files.input;

import ch.unibas.charmmtools.files.input.NBONDS.add_elec;
import ch.unibas.charmmtools.files.input.NBONDS.add_elec_opt;
import ch.unibas.charmmtools.files.input.NBONDS.add_ewald;
import ch.unibas.charmmtools.files.input.NBONDS.add_vdw;
import ch.unibas.charmmtools.files.input.NBONDS.cut_type;
import ch.unibas.charmmtools.files.input.NBONDS.nbonds_type;
import ch.unibas.charmmtools.files.input.NBONDS.nbxmod_type;
import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;

/**
 *
 * @author hedin
 */
public class CHARMM_input {

    private Writer writer = null;

    private String title = "";

    private Date d = new Date();

    /*
     * bomlev is the error level causing abortion of CHARMM
     * prnlev regulates the level of output from CHARMM
     * we set here those variable to 2 "decent" values
     */
    private int bomlev = 0, prnlev = 2;
    
    //for storing the command line related to the rtf topology file
    //private String rtf_String;

    //for storing the command line related to the rtf topology file
    //private String param_String;
    
    //for storing the command line related to the psf structure file
    //private String coord_String;
    //the fortran unit to use for I/O
    //private int coord_unit;

    //private NBONDS nbonds_params;

    /**
     * If content of the field has to be retrieved later on it is stored on an internal CharArrayWriter within this class
     *
     * @param crdname
     * @param topolname
     * @param ffname
     * @throws java.io.IOException
     */
    public CHARMM_input(String crdname, String topolname, String ffname) throws IOException {

        writer = new CharArrayWriter();

        //prepare and print title
        this.print_title(crdname);

        //prepare and print io section
        this.print_ioSection(topolname, ffname);

        this.print_crdSection(crdname);

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
     * @param crdname
     * @param topolname
     * @param ffname
     * @param outfile
     * @throws java.io.IOException
     */
    public CHARMM_input(String crdname, String topolname, String ffname, File outfile) throws IOException {

        writer = new BufferedWriter(new FileWriter(outfile));

        //prepare and print title
        this.print_title(crdname);

        //prepare and print io section
        this.print_ioSection(topolname, ffname);

        this.print_crdSection(crdname);

        //prepare non bonded parameters line and print it
        this.print_nbondsSection();

        this.print_ShakeSection();

        this.print_MiniSection();
        this.print_DynaSection();
        this.print_StopSection();

        writer.close();

    }

    public CHARMM_input(String crdname, String topolname, String ffname, String lpunname) throws IOException {

        writer = new CharArrayWriter();

        //prepare and print title
        this.print_title(crdname);

        //prepare and print io section
        this.print_ioSection(topolname, ffname);

        this.print_crdSection(crdname);

        //prepare non bonded parameters line and print it
        this.print_nbondsSection();

        this.print_ShakeSection();

        //add section with lpun file
        this.print_lpunfile(lpunname);

        this.print_MiniSection();
        this.print_DynaSection();
        this.print_StopSection();

    }

    public CHARMM_input(String crdname, String topolname, String ffname, String lpunname, File outfile) throws IOException {

        writer = new BufferedWriter(new FileWriter(outfile));

        //prepare and print title
        this.print_title(crdname);

        //prepare and print io section
        this.print_ioSection(topolname, ffname);

        this.print_crdSection(crdname);

        //prepare non bonded parameters line and print it
        this.print_nbondsSection();

        this.print_ShakeSection();

        //add section with lpun file
        this.print_lpunfile(lpunname);

        this.print_MiniSection();
        this.print_DynaSection();
        this.print_StopSection();

        writer.close();

    }

    /**
     * Creates the header part of charmm input file, i.e. containing a title and bomlev and prnlev parameters
     *
     * @param crdfile The name of the coordinates file in CHARMM format
     * @throws IOException
     */
    private void print_title(String crdfile) throws IOException {
        this.title += "* CHARMM input file for " + crdfile + "\n";
        this.title += "* generated on " + d.toString() + "\n";
        this.title += "* by user " + System.getProperty("user.name") + " on machine "
                + System.getProperty("os.name") + " " + System.getProperty("os.arch") + " "
                + System.getProperty("os.version") + "\n";
        this.title += "*\n";

        //then print it
        writer.write(this.title + "\n");

        //print error level and print level
        writer.write("bomlev " + this.bomlev + "\n");
        writer.write("prnlev " + this.prnlev + "\n\n");
    }

    /**
     * Creates the section where topology and parameter files are loaded
     *
     * @param topol The name of the topology file in CHARMM format
     * @param par The name of the parameters file in CHARMM format
     * @throws IOException
     */
    private void print_ioSection(String topol, String par) throws IOException {
        //print commands for reading forcefield parameters and topology file
        writer.write("! read parameters and coordinates" + "\n");
        writer.write("read rtf card name -" + "\n");
        writer.write("\t" + topol + "\n");
        writer.write("read param card name -" + "\n");
        writer.write("\t" + par + "\n\n");
    }

    private void print_crdSection(String crdfile) throws IOException {
        writer.write("OPEN UNIT 10 CARD READ NAME -" + "\n");
        writer.write("\t" + crdfile + "\n");
        writer.write("READ SEQUENCE PDB UNIT 10" + "\n");
        writer.write("GENERATE SOLU" + "\n");
        writer.write("REWIND UNIT 10" + "\n");
        writer.write("READ COOR PDB UNIT 10" + "\n");
        writer.write("CLOSE UNIT 10" + "\n\n");
    }

    /**
     * Creates the section where nonbonded parameters are defined
     *
     * @throws IOException
     */
    private void print_nbondsSection() throws IOException {
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

    private void print_ShakeSection() throws IOException {
        writer.write("SHAKE BONH PARA SELE ALL END" + "\n\n");
    }

    private void print_lpunfile(String lpunname) throws IOException {
        writer.write("OPEN UNIT 40 CARD READ NAME -" + "\n");
        writer.write(lpunname + "\n");
        writer.write("MTPL MTPUNIT 40" + "\n");
        writer.write("CLOSE UNIT 40" + "\n\n");
    }

    private void print_MiniSection() throws IOException {
        writer.write("mini sd nstep 500 print 10" + "\n\n");
    }

    private void print_DynaSection() throws IOException {
        writer.write("DYNA LEAP STRT NSTEP 20000 TIMESTEP 0.001 -" + "\n");
        writer.write("\t" + "NTRFRQ 100 -" + "\n");
        writer.write("\t" + "IPRFRQ 0 INBFRQ -1 IMGFRQ 250 -" + "\n");
        writer.write("\t" + "TBATH 0. RBUF 0. ILBFRQ 10 FIRSTT 0. -" + "\n");
        writer.write("\t" + "NPRINT 1000 NSAVC -1" + "\n\n");
    }

    private void print_StopSection() throws IOException {
        writer.write("STOP" + "\n\n");
    }

    /**
     * Returns a long String which is a CHARMM input file ready for use.
     *
     * @return The content of the built input file, for example for editing purposes
     */
    public String getContentOfInputFile() {
        return writer.toString();
    }
}//end of class
