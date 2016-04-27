/*
 * Copyright (c) 2016, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.files.coordinates;

import ch.unibas.charmmtools.files.structure.PSF;
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
import java.util.List;
import java.util.TimeZone;

/**
 * This class is used for writing a PDB coordinates file 
 * compatible with CHARMM
 * @author hedin
 */
public class PDB_generate extends PDB implements coordinates_writer{

    /**
     * writer object containing text content
     */
    private Writer writer = null;

    /**
     * A PSF file is required for writing properly a PDB file
     */
    private PSF psff;

    /**
     * Constructor using a psf as input
     * 
     * @param _psff a PSF object previously created
     */
    public PDB_generate(PSF _psff) {

        this.psff = _psff;
        this.fname = this.psff.getMyname();
        try {
            writer = new CharArrayWriter();
            this.writeTitle();
            this.writePdbCore();
            this.writePdbEnd();
        } catch (IOException ex) {
            logger.error("Error while generating PDB file : " + ex);
        }
    }

    /**
     * Writes top of the PDB file containing title and various info
     * @throws IOException Thrown if problem happens when writing file
     */
    private void writeTitle() throws IOException {

        DateFormat df = new SimpleDateFormat();
        df.setTimeZone(TimeZone.getDefault());
        Date d = new Date(df.format(new Date()));

        writer.write("REMARK PDB file for " + fname + ".xyz\n");
        writer.write("REMARK generated on " + d.toString() + "\n");
        writer.write("REMARK by user " + System.getProperty("user.name") + " on machine "
                + System.getProperty("os.name") + " " + System.getProperty("os.arch") + " "
                + System.getProperty("os.version") + "\n");
    }

    /**
     * Writes the core of the pdb file containing coordinates and other data
     * @throws IOException Thrown if problem happens when writing file
     */
    private void writePdbCore() throws IOException {

        String line = "";
        List<Atom> atlist = psff.getAtomList();
        
        for (Atom a : atlist) {
            line = String.format(PDB.charmmFormat, "ATOM  ", a.getCHARMMAtomID(), a.getRtfType(),
                    a.getResName(), a.getResID(), a.getX(), a.getY(), a.getZ(),
                    1.0, 0.0, a.getSegName());
            writer.write(line+"\n");
        }
    }

    /**
     * Writes end of the pdb ; for the moment only the end keyword but official 
     * pdb may have much more here
     * @throws IOException Thrown if problem happens when writing file
     */
    private void writePdbEnd() throws IOException {
        writer.write("END\n");
    }

    /**
     * Returns content of the writer object as a string
     * @return string corresponding to content of a pdb file
     */
    @Override
    public String getTextContent() {
        return writer.toString();
    }
    
    /**
     * Saves the pdb file in a given directory
     * 
     * @param dir a directory where to save the pdb file
     * @throws IOException Thrown if problem happens when writing file
     */
    @Override
    public void writeFile(File dir) throws IOException {
        Writer writerf = new BufferedWriter(
                new FileWriter(
                        new File(dir,fname+".pdb")
                )
        );
        writerf.write(writer.toString());
        writerf.close();
    }
    
    /**
     * Define content of the writer object using a string
     * @param content an input string containing some charmm cor content
     * @throws IOException Thrown if problem happens when writing file
     */
    @Override
    public void setModifiedTextContent(String content) throws IOException{
        writer.close();
        writer = null;
        writer = new CharArrayWriter();
        writer.write(content);
    }

}
