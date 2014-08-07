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
import ch.unibas.charmmtools.types.Improper;
import ch.unibas.fittingwizard.application.xyz.XyzAtom;
import ch.unibas.fittingwizard.application.xyz.XyzFile;
import ch.unibas.fittingwizard.application.xyz.XyzFileParser;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.BasicConfigurator;

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

        System.out.println("Atoms list :");
        for (Atom at : atmlist) {
            String name = at.getAtomName();
            String hybr = at.getHybridisation();
            System.out.println(at.getAtomID() + " " + name + " " + hybr + " " + at.getX() + " " + at.getY() + " " + at.getZ());
            System.out.print("Connectivity dump: ");
            HashMap<String, Integer> map = at.getConnectivity();
            System.out.println(map.toString());
        }

        List<Bond> bndlist = rtff.getBndTypeList();
        System.out.println("Bonds list :");
        for (Bond bd : bndlist) {
            Atom a1 = bd.getA1();
            Atom a2 = bd.getA2();
            System.out.println("Bond between atoms " + a1.getAtomID() + ":" + a1.getAtomName() + " and " + a2.getAtomID() + ":" + a2.getAtomName() + " of length " + bd.getLength());
        }

        List<Improper> implist = rtff.getImprTypeList();
        System.out.println("Impropers list :");
        for (Improper imp : implist) {
            Atom a1 = imp.getA1();
            Atom a2 = imp.getA2();
            Atom a3 = imp.getA3();
            Atom a4 = imp.getA4();
            System.out.println("Improper between atoms " + a1.getAtomID() + ":" + a1.getAtomName()
                    + " and " + a2.getAtomID() + ":" + a2.getAtomName()
                    + " and " + a3.getAtomID() + ":" + a3.getAtomName()
                    + " and " + a4.getAtomID() + ":" + a4.getAtomName()
                    + " of value " + imp.getDihe());
        }

        System.out.println("RTF types :");
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
        // 3 calls to gen_type are required
        for (int i = 0; i < 3; i++) {
            this.gen_type();
            this.find_impropers();
        }
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

        int connect = 0;

        //loop on all atoms to get type of C atoms
        for (Atom at : this.atmTypeList) {
            if (at.getAtomName().equals("C")) {
                String hybr = at.getHybridisation();
                List<Integer> lst = at.getLinkingList();
                switch (hybr) {
                    case "sp3":
                        connect = at.getConnectivity().getOrDefault("H", 0);//$bound{"H"}[$i]
                        if (connect == 3 || connect == 4) {
                            at.setRtfType("CT3");
                        } else if (connect == 2) {
                            at.setRtfType("CT2");
                        } else {
                            at.setRtfType("CT1");
                        }
                        break;
                    case "sp2":
                        connect = at.getConnectivity().getOrDefault("C", 0);//$bound{"C"}[$i]
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
                                if (at.getConnectivity().getOrDefault("O", 0) == 1
                                        || at.getConnectivity().getOrDefault("N", 0) == 1) {
                                    at.setRtfType("C");
                                }
                            } else if (at.getConnectivity().getOrDefault("H", 0) == 1) {
                                at.setRtfType("CE1");
                            } else if (at.getConnectivity().getOrDefault("H", 0) == 1
                                    || at.getConnectivity().getOrDefault("N", 0) == 1) {
                                at.setRtfType("C");
                            }
                            int idx1 = at.getLinkingList().get(0);
                            int idx2 = at.getLinkingList().get(1);
                            int idx3 = at.getLinkingList().get(2);
                            if (at.getRtfType().equals("CA")) {
                                String s1 = atmTypeList.get(idx1).getRtfType();
                                String s2 = atmTypeList.get(idx2).getRtfType();
                                String s3 = atmTypeList.get(idx3).getRtfType();
//                                System.out.println(idx1 + " " + idx2 + " " + idx3);
//                                System.out.println(s1 + " " + s2 + " " + s3);
//                                System.out.flush();
                                if (s1.contains("NR") || s2.contains("NR") || s3.contains("NR")) {
                                    at.setRtfType("CPH1");
                                }
                            }
                        }//connect==2
                        else if (connect == 1) {
                            if (at.getConnectivity().getOrDefault("N", 0) == 1
                                    && at.getConnectivity().getOrDefault("O", 0) == 1) {
                                at.setRtfType("C");
                            } else if (at.getConnectivity().getOrDefault("O", 0) == 2) {
                                at.setRtfType("CC");
                            } else if (at.getConnectivity().getOrDefault("N", 0) == 1
                                    && at.getConnectivity().getOrDefault("H", 0) == 1) {
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
                            } else if (at.getConnectivity().getOrDefault("H", 0) == 2) {
                                at.setRtfType("CE2");
                            }
                        }//connect==1
                        else if (at.getConnectivity().getOrDefault("N", 0) == 3) {
                            at.setRtfType("C");
                        } else if (at.getConnectivity().getOrDefault("N", 0) == 2
                                || at.getConnectivity().getOrDefault("O", 0) == 1) {
                            at.setRtfType("C");
                        } else if (at.getConnectivity().getOrDefault("N", 0) == 2
                                || at.getConnectivity().getOrDefault("H", 0) == 1) {
                            at.setRtfType("CPH2");
                        }
                        break;
                    default:
                        break;
                }//switch on hybridisation
            }//loop on C atoms
        }//loop on all atoms

        //loop on all atoms for O
        for (Atom at : this.atmTypeList) {
            if (at.getAtomName().equals("O")) {
                if (at.getNumberOfBonds() == 1) {
                    int idx1 = at.getLinkingList().get(0);//$link[$i][1]
                    int iidx1 = atmTypeList.get(idx1).getLinkingList().get(0);//$link[$link[$i][1]][1]
                    int iidx2 = atmTypeList.get(idx1).getLinkingList().get(1);//$link[$link[$i][1]][2]
                    int iidx3 = atmTypeList.get(idx1).getLinkingList().get(2);//$link[$link[$i][1]][3]
                    if (atmTypeList.get(idx1).getRtfType().equals("C")) {
                        at.setRtfType("O");
                    } else if (atmTypeList.get(iidx1).getRtfType().equals("OH1")
                            || atmTypeList.get(iidx2).getRtfType().equals("OH1")
                            || atmTypeList.get(iidx3).getRtfType().equals("OH1")) {
                        at.setRtfType("OB");
                    } else if (atmTypeList.get(iidx1).getRtfType().equals("OS")
                            || atmTypeList.get(iidx2).getRtfType().equals("OS")
                            || atmTypeList.get(iidx3).getRtfType().equals("OS")) {
                        at.setRtfType("OB");
                    } else if (atmTypeList.get(idx1).getRtfType().equals("CC")) {
                        at.setRtfType("OC");
                    }
                }//end getNumberOfBonds() == 1
                else if (at.getNumberOfBonds() == 2) {
                    int idx1 = at.getLinkingList().get(0);//$link[$i][1]
                    int idx2 = at.getLinkingList().get(1);//$link[$i][2]
                    connect = at.getConnectivity().getOrDefault("H", 0);//$bound{"H"}[$i]
                    if (connect == 2) {
                        at.setRtfType("OT");
                    } else if (connect == 1) {
                        if (atmTypeList.get(idx1).getRtfType().equals("CC")
                                || atmTypeList.get(idx2).getRtfType().equals("CC")) {
                            at.setRtfType("OH1");
                        } else if (!atmTypeList.get(idx1).getRtfType().equals("CC") || !atmTypeList.get(idx2).getRtfType().equals("CC")) {
                            at.setRtfType("OH1");
                        }
                    } else if (at.getConnectivity().getOrDefault("C", 0) == 2) {
                        if (atmTypeList.get(idx1).getRtfType().equals("CC")
                                || atmTypeList.get(idx2).getRtfType().equals("CC")) {
                            at.setRtfType("OS");
                        }
                    }
                }//end getNumberOfBonds() == 2

            }//loop on O atoms
        }//loop on all atoms

        //loop on all atoms for N
        for (Atom at : this.atmTypeList) {
            if (at.getAtomName().equals("N")) {
                String hybr = at.getHybridisation();
                switch (hybr) {
                    case "sp3":

                        connect = at.getConnectivity().getOrDefault("H", 0);//$bound{"H"}[$i]
                        if (connect == 3) {
                            at.setRtfType("NH3");
                        } else if (connect == 2 || connect == 1) {
                            at.setRtfType("NP");
                        } else {
                            connect = at.getConnectivity().getOrDefault("C", 0);
                            if (connect == 3) {
                                at.setRtfType("NP");
                            }
                        }

                        break;
                    case "sp2":

                        int idx1 = at.getLinkingList().get(0);//$link[$i][1]
                        int idx2 = at.getLinkingList().get(1);//$link[$i][2]
                        int idx3 = at.getLinkingList().get(2);//$link[$i][3]

                        if (atmTypeList.get(idx1).getRtfType().equals("CA") || atmTypeList.get(idx2).getRtfType().equals("CA")
                                || atmTypeList.get(idx3).getRtfType().equals("CA")) {
                            //aniline
                            at.setRtfType("NH2");
                        } else if ((atmTypeList.get(idx1).getRtfType().equals("C") || atmTypeList.get(idx2).getRtfType().equals("C") || atmTypeList.get(idx3).getRtfType().equals("C"))
                                && (at.getConnectivity().getOrDefault("H", 0) == 2)) {
                            //secondary amide
                            at.setRtfType("NH2");
                        } else if ((atmTypeList.get(idx1).getRtfType().equals("C") || atmTypeList.get(idx2).getRtfType().equals("C") || atmTypeList.get(idx3).getRtfType().equals("C"))
                                && (at.getConnectivity().getOrDefault("H", 0) == 1)) {
                            //primary amide
                            at.setRtfType("NH1");
                        } else if ((atmTypeList.get(idx1).getRtfType().equals("C") || atmTypeList.get(idx2).getRtfType().equals("C") || atmTypeList.get(idx3).getRtfType().equals("C"))
                                && (at.getConnectivity().getOrDefault("H", 0) == 0)) {
                            //proline type N peptide
                            at.setRtfType("N");
                        }

                        if ((at.getConnectivity().getOrDefault("C", 0) == 2) && (at.getConnectivity().getOrDefault("H", 0) == 0)) {
                            if (atmTypeList.get(idx1).getRtfType().contains("CPH") || atmTypeList.get(idx2).getRtfType().contains("CPH")) {
                                at.setRtfType("NR2");
                            }
                        }

                        if ((at.getConnectivity().getOrDefault("C", 0) == 2) && (at.getConnectivity().getOrDefault("H", 0) == 1)) {
                            if (atmTypeList.get(idx1).getRtfType().contains("CPH") || atmTypeList.get(idx2).getRtfType().contains("CPH") || atmTypeList.get(idx3).getRtfType().contains("CPH")) {
                                at.setRtfType("NR1");
                            }
                        }

                        break;
                    default:
                        break;
                }
            }//loop on N atoms
        }//loop on all atoms

        //loop on all atoms for S
        for (Atom at : this.atmTypeList) {
            if (at.getAtomName().equals("S")) {

                if (at.getConnectivity().getOrDefault("C", 0) == 2) {
                    at.setRtfType("S");
                }

                if ((at.getConnectivity().getOrDefault("C", 0) == 1) && (at.getConnectivity().getOrDefault("H", 0) == 1)) {
                    at.setRtfType("S");
                }

                if ((at.getConnectivity().getOrDefault("C", 0) == 1) && (at.getConnectivity().getOrDefault("S", 0) == 1)) {
                    at.setRtfType("SM");
                }

            }//loop on S atoms
        }//loop on all atoms

        // TODO : work on H atoms
        for (Atom at : this.atmTypeList) {
            if (at.getAtomName().equals("H")) {
                int idx1 = at.getLinkingList().get(0);//$link[$i][1]
                if (at.getConnectivity().getOrDefault("N", 0) == 1) {
                    at.setRtfType("H");
                } else if (at.getConnectivity().getOrDefault("O", 0) == 1) {
                    if (atmTypeList.get(idx1).getRtfType().equals("OH1")) {
                        at.setRtfType("H");
                    }
                    if (atmTypeList.get(idx1).getRtfType().equals("OT")) {
                        at.setRtfType("HT");
                    }
                } else if (at.getConnectivity().getOrDefault("S", 0) == 1) {
                    at.setRtfType("HS");
                } else if (at.getConnectivity().getOrDefault("C", 0) == 1) {
                    if (atmTypeList.get(idx1).getHybridisation().equals("sp3")) {
                        at.setRtfType("HA");
                    } else {
                        switch (atmTypeList.get(idx1).getRtfType()) {
                            case "CE1":
                                at.setRtfType("HA1");
                                break;
                            case "CE2":
                                at.setRtfType("HA2");
                                break;
                            case "CA":
                                at.setRtfType("HP");
                                break;
                            case "CPH1":
                                at.setRtfType("HR3");
                                break;
                            case "CPH2":
                                at.setRtfType("HR1");
                                break;
                        }//end switch
                    }
                }
            }//loop on H atoms
        }//loop on all atoms
    
    }//end gen_type()

    /**
     * this generates the list of improper dihedrals
     */
    private void find_impropers() {
        for (Atom at : atmTypeList) {
            if ((at.getAtomName().equals("C") || at.getAtomName().equals("N"))
                    && (at.getHybridisation().equals("sp2")) && (!at.getRtfType().equals("NR2"))) {
                nimpr++;
                int a2, a3, a4;
                a2 = at.getLinkingList().get(0);
                a3 = at.getLinkingList().get(1);
                a4 = at.getLinkingList().get(2);
                imprTypeList.add(new Improper(at,
                        atmTypeList.get(a2),
                        atmTypeList.get(a3),
                        atmTypeList.get(a4)));
            }
        }//loop on all atoms
    }//end find_impropers()

}//end of class
