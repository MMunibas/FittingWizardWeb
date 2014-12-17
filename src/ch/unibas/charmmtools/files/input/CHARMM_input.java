/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.files.input;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;

/**
 *
 * @author hedin
 */
public abstract class CHARMM_input {
    protected Writer writer = null;
    protected String title = "";
    protected Date d = new Date();
    /*
     * bomlev is the error level causing abortion of CHARMM
     * prnlev regulates the level of output from CHARMM
     * we set here those variable to 2 "decent" values
     */
    protected int bomlev = 0;
    protected int prnlev = 2;

    /**
     * Creates the header part of charmm input file, i.e. containing a title and bomlev and prnlev parameters
     *
     * @param crdfile The name of the coordinates file in CHARMM format
     * @throws IOException
     */
    protected void print_title(String crdfile) throws IOException {
        this.title += "* CHARMM input file for " + crdfile + "\n";
        this.title += "* generated on " + d.toString() + "\n";
        this.title += "* by user " + System.getProperty("user.name") + " on machine " + System.getProperty("os.name") + " " + System.getProperty("os.arch") + " " + System.getProperty("os.version") + "\n";
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
    protected void print_ioSection(String topol, String par) throws IOException {
        //print commands for reading forcefield parameters and topology file
        writer.write("! read parameters and coordinates" + "\n");
        writer.write("read rtf card name -" + "\n");
        writer.write("\t" + topol + "\n");
        writer.write("read param card name -" + "\n");
        writer.write("\t" + par + "\n\n");
    }

    protected abstract void print_crdSection(String crdfile) throws IOException;

    protected void print_crystalSection() throws IOException{}
    
    /**
     * Creates the section where nonbonded parameters are defined
     *
     * @throws IOException
     */
    protected abstract void print_nbondsSection() throws IOException;

    protected void print_ShakeSection() throws IOException {
        writer.write("SHAKE BONH PARA SELE ALL END" + "\n\n");
    }

    protected abstract void print_lpunfile(String lpunname) throws IOException;

    protected abstract void print_MiniSection() throws IOException;

    protected abstract void print_DynaSection() throws IOException;

    protected void print_StopSection() throws IOException {
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
    
}
