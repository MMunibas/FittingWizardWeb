/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.application.base;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;

/**
 * A typed file with some extra methods.
 * User: mhelmer
 * Date: 16.12.13
 * Time: 16:20
 */
public class MoleculesDir {
    private final File directory;

    public MoleculesDir(File directory) {
        directory.mkdir();
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Given directory does not exist " + directory.getAbsolutePath());
        }
        this.directory = new File(FilenameUtils.normalize(directory.getAbsolutePath()));
    }

    public File getDirectory() {
        return directory;
    }

    public boolean contains(File selectedDir) {
        return FilenameUtils.equalsNormalized(directory.getAbsolutePath(), selectedDir.getParent());
    }

    /**
     * Delets a molecule from the molecule directory.
     * @param name
     */
    public void deleteMolecule(String name) {
        File molDir = new File(directory, name);
        try {
            FileUtils.deleteDirectory(molDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not delete existing molecule directory " + molDir.getAbsolutePath());
        }
    }
}
