/*
 * Copyright (c) 2016, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.files.structure;

import ch.unibas.charmmtools.exceptions.NotPsfException;
import ch.unibas.charmmtools.internals.Angle;
import ch.unibas.charmmtools.internals.Atom;
import ch.unibas.charmmtools.internals.Bond;
import ch.unibas.charmmtools.internals.Dihedral;
import ch.unibas.charmmtools.internals.Improper;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * This class is derived from the abstract PSF class, it contains a parse method
 * reading a PSF file and storing all the stuff in inherited attributes .
 *
 * @author hedin
 */
public final class PSF_read extends PSF {

    /**
     * This is in charge of reading text
     */
    private Scanner s = null;

    /**
     * Delimitation string for parsing (one or more white spaces)
     */
    private final String delims = "\\s+";

    /**
     * Constructor reading PSF and building an object with its content
     *
     * @param filename The path to the psf file
     */
    public PSF_read(String filename) {
        myname = filename;

        try {
            s = new Scanner(new FileInputStream(new File(myname)));
        } catch (FileNotFoundException ex) {
            logger.error("Cannot find your psf file : " + ex.getMessage());
        }

        try {
            parse();
        } catch (NotPsfException ex) {
            logger.error("Error while reading your psf file : " + ex.getMessage());
        }

        s.close();
        s = null;
    }

    /**
     * Parses the psf file and stores its data
     *
     * @throws NotPsfException Thrown if the file does not seems to be a valid
     * psf file
     */
    private void parse() throws NotPsfException {

        String[] tokens = null;
        String inp = s.nextLine();

        // check if this is really a psf
        if (!inp.contains("PSF")) {
            throw new NotPsfException(myname, inp);
        }

        if (inp.contains("EXT")) {
            isExtendedFormat = true;
            logger.info("This PSF uses the EXTended format.");
        }

        // if some keywords are present on first line, set booleans
        if (inp.contains("CMAP")) {
            isUsingCMAP = true;
            logger.info("This PSF may contain CMAP data.");
        }
        if (inp.contains("CHEQ")) {
            isUsingCHEQ = true;
            logger.info("This PSF may contain CHEQ data.");
        }
        if (inp.contains("DRUDE")) {
            isUsingDRUDE = true;
            logger.info("This PSF may contain CHEQ data.");
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
        logger.debug("natom from PSF : " + natom);


        // read params of atom section
        for (int i = 0; i < natom; i++) {
            inp = s.nextLine();
            tokens = inp.trim().split(delims);
            int idx = 0;
            atomList.add(
                    new Atom(Integer.parseInt(tokens[idx++]))
            );
            atomList.get(i).setSegName(tokens[idx++]);
            atomList.get(i).setResID(Integer.parseInt(tokens[idx++]));
            atomList.get(i).setResName(tokens[idx++]);
            atomList.get(i).setAtomName(tokens[idx++]);
            atomList.get(i).setTypeID(Integer.parseInt(tokens[idx++]));
            atomList.get(i).setCharge(Float.parseFloat(tokens[idx++]));
            atomList.get(i).setMass(Float.parseFloat(tokens[idx++]));
            atomList.get(i).setImove(Integer.parseInt(tokens[idx++]));
        }

        // go to bonds section
        do {
            inp = s.nextLine();
        } while (!inp.contains("NBOND"));
        tokens = inp.trim().split(delims);
        nbond = Integer.parseInt(tokens[0]);
        logger.debug("nbond from PSF : " + nbond);
        
        // Fill bond array
        for (int i = 0; i < nbond; i++) {
            bondList.add(
                    new Bond(atomList.get(s.nextInt()), atomList.get(s.nextInt()))
            );
        }

        // go to angles section
        do {
            inp = s.nextLine();
        } while (!inp.contains("NTHETA"));
        tokens = inp.trim().split(delims);
        ntheta = Integer.parseInt(tokens[0]);
        logger.debug("nangles from PSF : " + ntheta);
        
        // Fill angle array
        for (int i = 0; i < ntheta; i++) {
            angleList.add(
                    new Angle(atomList.get(s.nextInt()), atomList.get(s.nextInt()), atomList.get(s.nextInt()))
            );
        }

        // go to dihedrals section
        do {
            inp = s.nextLine();
        } while (!inp.contains("NPHI"));
        tokens = inp.trim().split(delims);
        nphi = Integer.parseInt(tokens[0]);
        logger.debug("nphi from PSF : " + nphi);

        // Fill dihe array
        for (int i = 0; i < nphi; i++) {
            diheList.add(
                    new Dihedral(atomList.get(s.nextInt()), atomList.get(s.nextInt()), atomList.get(s.nextInt()), atomList.get(s.nextInt()))
            );
        }

        // go to impropers section
        do {
            inp = s.nextLine();
        } while (!inp.contains("NIMPHI"));
        tokens = inp.trim().split(delims);
        nimphi = Integer.parseInt(tokens[0]);
        logger.debug("nimphi from PSF : " + nimphi);

        // Fill impropers array
        for (int i = 0; i < nphi; i++) {
            imprList.add(
                    new Improper(atomList.get(s.nextInt()), atomList.get(s.nextInt()), atomList.get(s.nextInt()), atomList.get(s.nextInt()))
            );
        }

    } //end of parse routine

}//end class
