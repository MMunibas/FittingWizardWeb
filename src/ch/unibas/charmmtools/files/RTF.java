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

    protected List<?> InputDataAtoms = null;

    protected int natom = 0;
    protected int nbonds = 0;
    protected int nimpr = 0;

    protected HashMap<String, Double> covRadList = null;

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

    public RTF() {
        this.covRadList = new HashMap<String, Double>() {
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
    }//ctor

    public RTF(String csvfile) {
        this.covRadList = new HashMap<String, Double>();
        readCovRad(csvfile);
    }//ctor

    public final void readCovRad(String csvfilename) {

        CSVReader csv = null;
        String[] dat = null;
        try {

            csv = new CSVReader(new FileReader(csvfilename));
            //ignore first line
            dat = csv.readNext();

            //iterate over csv file to get covalence radii
            while ((dat = csv.readNext()) != null) {
                this.covRadList.put(dat[1], Double.parseDouble(dat[2]));
            }
   
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RTF.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RTF.class.getName()).log(Level.SEVERE, null, ex);
        }

            

    }//end readCovRad

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

}
