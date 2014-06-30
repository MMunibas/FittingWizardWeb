/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */

package ch.unibas.charmmtools.files;

import ch.unibas.charmmtools.types.Angle;
import ch.unibas.charmmtools.types.Atom;
import ch.unibas.charmmtools.types.Bond;
import ch.unibas.charmmtools.types.Dihedral;
import ch.unibas.charmmtools.types.Improper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author hedin
 */
public abstract class RTF {

    protected List<?> InputDataAtoms = null;

    protected int natom = 0;
    protected int nbonds = 0;

    protected HashMap<String, Double> covRadList;

    protected HashMap<Integer, String> C_hybridList;
    protected HashMap<Integer, String> O_hybridList;
    protected HashMap<Integer, String> N_hybridList;

    protected List<Atom> atmTypeList = new ArrayList<>();
    protected List<Bond> bndTypeList = new ArrayList<>();
    protected List<Angle> angTypeList = new ArrayList<>();
    protected List<Dihedral> diheTypeList = new ArrayList<>();
    protected List<Improper> imprTypeList = new ArrayList<>();

    public RTF() {
        this.N_hybridList = new HashMap<Integer, String>() {
            {
                put(4, "sp3");
                put(3, "sp3");
                put(2, "sp2");
                put(1, "sp");
            }
        };
        this.O_hybridList = new HashMap<Integer, String>() {
            {
                put(2, "sp3");
                put(1, "sp2");
            }
        };
        this.C_hybridList = new HashMap<Integer, String>() {
            {
                put(4, "sp3");
                put(3, "sp2");
                put(2, "sp");
            }
        };
        this.covRadList = new HashMap<String, Double>() {
            {
//            put("C", 0.8);
//            put("H", 0.4);
//            put("O", 0.8);
//            put("N", 0.8);
//            put("S", 1.2);
//            put("P", 1.2);
//            put("F", 1.3);
                put("C", 0.77);
                put("H", 0.37);
                put("O", 0.73);
                put("N", 0.75);
                put("S", 1.02);
                put("P", 1.06);
                put("F", 0.71);
                put("Se", 1.16);
            }
        };
    }//ctor

    /**
     * @return the atmTypeList
     */
    public List<Atom> getAtmTypeList() {
        return atmTypeList;
    }

    /**
     * @return the bndTypeList
     */
    public List<Bond> getBndTypeList() {
        return bndTypeList;
    }
}
