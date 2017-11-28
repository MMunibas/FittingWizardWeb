/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.scripts.vmd;

import ch.unibas.fitting.shared.config.Settings;
import ch.unibas.fitting.shared.scripts.fitmtp.RealFitMtpScript;
import ch.unibas.fitting.shared.scripts.lra.RealLRAScript;
import ch.unibas.fitting.shared.scripts.multipolegauss.RealMultipoleGaussScript;
import ch.unibas.fitting.shared.fieldcomp.FieldcompRunner;
import ch.unibas.fitting.shared.scripts.base.PythonScriptRunner;
import ch.unibas.fitting.shared.scripts.base.ScriptUtilities;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.log4j.Logger;

public class RealVmdDisplayScript implements IVmdDisplayScript {

    private final static Logger logger = Logger.getLogger(RealVmdDisplayScript.class);

    private static final String ExportScriptNameKey = "scripts.export_data";
    private static final String VmdScriptNameKey = "scripts.vmd_display";

    private static final String CubeOutputNamePostfix = "_mtpcube";
    
    private final File exportScriptFile;
    private final FieldcompRunner fieldcompRunner;
    private final PythonScriptRunner pyRunner;
    private final VmdRunner vmdRunner;
    private final File vmdScriptFile;
    private final File outputDir;
    private final File moleculesDir;

    public RealVmdDisplayScript(Settings settings, File outputDir, File moleculesDir) {
        this.moleculesDir = moleculesDir;
        exportScriptFile = new File(settings.getScriptsDir(), settings.getValue(ExportScriptNameKey));
        vmdScriptFile = new File(settings.getScriptsDir(), settings.getValue(VmdScriptNameKey));

        this.outputDir = outputDir;
        this.fieldcompRunner = new FieldcompRunner(settings.getScriptsDir());
        this.pyRunner = new PythonScriptRunner();
        this.vmdRunner = new VmdRunner();
    }

    @Override
    public Void execute(VmdDisplayInput input) {
        
    	// run export
    	pyRunner.setWorkingDir(outputDir);

        // -txt parameter
        String fitResultFileName = RealFitMtpScript.getResultFileNameForFit();
        File fitResultFile = new File(outputDir, fitResultFileName);

        // -pun co2_l.pun
        File moleculeLPunFile = getLPunFileForMolecule(input.getMoleculeName());

        // -out co2fit.pun
        File punOutputFile = getPunOutputFile(input.getFitId(), input.getMoleculeName());

        ScriptUtilities.verifyFileExistence(fitResultFile);
        ScriptUtilities.verifyFileExistence(moleculeLPunFile);

        ScriptUtilities.deleteFileIfExists(punOutputFile);
        
        List<String> pyArgs = Arrays.asList("-txt", fitResultFile.getAbsolutePath(),
                "-pun", moleculeLPunFile.getAbsolutePath(),
                "-out", punOutputFile.getName());
        // run script 1
        pyRunner.exec(exportScriptFile, pyArgs);
        ScriptUtilities.verifyFileExistence(punOutputFile);
        
        // run fieldcomp
        
        File currentMoleculeDir = getMoleculeDirForMolecule(input.getMoleculeName());
        fieldcompRunner.setWorkingDir(currentMoleculeDir);
        
        // -vdw parameter
        File moleculeVdwFile = getVdwFileForMolecule(input.getMoleculeName());
        File modifiedVdwFile = new File(outputDir, moleculeVdwFile.getName());
        
        createModifiedVdwFile(moleculeVdwFile, modifiedVdwFile, input.getFitRank());
        
        // -cube parameter
        File moleculeCubeFile = getCubeFileForMolecule(input.getMoleculeName());
        
        List<String> fieldcompArgs = Arrays.asList("-cube", /*moleculeCubeFile.getName()*/ moleculeCubeFile.getAbsolutePath(),
                "-vdw", modifiedVdwFile.getAbsolutePath(),
                "-pun", punOutputFile.getAbsolutePath(),
                "-cubeout");
        
        fieldcompRunner.exec(fieldcompArgs);
        
        String systemDir = System.getProperty("user.dir");
        File outputFile = getDefaultCubeOutputFile(input.getMoleculeName());
        File cubeOutputFile = getCubeOutputFile(input.getMoleculeName());

        // copy output file from default location to output dir
        ScriptUtilities.verifyFileExistence(outputFile);
          
        try {
        	FileUtils.copyFile(outputFile, cubeOutputFile);
        	outputFile.delete();
        } catch (IOException e) {
            throw new RuntimeException("Could not copy file " + outputFile.getAbsolutePath() + " to destination " + cubeOutputFile.getAbsolutePath(), e);
        }
        
        ScriptUtilities.verifyFileExistence(cubeOutputFile);
        
        // display in vmd
        vmdRunner.exec(Arrays.asList(cubeOutputFile.getAbsolutePath(), "-e", vmdScriptFile.getAbsolutePath()));
        
        return null;
    }

