/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.presentation.addmolecule;

import ch.unibas.fittingwizard.application.xyz.XyzFile;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: mhelmer
 * Date: 07.12.13
 * Time: 11:49
 * To change this template use File | Settings | File Templates.
 */
public class CoordinatesDto {
    private final File coordinatesFile;
    private final XyzFile xyzFile;

    public CoordinatesDto(XyzFile xyzFile) {
        this.coordinatesFile = xyzFile.getSource();
        this.xyzFile = xyzFile;
    }

    public CoordinatesDto(File coordinatesFile) {
        this.xyzFile = null;
        this.coordinatesFile = coordinatesFile;
    }

    public File getCoordinatesFile() {
        return coordinatesFile;
    }

    public XyzFile getXyzFile() {
        return xyzFile;
    }
}
