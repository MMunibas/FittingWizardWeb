/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.gaussian.addmolecule;

import ch.unibas.fitting.shared.xyz.XyzFile;

/**
 * Created with IntelliJ IDEA.
 * User: mhelmer
 * Date: 07.12.13
 * Time: 11:57
 * To change this template use File | Settings | File Templates.
 */
public class MultipoleGaussParameterDto {
    private final XyzFile xyzFile;

    public MultipoleGaussParameterDto(XyzFile xyzFile) {
        this.xyzFile = xyzFile;
    }

    public XyzFile getXyzFile() {
        return xyzFile;
    }
}
