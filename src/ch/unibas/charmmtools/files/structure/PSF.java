/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
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

    // the lists read from PSF
    protected List<Atom> atomList = null;
    protected List<Bond> bondList = null;
    protected List<Angle> angleList = null;
    protected List<Dihedral> diheList = null;
    protected List<Improper> imprList = null;
    
    /**
     * @return the myname
     */
    public String getMyname() {
        return myname;
    }

    /**
     * @return the isExtendedFormat
     */
    public boolean isIsExtendedFormat() {
        return isExtendedFormat;
    }

    /**
     * @return the isUsingCMAP
     */
    public boolean isIsUsingCMAP() {
        return isUsingCMAP;
    }

    /**
     * @return the isUsingCHEQ
     */
    public boolean isIsUsingCHEQ() {
        return isUsingCHEQ;
    }

    /**
     * @return the isUsingDRUDE
     */
    public boolean isIsUsingDRUDE() {
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
     * @return the nbond
     */
    public int getNbond() {
        return nbond;
    }

    /**
     * @return the ntheta
     */
    public int getNtheta() {
        return ntheta;
    }

    /**
     * @return the nphi
     */
    public int getNphi() {
        return nphi;
    }

    /**
     * @return the nimphi
     */
    public int getNimphi() {
        return nimphi;
    }

    /**
     * @return the atomList
     */
    public List<Atom> getAtomList() {
        return atomList;
    }

    /**
     * @return the bondList
     */
    public List<Bond> getBondList() {
        return bondList;
    }

    /**
     * @return the angleList
     */
    public List<Angle> getAngleList() {
        return angleList;
    }

    /**
     * @return the diheList
     */
    public List<Dihedral> getDiheList() {
        return diheList;
    }

    /**
     * @return the imprList
     */
    public List<Improper> getImprList() {
        return imprList;
    }
    
    /**
     * @param args the command line arguments
     */
//    public static void main(String[] args) {
//
//        try {
//            // initialise logger
//            BasicConfigurator.configure();
//
//            //path to xyz file and csv file
//            String xyzName = args[0];
//            String csvName = args[1];
//
//            //reads a coordinates file (xyz format only)
//            XyzFile xyzf = XyzFileParser.parse(new File(xyzName));
//
//            //generates a topology file
//            RTF rtff = new RTF_generate(xyzf, csvName);
//            
//            //then a PSF file re-using data from PSF 
//            PSF psff = new PSF_generate(rtff);
//            
//        } catch (IOException ex) {
//            Logger.getLogger(RTF_generate.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//    }

} // end of class PSF
