/*
 * Copyright (c) 2016, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */

package ch.unibas.charmmtools.files.structure;

import ch.unibas.charmmtools.internals.Angle;
import ch.unibas.charmmtools.internals.Atom;
import ch.unibas.charmmtools.internals.Bond;
import ch.unibas.charmmtools.internals.Dihedral;
import ch.unibas.charmmtools.internals.Improper;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * This abstract class contains attributes and setters/getters useful for reading or generating a CHARMM PSF file.
 *
 * @author hedin
 */
public abstract class PSF {

    protected static final Logger logger = Logger.getLogger(PSF.class);
    
    /**
     * Name of the PSF file
     */
    protected String myname;

    /**
     * For checking weather the PSF is using extended precision
     */
    protected boolean isExtendedFormat = false;
    
    /**
     * For checking weather the PSF provides CMAP terms
     */
    protected boolean isUsingCMAP = false;
    
    /**
     * For checking weather the PSF provides CHEQ terms
     */
    protected boolean isUsingCHEQ = false;
    
    /**
     * For checking weather the DRUDE is using extended precision
     */
    protected boolean isUsingDRUDE = false;

    /**
     * Number of lines for titles
     */
    protected int ntitle;
    
    /**
     * Number of atoms in PSF
     */
    protected int natom;
    
    /**
     * Number of bonds in PSF
     */
    protected int nbond;
    
    /**
     * Number of angles in PSF
     */
    protected int ntheta;
    
    /**
     * Number of Dihedrals in PSF
     */
    protected int nphi;
    
    /**
     * Number of improper dihedrals in PSF
     */
    protected int nimphi;

    /**
     * A list for storing all atoms
     */
    protected List<Atom> atomList = null;
    
    /**
     * A list for storing all bonds
     */
    protected List<Bond> bondList = null;
    
    /**
     * A list for storing all angles
     */
    protected List<Angle> angleList = null;
    
    /**
     * A list for storing all dihedrals
     */
    protected List<Dihedral> diheList = null;
    
    /**
     * A list for storing all impropers
     */
    protected List<Improper> imprList = null;
    
    /**
     * For getting file name
     * @return the file name
     */
    public String getMyname() {
        return myname;
    }

    /**
     * To check if extended format is used
     * @return the isExtendedFormat boolean
     */
    public boolean isIsExtendedFormat() {
        return isExtendedFormat;
    }

    /**
     * To check if CMAP are present
     * @return the isUsingCMAP boolean
     */
    public boolean isIsUsingCMAP() {
        return isUsingCMAP;
    }

    /**
     * To check if CHEQ is present
     * @return the isUsingCHEQ boolean
     */
    public boolean isIsUsingCHEQ() {
        return isUsingCHEQ;
    }

    /**
     * To check if DRUDE is present
     * @return the isUsingDRUDE boolean
     */
    public boolean isIsUsingDRUDE() {
        return isUsingDRUDE;
    }

    /**
     * To get ntitle
     * @return the ntitle
     */
    public int getNtitle() {
        return ntitle;
    }

    /**
     * To get the number of atoms
     * @return the natom
     */
    public int getNatom() {
        return natom;
    }

    /**
     * To get the number of bonds
     * @return the nbond
     */
    public int getNbond() {
        return nbond;
    }

    /**
     * To get the number of angles
     * @return the ntheta
     */
    public int getNtheta() {
        return ntheta;
    }

    /**
     * To get the number of dihedrals
     * @return the nphi
     */
    public int getNphi() {
        return nphi;
    }

    /**
     * To get the number of impropers
     * @return the nimphi
     */
    public int getNimphi() {
        return nimphi;
    }

    /**
     * To get the all atoms list
     * @return the atomList
     */
    public List<Atom> getAtomList() {
        return atomList;
    }

    /**
     * To get the all bonds list
     * @return the bondList
     */
    public List<Bond> getBondList() {
        return bondList;
    }

    /**
     * To get the all angles list
     * @return the angleList
     */
    public List<Angle> getAngleList() {
        return angleList;
    }

    /**
     * To get the all dihedral list
     * @return the diheList
     */
    public List<Dihedral> getDiheList() {
        return diheList;
    }

    /**
     * To get the all impropers list
     * @return the imprList
     */
    public List<Improper> getImprList() {
        return imprList;
    }

} // end of class PSF
