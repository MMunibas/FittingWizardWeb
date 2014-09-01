/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */

package ch.unibas.charmmtools.files;

import java.util.Scanner;

import ch.unibas.charmmtools.structures.Atom;
import ch.unibas.charmmtools.structures.Angle;
import ch.unibas.charmmtools.structures.Bond;
import ch.unibas.charmmtools.structures.Dihedral;
import ch.unibas.charmmtools.structures.Improper;

/**
 * This abstract class contains attributes and setters/getters useful for reading or generating a CHARMM PSF file.
 *
 * @author hedin
 */
public abstract class PSF {

    protected String myname;

    protected boolean isExtendedFormat = false;
    protected boolean isUsingCMAP = false;
    protected boolean isUsingCHEQ = false;
    protected boolean isUsingDRUDE = false;

    protected int ntitle;
    protected int natom;
    protected int nbond;
    protected int ntheta;
    protected int nphi;
    protected int nimphi;
//    protected int ndon;
//    protected int nacc;
//    protected int nnb;
//    protected int molnt;

    // always present in PSF
//    protected int[] atomID = null;
//    protected String[] segName = null;
//    protected int[] resID = null;
//    protected String[] resName = null;
//    protected String[] atomName = null;
//    protected int[] typeID = null;
//    protected float[] charge = null;
//    protected float[] mass = null;
//    protected int[] imove = null;

    // optional data depending on booleans isUsingCHEQ isUsingDRUDE
//    protected double[] opt1 = null;
//    protected double[] opt2 = null;

    // the lists read from PSF
    protected Atom[] atomList = null;
    protected Bond[] bondList = null;
    protected Angle[] angleList = null;
    protected Dihedral[] diheList = null;
    protected Improper[] imprList = null;

    protected Scanner s = null;

    protected final String delims = "\\s+";

} // end of class PSF
