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
 *
 * @author hedin
 */
public class RTF_generate extends RTF {

    private List<XyzAtom> atoms;

    public RTF_generate(XyzFile xyz) {
        atoms = xyz.getAtoms();
        this.natom = xyz.getAtomCount();
    }

    @Override
    protected void allocate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
