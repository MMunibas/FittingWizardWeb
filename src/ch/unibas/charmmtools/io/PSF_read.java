/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.io;

import ch.unibas.charmmtools.exceptions.NotPsfException;
import ch.unibas.charmmtools.types.Angle;
import ch.unibas.charmmtools.types.Bond;
import ch.unibas.charmmtools.types.Dihedral;
import ch.unibas.charmmtools.types.Improper;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hedin
 */
public class PSF_read extends PSF {

    public PSF_read(String filename) {
        myname = filename;

        try {
            s = new Scanner(new FileInputStream(new File(myname)));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PSF.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            parse();
        } catch (NotPsfException ex) {
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

}
