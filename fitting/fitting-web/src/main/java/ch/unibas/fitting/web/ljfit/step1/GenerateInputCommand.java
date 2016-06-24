package ch.unibas.fitting.web.ljfit.step1;

import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Generator_DGHydr;
import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Input;
import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Input_GasPhase;
import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Input_PureLiquid;
import ch.unibas.fitting.shared.config.Settings;
import ch.unibas.fitting.shared.directories.IUserDirectory;
import ch.unibas.fitting.shared.fitting.Fit;
import ch.unibas.fitting.shared.workflows.base.WorkflowContext;
import ch.unibas.fitting.shared.workflows.charmm.GenerateInputInput;
import ch.unibas.fitting.shared.workflows.charmm.IGenerateInputWorkflow;
import ch.unibas.fitting.shared.workflows.charmm.MockGenerateInputWorkflow;
import ch.unibas.fitting.web.application.IAmACommand;
import ch.unibas.fitting.web.application.IBackgroundTasks;
import ch.unibas.fitting.web.application.TaskHandle;
import ch.unibas.fitting.web.ljfit.step2.ShowGeneratedInputPage;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Created by tschmidt on 24.06.2016.
 */
public class GenerateInputCommand implements IAmACommand {

    private Logger Logger = org.apache.log4j.Logger.getLogger(this.getClass());

    @Inject
    private Settings settings;
    @Inject
    private IBackgroundTasks tasks;
    @Inject
    private IUserDirectory userDirectory;

    public UUID generateInput(String username, File parFile, File rtfFile, File molFile,
                              File liquidFile, File solventFile, File lpunFile, Double lambda) {

        Logger.debug(settings.getTestdataCharmmGenerateInputOutputDir().getAbsoluteFile());
        Logger.debug(settings.getTestdataFitOutput().getAbsoluteFile());
        IGenerateInputWorkflow wf = new MockGenerateInputWorkflow(settings.getTestdataCharmmGenerateInputOutputDir(),
                userDirectory.getCharmmOutputDir(username).getGeneratedInputOutputDir());

        wf.execute(WorkflowContext.withInput(new GenerateInputInput(new File(""), new File(""), new File(""), new File(""),new File(""), new File(""), 0.1)));

        File gas_dir = userDirectory.getCharmmOutputDir(username).getGeneratedInputOutputDir().getGasDir();
        File solv_dir = userDirectory.getCharmmOutputDir(username).getGeneratedInputOutputDir().getSolvDir();
        File gas_vdw_dir  = userDirectory.getCharmmOutputDir(username).getGeneratedInputOutputDir().getGasVdwDir();
        File gas_mtp_dir  = userDirectory.getCharmmOutputDir(username).getGeneratedInputOutputDir().getGasMtpDir();
        File solv_vdw_dir = userDirectory.getCharmmOutputDir(username).getGeneratedInputOutputDir().getSolvVdwDir();
        File solv_mtp_dir =  userDirectory.getCharmmOutputDir(username).getGeneratedInputOutputDir().getSolvMtpDir();

        Logger.debug("dirs " + gas_vdw_dir + " " + gas_mtp_dir + " " + solv_vdw_dir + " "  + solv_mtp_dir );

        TaskHandle output = tasks.execute(username,
                "Charmm Generate Input",
                () -> {

//                    CHARMM_Generator_DGHydr in_gas_vdw = null, in_gas_mtp = null,
//                            in_solv_vdw = null, in_solv_mtp = null;
//
//                    File gasFile = null;
//                    CHARMM_Input gasInp = null;
//                    try {
//                        gasFile = new File(gas_vdw_dir.getParent(), "gas_phase.inp");
//                        gasInp = new CHARMM_Input_GasPhase(molFile.getAbsolutePath(), rtfFile.getAbsolutePath(),
//                                parFile.getAbsolutePath(), lpunFile.getAbsolutePath(), gasFile);
//
//                    } catch (IOException ex) {
//                        Logger.error("Error while generating " + gasFile.getAbsolutePath() + " : " + ex);
//                    }
//
//                    File liqFile = null;
//                    CHARMM_Input liqInp = null;
//                    try {
//                        liqFile = new File(solv_vdw_dir.getParent(), "pure_liquid.inp");
//                        liqInp = new CHARMM_Input_PureLiquid(liquidFile.getAbsolutePath(), rtfFile.getAbsolutePath(),
//                                parFile.getAbsolutePath(), lpunFile.getAbsolutePath(), liqFile);
//                    } catch (IOException ex) {
//                        Logger.error("Error while generating " + liqFile.getAbsolutePath() + " : " + ex);
//                    }
//
//                    in_gas_vdw = new CHARMM_Generator_DGHydr(molFile.getAbsolutePath(), rtfFile.getAbsolutePath(),
//                            parFile.getAbsolutePath(), lpunFile.getAbsolutePath(), "vdw",
//                            0.0, lambda, 1.0, gas_vdw_dir);
//
//                    in_gas_mtp = new CHARMM_Generator_DGHydr(molFile.getAbsolutePath(), rtfFile.getAbsolutePath(),
//                            parFile.getAbsolutePath(), lpunFile.getAbsolutePath(), "mtp",
//                            0.0, lambda, 1.0, gas_mtp_dir);
//
//                    in_solv_vdw = new CHARMM_Generator_DGHydr(molFile.getAbsolutePath(), solventFile.getAbsolutePath(),
//                            rtfFile.getAbsolutePath(), rtfFile.getAbsolutePath(),
//                            parFile.getAbsolutePath(), lpunFile.getAbsolutePath(), "vdw", 0.0, lambda, 1.0, solv_vdw_dir);
//
//                    in_solv_mtp = new CHARMM_Generator_DGHydr(molFile.getAbsolutePath(), solventFile.getAbsolutePath(),
//                            rtfFile.getAbsolutePath(), rtfFile.getAbsolutePath(),
//                            parFile.getAbsolutePath(), lpunFile.getAbsolutePath(), "mtp", 0.0, lambda, 1.0, solv_mtp_dir);



//                    FitMtpOutput result = fitScript.execute(input);
//
//                    double rmse = fitOutputParser.parseRmseValue(result.getOutputFile());
//
//                    List<OutputAtomType> outputAtomTypes = chargesFileParser.parseOutputFile(result.getResultsFile());
//                    File intialQsFile = input.getInitalChargesFile();
//
//                    InitialQ00 initialQs = new ChargesFileParser().parseInitalCharges(intialQsFile);
//                    // todo verify initial charges vs generated output
//
//                    Fit fit = fitRepo.createFit(username, rmse, input.getRank(), outputAtomTypes, initialQs);
                    Thread.sleep(5000);

                    return null;
                },
                (fit, pp) -> {
                    //pp.add("task_id", fit.getId());
                    return ShowGeneratedInputPage.class;
                });
        return output.getId();

    };
}
