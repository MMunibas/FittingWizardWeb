package ch.unibas.fitting.shared.charmm.web;

import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Generator_DGHydr;
import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Input_GasPhase;
import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Input_PureLiquid;
import ch.unibas.fitting.shared.charmm.generate.outputs.CHARMM_Output_GasPhase;
import ch.unibas.fitting.shared.charmm.generate.outputs.CHARMM_Output_PureLiquid;
import ch.unibas.fitting.shared.charmm.scripts.ClusterParameter;
import ch.unibas.fitting.shared.config.Settings;
import ch.unibas.fitting.shared.directories.UserDirectory;
import ch.unibas.fitting.shared.workflows.charmm.CharmmInputContainer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by tschmidt on 27.06.2016.
 */
public class MockRunCharmmWorkflowNew implements IRunCharmmWorkflowNew {

    private final Logger LOGGER = Logger.getLogger(MockRunCharmmWorkflowNew.class);

    private final File testdataDir;

    @Inject
    public MockRunCharmmWorkflowNew(Settings settings) {
        this.testdataDir = settings.getTestdataCharmmOutputDir();
    }

    @Override
    public CharmmResult executeCharmm(
            CharmmInputContainer container,
            ClusterParameter clusterParameter) {

        File gasDir = new File(testdataDir, "gas");
        File solvDir = new File(testdataDir, "solv");

        CHARMM_Input_PureLiquid pureLiquid = container.getLiquidInput();
        CHARMM_Input_GasPhase gasPhase = container.getGasInput();

        LOGGER.debug("files " + new File(gasDir, "gas_phase.out")
                + " "
                + new File(solvDir, "pure_liquid.out")
                + " "
                + gasPhase.getWorkDir()
                + " "
                + pureLiquid.getWorkDir() );

        copyTestFileToDir(new File(gasDir, "gas_phase.out"), gasPhase.getWorkDir());
        copyTestFileToDir(new File(gasDir, "mtp/dg.out"), container.getGasMtp().getWorkDir());
        copyTestFileToDir(new File(gasDir, "vdw/dg.out"), container.getGasVdw().getWorkDir());

        copyTestFileToDir(new File(solvDir, "pure_liquid.out"), pureLiquid.getWorkDir());
        copyTestFileToDir(new File(solvDir, "mtp/dg.out"), container.getSolvMtp().getWorkDir());
        copyTestFileToDir(new File(solvDir, "vdw/dg.out"), container.getSolvVdw().getWorkDir());

        CHARMM_Output_GasPhase gasOut = new CHARMM_Output_GasPhase(new File(gasPhase.getWorkDir(), "gas_phase.out"));
        CHARMM_Output_PureLiquid pureLiquidOut = new CHARMM_Output_PureLiquid(new File(pureLiquid.getWorkDir(), "pure_liquid.out"));

        var out = CharmmResultParser.parseOutput(container.getCharmmRunDir());

        return new CharmmResult(gasOut, pureLiquidOut, out);
    }

    protected void copyTestFileToDir(File srcFile, File destinationDir) {
        LOGGER.debug(String.format("copyTestFileToDir %s to %s.",
                FilenameUtils.normalize(srcFile.getAbsolutePath()),
                FilenameUtils.normalize(destinationDir.getAbsolutePath())));
        try {
            FileUtils.copyFileToDirectory(srcFile, destinationDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not copy mock data to output directory.", e);
        }
    }
}
