/*
 * Copyright (c) 2016, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */

package ch.unibas.charmmtools.files.structure;

import ch.unibas.charmmtools.files.coordinates.coordinates_writer;
import ch.unibas.charmmtools.files.topology.RTF;
import ch.unibas.charmmtools.internals.Atom;
import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * This class derived from the abstract PSF class is used for generating a new psf file useful for CHARMM
 *
 * @author hedin
 */
public final class PSF_generate extends PSF implements coordinates_writer{

    /**
     * This header string will contain PSF keywords
     */
    private String header = "PSF";

    /**
     * The following formatXX are used for storing system.format strings reproducing the fixed format of fortan files
     * used for generating a charmm compatible PSF
     */
    private String format00, format01, format01a, format01b,
            format02, format02a, format02b, format03,format04,
            format05, format06, format07, format08;
    
    /**
     * the writer object for storing strings before saving to a file
     */
    protected Writer writer = null;
    
    /**
     * Constructor for building a PSF file. Requires an already built topology file
     * @param topolInfo a RTF topology object already constructed before
     * @throws IOException Thrown if problem happens when writing file
     */
    public PSF_generate(RTF topolInfo) throws IOException {

        this.natom = topolInfo.getNatom();
        this.nbond = topolInfo.getNbonds();
        this.ntheta = topolInfo.getAngTypeList().size();//angles
        this.nphi = topolInfo.getDiheTypeList().size();//dihedrals
        this.nimphi = topolInfo.getNimpr();//impropers
        
        this.atomList = topolInfo.getAtmTypeList();
        this.bondList = topolInfo.getBndTypeList();
        this.angleList = topolInfo.getAngTypeList();
        this.diheList = topolInfo.getDiheTypeList();
        this.imprList = topolInfo.getImprTypeList();

        this.isExtendedFormat = false;
        this.isUsingCHEQ = false;
        this.isUsingCMAP = false;
        this.isUsingDRUDE = false;

        this.myname = topolInfo.getFname();

        //fix possibly missing mass of atoms
        this.fixMass(topolInfo);
        this.fixResSegNames();

        //generate psf file
        writer = new CharArrayWriter();
        this.generate();
        
    }

    /**
     * If mass missing use facility provided by the RTF class to find one
     * 
     * @param topolInfo a RTF object containing topology information 
     */
    private void fixMass(RTF topolInfo) {
        for (Atom at : atomList) {
            if (at.getMass() < 1.0) {
                at.setMass(topolInfo.findMass(at.getAtomName()));
            }
        }
    }
    
    /**
     * Fix some missing residues names which were previously set to UNKnown
     */
    private void fixResSegNames()
    {
        for (Atom at : atomList) {
            
            if(at.getResName().compareToIgnoreCase("UNK")==0)
                at.setResName(myname.substring(0, 3).toUpperCase());
            
            if(at.getSegName().compareToIgnoreCase("UNK")==0)
                at.setSegName(myname.substring(0, 3).toUpperCase());
            
        }
    }

    /**
     * Generates a proper PSF by calling in order all methods
     * @throws IOException Thrown if problem happens when writing file
     */
    private void generate() throws IOException {
        this.setFormats();
        this.writeHeaderAndTitle();
        this.writeAtomSection();
        this.writeBondSection();
        this.writeAngleSection();
        this.writeDiheSection();
        this.writeImprSection();
    }

