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
import java.util.Iterator;

/**
 *
 * @author hedin
 */
public final class RTF_generate extends RTF {


    public RTF_generate(XyzFile xyz) {

        InputDataAtoms = xyz.getAtoms();
        Iterator<?> iterator = InputDataAtoms.iterator();
        while (iterator.hasNext()) {
            XyzAtom it = (XyzAtom) iterator.next();
            atmList.add(new Atom(it.getIndex(), it.getName(),
                    it.getX(), it.getY(), it.getZ()));
        }

        this.natom = xyz.getAtomCount();

        this.generate();
    }

    private void generate() {
        this.gen_bonds();
    }

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
