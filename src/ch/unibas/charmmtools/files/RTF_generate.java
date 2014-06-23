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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hedin
 */
public final class RTF_generate extends RTF {

    private List<XyzAtom> atoms = null;

    //covalence radii
    private final double C = 0.8;
    private final double H = 0.4;
    private final double O = 0.8;
    private final double N = 0.8;
    private final double S = 1.2;
    private final double P = 1.2;
    private final double F = 1.3;

    private List<Double> x = null, y = null, z = null;
    private List<Integer> id = null;
    private List<String> name = null;

    public RTF_generate(XyzFile xyz) {
        atoms = xyz.getAtoms();
        this.natom = xyz.getAtomCount();
        this.allocate();
        this.copy_data();
//        this.generate();
    }

    @Override
    protected void allocate() {
        x = new ArrayList<>();
        y = new ArrayList<>();
        z = new ArrayList<>();
        id = new ArrayList<>();
        name = new ArrayList<>();
    }

    protected void copy_data() {
        for (XyzAtom it : atoms) {
            x.add(it.getX());
            y.add(it.getY());
            z.add(it.getZ());
            id.add(it.getIndex());
            name.add(it.getName());
        }
    }

    public void generate() {
        this.gen_bonds();
    }

    private void gen_bonds() {

    }

}//end of class
