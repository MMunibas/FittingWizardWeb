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
import ch.unibas.charmmtools.types.Atom;
import ch.unibas.charmmtools.types.Bond;
import ch.unibas.fittingwizard.application.xyz.XyzAtom;
import ch.unibas.fittingwizard.application.xyz.XyzFile;
import ch.unibas.fittingwizard.application.xyz.XyzFileParser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
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

        String fname = args[0];
        XyzFile xyzf = XyzFileParser.parse(new File(fname));

        RTF rtff  = new RTF_generate(xyzf, "cov_rad.csv");

        List<Atom> atmlist = rtff.getAtmTypeList();

//        System.out.println("Atoms list");
//        for (Atom at : atmlist) {
//            String name = at.getAtomName();
//            String hybr = at.getHybridisation();
//            System.out.println(at.getAtomID() + " " + name + " " + hybr + " " + at.getX() + " " + at.getY() + " " + at.getZ());
//            System.out.print("Connectivity dump: ");
//            HashMap<String, Integer> map = at.getConnectivity();
//            System.out.println(map.toString());
//        }
//
//        List<Bond> bndlist = rtff.getBndTypeList();
//        System.out.println("Bonds list :");
//        for (Bond bd : bndlist) {
//            Atom a1 = bd.getA1();
//            Atom a2 = bd.getA2();
//            System.out.println("Bond between atoms " + a1.getAtomID() + ":" + a1.getAtomName() + " and " + a2.getAtomID() + ":" + a2.getAtomName() + " of length " + bd.getLength());
//        }
        System.out.println("RTF types");
        for (Atom at : atmlist) {
            String name = at.getAtomName();
            String hybr = at.getHybridisation();
            String type = at.getRtfType();
            System.out.println(at.getAtomID() + ":" + name + " has bybridisation " + hybr + " and has rtftype  : " + type);
        }

    }

    public RTF_generate(XyzFile xyz){

        super();

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

    public RTF_generate(XyzFile xyz, String csv){

        super(csv);

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

    private void generate(){
        this.gen_bonds();
        this.gen_hybridisation();
        this.gen_type();
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
                    atmTypeList.get(i).addConnectivity(atmTypeList.get(j).getAtomName());
                    atmTypeList.get(j).addConnectivity(atmTypeList.get(i).getAtomName());
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
                default:
                    /* TODO */
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

    /**
     * depending on hybridisation of a given atom find the type used for forcefield, i.e. CT2, CT3, CA, ...
     * it is called 3 times consecutively
     */
    private void gen_type(){

        int connect = -1;

        //first loop on all atoms to get type of C atoms
        for (Atom at : this.atmTypeList) {
            if (at.getAtomName().equals("C")) {
                String hybr = at.getHybridisation();
                List<Integer> lst = at.getLinkingList();
                switch (hybr) {
                    case "sp3":
                        connect = at.getConnectivity().getOrDefault("H", -1);
                        if (connect == 3 || connect == 4) {
                            at.setRtfType("CT3");
                        } else if (connect == 2) {
                            at.setRtfType("CT2");
                        } else {
                            at.setRtfType("CT1");
                        }
                        break;
                    case "sp2":
                        connect = at.getConnectivity().getOrDefault("C", -1);
                        if (lst.size() > 3) {
                            throw new IndexOutOfBoundsException("LinkingList for a C SP2 atom has too much elements (more than 3)!");
                        }
                        String h1 = atmTypeList.get(lst.get(0)).getHybridisation();
                        String h2 = atmTypeList.get(lst.get(1)).getHybridisation();
                        String h3 = atmTypeList.get(lst.get(2)).getHybridisation();
                        if (connect == 3) {
                            if (h1.equals("sp2") && h2.equals("sp2") && h3.equals("sp2")) {
                                at.setRtfType("CA");
                            } else if ((h1.equals("sp2") && h2.equals("sp2"))
                                    || (h1.equals("sp2") && h3.equals("sp2"))
                                    || (h2.equals("sp2") && h3.equals("sp2"))) {
                                at.setRtfType("CA");
                            } else if ((h1.equals("sp3") && h2.equals("sp3"))
                                    || (h1.equals("sp3") && h3.equals("sp3"))
                                    || (h2.equals("sp3") && h3.equals("sp3"))) {
                                at.setRtfType("CE1");
                            }
                        }//connect==3
                        else if (connect == 2) {
                            if ((h1.equals("sp2") && h2.equals("sp2"))
                                    || (h1.equals("sp2") && h3.equals("sp2"))
                                    || (h2.equals("sp2") && h3.equals("sp2"))) {
                                at.setRtfType("CA");
                            } else if ((h1.equals("sp3") && h2.equals("sp3"))
                                    || (h1.equals("sp3") && h3.equals("sp3"))
                                    || (h2.equals("sp3") && h3.equals("sp3"))) {
                                if (at.getConnectivity().getOrDefault("O", -1) == 1
                                        || at.getConnectivity().getOrDefault("N", -1) == 1) {
                                    at.setRtfType("C");
                                }
                            } else if (at.getConnectivity().getOrDefault("H", -1) == 1) {
                                at.setRtfType("CE1");
                            } else if (at.getConnectivity().getOrDefault("H", -1) == 1
                                    || at.getConnectivity().getOrDefault("N", -1) == 1) {
                                at.setRtfType("C");
                            }
                            int idx1 = at.getLinkingList().get(0);
                            int idx2 = at.getLinkingList().get(1);
                            int idx3 = at.getLinkingList().get(2);
                            if (at.getRtfType().equals("CA")
                                    && (atmTypeList.get(idx1).getRtfType().substring(0, 2).equals("NR")
                                    || atmTypeList.get(idx2).getRtfType().substring(0, 2).equals("NR")
                                    || atmTypeList.get(idx3).getRtfType().substring(0, 2).equals("NR"))) {
                                at.setRtfType("CPH1");
                            }
                        }//connect==2
                        else if (connect == 1) {
                            if (at.getConnectivity().getOrDefault("N", -1) == 1
                                    && at.getConnectivity().getOrDefault("O", -1) == 1) {
                                at.setRtfType("C");
                            } else if (at.getConnectivity().getOrDefault("O", -1) == 2) {
                                at.setRtfType("CC");
                            } else if (at.getConnectivity().getOrDefault("N", -1) == 1
                                    && at.getConnectivity().getOrDefault("H", -1) == 1) {
                                int idx1 = at.getLinkingList().get(0);
                                int idx2 = at.getLinkingList().get(1);
                                int idx3 = at.getLinkingList().get(2);
                                if (atmTypeList.get(idx1).getRtfType().equals("CA")
                                        || atmTypeList.get(idx2).getRtfType().equals("CA")
                                        || atmTypeList.get(idx3).getRtfType().equals("CA")) {
                                    at.setRtfType("CPH2");
                                } else {
                                    at.setRtfType("CA");
                                }
                            } else if (at.getConnectivity().getOrDefault("H", -1) == 2) {
                                at.setRtfType("CE2");
                            }
                        }//connect==1
                        else if (at.getConnectivity().getOrDefault("N", -1) == 3) {
                            at.setRtfType("C");
                        } else if (at.getConnectivity().getOrDefault("N", -1) == 2
                                || at.getConnectivity().getOrDefault("O", -1) == 1) {
                            at.setRtfType("C");
                        } else if (at.getConnectivity().getOrDefault("N", -1) == 2
                                || at.getConnectivity().getOrDefault("H", -1) == 1) {
                            at.setRtfType("CPH2");
                        }
                        break;
                    default:
                        break;
                }//switch on hybridisation
            }//loop on C atoms
        }//first loop on all atoms

        //second loop on all atoms for O
        for (Atom at : this.atmTypeList) {
            if (at.getAtomName().equals("O")) {
                String hybr = at.getHybridisation();
                List<Integer> lst = at.getLinkingList();
                /* TODO */
            }//loop on O atoms
        }//second loop on all atoms

    }//end gen_type()



}//end of class
