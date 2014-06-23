/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */

package ch.unibas.charmmtools.files;

import ch.unibas.fittingwizard.application.xyz.XyzAtom;
import ch.unibas.fittingwizard.application.xyz.XyzFile;
import java.util.List;

/**
 * This class derived from the abstract PSF class is used for generating a new psf file useful for CHARMM
 *
 * @author hedin
 */
public final class PSF_generate extends PSF {

    private List<XyzAtom> atoms;

    public PSF_generate(XyzFile xyz) {
        this.atoms = xyz.getAtoms();
        this.natom = xyz.getAtomCount();
        generate();
    }

    private void generate() {
        //allocate();
    }

    @Override
    protected void allocate() {
        //allocate memory
        this.atomID = new int[natom];
        this.segName = new String[natom];
        this.resID = new int[natom];
        this.resName = new String[natom];
        this.atomName = new String[natom];
        this.typeID = new int[natom];
        this.charge = new float[natom];
        this.mass = new float[natom];
        this.imove = new int[natom];

        /* TODO : handle DRUDE and CHECK */
    }

}//end class
