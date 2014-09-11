/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */

package ch.unibas.charmmtools.files;

import ch.unibas.charmmtools.structures.Atom;
import ch.unibas.charmmtools.structures.Bond;
import ch.unibas.charmmtools.structures.Improper;
import ch.unibas.charmmtools.structures.InternalCoordinates;
import ch.unibas.fittingwizard.application.xyz.XyzAtom;
import ch.unibas.fittingwizard.application.xyz.XyzFile;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author hedin
 */
public final class RTF_generate extends RTF {

    private String fname = null;

    public RTF_generate(XyzFile xyz) throws IOException {

        super();

        this.fname = xyz.getMoleculeName();

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

    public RTF_generate(XyzFile xyz, String csv) throws IOException {

        super(csv);

        this.fname = xyz.getMoleculeName();

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

    private void generate() throws IOException {
        this.gen_bonds();
        this.gen_hybridisation();
        // 3 calls to gen_type are required
        for (int i = 0; i < 3; i++) {
            this.gen_type();
        }
        this.find_impropers();
        this.find_IC();
        this.write_topology_file();
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

                    List<Integer> tmpAt = at.getLinkingList();
                    int idx1 = 0, iidx1 = 0, iidx2 = 0, iidx3 = 0;
                    if (tmpAt.size() >= 1) {
                        idx1 = tmpAt.get(0);//$link[$i][1]
                    }
                    List<Integer> tmpAt2 = atmTypeList.get(idx1).getLinkingList();
                    if (tmpAt2.size() >= 1) {
                        iidx1 = tmpAt2.get(0);//$link[$link[$i][1]][1]
                    }
                    if (tmpAt2.size() >= 2) {
                        iidx2 = tmpAt2.get(1);//$link[$link[$i][1]][2]
                    }
                    if (tmpAt2.size() >= 3) {
                        iidx3 = tmpAt2.get(2);//$link[$link[$i][1]][3]
                    }

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

    private void find_IC() {

        //Calculates IC;
        for (int i = 0; i < atmTypeList.size(); i++) {

            for (int j = 0; j < atmTypeList.get(i).getNumberOfBonds(); j++) {
                int l1 = atmTypeList.get(i).getLinkingList().get(j);

                for (int k = 0; k < atmTypeList.get(l1).getNumberOfBonds(); k++) {
                    int l2 = atmTypeList.get(l1).getLinkingList().get(k);

                    if (l2 != i) {

                        for (int l = 0; l < atmTypeList.get(l2).getNumberOfBonds(); l++) {
                            int l3 = atmTypeList.get(l2).getLinkingList().get(l);

                            if ((l3 != i) && (l3 != l1)) {

                                boolean test = false;
                                for (int m = 0; m < IC_List.size(); m++) {
                                    if ((i == IC_List.get(m).getAt4().getAtomID())
                                            && (l1 == IC_List.get(m).getAt3().getAtomID())
                                            && (l2 == IC_List.get(m).getAt2().getAtomID())
                                            && (l3 == IC_List.get(m).getAt1().getAtomID())) {
                                        test = true;
                                    }
                                }//m loop

                                if (!test) {
                                    IC_List.add(new InternalCoordinates(atmTypeList.get(i),
                                            atmTypeList.get(l1),
                                            atmTypeList.get(l2),
                                            atmTypeList.get(l3),
                                            false));
                                }//end !test

                            }//if ((l3 != i) && (l3 != l1))

                        }//loop l

                    }// end if (l2 != i)

                }//loop k

            }//loop j

        }//loop on all atoms

        // register impropers
        if (this.nimpr != 0) {
        for (int i = 0; i < atmTypeList.size(); i++) {
            IC_List.add(new InternalCoordinates(
                    imprTypeList.get(i).getA2(),
                    imprTypeList.get(i).getA3(),
                    imprTypeList.get(i).getA1(),
                    imprTypeList.get(i).getA4(),
                    true
            ));
            }//loop on all atoms
        }//end this.nimpr != 0

    }//end find_IC()

    private void write_topology_file() throws IOException {
        Date d = new Date();
        BufferedWriter writer = new BufferedWriter(new FileWriter("topol.rtf"));

        writer.write("* ...\n");
        writer.write("* Build RTF for " + this.fname + ".xyz\n");
        writer.write("* generated on " + d.toString() + "\n");
        writer.write("*\n");

        writer.write("   22    0 \n\n");

        writer.write("AUTOGENERATE ANGLES DIHE\n");
        writer.write("DEFA FIRS NONE LAST NONE\n\n");

        writer.write("RESI LIG   0.000\n");
        writer.write("GROUP\n");

        for (Atom at : atmTypeList) {
            writer.write(String.format("ATOM %s %s %6.4f\n",
                    at.getAtomName(), at.getRtfType(), at.getCharge()));
        }

        for (Bond bnd : bndTypeList) {
            writer.write(String.format("BOND %s %s\n",
                    bnd.getA1().getAtomName(), bnd.getA2().getAtomName()
            ));
        }

        for (Improper impr : imprTypeList) {
            writer.write(String.format("IMPH %s %s %s %s\n",
                    impr.getA1().getAtomName(), impr.getA2().getAtomName(),
                    impr.getA3().getAtomName(), impr.getA4().getAtomName()
            ));
        }

        for (InternalCoordinates ic : IC_List) {
            writer.write(String.format("IC %s %s %s%s %s   ",
                    ic.getAt1().getAtomName(), ic.getAt2().getAtomName(), (ic.isImproper() ? "*" : " "),
                    ic.getAt3().getAtomName(), ic.getAt4().getAtomName()
            ));
            writer.write(String.format("%4.2f %7.2f %7.2f %7.2f   %4.2f\n",
                    ic.getBndAB(), ic.getAngABC(), ic.getDiheABCD(), ic.getAngBCD(), ic.getBndCD()
            ));
        }

        writer.write("\nEND\n");

        writer.close();

    }//end write_topology_file()

}//end of class
