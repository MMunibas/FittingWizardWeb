/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.test;

import ch.unibas.charmmtools.files.input.NBONDS;
import ch.unibas.charmmtools.files.input.NBONDS.add_elec;
import ch.unibas.charmmtools.files.input.NBONDS.add_elec_opt;
import ch.unibas.charmmtools.files.input.NBONDS.add_ewald;
import ch.unibas.charmmtools.files.input.NBONDS.add_vdw;
import ch.unibas.charmmtools.files.input.NBONDS.cut_type;
import ch.unibas.charmmtools.files.input.NBONDS.nbonds_type;
import ch.unibas.charmmtools.files.input.NBONDS.nbxmod_type;

/**
 *
 * @author hedin
 */
public class CHARMM_Input_Test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

        nbonds_type nbtype = nbonds_type.ATOM;
        add_elec electype = add_elec.ELEC;
        add_vdw vdwtype = add_vdw.VDW;
        add_ewald ewaldtype = add_ewald.NOEWald;
        add_elec_opt elecopt = add_elec_opt.CDIElec;
        cut_type cuttype = cut_type.SHIFted;
        nbxmod_type nbxmod = nbxmod_type.PRESERVE;

        NBONDS nbstring = new NBONDS(nbtype, electype, vdwtype, ewaldtype, elecopt, cuttype, nbxmod);

        System.out.println(nbstring.getNB_params());
    }

}
