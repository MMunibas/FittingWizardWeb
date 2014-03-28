/*
 * Copyright (c) 2013, hedin
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package ch.unibas.charmmtools.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

import ch.unibas.charmmtools.exceptions.NotPsfException;
import ch.unibas.charmmtools.types.Angle;
import ch.unibas.charmmtools.types.Bond;
import ch.unibas.charmmtools.types.Dihedral;
import ch.unibas.charmmtools.types.Improper;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hedin
 */
public final class PSF {

    String myname;

    private boolean isExtendedFormat = false;
    private boolean isUsingCMAP = false;
    private boolean isUsingCHEQ = false;
    private boolean isUsingDRUDE = false;

    private int ntitle;
    private int natom;
    private int nbond;
    private int ntheta;
    private int nphi;
    private int nimphi;
//    private int ndon;
//    private int nacc;
//    private int nnb;
//    private int molnt;

    // always present in PSF
    private int[] atomID = null;
    private String[] segName = null;
    private int[] resID = null;
    private String[] resName = null;
    private String[] atomName = null;
    private int[] typeID = null;
    private float[] charge = null;
    private float[] mass = null;
    private int[] imove = null;

    // optional data depending on booleans isUsingCHEQ isUsingDRUDE
//    private double[] opt1 = null;
//    private double[] opt2 = null;

    // the lists read from PSF
    private Bond[] bondList = null;
    private Angle[] angleList = null;
    private Dihedral[] diheList = null;
    private Improper[] imprList = null;

    private Scanner s = null;

    private final String delims = "\\s+";

    public PSF(String filename){
        myname = filename;

        try {
            s = new Scanner(new FileInputStream(new File(myname)));
        } catch (FileNotFoundException ex) {
//            System.err.println(ex.getMessage());
            Logger.getLogger(PSF.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            parse();
        } catch (NotPsfException ex) {
//            System.err.println(ex.getMessage());
            Logger.getLogger(PSF.class.getName()).log(Level.SEVERE, null, ex);
        }

        s.close();
        s = null;
    }

    private void parse() throws NotPsfException {

        String[] tokens = null;
        String inp = s.nextLine();

        // check if this is really a psf
        if (!inp.contains("PSF")) {
            throw new NotPsfException(myname, inp);
        }

        if (inp.contains("EXT")) {
            isExtendedFormat = true;
            System.out.println("This PSF uses the EXTended format.");
        }

        // if some keywords are present on first line, set booleans
        if (inp.contains("CMAP")) {
            isUsingCMAP = true;
            System.out.println("This PSF may contain CMAP data.");
        }
        if (inp.contains("CHEQ")) {
            isUsingCHEQ = true;
            System.out.println("This PSF may contain CHEQ data.");
        }
        if (inp.contains("DRUDE")) {
            isUsingDRUDE = true;
            System.out.println("This PSF may contain CHEQ data.");
        }

        // skip one or more useless lines between first line and line containing ntitle
        do {
            inp = s.nextLine();
        } while (!inp.contains("NTITLE"));
        tokens = inp.trim().split(delims);
        // read ntitle and skip Title lines
        ntitle = Integer.parseInt(tokens[0]);
        for (int i = 0; i < ntitle; i++) {
            inp = s.nextLine();
        }

        // again skip blank lines until we find the natom integer
        do {
            inp = s.nextLine();
        } while (!inp.contains("NATOM"));
        tokens = inp.trim().split(delims);
        // get the natom from PSF
        natom = Integer.parseInt(tokens[0]);
//        System.out.println("natom from PSF : " + natom);

        allocate();

        // read params of atom section
        for (int i = 0; i < natom; i++) {
            inp = s.nextLine();
            tokens = inp.trim().split(delims);
            int idx = 0;
            atomID[i] = Integer.parseInt(tokens[idx++]);
            segName[i] = tokens[idx++];
            resID[i] = Integer.parseInt(tokens[idx++]);
            resName[i] = tokens[idx++];
            atomName[i] = tokens[idx++];
            typeID[i] = Integer.parseInt(tokens[idx++]);
            charge[i] = Float.parseFloat(tokens[idx++]);
            mass[i] = Float.parseFloat(tokens[idx++]);
            imove[i] = Integer.parseInt(tokens[idx++]);
        }

        // go to bonds section
        do {
            inp = s.nextLine();
        } while (!inp.contains("NBOND"));
        tokens = inp.trim().split(delims);
        nbond = Integer.parseInt(tokens[0]);
//        System.out.println("nbond from PSF : " + nbond);
        // Fill bond array
        bondList = new Bond[nbond];
        for (int i = 0; i < nbond; i++) {
            bondList[i] = new Bond(s.nextInt(), s.nextInt());
        }

        // go to angles section
        do {
            inp = s.nextLine();
        } while (!inp.contains("NTHETA"));
        tokens = inp.trim().split(delims);
        ntheta = Integer.parseInt(tokens[0]);
//        System.out.println("nangles from PSF : " + ntheta);
        // Fill bond array
        angleList = new Angle[ntheta];
        for (int i = 0; i < ntheta; i++) {
            angleList[i] = new Angle(s.nextInt(), s.nextInt(), s.nextInt());
        }

        // go to dihedrals section
        do {
            inp = s.nextLine();
        } while (!inp.contains("NPHI"));
        tokens = inp.trim().split(delims);
        nphi = Integer.parseInt(tokens[0]);
//        System.out.println("nphi from PSF : " + nphi);
        // Fill bond array
        diheList = new Dihedral[nphi];
        for (int i = 0; i < nphi; i++) {
            diheList[i] = new Dihedral(s.nextInt(), s.nextInt(), s.nextInt(), s.nextInt());
        }

        // go to impropers section
        do {
            inp = s.nextLine();
        } while (!inp.contains("NIMPHI"));
        tokens = inp.trim().split(delims);
        nimphi = Integer.parseInt(tokens[0]);
//        System.out.println("nimphi from PSF : " + nimphi);
        // Fill bond array
        imprList = new Improper[nimphi];
        for (int i = 0; i < nphi; i++) {
            imprList[i] = new Improper(s.nextInt(), s.nextInt(), s.nextInt(), s.nextInt());
        }

    } //end of parse routine

    private void allocate() {
        //allocate memory
        atomID = new int[natom];
        segName = new String[natom];
        resID = new int[natom];
        resName = new String[natom];
        atomName = new String[natom];
        typeID = new int[natom];
        charge = new float[natom];
        mass = new float[natom];
        imove = new int[natom];

        /* TODO : handle DRUDE and CHECK */
    }

    /**
     * @return the isExtendedFormat
     */
    public boolean isExtendedFormat() {
        return isExtendedFormat;
    }

    /**
     * @return the isUsingCMAP
     */
    public boolean isUsingCMAP() {
        return isUsingCMAP;
    }

    /**
     * @return the isUsingCHEQ
     */
    public boolean isUsingCHEQ() {
        return isUsingCHEQ;
    }

    /**
     * @return the isUsingDRUDE
     */
    public boolean isUsingDRUDE() {
        return isUsingDRUDE;
    }

    /**
     * @return the ntitle
     */
    public int getNtitle() {
        return ntitle;
    }

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
     * @return the segName
     */
    public String[] getSegName() {
        return segName;
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
     * @return the typeID
     */
    public int[] getTypeID() {
        return typeID;
    }

    /**
     * @return the charge
     */
    public float[] getCharge() {
        return charge;
    }

    /**
     * @return the mass
     */
    public float[] getMass() {
        return mass;
    }

    /**
     * @return the imove
     */
    public int[] getImove() {
        return imove;
    }

} // end of class PSF
