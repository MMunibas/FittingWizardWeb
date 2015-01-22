/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.scripts;

import java.io.File;
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
    
    protected String par,top,lpun,crd;
    protected File out;
    
    protected CHARMM_input(String _crd, String _top, String _par)
    {
        this.crd = _crd;
        this.par = _top;
        this.top = _par;
    }
    
    public CHARMM_input(String _crd, String _top, String _par, File _outf){
        this.crd = _crd;
        this.par = _top;
        this.top = _par;
        this.out = _outf;
    }

    protected CHARMM_input(String _crd, String _top, String _par, String _lpun)
    {
        this.crd = _crd;
        this.par = _top;
        this.top = _par;
        this.lpun = _lpun;
    }
    
    protected CHARMM_input(String _crd, String _top, String _par, String _lpun, File _outf)
    {
        this.crd = _crd;
        this.par = _top;
        this.top = _par;
        this.lpun = _lpun;
        this.out = _outf;
    }
            
    /**
     * Creates the header part of charmm input file, i.e. containing a title and bomlev and prnlev parameters
     *
     * @throws IOException
     */
    protected void print_title() throws IOException {
        this.title += "* CHARMM input file for " + crd + "\n";
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
     * @throws IOException
     */
    protected void print_ioSection() throws IOException {
        //print commands for reading forcefield parameters and topology file
        writer.write("! read parameters and coordinates" + "\n");
        writer.write("read rtf card name -" + "\n");
        writer.write("\t" + top + "\n");
        writer.write("read param card name -" + "\n");
        writer.write("\t" + par + "\n\n");
    }

    protected abstract void print_crdSection() throws IOException;

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

    protected abstract void print_lpunfile() throws IOException;

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

    /**
     * @return the par
     */
    public String getPar() {
        return par;
    }

    /**
     * @return the top
     */
    public String getTop() {
        return top;
    }

    /**
     * @return the lpun
     */
    public String getLpun() {
        return lpun;
    }

    /**
     * @return the crd
     */
    public String getCrd() {
        return crd;
    }

    /**
     * @return the out
     */
    public File getOut() {
        return out;
    }
    
}
