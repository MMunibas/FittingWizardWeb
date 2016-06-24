package ch.unibas.fitting.shared.workflows.charmm;

import ch.unibas.fitting.shared.config.Settings;
import ch.unibas.fitting.shared.directories.CharmmGeneratedInputOutputDir;
import ch.unibas.fitting.shared.workflows.base.WorkflowContext;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by tschmidt on 24.06.2016.
 */
public class MockGenerateInputWorkflow implements IGenerateInputWorkflow {

    private final Logger Logger = org.apache.log4j.Logger.getLogger(MockGenerateInputWorkflow.class);

    private CharmmGeneratedInputOutputDir outputBaseDir;
    private File testdataDir;

    @Inject
    public MockGenerateInputWorkflow(Settings settings, CharmmGeneratedInputOutputDir outputBaseDir) {
        this.testdataDir = settings.getTestdataCharmmGenerateInputOutputDir();
        this.outputBaseDir = outputBaseDir;
    }

    @Override
    public GenerateInputOutput execute(WorkflowContext<GenerateInputInput> context) {
        Logger.info("Executing mock generate input workflow.");

        File gasTestdataDir = new File(testdataDir, "gas");
        File solvTestdataDir = new File(testdataDir, "solv");
        File gasVdwTestdataDir = new File(gasTestdataDir, "vdw");
        File gasMtpTestdataDir = new File(gasTestdataDir, "mtp");
        File solvVdwTestdataDir = new File(solvTestdataDir, "vdw");
        File solvMtpTestdataDir = new File(solvTestdataDir, "mtp");

        Logger.info("dirs " + gasTestdataDir.getAbsolutePath());

        Collection<File> gasSourceFiles = FileUtils.listFiles(gasTestdataDir.getAbsoluteFile(), new String[]{"inp"}, false);
        Logger.debug("gasTestdataDir " + gasTestdataDir);
        for (File f : gasSourceFiles) {
            Logger.debug("gas Source file " + f.getAbsolutePath());
        }
        ;
        copyTestFiles(gasSourceFiles, outputBaseDir.getGasDir());

        Collection<File> solvSourceFiles = FileUtils.listFiles(solvTestdataDir, new String[]{"inp"}, false);
        Logger.debug("solvTestdataDir " + solvTestdataDir);
        for (File f : solvSourceFiles) {
            Logger.debug("solv Source file " + f.getAbsolutePath());
        }
        ;
        copyTestFiles(solvSourceFiles, outputBaseDir.getSolvDir());

        Collection<File> gasVdwSourceFiles = FileUtils.listFiles(gasVdwTestdataDir, new String[]{"inp"}, false);
        Logger.debug("gasVdwTestdataDir " + gasVdwTestdataDir);
        for (File f : gasVdwSourceFiles) {
            Logger.debug("gas vdw Source file " + f.getAbsolutePath());
        }
        ;
        copyTestFiles(gasVdwSourceFiles, outputBaseDir.getGasVdwDir());
        Collection<File> gasMtpSourceFiles = FileUtils.listFiles(gasMtpTestdataDir, new String[]{"inp"}, false);
        copyTestFiles(gasMtpSourceFiles, outputBaseDir.getGasMtpDir());
        Collection<File> solvVdeSourceFiles = FileUtils.listFiles(solvVdwTestdataDir, new String[]{"inp"}, false);
        copyTestFiles(solvVdeSourceFiles, outputBaseDir.getSolvVdwDir());
        Collection<File> solvMtpSourceFiles = FileUtils.listFiles(solvMtpTestdataDir, new String[]{"inp"}, false);
        copyTestFiles(solvMtpSourceFiles, outputBaseDir.getSolvMtpDir());

        GenerateInputOutput output = new GenerateInputOutput(gasSourceFiles.iterator().next(),
                solvSourceFiles.iterator().next(),
                new ArrayList(gasVdwSourceFiles),
                new ArrayList<>(gasMtpSourceFiles),
                new ArrayList<>(solvVdeSourceFiles),
                new ArrayList<>(solvMtpSourceFiles));

        return output;
    }

    private void copyTestFiles(Collection<File> srcFiles, File destinationDir) {
        for (File srcFile : srcFiles) {
            Logger.debug(String.format("copyTestFile %s to directory %s.",
                    FilenameUtils.normalize(srcFile.getAbsolutePath()),
                    FilenameUtils.normalize(destinationDir.getAbsolutePath())));
            try {
                FileUtils.copyFileToDirectory(srcFile, destinationDir);
            } catch (IOException e) {
                throw new RuntimeException("Could not copy mock data to output directory.");
            }
        };
    }
}
