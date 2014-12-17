/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.files.input;

import java.io.File;
import java.io.IOException;

/**
 * gas_phase charmm calculation
 * extends the abstract CHARMM_input class
 * @author hedin
 */
public class CHARMM_input_GasPhase extends CHARMM_input {


    /**
     * If content of the field has to be retrieved later on it is stored on an internal CharArrayWriter within this class
     *
     * @param crdname
     * @param topolname
     * @param ffname
     * @throws java.io.IOException
     */
    public CHARMM_input_GasPhase(String crdname, String topolname, String ffname) throws IOException {

        super(crdname,topolname,ffname);

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
    public CHARMM_input_GasPhase(String crdname, String topolname, String ffname, File outfile) throws IOException {

        super(crdname,topolname,ffname,outfile);

    }

    /**
     * If content of the field has to be retrieved later on it is stored on an internal CharArrayWriter within this class
     * Requires also a lpun file when MTP module is used
     * 
     * @param crdname
     * @param topolname
     * @param ffname
     * @param lpunname
     * @throws java.io.IOException
     */
    public CHARMM_input_GasPhase(String crdname, String topolname, String ffname, String lpunname) throws IOException {

        super(crdname,topolname,ffname,lpunname);

    }

    /**
     * If content of the field has to be directly written to a file we use a BufferedWriter type
     * Requires also a lpun file when MTP module is used
     * 
     * @param crdname
     * @param topolname
     * @param ffname
     * @param lpunname
     * @param outfile
     * @throws java.io.IOException
     */
    public CHARMM_input_GasPhase(String crdname, String topolname, String ffname, String lpunname, File outfile) throws IOException {

        super(crdname,topolname,ffname,lpunname,outfile);

    }
    
    /**
     * Creates the header part of charmm input file, i.e. containing a title and bomlev and prnlev parameters
     *
     * @param crdfile The name of the coordinates file in CHARMM format
     * @throws IOException
     */
    @Override
    protected void print_title(String crdfile) throws IOException {
        this.title += "* CHARMM input file for " + crdfile + "\n";
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

}//end of class
