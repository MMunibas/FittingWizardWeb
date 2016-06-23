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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

/**
 * A typed file with some extra methods.
 * User: mhelmer
 * Date: 16.12.13
 * Time: 16:20
 */
public class MoleculesDir extends FittingDirectory {

    private final static String MtpFitSuffix = "_mtpfittab.txt";

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

    public List<String> listAllMolecules() {
        return Arrays.asList(getDirectory().listFiles(File::isDirectory))
                .stream()
                .map(file -> file.getName())
                .collect(Collectors.toList());
    }

    public File getMoleculeDir(String moleculeName) {
        return new File(getDirectory(), moleculeName);
    }

    /**
     * Finds all available _mtpfittab.txt files in the molecule subdirectories.
     * @return
     */
    public List<File> findAllFitTabFiles() {
        LOGGER.debug("findAllFitTabFiles");
        List<File> files = new ArrayList<>(FileUtils.listFiles(getDirectory(), new IOFileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(MtpFitSuffix);
            }

            @Override
            public boolean accept(File dir, String name) {
                return false;
            }
        }, TrueFileFilter.TRUE));

        if (files.size() == 0) {
            throw new DirectoryException("Could not find any fit tab results file in "
                    + FilenameUtils.normalize(getDirectory().getAbsolutePath()));
        }
        for (File file : files) {
            LOGGER.debug("Found fit tab file: " + FilenameUtils.normalize(file.getAbsolutePath()));
        }
        return files;
    }

    public File findLPunFileFor(String moleculeName) {
        File f = new File(getMoleculeDir(moleculeName), moleculeName + "_l.pun");
        throwIfFileIsMissing(f, "Could not find LPUN file.");
        return f;
    }

    public File findFitTabFileFor(String moleculeName) {
        File f = new File(getMoleculeDir(moleculeName), moleculeName + MtpFitSuffix);
        throwIfFileIsMissing(f, "Could not find MTP Fit file.");
        return f;
    }

    private void throwIfFileIsMissing(File file, String msg) {
        if (!file.exists())
            throw new DirectoryException(msg + " " + FilenameUtils.normalize(file.getAbsolutePath()));
    }
}
