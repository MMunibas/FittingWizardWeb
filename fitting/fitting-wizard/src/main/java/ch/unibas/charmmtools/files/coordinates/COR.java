/*
 * Copyright (c) 2016, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */

package ch.unibas.charmmtools.files.coordinates;

import org.apache.log4j.Logger;

/**
 * This abstract class defines a CHARMM coordinates COR file
 *
 * @author hedin
 */
public abstract class COR {

    protected static final Logger logger = Logger.getLogger(COR.class);
    
    /**
     * The number of atoms for this coordinates file
     */
    protected int natom;

    /**
     * the CHARMM atom id as integer
     */
    protected int[] atomID = null;

    /**
     * the CHARMM residue id as integer
     */
    protected int[] resID = null;

    /**
     * the CHARMM residue name
     */
    protected String[] resName = null;

    /**
     * the CHARMM atom name (i.e. forcefield type)
     */
    protected String[] atomName = null;

    /**
     * the x y z coordinates of the atom
     */
    protected float[] x,y,z = null;

    /**
     * the CHARMM segment name
     */
    protected String[] segName = null;

    /**
     * the CHARMM segment id as integer
     */
    protected int[] segID = null;

    /**
     * corresponds to the weight column of the cor file sometimes 
     * used for storing (weird) things
     */
    protected float[] weight = null;

    /**
     * used for parsing lines, defines where to split strings (one or more white spaces)
     */
    protected final String delims = "\\s+";

    /**
     * Set to true if extended format cor file (higher precision for coordinates)
     */
    protected boolean isExtendedFormat = false;

    /**
     * performs memory reservation/allocation for the arrays
     */
    protected void allocate() {
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

    /**
     * Returns natom
     * @return the number of atoms in cor file
     */
    public int getNatom() {
        return natom;
    }

    /**
     * Returns the Atom ID array
     * @return the atomID array of cor file
     */
    public int[] getAtomID() {
        return atomID;
    }

    /**
     * Returns the Residue ID array
     * @return the resID array of cor file
     */
    public int[] getResID() {
        return resID;
    }

    /**
     * Returns the resName array
     * @return the resName array of cor file
     */
    public String[] getResName() {
        return resName;
    }

    /**
     * Returns the atomName array
     * @return the atomName array of cor file
     */
    public String[] getAtomName() {
        return atomName;
    }

    /**
     * Returns the x coordinates array
     * @return the x coordinates array of cor file
     */
    public float[] getX() {
        return x;
    }

    /**
     * Returns the y coordinates array
     * @return the y coordinates array of cor file
     */
    public float[] getY() {
        return y;
    }

    /**
     * Returns the z coordinates array
     * @return the z coordinates array of cor file
     */
    public float[] getZ() {
        return z;
    }

    /**
     * Returns the segName array
     * @return the segName array of cor file
     */
    public String[] getSegName() {
        return segName;
    }

    /**
     * Returns the segments ID array
     * @return the segID array of cor file
     */
    public int[] getSegID() {
        return segID;
    }

    /**
     * Returns the weight array
     * @return the weight array of cor file
     */
    public float[] getWeight() {
        return weight;
    }

    /**
     * In order to know if the currently read core file is extended or not
     * @return the isExtendedFormat boolean value from cor file
     */
    public boolean isExtendedFormat() {
        return isExtendedFormat;
    }

} // end class
