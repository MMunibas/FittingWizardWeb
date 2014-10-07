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
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 *
 * @author hedin
 */
public class CHARMM_input {

    private BufferedWriter writer = null;

    private String title = "";

    private Date d = new Date();

    /*
     * bomlev is the error level causing abortion of CHARMM
     * prnlev regulates the level of output from CHARMM
     * we set here those variable to 2 "decent" values
     */
    private int bomlev = 0, prnlev = 2;
    
    //for storing the command line related to the rtf topology file
    private String rtf_String;

    //for storing the command line related to the rtf topology file
    private String param_String;
    
    //for storing the command line related to the psf structure file
    private String coord_String;
    //the fortran unit to use for I/O
    private int coord_unit;

    private NBONDS nbonds_params;

    public CHARMM_input(String crdname, String topolname, String ffname) throws IOException {

        writer = new BufferedWriter(new FileWriter(crdname + ".inp"));

        //prepare and print title
        this.print_title(crdname);

        //prepare and print io section
        this.print_iosection(topolname, ffname);

        //prepare non bonded parameters line and print it
        this.print_nbondsSection();

        writer.close();
    }

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

    private void print_iosection(String topol, String par) throws IOException {
        //print commands for reading forcefield parameters and topology file
        writer.write("! read parameters and coordinates" + "\n");
        writer.write("read rtf card name -" + "\n");
        writer.write("   ./" + topol + "\n");
        writer.write("read param card name -" + "\n");
        writer.write("   ./" + par + "\n\n");
    }

    private void print_nbondsSection() throws IOException {
        nbonds_type nbtype = nbonds_type.ATOM;
        add_elec electype = add_elec.ELEC;
        add_vdw vdwtype = add_vdw.VDW;
        add_ewald ewaldtype = add_ewald.NOEWald;
        add_elec_opt elecopt = add_elec_opt.CDIElec;
        cut_type cuttype = cut_type.SHIFted;
        nbxmod_type nbxmod = nbxmod_type.PRESERVE;
        nbonds_params = new NBONDS(nbtype, electype, vdwtype, ewaldtype, elecopt, cuttype, nbxmod);
        writer.write("! Non bonded parameters" + "\n");
        writer.write(nbonds_params.getNB_params() + "\n\n");
    }

}//end of class
