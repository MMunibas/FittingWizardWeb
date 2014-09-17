/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */

package ch.unibas.charmmtools.files;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * This class derived from the abstract PSF class is used for generating a new psf file useful for CHARMM
 *
 * @author hedin
 */
public final class PSF_generate extends PSF {

    private String header = "PSF";

    private String format00, format01, format01a, format01b;
    private String format02, format02a, format02b, format03;
    private String format04, format05, format06, format07, format08;

    private BufferedWriter writer = null;

    public PSF_generate(RTF topolInfo) throws IOException {

        /*
            TODO : NEED TO IMPROVE THIS PART
        */
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

        //force extended output and disable other options
        this.isExtendedFormat = true;
        this.isUsingCHEQ = false;
        this.isUsingCMAP = false;
        this.isUsingDRUDE = false;

        this.myname = topolInfo.getFname();

        writer = new BufferedWriter(new FileWriter(this.myname + ".psf"));

        this.generate();

        writer.close();
    }

    private void generate() throws IOException {
        this.setFormats();
        this.writeHeaderAndTitle();
        this.writeNatomSection();
    }

    private void setFormats() {
        if (isExtendedFormat) {
            format00 = "%10d%s";
            format01 = "%10d %8c %8c %8c %8c %4d %14.6G%14.6G%8d";
            format01a = "%10d %8c %8c %8c %8c %4d %14.6G%14.6G%8d%14.6G%14.6G";
            format01b = "%10d %8c %8c %8c %8c %4d %14.6G%14.6G%8d%14.6G%14.6G%1b";
            format02 = "%10d %8c %8c %8c %8c %6c %14.6G%14.6G%8d%14.6G%14.6G";
            format02a = "%10d %8c %8c %8c %8c %6c %14.6G%14.6G%8d%14.6G%14.6G";
            format02b = "%10d %8c %8c %8c %8c %6c %14.6G%14.6G%8d%14.6G%14.6G";
            format03 = "%10d%10d%10d%10d%10d%10d%10d%10d";
            format04 = "%10d%10d%10d%10d%10d%10d%10d%10d%10d";
            format05 = "%10d%10d%s";
            format06 = "%10d%10d   %1b%14.6G%14.6G%14.6G";
            format07 = "          %14.6G%14.6G%14.6G";
            format08 = "%10d%s";

            header += " EXT";
        } else {
            format00 = "%8d%s";
            format01 = "%8d %4c %4c %4c %4c %4d %14.6G%14.6G%8d";
            format01a = "%8d %4c %4c %4c %4c %4d %14.6G%14.6G%8d%14.6G%14.6G";
            format02 = "%8d %4c %4c %4c %4c %4c %14.6G%14.6G%8d";
            format02a = "%8d %4c %4c %4c %4c %4c %14.6G%14.6G%8d%14.6G%14.6G";
            format03 = "%8d%8d%8d%8d%8d%8d%8d%8d";
            format04 = "%8d%8d%8d%8d%8d%8d%8d%8d%8d";
            format05 = "%8d%8d%s";
            format06 = "%8d%8d   %1b%14.6G%14.6G%14.6G";
            format07 = "        %14.6G%14.6G%14.6G";
            format08 = "%8d%s";
        }
    }

    private void writeHeaderAndTitle() throws IOException {

        Date d = new Date();

        //keywords at the top of file : PSF, EXT, CMAP, CHECK ...
        writer.write(header + "\n");

        //write a title of a few lines with file name, date, ...
        writer.write(String.format(format00, 3, " !NTITLE\n"));
        writer.write("* PSF file for " + this.myname + ".xyz\n");
        writer.write("* generated on " + d.toString() + "\n");
        writer.write("* by user " + System.getProperty("user.name") + " on machine "
                + System.getProperty("os.name") + " " + System.getProperty("os.arch") + " "
                + System.getProperty("os.version") + "\n");
        writer.write("\n");

    }

    private void writeNatomSection() throws IOException {
        writer.write(String.format(format00, this.natom, " !NATOM\n"));
    }



}//end class