    /**
     * Defines the format depending on value of the extended format boolean
     * Taken directly from CHARMMs fortran code and adapted
     */
    private void setFormats() {
        if (isExtendedFormat) {
            format00 = "%10d%s";
            format01 = "%10d %-8s %-8s %-8s %-8s %4d %14.6G%14.6G%8d";
            format01a = "%10d %-8s %-8s %-8s %-8s %4d %14.6G%14.6G%8d%14.6G%14.6G";
            format01b = "%10d %-8s %-8s %-8s %-8s %4d %14.6G%14.6G%8d%14.6G%14.6G%1b";
            format02 = "%10d %-8s %-8s %-8s %-8s %-6s %14.6G%14.6G%8d%14.6G%14.6G";
            format02a = "%10d %-8s %-8s %-8s %-8s %-6s %14.6G%14.6G%8d%14.6G%14.6G";
            format02b = "%10d %-8s %-8s %-8s %-8s %-6s %14.6G%14.6G%8d%14.6G%14.6G";
            format03 = "%10d%10d";
            format04 = "%10d%10d%10d";
            format05 = "%10d%10d%s";
            format06 = "%10d%10d   %1b%14.6G%14.6G%14.6G";
            format07 = "          %14.6G%14.6G%14.6G";
            format08 = "%10d%s";

            header += " EXT";
        } else {
            format00 = "%8d%s";
            format01 = "%8d %-4s %-4s %-4s %-4s %4d %14.6G%14.6G%8d";
            format01a = "%8d %-4s %-4s %-4s %-4s %4d %14.6G%14.6G%8d%14.6G%14.6G";
            format02 = "%8d %-4s %-4s %-4s %-4s %-4s %14.6G%14.6G%8d";
            format02a = "%8d %-4s %-4s %-4s %-4s %-4s %14.6G%14.6G%8d%14.6G%14.6G";
            format03 = "%8d%8d";
            format04 = "%8d%8d%8d";
            format05 = "%8d%8d%s";
            format06 = "%8d%8d   %1b%14.6G%14.6G%14.6G";
            format07 = "        %14.6G%14.6G%14.6G";
            format08 = "%8d%s";
        }
    }

    /**
     * Write top of the file containing title and info in header
     * @throws IOException Thrown if problem happens when writing file
     */
    private void writeHeaderAndTitle() throws IOException {

        DateFormat df = new SimpleDateFormat();
        df.setTimeZone(TimeZone.getDefault());
        Date d = new Date(df.format(new Date()));

        //keywords at the top of file : PSF, EXT, CMAP, CHECK ...
        writer.write(header + "\n\n");

        //write a title of a few lines with file name, date, ...
        writer.write(String.format(format00, 3, " !NTITLE\n"));
        writer.write("* PSF file for " + this.myname + ".xyz\n");
        writer.write("* generated on " + d.toString() + "\n");
        writer.write("* by user " + System.getProperty("user.name") + " on machine "
                + System.getProperty("os.name") + " " + System.getProperty("os.arch") + " "
                + System.getProperty("os.version") + "\n");
        writer.write("\n");

    }

    /**
     * Writes section listing the atoms at beginning
     * @throws IOException Thrown if problem happens when writing file
     */
    private void writeAtomSection() throws IOException {
        writer.write(String.format(format00, this.natom, " !NATOM\n"));
        for (Atom at : this.atomList) {
            writer.write(String.format(
                    format01,
                    at.getCHARMMAtomID(), at.getSegName(), Integer.toString(at.getResID()), at.getResName(),
                    at.getRtfType(), at.getTypeID(), at.getCharge(), at.getMass(), 0) + "\n");
        }
        writer.write("\n");
    }

    /** 
     * Writes section where bonds are defined
     * Note that charmm writes maximum 4 bonds per line 
     * 
     * @throws IOException Thrown if problem happens when writing file
     */
    private void writeBondSection() throws IOException {
        String line;
        writer.write(String.format(format00, this.nbond, " !NBOND: bonds\n"));
        for (int bnd = 0; bnd < nbond; bnd++) {
            line = String.format(format03,bondList.get(bnd).getA1().getCHARMMAtomID(), bondList.get(bnd).getA2().getCHARMMAtomID());
            int loc = bnd+1;
            if(loc<nbond)
                line += String.format(format03,bondList.get(++bnd).getA1().getCHARMMAtomID(), bondList.get(bnd).getA2().getCHARMMAtomID());
            loc = bnd+1;
            if(loc<nbond)
                line += String.format(format03,bondList.get(++bnd).getA1().getCHARMMAtomID(), bondList.get(bnd).getA2().getCHARMMAtomID());
            loc = bnd+1;
            if(loc<nbond)
                line += String.format(format03,bondList.get(++bnd).getA1().getCHARMMAtomID(), bondList.get(bnd).getA2().getCHARMMAtomID());
            writer.write(line+"\n");
        }
        writer.write("\n");
    }

