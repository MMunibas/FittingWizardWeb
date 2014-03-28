package ch.scs.unibas.fittingwizard.infrastructure;

import ch.scs.unibas.fittingwizard.Settings;
import ch.scs.unibas.fittingwizard.application.molecule.MoleculeId;
import ch.scs.unibas.fittingwizard.application.scripts.export.ExportScriptInput;
import ch.scs.unibas.fittingwizard.application.scripts.export.ExportScriptOutput;
import ch.scs.unibas.fittingwizard.application.scripts.export.IExportScript;
import ch.scs.unibas.fittingwizard.infrastructure.base.PythonScriptRunner;
import ch.scs.unibas.fittingwizard.infrastructure.base.ScriptUtilities;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * User: mhelmer
 * Date: 13.12.13
 * Time: 15:16
 */
public class RealExportScript implements IExportScript {

    private final static Logger logger = Logger.getLogger(RealExportScript.class);

    public static final String ExportScriptNameKey = "scripts.export_data";
    public static final String ConvertExportScriptNameKey = "scripts.export_data_convert";

    private final File exportScriptFile;
    private final File convertScriptFile;
    public final PythonScriptRunner runner;
    private final File outputDir;
    private final File moleculesDir;

    public RealExportScript(Settings settings, File outputDir, File moleculesDir) {
        this.moleculesDir = moleculesDir;
        exportScriptFile = new File(settings.getScriptsDir(), settings.getValue(ExportScriptNameKey));
        convertScriptFile = new File(settings.getScriptsDir(), settings.getValue(ConvertExportScriptNameKey));

        this.outputDir = outputDir;
        this.runner = new PythonScriptRunner();
    }

    @Override
    public ExportScriptOutput execute(ExportScriptInput input) {
        runner.setWorkingDir(outputDir);

        // -txt parameter
        String fitResultFileName = RealFitScript.getResultFileNameForFit(input.getFitId());
        File fitResultFile = new File(outputDir, fitResultFileName);

        // -pun co2_l.pun
        File moleculeLPunFile = getLPunFileForMolecule(input.getMoleculeId());

        //-out co2fit.pun
        File punOutputFile = getPunOutputFile(input.getFitId(), input.getMoleculeId());

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
        File lPunOutputFile = getLPunOutputFile(punOutputFile);
        ScriptUtilities.deleteFileIfExists(lPunOutputFile);

        args = Arrays.asList("-pun", punOutputFile.getName());
        // run script 2
        runner.exec(convertScriptFile, args);

        ScriptUtilities.verifyFileExistence(lPunOutputFile);

        return new ExportScriptOutput(lPunOutputFile);
    }

    private File getLPunOutputFile(File exportFileName) {
        // for exmpale: fit_1_co2.pun  -->   fit_1_co2.lpun
        String name = FilenameUtils.removeExtension(exportFileName.getName()) + ".lpun";
        File convertedFile = new File(outputDir, name);
        return convertedFile;
    }

    private File getPunOutputFile(int fitId, MoleculeId moleculeId) {
        // for exmpale: fit_1_co2.pun
        String name = String.format("%s%s_%s.pun", RealFitScript.FitNamePrefix, String.valueOf(fitId), moleculeId.getName());
        File exportOutput = new File(outputDir, name);
        return exportOutput;
    }

    private File getLPunFileForMolecule(MoleculeId moleculeId) {
        String lPunFileName = moleculeId.getName() + RealLRAScript.LPunExtension;
        Collection<File> files = FileUtils.listFiles(moleculesDir, new NameFileFilter(lPunFileName), TrueFileFilter.TRUE);
        if (files.size() != 1) {
            throw new RuntimeException(String.format("No or too many %s files found in %s.", lPunFileName, moleculesDir.getAbsolutePath()));
        }
        File lPunFile = files.iterator().next();
        return lPunFile;
    }
}
