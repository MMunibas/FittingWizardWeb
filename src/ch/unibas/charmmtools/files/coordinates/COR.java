/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */

package ch.unibas.charmmtools.files.coordinates;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class reads a COR file but can also be used for writing a COR with a similar structure but updated coordinates
 *
 * @author hedin
 */
public class COR {

    private int natom;

    private int[] atomID = null;
    private int[] resID = null;
    private String[] resName = null;
    private String[] atomName = null;
    private float[] x = null;
    private float[] y = null;
    private float[] z = null;
    private String[] segName = null;
    private int[] segID = null;
    private float[] weight = null;

    private final String delims = "\\s+";

    private boolean isExtendedFormat = false;

    private Scanner s = null;

    public COR(String filename){
        try {
            // open the cor text file (buffered to have efficient IO even if large file)
            s = new Scanner(new FileInputStream(new File(filename)));
        } catch (FileNotFoundException ex) {
//            System.err.println(ex.getMessage());
            Logger.getLogger(COR.class.getName()).log(Level.SEVERE, null, ex);
        }
        parse();
        s.close();
        s = null;
    }

    private void parse() {

        // skip comment lines
        String inp = s.nextLine();
        while (inp.contains("*")) {
            inp = s.nextLine();
        }

        // if the line containing natom also contains EXT it means the file is extended format
        if (inp.contains("EXT")) {
            isExtendedFormat = true;
            System.out.println("Extended format COR file detected !");
        }

        // get natom from cor
        String[] tokens = inp.trim().split(delims);

//        for (int i = 0; i < tokens.length; i++) {
//            tokens[i] = tokens[i].trim();
//        }
//        for (int i = 0; i < tokens.length; i++) {
//            System.out.println(i);
//            System.out.println(tokens[i]);
//        }
//        System.exit(0);
        natom = Integer.parseInt(tokens[0]);

        allocate();

        for (int i = 0; i < natom; i++) {
            inp = s.nextLine();
            tokens = inp.trim().split(delims);
            int idx = 0;
            atomID[i] = Integer.parseInt(tokens[idx++]);
            resID[i] = Integer.parseInt(tokens[idx++]);
            resName[i] = tokens[idx++];
            atomName[i] = tokens[idx++];
            x[i] = Float.parseFloat(tokens[idx++]);
            y[i] = Float.parseFloat(tokens[idx++]);
            z[i] = Float.parseFloat(tokens[idx++]);
            segName[i] = tokens[idx++];
            segID[i] = Integer.parseInt(tokens[idx++]);
            weight[i] = Float.parseFloat(tokens[idx++]);
        }

    }

    private void allocate() {
        //allocate memory
        atomID = new int[natom];
        resID = new int[natom];
        resName = new String[natom];
        atomName = new String[natom];
        x = new float[natom];
        y = new float[natom];
        z = new float[natom];
        segName = new String[natom];
        segID = new int[natom];
        weight = new float[natom];
    }

    public void changeCoordinates(float[] rx, float[] ry, float[] rz) {
        if (rx.length == this.x.length) {
            System.arraycopy(rx, 0, this.x, 0, this.x.length);
        }

        if (ry.length == this.y.length) {
            System.arraycopy(ry, 0, this.y, 0, this.y.length);
        }

        if (rz.length == this.z.length) {
            System.arraycopy(rz, 0, this.z, 0, this.z.length);
        }
    }

    public void dumpCOR(String OutFileName){

        BufferedWriter of = null;
        String format = "";
        String line = "";

        if (this.isExtendedFormat) {
            /*fortran :
             write(iunit,'(i10,2x,a)') nslct,'EXT'
             fm2='(2I10,2X,A8,2X,A8,3F20.10,2X,A8,2X,A8,F20.10)'
             */
            format = "%10d%10d  %-8s  %-8s%20.10f%20.10f%20.10f  %-8s  %-8s%20.10f\n";
        } else {
            /*fortran :
             write(iunit,'(i5)') nslct
             fm2='(2I5,1X,A4,1X,A4,3F10.5,1X,A4,1X,A4,F10.5)'
             */
            format = "%5d%5d %-4s %-4s%10.5f%10.5f%10.5f %-4s %-4s%10.5f\n";
        }

        try {

            of = new BufferedWriter(new FileWriter(OutFileName));

            // First put some comment line
            of.write("* Generated with CHARMM_tools\n");
            of.write("* User : " + System.getProperty("user.name") + "\n");
            of.write("* Date : " + new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z").format(Calendar.getInstance().getTime()) + "\n");
            of.write("*\n");

            // then the number of atoms and possibly the extended keyword
            if (this.isExtendedFormat) {
                line = String.format("%10d  EXT\n", this.natom);
            } else {
                line = String.format("%5d\n", this.natom);
            }
            of.write(line);

            //then loop and write all elements
            for (int i = 0; i < this.natom; i++) {
                line = String.format(format,
                        atomID[i],
                        resID[i],
                        resName[i],
                        atomName[i],
                        x[i],
                        y[i],
                        z[i],
                        segName[i],
                        segID[i],
                        weight[i]
                );
                of.write(line);
            }// end for

            // close stream properly
            of.close();

        } catch (IOException ex) {
//            System.err.println(ex.getMessage());
            Logger.getLogger(COR.class.getName()).log(Level.SEVERE, null, ex);
        }

    }// end of dumpCOR

    /**
     * @return the natom
     */
    public int getNatom() {
        return natom;
    }

    /**
     * @return the atomID
     */
    public int[] getAtomID() {
        return atomID;
    }

    /**
     * @return the resID
     */
    public int[] getResID() {
        return resID;
    }

    /**
     * @return the resName
     */
    public String[] getResName() {
        return resName;
    }

    /**
     * @return the atomName
     */
    public String[] getAtomName() {
        return atomName;
    }

    /**
     * @return the x
     */
    public float[] getX() {
        return x;
    }

    /**
     * @return the y
     */
    public float[] getY() {
        return y;
    }

    /**
     * @return the z
     */
    public float[] getZ() {
        return z;
    }

    /**
     * @return the segName
     */
    public String[] getSegName() {
        return segName;
    }

    /**
     * @return the segID
     */
    public int[] getSegID() {
        return segID;
    }

    /**
     * @return the weight
     */
    public float[] getWeight() {
        return weight;
    }

    /**
     * @return the isExtendedFormat
     */
    public boolean isExtendedFormat() {
        return isExtendedFormat;
    }

} // end class