    /**
     * Writes section where angles are defined
     * Note that charmm writes maximum 3 angles per line
     * 
     * @throws IOException Thrown if problem happens when writing file
     */
    private void writeAngleSection() throws IOException {
        String line;
        writer.write(String.format(format00, this.ntheta, " !NTHETA: angles\n"));
        for (int ang = 0; ang < ntheta; ang++) {
            line = String.format(format04,angleList.get(ang).getA1().getCHARMMAtomID(), angleList.get(ang).getA2().getCHARMMAtomID(), angleList.get(ang).getA3().getCHARMMAtomID());
            int loc = ang+1;
            if(loc<ntheta)
                line += String.format(format04,angleList.get(++ang).getA1().getCHARMMAtomID(), angleList.get(ang).getA2().getCHARMMAtomID(), angleList.get(ang).getA3().getCHARMMAtomID());
            loc = ang+1;
            if(loc<ntheta)
                line += String.format(format04,angleList.get(++ang).getA1().getCHARMMAtomID(), angleList.get(ang).getA2().getCHARMMAtomID(), angleList.get(ang).getA3().getCHARMMAtomID());
            writer.write(line+"\n");
        }
        writer.write("\n");
    }

    /**
     * Writes section where dihedrals are defined
     * 
     * @throws IOException Thrown if problem happens when writing file
     */
    private void writeDiheSection() throws IOException {
        String line;
        writer.write(String.format(format00, this.nphi, " !NPHI: dihedrals\n"));
        for (int dihe = 0; dihe < nphi; dihe++) {
            line =  String.format(format03,diheList.get(dihe).getA1().getCHARMMAtomID(), diheList.get(dihe).getA2().getCHARMMAtomID());
            line += String.format(format03,diheList.get(dihe).getA3().getCHARMMAtomID(), diheList.get(dihe).getA4().getCHARMMAtomID());
            int loc = dihe+1;
            if(loc<nphi){
                line += String.format(format03,diheList.get(++dihe).getA1().getCHARMMAtomID(), diheList.get(dihe).getA2().getCHARMMAtomID());
                line += String.format(format03,diheList.get(dihe).getA3().getCHARMMAtomID(), diheList.get(dihe).getA4().getCHARMMAtomID());
            }
            writer.write(line+"\n");
        }
        writer.write("\n");
    }

    /**
     * Writes section where impropers are defined
     * 
     * @throws IOException Thrown if problem happens when writing file
     */
    private void writeImprSection() throws IOException {
        String line;
        writer.write(String.format(format00, this.nimphi, " !NIMPHI: impropers\n"));
        for (int impr = 0; impr < nimphi; impr++) {
            line =  String.format(format03,imprList.get(impr).getA1().getCHARMMAtomID(), imprList.get(impr).getA2().getCHARMMAtomID());
            line += String.format(format03,imprList.get(impr).getA3().getCHARMMAtomID(), imprList.get(impr).getA4().getCHARMMAtomID());
            int loc = impr+1;
            if(loc<nimphi){
                line += String.format(format03,imprList.get(++impr).getA1().getCHARMMAtomID(), imprList.get(impr).getA2().getCHARMMAtomID());
                line += String.format(format03,imprList.get(impr).getA3().getCHARMMAtomID(), imprList.get(impr).getA4().getCHARMMAtomID());
            }
            writer.write(line+"\n");
        }
        writer.write("\n");
    }
    
    /**
     * Returns content of the writer object as a string
     * @return string corresponding to content of a psf file
     */
    @Override
    public String getTextContent()
    {
        return writer.toString();
    }

    /**
     * Saves the psf file in a given directory
     * 
     * @param dir a directory where to save the psf file
     * @throws IOException
     */
    @Override
    public void writeFile(File dir) throws IOException{
        Writer writerf = new BufferedWriter(
                new FileWriter(
                        new File(dir,myname+".psf")
                )
        );
        writerf.write(writer.toString());
        writerf.close();
    }
    
    /**
     * Define content of the writer object using a string
     * 
     * @param content an input string containing some charmm psf content
     * @throws IOException
     */
    @Override
    public void setModifiedTextContent(String content) throws IOException{
        writer.close();
        writer = null;
        writer = new CharArrayWriter();
        writer.write(content);
    }

}//end class
