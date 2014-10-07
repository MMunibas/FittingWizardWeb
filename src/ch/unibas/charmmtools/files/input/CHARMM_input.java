/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.files.input;

import java.util.Date;

/**
 *
 * @author hedin
 */
public class CHARMM_input {

    private String title = "";

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

    public CHARMM_input(String myname) {
        //prepare title
        Date d = new Date();
        this.title += "* CHARMM input file for " + myname + "\n";
        this.title += "* generated on " + d.toString() + "\n";
        this.title += "* by user " + System.getProperty("user.name") + " on machine "
                + System.getProperty("os.name") + " " + System.getProperty("os.arch") + " "
                + System.getProperty("os.version") + "\n";
        this.title += "*\n";

        //then print it
        System.out.println(this.title);

        //print error level and print level
        System.out.println("bomlev " + this.bomlev);
        System.out.println("prnlev " + this.prnlev + "\n");

        //prepare non bonded parameters line and print it
        NBONDS.nbonds_type nbtype = NBONDS.nbonds_type.ATOM;
        NBONDS.add_elec electype = NBONDS.add_elec.ELEC;
        NBONDS.add_vdw vdwtype = NBONDS.add_vdw.VDW;
        NBONDS.add_ewald ewaldtype = NBONDS.add_ewald.NOEWald;
        NBONDS.add_elec_opt elecopt = NBONDS.add_elec_opt.CDIElec;
        NBONDS.cut_type cuttype = NBONDS.cut_type.SHIFted;
        NBONDS.nbxmod_type nbxmod = NBONDS.nbxmod_type.PRESERVE;
        NBONDS nbstring = new NBONDS(nbtype, electype, vdwtype, ewaldtype, elecopt, cuttype, nbxmod);
        System.out.println(nbstring.getNB_params() + "\n");
    }

}//end of class
