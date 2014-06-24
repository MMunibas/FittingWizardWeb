/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */

package ch.unibas.charmmtools.types;

/**
 *
 * @author hedin
 */
public class Atom {

    private int atomID = 0;
    private String segName = null;
    private int resID = 0;
    private String resName = null;
    private String atomName = null;
    private int typeID = 0;
    private double charge = 0.0;
    private double mass = 0.0;
    private int imove = 0;

    private double x = 0.0, y = 0.0, z = 0.0;

    public Atom(int _id) {
        this.atomID = _id;
    }

    public Atom(int _id, String _symbol) {
        this.atomID = _id;
        this.atomName = _symbol;
    }

    public Atom(int _id, double _x, double _y, double _z) {
        this.atomID = _id;
        this.x = _x;
        this.y = _y;
        this.z = _z;
    }

    public Atom(int _id, String _symbol, double _x, double _y, double _z) {
        this.atomID = _id;
        this.atomName = _symbol;
        this.x = _x;
        this.y = _y;
        this.z = _z;
    }

    /**
     * @return the atomID
     */
    public int getAtomID() {
        return atomID;
    }

    /**
     * @param atomID the atomID to set
     */
    public void setAtomID(int atomID) {
        this.atomID = atomID;
    }

    /**
     * @return the segName
     */
    public String getSegName() {
        return segName;
    }

    /**
     * @param segName the segName to set
     */
    public void setSegName(String segName) {
        this.segName = segName;
    }

    /**
     * @return the resID
     */
    public int getResID() {
        return resID;
    }

    /**
     * @param resID the resID to set
     */
    public void setResID(int resID) {
        this.resID = resID;
    }

    /**
     * @return the resName
     */
    public String getResName() {
        return resName;
    }

    /**
     * @param resName the resName to set
     */
    public void setResName(String resName) {
        this.resName = resName;
    }

    /**
     * @return the atomName
     */
    public String getAtomName() {
        return atomName;
    }

    /**
     * @param atomName the atomName to set
     */
    public void setAtomName(String atomName) {
        this.atomName = atomName;
    }

    /**
     * @return the typeID
     */
    public int getTypeID() {
        return typeID;
    }

    /**
     * @param typeID the typeID to set
     */
    public void setTypeID(int typeID) {
        this.typeID = typeID;
    }

    /**
     * @return the charge
     */
    public double getCharge() {
        return charge;
    }

    /**
     * @param charge the charge to set
     */
    public void setCharge(double charge) {
        this.charge = charge;
    }

    /**
     * @return the mass
     */
    public double getMass() {
        return mass;
    }

    /**
     * @param mass the mass to set
     */
    public void setMass(double mass) {
        this.mass = mass;
    }

    /**
     * @return the imove
     */
    public int getImove() {
        return imove;
    }

    /**
     * @param imove the imove to set
     */
    public void setImove(int imove) {
        this.imove = imove;
    }

    /**
     * @return the x
     */
    public double getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public double getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * @return the z
     */
    public double getZ() {
        return z;
    }

    /**
     * @param z the z to set
     */
    public void setZ(double z) {
        this.z = z;
    }

}//end class