    private File getMoleculeDirForMolecule(String moleculeName) {
    	return new File(moleculesDir.getAbsolutePath(), moleculeName);
    }
    
    private File getDefaultCubeOutputFile(String moleculeName) {
    	String name = String.format(
    	        "%s%s.cube",
                moleculeName,
                CubeOutputNamePostfix);
    	File currentMoleculeDir = getMoleculeDirForMolecule(moleculeName);
        File cubeDefaultOutput = new File(currentMoleculeDir.getAbsolutePath(), name);
        return cubeDefaultOutput;
    }
    
    private File getPunOutputFile(int fitId, String moleculeName) {
        // for example: fit_1_co2.pun
        String name = String.format(
                "%s%s_%s.pun",
                RealFitMtpScript.FitNamePrefix,
                String.valueOf(fitId),
                moleculeName);
        File exportOutput = new File(outputDir, name);
        return exportOutput;
    }

    private File getLPunFileForMolecule(String moleculeName) {
        String lPunFileName = moleculeName + RealLRAScript.LPunExtension;
        Collection<File> files = FileUtils.listFiles(moleculesDir, new NameFileFilter(lPunFileName), TrueFileFilter.TRUE);
        if (files.size() != 1) {
            throw new RuntimeException(String.format("No or too many %s files found in %s.", lPunFileName, moleculesDir.getAbsolutePath()));
        }
        File lPunFile = files.iterator().next();
        return lPunFile;
    }
    
    private File getVdwFileForMolecule(String moleculeName) {
        String vdwFileName = moleculeName + RealMultipoleGaussScript.vdwExtension;
        Collection<File> files = FileUtils.listFiles(moleculesDir, new NameFileFilter(vdwFileName), TrueFileFilter.TRUE);
        if (files.size() != 1) {
            throw new RuntimeException(String.format("No or too many %s files found in %s.", vdwFileName, moleculesDir.getAbsolutePath()));
        }
        File vdwFile = files.iterator().next();
        return vdwFile;
    }
    
    private File getCubeFileForMolecule(String moleculeName) {
        String cubeFileName = moleculeName + RealMultipoleGaussScript.cubeExtension;
        Collection<File> files = FileUtils.listFiles(moleculesDir, new NameFileFilter(cubeFileName), TrueFileFilter.TRUE);
        if (files.size() != 1) {
            throw new RuntimeException(String.format("No or too many %s files found in %s.", cubeFileName, moleculesDir.getAbsolutePath()));
        }
        File cubeFile = files.iterator().next();
        return cubeFile;
    }
    
    private File getCubeOutputFile(String moleculeName) {
    	// *mol*_mtpcube.cube
    	String name = String.format("%s%s.cube", moleculeName, CubeOutputNamePostfix);
    	File cubeOutputFile = new File(outputDir, name);
    	return cubeOutputFile;
    }
    
    private void createModifiedVdwFile(File vdwInputFile, File vdwOutputFile, int fitRank) {
        List<String> lines;
        List<String> modLines = new LinkedList<String>();
        try {
            lines = FileUtils.readLines(vdwInputFile);
        } catch (IOException e) {
            throw new RuntimeException("Could not read form vdw file.\n" + vdwInputFile.getAbsolutePath(), e);
        }
        
        for (String line: lines) {
        	String modifiedLine = line.replaceAll("[0-2]$", String.valueOf(fitRank));
        	modLines.add(modifiedLine);
        }
        
        try {
        	FileUtils.writeLines(vdwOutputFile, modLines);	        
		} catch (IOException e) {
			throw new RuntimeException("Could not write to modified vdw file.\n" + vdwOutputFile.getAbsolutePath(), e);
		}
    }
    
}
