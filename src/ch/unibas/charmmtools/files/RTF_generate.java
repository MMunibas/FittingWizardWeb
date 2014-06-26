/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */

package ch.unibas.charmmtools.files;

import ch.unibas.charmmtools.types.Atom;
import ch.unibas.charmmtools.types.Bond;
import ch.unibas.fittingwizard.application.xyz.XyzAtom;
import ch.unibas.fittingwizard.application.xyz.XyzFile;
import ch.unibas.fittingwizard.application.xyz.XyzFileParser;
import java.io.File;
import java.util.Iterator;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 *
 * @author hedin
 */
public final class RTF_generate extends RTF {

    public static void main(String[] args) {

        // initialise logger
        BasicConfigurator.configure();

//        String fname = "/home/hedin/progra/workflowopt/data/testdata/molecules/ethane.xyz";
//        String fname = "/home/hedin/progra/workflowopt/3G61.xyz";
        String fname = args[0];
        XyzFile xyzf = XyzFileParser.parse(new File(fname));

        RTF rtff = new RTF_generate(xyzf);

//        List<Atom> atmlist = rtff.getAtmTypeList();
//
//        System.out.println("Atoms list");
//        for (Atom at : atmlist) {
//            String name = at.getAtomName();
//            String hybr = at.getHybridisation();
//            System.out.println(at.getAtomID() + " " + name + " " + hybr + " " + at.getX() + " " + at.getY() + " " + at.getZ());
//        }
//
//        List<Bond> bndlist = rtff.getBndTypeList();
//        System.out.println("Bonds list :");
//        for (Bond bd : bndlist) {
//            Atom a1 = bd.getA1();
//            Atom a2 = bd.getA2();
//            System.out.println("Bond between atoms " + a1.getAtomID() + ":" + a1.getAtomName() + " and " + a2.getAtomID() + ":" + a2.getAtomName() + " of length " + bd.getLength());
//        }
    }

    public RTF_generate(XyzFile xyz) {

        InputDataAtoms = xyz.getAtoms();
        Iterator<XyzAtom> iterator = null;
        iterator = (Iterator<XyzAtom>) InputDataAtoms.iterator();

        while (iterator.hasNext()) {
            XyzAtom it = iterator.next();
            atmTypeList.add(new Atom(it.getIndex(), it.getName(),
                    it.getX(), it.getY(), it.getZ()));
        }

        this.natom = xyz.getAtomCount();

        this.generate();
    }//ctor

    private void generate() {
        this.gen_bonds();
        this.gen_hybridisation();
    }

    private void gen_bonds() {
        double dist;

        for (int i = 0; i < natom; i++) {
            for (int j = i + 1; j < natom; j++) {
                if (j == i) {
                    continue;
                }
                dist = Bond.calcLength(atmTypeList.get(i), atmTypeList.get(j));
                if (dist < covRadList.get(atmTypeList.get(i).getAtomName())
                        + covRadList.get(atmTypeList.get(j).getAtomName())) {
                    this.nbonds++;
                    bndTypeList.add(new Bond(atmTypeList.get(i), atmTypeList.get(j), dist));
                    atmTypeList.get(i).addBondTo(j);
                    atmTypeList.get(j).addBondTo(i);
                    /*TODO*/
                    ;
                }//end if
            }//for j
        }//for i

    }//end of gen_bonds

    /**
     * To determine the hybridisation state of C , N , O atoms
     */
    private void gen_hybridisation() {

        /*
         * C and O atoms have to be treated first because we need this information for
         * treating N atoms afterwards.
         */
        for (Atom it : atmTypeList) {
            switch (it.getAtomName()) {
                case "C":
                    it.setHybridisation(C_hybridList.get(it.getNumberOfBonds()));
                    break;
                case "O":
                    it.setHybridisation(O_hybridList.get(it.getNumberOfBonds()));
                    break;
//                case "N":
//                    if (it.getNumberOfBonds() == 3) {
//                        if () {
//
//                        } else {
//                            it.setHybridisation(N_hybridList.get(it.getNumberOfBonds()));
//                        }
//                    }
//                    break;
                default:
                    break;
            }//end of switch
        }//end of for

        /*
         * Then we treat the N atoms
         */
        for (Atom it : atmTypeList) {
            if (it.getAtomName().equals("N") && it.getNumberOfBonds() == 3) {
                if (atmTypeList.get(it.getBondAt(0)).getHybridisation().equals("sp2")
                        || atmTypeList.get(it.getBondAt(1)).getHybridisation().equals("sp2")
                        || atmTypeList.get(it.getBondAt(2)).getHybridisation().equals("sp2")) {
                    it.setHybridisation("sp2");
                } else {
                    it.setHybridisation("sp3");
                }
            } // if it is N
        } //for atomlist

    }// end of gen_hybridation


}//end of class
