package ch.unibas.fitting.shared.workflows.charmm;

import ch.unibas.fitting.shared.config.Settings;
import ch.unibas.fitting.shared.directories.CharmmRunFileContainer;
import ch.unibas.fitting.shared.directories.LjFitSessionDir;
import ch.unibas.fitting.shared.workflows.base.WorkflowContext;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * Created by tschmidt on 24.06.2016.
 */
public class MockGenerateInputWorkflow extends RealGenerateInputWorkflow {

    private final Logger LOGGER = Logger.getLogger(MockGenerateInputWorkflow.class);

    private File testdataDir;

    @Inject
    public MockGenerateInputWorkflow(Settings settings) {
        super(settings);
        this.testdataDir = settings.getTestdataCharmmOutputDir();
    }

    @Override
    public CharmmInputContainer execute(WorkflowContext<GenerateInputWorkflowInput> context) {
        LOGGER.info("Executing mock generate input workflow.");

        LjFitSessionDir dir = context.getParameter().ljFitSessionDir;
        CharmmRunFileContainer charmmRunDir = dir.createRunDir();

        File gasTestdataDir = new File(testdataDir, "gas");
        File solvTestdataDir = new File(testdataDir, "solv");
        File gasVdwTestdataDir = new File(gasTestdataDir, "vdw");
        File gasMtpTestdataDir = new File(gasTestdataDir, "mtp");
        File solvVdwTestdataDir = new File(solvTestdataDir, "vdw");
        File solvMtpTestdataDir = new File(solvTestdataDir, "mtp");

        LOGGER.info("dirs " + gasTestdataDir.getAbsolutePath());

        Collection<File> gasSourceFiles = FileUtils.listFiles(gasTestdataDir.getAbsoluteFile(), new String[]{"inp"}, false);
        LOGGER.debug("gasTestdataDir " + gasTestdataDir);
        for (File f : gasSourceFiles) {
            LOGGER.debug("gas Source file " + f.getAbsolutePath());
        }

        copyTestFiles(gasSourceFiles, charmmRunDir.getGasDir());

        Collection<File> solvSourceFiles = FileUtils.listFiles(solvTestdataDir, new String[]{"inp"}, false);
        LOGGER.debug("solvTestdataDir " + solvTestdataDir);
        for (File f : solvSourceFiles) {
            LOGGER.debug("solv Source file " + f.getAbsolutePath());
        }

        copyTestFiles(solvSourceFiles, charmmRunDir.getSolvDir());

        Collection<File> gasVdwSourceFiles = FileUtils.listFiles(gasVdwTestdataDir, new String[]{"inp"}, false);
        LOGGER.debug("gasVdwTestdataDir " + gasVdwTestdataDir);
        for (File f : gasVdwSourceFiles) {
            LOGGER.debug("gas vdw Source file " + f.getAbsolutePath());
        }

        copyTestFiles(gasVdwSourceFiles, charmmRunDir.getGasVdwDir());
        Collection<File> gasMtpSourceFiles = FileUtils.listFiles(gasMtpTestdataDir, new String[]{"inp"}, false);
        copyTestFiles(gasMtpSourceFiles, charmmRunDir.getGasMtpDir());
        Collection<File> solvVdeSourceFiles = FileUtils.listFiles(solvVdwTestdataDir, new String[]{"inp"}, false);
        copyTestFiles(solvVdeSourceFiles, charmmRunDir.getSolvVdwDir());
        Collection<File> solvMtpSourceFiles = FileUtils.listFiles(solvMtpTestdataDir, new String[]{"inp"}, false);
        copyTestFiles(solvMtpSourceFiles, charmmRunDir.getSolvMtpDir());

        CharmmInputContainer output = prepareInput(context.getParameter().ljFitSession, charmmRunDir);

        return output;
    }

    private void copyTestFiles(Collection<File> srcFiles, File destinationDir) {
        for (File srcFile : srcFiles) {
            LOGGER.debug(String.format("copyTestFileToDir %s to directory %s.",
                    FilenameUtils.normalize(srcFile.getAbsolutePath()),
                    FilenameUtils.normalize(destinationDir.getAbsolutePath())));
            try {
                FileUtils.copyFileToDirectory(srcFile, destinationDir);
            } catch (IOException e) {
                throw new RuntimeException("Could not copy mock data to output directory.");
            }
        }
    }
}
