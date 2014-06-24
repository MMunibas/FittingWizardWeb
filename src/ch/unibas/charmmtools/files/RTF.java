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
import java.util.Hashtable;
import java.util.List;

/**
 *
 * @author hedin
 */
public abstract class RTF {

    protected List<?> InputDataAtoms = null;

    protected int natom;
    protected int nbonds = 0;

    protected Hashtable<String, Double> covRad = new Hashtable<String, Double>() {
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

    protected List<Atom> atmList = new ArrayList<>();
    protected List<Bond> bndList = new ArrayList<>();
    protected List<Angle> angList = new ArrayList<>();
    protected List<Dihedral> diheList = new ArrayList<>();
    protected List<Improper> imprList = new ArrayList<>();
}
