/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */

package ch.unibas.charmmtools.files.coordinates;

import org.apache.log4j.Logger;

/**
 * This class reads a COR file but can also be used for writing a COR with a similar structure but updated coordinates
 *
 * @author hedin
 */
public abstract class COR {

    protected static final Logger logger = Logger.getLogger(COR.class);
    
    protected int natom;

    protected int[] atomID = null;
    protected int[] resID = null;
    protected String[] resName = null;
    protected String[] atomName = null;
    protected float[] x = null;
    protected float[] y = null;
    protected float[] z = null;
    protected String[] segName = null;
    protected int[] segID = null;
    protected float[] weight = null;

    protected final String delims = "\\s+";

    protected boolean isExtendedFormat = false;

    protected void allocate() {
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

//    public void changeCoordinates(float[] rx, float[] ry, float[] rz) {
//        if (rx.length == this.x.length) {
//            System.arraycopy(rx, 0, this.x, 0, this.x.length);
//        }
//
//        if (ry.length == this.y.length) {
//            System.arraycopy(ry, 0, this.y, 0, this.y.length);
//        }
//
//        if (rz.length == this.z.length) {
//            System.arraycopy(rz, 0, this.z, 0, this.z.length);
//        }
//    }

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
