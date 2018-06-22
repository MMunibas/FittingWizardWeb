package ch.unibas.fitting.web.ljfit.commands;

import ch.unibas.fitting.application.algorithms.ljfit.LjFitJsonResult;
import ch.unibas.fitting.application.directories.IUserDirectory;
import ch.unibas.fitting.application.directories.LjFitRunDir;
import ch.unibas.fitting.application.directories.LjFitSessionDir;
import ch.unibas.fitting.infrastructure.JsonSerializer;
import ch.unibas.fitting.application.algorithms.ljfit.UploadedFiles;
import ch.unibas.fitting.application.algorithms.ljfit.LjFitRunInput;
import ch.unibas.fitting.application.algorithms.ljfit.LjFitRunResult;
import ch.unibas.fitting.application.algorithms.ljfit.LjFitSession;
import ch.unibas.fitting.application.calculation.CalculationManagementClient;
import ch.unibas.fitting.application.calculation.manager.StartDefinition;
import ch.unibas.fitting.web.calculation.NavigationInfo;
import ch.unibas.fitting.application.algorithms.ljfit.LjFitRepository;
import ch.unibas.fitting.web.ljfit.fitting.step2.LjSessionPage;
import ch.unibas.fitting.web.misc.PageNavigation;
import io.vavr.collection.List;
import io.vavr.control.Option;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;

/**
 * Created by mhelmer on 24.06.2016.
 */
public class RunLjFitsCommand {
    private static final Logger LOGGER = Logger.getLogger(RunLjFitsCommand.class);

    @Inject
    private LjFitRepository ljFitRepository;

    @Inject
    private IUserDirectory userDirectory;

    @Inject
    private JsonSerializer serializer;

    @Inject
    private CalculationManagementClient calculationClient;

    public void executeNew(String username, RunFromPage runs) {

        var definitions = prepare(username, runs);

        var response = calculationClient.spawnTask("Running LJ Fits",
                username,
                new NavigationInfo(() -> PageNavigation.ToPage(LjSessionPage.class), () -> PageNavigation.ToPage(LjSessionPage.class)),
                definitions.toJavaArray(StartDefinition.class));

        PageNavigation.ToProgressForCalculation(response);
    }

    private List<StartDefinition> prepare(String username, RunFromPage run) {
        LjFitSession session = ljFitRepository.loadSessionForUser(username).get();
        LjFitSessionDir sessionDir = userDirectory.getLjFitSessionDir(username).get();
        UploadedFiles files = sessionDir.lookupUploadedFiles(session.getUploadedFileNames());

        var list = List.<StartDefinition>empty();
        for (var pair : run.runPairs) {
            LjFitRunDir runDir = sessionDir.createRunDir(pair.lambda_sigma, pair.lambda_epsilon);

            LjFitRunInput input = new LjFitRunInput(
                    pair.lambda_epsilon,
                    pair.lambda_sigma,
                    run.lambda_size_electrostatic,
                    run.lambda_size_vdw,
                    session.getSessionParameter().temperature);

            writeToJson(runDir.getRunInputJson(), input);

            var map = new HashMap<String, Object>();
            if (files.rtfFile != null)
                map.put("lj_filename_charmm_topology", files.rtfFile.getName());
            if (files.molFile != null)
                map.put("lj_filename_solute_pdb", files.molFile.getName());
            if (files.solventFile != null)
                map.put("lj_filename_solvent_pdb", files.solventFile.getName());
            if (files.parFile != null)
                map.put("lj_filename_charmm_parameter", files.parFile.getName());
            if (files.lpunFile != null)
                map.put("lj_filename_mtpl_lpun", files.lpunFile.getName());
            if (files.liquidFile != null)
                map.put("lj_filename_pureliquid_pdb", files.liquidFile.getName());
            if (files.resFile != null)
                map.put("lj_filename_charmm_restart", files.resFile.getName());

            map.put("lj_charmm_simulation_temperature", session.getSessionParameter().temperature);
            map.put("lj_scaling_factor_eps", pair.lambda_epsilon);
            map.put("lj_scaling_factor_sig", pair.lambda_sigma);
            map.put("lj_ti_lambda_window_size_electrostatic", run.lambda_size_electrostatic);
            map.put("lj_ti_lambda_window_size_vdw", run.lambda_size_vdw);

            map.put("reference_solvation_energy", session.getSessionParameter().expectedDeltaG);
            map.put("reference_liquid_density", session.getSessionParameter().expectedDensity);
            map.put("reference_vaporization_enthalpy", session.getSessionParameter().expectedDeltaH);

            list = list.append(new StartDefinition(
                    "ljfit",
                    map,
                    runDir.getDirectory(),
                    files.listFiles(),
                    Option.none(),
                    Option.of((json) -> {
                        var charmmResult = new LjFitJsonResult(json.get());
                        var runResult = new LjFitRunResult(session, input, charmmResult);
                        writeToJson(runDir.getRunOutputJson(), runResult);
                    }),
                    false));
        }
        return list;
    }

    private void writeToJson(File file, Object object) {
        String json = serializer.toJson(object);
        try {
            FileUtils.write(file, json, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException("Failed to write result json "+ file);
        }
    }
}

