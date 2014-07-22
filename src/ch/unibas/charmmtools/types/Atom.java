/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */

package ch.unibas.charmmtools.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author hedin
 */
public class Atom {

    /**
     * data from the PSF
     */
    private int atomID = 0;
    private String segName = "UNK";
    private int resID = 0;
    private String resName = "UNK";
    private String atomName = "UNK";
    private int typeID = 0;
    private double charge = 0.0;
    private double mass = 0.0;
    private int imove = 0;

    /**
     * Coordinates of this atom
     */
    private double x = 0.0, y = 0.0, z = 0.0;

    /**
     * Count the number of bonds of this atom with other atoms
     */
    private int numberOfBonds = 0;

    /**
     * Represents the hybridisation state of this atom : sp3, sp2, ...
     */
    private String hybridisation = "UNK";

    /**
     * List of AtomID to which this atom is linked ; at least of size 3
     */
    private List<Integer> linkingList = new ArrayList<>(3);

    /**
     * For a given atom, stores to which atom type this atom is linked
     */
    private HashMap<String, Integer> connectivity = new HashMap<>();

    /**
     * atom type for RTF file (CT2, CT3, CA, ...)
     */
    private String rtfType = "UNK";

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
     * saves the id of an atom to which this atom is connected
     *
     * @param atomId
     */
    public void addBondTo(int atomId) {
        linkingList.add(atomId);
        numberOfBonds++;
    }

    /**
     * This returns the atom id of the atom linked to the current atom
     *
     * @param index
     * @return For a given index, returns the atom id of the atom linked to the current atom
     */
    public int getBondAt(int index) {
        return linkingList.get(index);
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

    /**
     * @return the numberOfBonds
     */
    public int getNumberOfBonds() {
        return numberOfBonds;
    }

//    /**
//     * @param numberOfBonds the numberOfBonds to set
//     */
//    public void setNumberOfBonds(int numberOfBonds) {
//        this.numberOfBonds = numberOfBonds;
//    }

    /**
     * @return the hybridisation
     */
    public String getHybridisation() {
        return ((hybridisation == null) ? "UNK" : hybridisation);
//        return hybridisation;
    }

    /**
     * @param hybridisation the hybridisation to set
     */
    public void setHybridisation(String hybridisation) {
        this.hybridisation = hybridisation;
    }

    /**
     * @return the linkingList
     */
    public List<Integer> getLinkingList() {
        return linkingList;
    }

    /**
     * @param linkingList the linkingList to set
     */
    public void setLinkingList(List<Integer> linkingList) {
        this.linkingList = linkingList;
    }

    public void addConnectivity(String type) {
        if (this.connectivity.containsKey(type)) {
            this.connectivity.put(type, this.connectivity.get(type) + 1);
        } else {
            this.connectivity.put(type, 1);
        }
    }

    /**
     * @return the connectivity
     */
    public HashMap<String, Integer> getConnectivity() {
        return connectivity;
    }

    /**
     * @return the rtfType
     */
    public String getRtfType() {
        //return ((rtfType == null) ? "UNK" : rtfType);
        return rtfType;
    }

    /**
     * @param rtfType the rtfType to set
     */
    public void setRtfType(String rtfType) {
        this.rtfType = rtfType;
    }

}//end class
