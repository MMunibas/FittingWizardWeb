/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.charmm.generate.inputs;

import ch.unibas.fitting.shared.charmm.generate.CHARMM_InOut;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author hedin
 */
public abstract class CHARMM_Input implements CHARMM_InOut {

    protected static final Logger logger = Logger.getLogger(CHARMM_Input.class);

    protected Writer writer = null;
    protected String title = "";
    protected Date d = new Date();

    /**
     * contains the acceptable types of coordinate files, by default only *.pdb (the extension may be *.ent but that's
     * the same type of file)
     */
    protected final List<String> expectedFormats;

    /*
     * bomlev is the error level causing abortion of CHARMM
     * prnlev regulates the level of output from CHARMM
     * we set here those variable to 2 "decent" values
     */
    protected int bomlev = 0;
    protected int prnlev = 2;

    protected File par = null, top = null, lpun = null, cor = null;
    protected File out = null;

    protected String type = null;

    public CHARMM_Input(File _cor,
                        File _top,
                        File _par,
                        File _outf,
                        String _type) {
        this.cor = _cor;
        this.top = _top;
        this.par = _par;
        this.out = _outf;
        this.type = _type;

        this.expectedFormats = new ArrayList<>();
        this.expectedFormats.add("*.pdb");
        this.expectedFormats.add("*.ent");
        
        this.normalisePathAndCopyFiles();
    }

    protected CHARMM_Input(File _cor,
                           File _top,
                           File _par,
                           File _lpun,
                           File _outf,
                           String _type) {
        this.cor = _cor;
        this.top = _top;
        this.par = _par;
        this.lpun = _lpun;
        this.out = _outf;
        this.type = _type;

        this.expectedFormats = new ArrayList<>();
        this.expectedFormats.add("*.pdb");
        this.expectedFormats.add("*.ent");
        
        this.normalisePathAndCopyFiles();
    }

    private void normalisePathAndCopyFiles() {

        File parentFile = out.getParentFile();

        try {
            FileUtils.copyFileToDirectory(cor, parentFile);
            FileUtils.copyFileToDirectory(top, parentFile);
            FileUtils.copyFileToDirectory(par, parentFile);

            if (lpun != null) {
                FileUtils.copyFileToDirectory(lpun, parentFile);
            }

        } catch (IOException ex) {
            throw new RuntimeException("Error while copying required files", ex);
        }
    }

    /**
     * Calls all the print_* sections for effectively creating an input file either in a String or directly to a file
     * depending on the constructor that was initially called
     *
     * @throws java.io.IOException
     */
    protected abstract void build() throws IOException;

    /**
     * Creates the header part of charmm input file, i.e. containing a title and bomlev and prnlev parameters
     *
     * @throws IOException
     */
    protected void print_title() throws IOException {
        this.title += "* CHARMM input file for " + cor.getName() + "\n";
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
        writer.write("\t" + top.getName() + "\n");
        writer.write("read param card name -" + "\n");
        writer.write("\t" + par.getName() + "\n\n");
    }

    protected void print_corSection() throws IOException {
        writer.write("OPEN UNIT 10 CARD READ NAME -" + "\n");
        writer.write("\t" + cor.getName() + "\n");
        writer.write("READ SEQUENCE PDB UNIT 10" + "\n");
        writer.write("GENERATE SOLU" + "\n");
        writer.write("REWIND UNIT 10" + "\n");
        writer.write("READ COOR PDB UNIT 10" + "\n");
        writer.write("CLOSE UNIT 10" + "\n\n");
    }

    protected void print_crystalSection() throws IOException {
    }

    /**
     * Creates the section where nonbonded parameters are defined Abstract because this may vary a lot between the
     * different types of simulations
     *
     * @throws IOException
     */
    protected abstract void print_nbondsSection() throws IOException;

    protected void print_ShakeSection() throws IOException {
        writer.write("SHAKE BONH PARA SELE ALL END" + "\n\n");
    }

    /**
     * Prints the part that calls the MTPL module Abstract because this may vary a lot between the different types of
     * simulations
     *
     * @throws IOException
     */
    protected abstract void print_lpunfile() throws IOException;

    /**
     * Prints the part in charge of the minimisation procedure Abstract because this may vary a lot between the
     * different types of simulations
     *
     * @throws IOException
     */
    protected abstract void print_MiniSection() throws IOException;

    /**
     * Prints the part in charge of the molecular dynamics procedure Abstract because this may vary a lot between the
     * different types of simulations
     *
     * @throws IOException
     */
    protected abstract void print_DynaSection() throws IOException;

    protected void print_StopSection() throws IOException {
        writer.write("STOP" + "\n\n");
    }

    /**
     * Returns a long String which is a CHARMM input file ready for use.
     *
     * @return The content of the built input file, for example for editing purposes
     */
    @Override
    public String getText() {
        return writer.toString();
    }

    public File getPar() {
        return par;
    }

    /**
     * @return the top
     */
    public File getTop() {
        return top;
    }

    /**
     * @return the lpun
     */
    public File getLpun() {
        return lpun;
    }

    /**
     * @return the cor
     */
    public File getCrd() {
        return cor;
    }

    /**
     * @return the out
     */
    public File getOut() {
        return out;
    }

    /**
     * @return the inp
     */
//    public File getInp() {
//        return inp;
//    }
    /**
     * @param inp the inp to set
     */
//    public void setInp(File inp) {
//        this.inp = inp;
//    }
//    protected abstract void convertCoordinates(); 
    /**
     * @return the type
     */
    @Override
    public String getType() {
        return type;
    }
    
    @Override
    public File getWorkDir() {
        return out.getParentFile();
    }

}
