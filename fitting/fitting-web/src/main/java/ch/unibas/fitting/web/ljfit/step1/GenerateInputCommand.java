package ch.unibas.fitting.web.ljfit.step1;

import ch.unibas.fitting.shared.directories.IUserDirectory;
import ch.unibas.fitting.shared.fitting.Fit;
import ch.unibas.fitting.web.application.IAmACommand;
import ch.unibas.fitting.web.application.IBackgroundTasks;
import ch.unibas.fitting.web.application.TaskHandle;
import ch.unibas.fitting.web.ljfit.step2.ShowGeneratedInputPage;

import javax.inject.Inject;
import java.io.File;
import java.util.UUID;

/**
 * Created by tschmidt on 24.06.2016.
 */
public class GenerateInputCommand implements IAmACommand {

    @Inject
    private IBackgroundTasks tasks;
    @Inject
    private IUserDirectory userDirectory;

    public UUID generateInput(String username, File parFile, File rtfFile, File molFile,
                              File liquidFile, File solventFile, File lpunFile) {

        File outputDir = userDirectory.getCharmmOutputDir(username).getGeneratedInputOutputDir();

        File gasOutputDir = new File(outputDir, "gas");
        File solvOutputDir = new File(outputDir, "solv");
        File gas_vdw_dir  = new File(gasOutputDir, "vdw");
        File gas_mtp_dir  = new File(gasOutputDir, "mtp");
        File solv_vdw_dir = new File(solvOutputDir, "/vdw");
        File solv_mtp_dir = new File(solvOutputDir, "/mtp");

        TaskHandle output = tasks.execute(username,
                "Charmm Generate Input",
                () -> {
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
