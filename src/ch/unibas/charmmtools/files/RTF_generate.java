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
import ch.unibas.fittingwizard.application.xyz.XyzAtom;
import ch.unibas.fittingwizard.application.xyz.XyzFile;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 *
 * @author hedin
 */
public final class RTF_generate extends RTF {

    private List<XyzAtom> atoms = null;

    private int nbonds = 0;

    //covalence radii
//    private final double C = 0.8;
//    private final double H = 0.4;
//    private final double O = 0.8;
//    private final double N = 0.8;
//    private final double S = 1.2;
//    private final double P = 1.2;
//    private final double F = 1.3;
    private Hashtable<String, Double> covRad = new Hashtable<String, Double>() {
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

    private List<Atom> atmList = new ArrayList<>();
    private List<Bond> bndList = new ArrayList<>();
    private List<Angle> angList = new ArrayList<>();
    private List<Dihedral> diheList = new ArrayList<>();
    private List<Improper> imprList = new ArrayList<>();

    public RTF_generate(XyzFile xyz) {
        atoms = xyz.getAtoms();
        this.natom = xyz.getAtomCount();
//        this.allocate();
//        this.copy_data();
        for (XyzAtom it : atoms) {
            atmList.add(new Atom(it.getIndex(), it.getName(),
                    it.getX(), it.getY(), it.getZ()));
        }
        this.generate();
    }

//    @Override
//    protected void allocate() {
//        x = new ArrayList<>();
//        y = new ArrayList<>();
//        z = new ArrayList<>();
//        id = new ArrayList<>();
//        name = new ArrayList<>();
//        bonds = new ArrayList<>();
//    }

//    protected void copy_data() {
//        for (XyzAtom it : atoms) {
//            x.add(it.getX());
//            y.add(it.getY());
//            z.add(it.getZ());
//            id.add(it.getIndex());
//            name.add(it.getName());
//        }
//    }

    public void generate() {
//        this.gen_atomlist();
        this.gen_bonds();
    }

//    private void gen_atomlist() {
//
//    }

    private void gen_bonds() {
        double dist;

        for (int i = 0; i < natom; i++) {
            for (int j = i + 1; j < natom; j++) {
                if (j == i) {
                    continue;
                }
                dist = Bond.calcLength(atmList.get(i), atmList.get(j));
                if (dist < covRad.get(atmList.get(i).getAtomName())
                        + covRad.get(atmList.get(j).getAtomName())) {
                    this.nbonds++;
                    bndList.add(new Bond(atmList.get(i), atmList.get(j), dist));
                    atmList.get(i).setNumberOfBonds(atmList.get(i).getNumberOfBonds() + 1);
                    atmList.get(j).setNumberOfBonds(atmList.get(j).getNumberOfBonds() + 1);
                    /*TODO*/
                }//end if
            }//for j
        }//for i

    }//end of gen_bonds


}//end of class
