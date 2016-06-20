/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.directories;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 * A typed file with some extra methods.
 * User: mhelmer
 * Date: 16.12.13
 * Time: 16:20
 */
public class MoleculesDir extends FittingDirectory {


    public MoleculesDir(File directory) {
        super(directory);
    }

    public boolean contains(File selectedDir) {
        return FilenameUtils.equalsNormalized(getDirectory().getAbsolutePath(), selectedDir.getParent());
    }

    /**
     * Delets a molecule from the molecule directory.
     * @param name
     */
    public void deleteMolecule(String name) {
        File molDir = new File(getDirectory(), name);
        try {
            FileUtils.deleteDirectory(molDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not delete existing molecule directory " + molDir.getAbsolutePath());
        }
    }

    public File getDirectoryFor(String moleculeName) {
        return new File(getDirectory(), moleculeName);
    }
}
