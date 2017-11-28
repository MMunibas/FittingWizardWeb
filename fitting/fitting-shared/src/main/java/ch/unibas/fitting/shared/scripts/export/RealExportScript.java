/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.scripts.export;

import ch.unibas.fitting.shared.config.Settings;
import ch.unibas.fitting.shared.directories.MoleculesDir;
import ch.unibas.fitting.shared.scripts.fitmtp.RealFitMtpScript;
import ch.unibas.fitting.shared.scripts.lra.RealLRAScript;
import ch.unibas.fitting.shared.scripts.base.PythonScriptRunner;
import ch.unibas.fitting.shared.scripts.base.ScriptUtilities;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.log4j.Logger;

import javax.inject.Inject;

/**
 * User: mhelmer
 * Date: 13.12.13
 * Time: 15:16
 */
public class RealExportScript implements IExportScript {

    private final static Logger logger = Logger.getLogger(RealExportScript.class);

    private final File exportScriptFile;
    private final File convertScriptFile;
    public final PythonScriptRunner runner;

    @Inject
    public RealExportScript(Settings settings) {

        exportScriptFile = new File(settings.getScriptsDir(), "mtp_prm.to.pun.py");
        convertScriptFile = new File(settings.getScriptsDir(), "pun2charmmlpun.py");

        this.runner = new PythonScriptRunner();
    }

    @Override
    public ExportScriptOutput execute(ExportScriptInput input) {
        File outputDir = input.getFitOutputDir().getDirectory();
        runner.setWorkingDir(outputDir);

        // -txt parameter
        String fitResultFileName = RealFitMtpScript.getResultFileNameForFit();
        File fitResultFile = new File(outputDir, fitResultFileName);

        // -pun co2_l.pun
        File moleculeLPunFile = getLPunFileForMolecule(
                input.getMoleculesDir(),
                input.getMoleculeName());

        //-out co2fit.pun
        File punOutputFile = getPunOutputFile(
                outputDir,
                input.getFitId(),
                input.getMoleculeName());

        ScriptUtilities.verifyFileExistence(fitResultFile);
        ScriptUtilities.verifyFileExistence(moleculeLPunFile);

        ScriptUtilities.deleteFileIfExists(punOutputFile);

        List<String> args = Arrays.asList("-txt", fitResultFile.getAbsolutePath(),
                "-pun", moleculeLPunFile.getAbsolutePath(),
                "-out", punOutputFile.getName());
        // run script 1
        runner.exec(exportScriptFile, args);

        ScriptUtilities.verifyFileExistence(punOutputFile);

        //
        // second part which actually generates the expected output from the pun file.
        //
        File lPunOutputFile = getLPunOutputFile(outputDir, punOutputFile);
        ScriptUtilities.deleteFileIfExists(lPunOutputFile);

        args = Arrays.asList("-pun", punOutputFile.getName());
        // run script 2
        runner.exec(convertScriptFile, args);

        ScriptUtilities.verifyFileExistence(lPunOutputFile);

        return new ExportScriptOutput(lPunOutputFile);
    }

    protected File getLPunOutputFile(File outputDir, File exportFileName) {
        // for exmpale: fit_1_co2.pun  -->   fit_1_co2.lpun
        String name = FilenameUtils.removeExtension(exportFileName.getName()) + ".lpun";
        File convertedFile = new File(outputDir, name);
        return convertedFile;
    }

    private File getPunOutputFile(
            File outputDir,
            int fitId,
            String moleculeId) {
        // for exmpale: fit_1_co2.pun
        String name = String.format(
                "%s%s_%s.pun",
                RealFitMtpScript.FitNamePrefix,
                String.valueOf(fitId),
                moleculeId);
        File exportOutput = new File(outputDir, name);
        return exportOutput;
    }

    protected File getLPunFileForMolecule(
            MoleculesDir moleculesDir,
            String moleculeId) {
        String lPunFileName = moleculeId + RealLRAScript.LPunExtension;
        Collection<File> files = FileUtils.listFiles(moleculesDir.getDirectory(), new NameFileFilter(lPunFileName), TrueFileFilter.TRUE);
        if (files.size() != 1) {
            throw new RuntimeException(String.format("No or too many %s files found in %s.", lPunFileName, moleculesDir.getDirectory().getAbsolutePath()));
        }
        File lPunFile = files.iterator().next();
        return lPunFile;
    }
}
