/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */

package ch.unibas.charmmtools.io;

import java.util.Scanner;

import ch.unibas.charmmtools.types.Angle;
import ch.unibas.charmmtools.types.Bond;
import ch.unibas.charmmtools.types.Dihedral;
import ch.unibas.charmmtools.types.Improper;

/**
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
    protected int[] atomID = null;
    protected String[] segName = null;
    protected int[] resID = null;
    protected String[] resName = null;
    protected String[] atomName = null;
    protected int[] typeID = null;
    protected float[] charge = null;
    protected float[] mass = null;
    protected int[] imove = null;

    // optional data depending on booleans isUsingCHEQ isUsingDRUDE
//    protected double[] opt1 = null;
//    protected double[] opt2 = null;

    // the lists read from PSF
    protected Bond[] bondList = null;
    protected Angle[] angleList = null;
    protected Dihedral[] diheList = null;
    protected Improper[] imprList = null;

    protected Scanner s = null;

    protected final String delims = "\\s+";

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
