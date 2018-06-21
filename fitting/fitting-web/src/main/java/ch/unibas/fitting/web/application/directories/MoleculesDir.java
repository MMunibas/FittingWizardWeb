/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.web.application.directories;

import ch.unibas.fitting.web.application.algorithms.xyz.XyzFile;
import ch.unibas.fitting.web.application.algorithms.xyz.XyzFileParser;
import io.vavr.collection.List;
import io.vavr.control.Option;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * A typed file with some extra methods.
 * User: mhelmer
 * Date: 16.12.13
 * Time: 16:20
 */
public class MoleculesDir extends FittingDirectory {

    private final static String MtpFitSuffix = "_mtpfittab.txt";

    public MoleculesDir(String username, File directory) {
        super(username, directory);
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

    public io.vavr.collection.List<File> listAllMoleculeDirs() {
        return io.vavr.collection.List.ofAll(
                Arrays.asList(getDirectory().listFiles(File::isDirectory))
        );
    }

    public File createMoleculeDir(String moleculeName) {
        var f = getMoleculeDirFile(moleculeName);
        f.mkdirs();
        return f;
    }

    public File getMoleculeDirFile(String moleculeName) {
        return new File(getDirectory(), moleculeName);
    }

    public File findLPunFileFor(String moleculeName) {
        File f = new File(getMoleculeDirFile(moleculeName), moleculeName + "_l.pun");
        throwIfFileIsMissing(f, "Could not find LPUN file.");
        return f;
    }

    public File getFitTabFileFor(String moleculeName) {
        return new File(getMoleculeDirFile(moleculeName), moleculeName + MtpFitSuffix);
    }

    public File findFitTabFileFor(String moleculeName) {
        File f = getFitTabFileFor(moleculeName);
        throwIfFileIsMissing(f, "Could not find MTP Fit file.");
        return f;
    }

    private void throwIfFileIsMissing(File file, String msg) {
        if (!file.exists())
            throw new DirectoryException(msg + " " + FilenameUtils.normalize(file.getAbsolutePath()));
    }

    public File getXyzFileFor(String moleculeName) {
        String withoutExt = FilenameUtils.removeExtension(moleculeName);
        return new File(getDirectory(), withoutExt + ".xyz");
    }


    public Option<XyzFile> getXyzFile(String moleculeName) {
        File f = getXyzFileFor(moleculeName);
        if (!f.exists())
            return Option.none();
        XyzFile xyz = XyzFileParser.parse(f);
        return Option.of(xyz);
    }

    public File getUserChargesFile(String moleculeName) {
        File molDir = getMoleculeDirFile(moleculeName);
        return new File(molDir, "user_charges.json");
    }

    public String getAnyMoleculeName() {
        io.vavr.collection.List<File> names = listAllMoleculeDirs();
        String anyName = null;
        if (names.size()>0)
            anyName = names.get(0).getName();
        return anyName;
    }

    public List<String> listAllMoleculeNames() {
        return listAllMoleculeDirs()
                .map(f -> f.getName())
                .toList();
    }

    public List<File> listAllMtpFitTabFiles() {
        return listAllMoleculeDirs()
                .map(f -> getFitTabFileFor(f.getName()))
                .toList();
    }

    public File getResultsJson(String moleculeName) {
        var molDir = getMoleculeDirFile(moleculeName);
        return new File(molDir, "run_results.json");
    }
}
