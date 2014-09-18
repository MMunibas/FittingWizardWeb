/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */

package ch.unibas.charmmtools.files;

import au.com.bytecode.opencsv.CSVReader;
import ch.unibas.charmmtools.structures.Angle;
import ch.unibas.charmmtools.structures.Atom;
import ch.unibas.charmmtools.structures.Bond;
import ch.unibas.charmmtools.structures.Dihedral;
import ch.unibas.charmmtools.structures.Improper;
import ch.unibas.charmmtools.structures.InternalCoordinates;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hedin
 */
public abstract class RTF {

    protected String fname = null;

    protected List<?> InputDataAtoms = null;

    protected int natom = 0;
    protected int nbonds = 0;
    protected int nimpr = 0;

    protected HashMap<String, Double> covRad = null;
    protected HashMap<String, Double> atomicWeight = null;

    protected HashMap<Integer, String> C_hybridList = new HashMap<Integer, String>() {
        {
            put(4, "sp3");
            put(3, "sp2");
            put(2, "sp");
        }
    };

    protected HashMap<Integer, String> O_hybridList = new HashMap<Integer, String>() {
        {
            put(2, "sp3");
            put(1, "sp2");
        }
    };

    protected HashMap<Integer, String> N_hybridList = new HashMap<Integer, String>() {
        {
            put(4, "sp3");
            put(3, "sp3");
            put(2, "sp2");
            put(1, "sp");
        }
    };

    protected List<Atom> atmTypeList = new ArrayList<>();
    protected List<Bond> bndTypeList = new ArrayList<>();
    protected List<Angle> angTypeList = new ArrayList<>();
    protected List<Dihedral> diheTypeList = new ArrayList<>();
    protected List<Improper> imprTypeList = new ArrayList<>();
    protected List<InternalCoordinates> IC_List = new ArrayList<>();

    public RTF() {
        this.covRad = new HashMap<String, Double>() {
            {
                put("C", 0.8);
                put("H", 0.4);
                put("O", 0.8);
                put("N", 0.8);
                put("S", 1.2);
                put("P", 1.2);
                put("F", 1.3);
            }
        };
        this.atomicWeight = new HashMap<String, Double>() {
            {
                put("C", 12.011);
                put("H", 1.008);
                put("O", 15.999);
                put("N", 14.007);
                put("S", 32.060);
                put("P", 30.974);
                put("F", 18.998);
            }
        };
    }//ctor

    public RTF(String atomicInfo) {
        this.covRad = new HashMap<String, Double>();
        this.atomicWeight = new HashMap<String, Double>();
        readAtomicInfo(atomicInfo);
    }//ctor

    public final void readAtomicInfo(String atInfoFileName) {

        CSVReader csv = null;
        String[] dat = null;
        try {

            csv = new CSVReader(new FileReader(atInfoFileName));
            //ignore first line
            dat = csv.readNext();

            //iterate over csv file to get atomic data
            while ((dat = csv.readNext()) != null) {
                this.covRad.put(dat[1], Double.parseDouble(dat[2]));
                this.atomicWeight.put(dat[1], Double.parseDouble(dat[3]));
            }
   
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RTF.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RTF.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//end readAtomicInfo

    /**
     * @return the natom
     */
    public int getNatom() {
        return natom;
    }

    /**
     * @return the nbonds
     */
    public int getNbonds() {
        return nbonds;
    }

    /**
     * @return the nimpr
     */
    public int getNimpr() {
        return nimpr;
    }

    /**
     * @return the atmTypeList
     */
    public final List<Atom> getAtmTypeList() {
        return atmTypeList;
    }

    /**
     * @return the bndTypeList
     */
    public final List<Bond> getBndTypeList() {
        return bndTypeList;
    }

    /**
     * @return the angTypeList
     */
    public List<Angle> getAngTypeList() {
        return angTypeList;
    }

    /**
     * @return the diheTypeList
     */
    public List<Dihedral> getDiheTypeList() {
        return diheTypeList;
    }

    /**
     * @return the imprTypeList
     */
    public List<Improper> getImprTypeList() {
        return imprTypeList;
    }

    /**
     * @return the IC_List
     */
    public List<InternalCoordinates> getIC_List() {
        return IC_List;
    }

    /**
     * @return the fname
     */
    public String getFname() {
        return fname;
    }

    public double findMass(String atname) {
        return atomicWeight.get(atname);
    }

}//end of RTF class
