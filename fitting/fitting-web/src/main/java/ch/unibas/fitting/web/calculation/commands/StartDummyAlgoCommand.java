package ch.unibas.fitting.web.calculation.commands;

import ch.unibas.fitting.shared.config.Settings;
import ch.unibas.fitting.shared.directories.IUserDirectory;
import ch.unibas.fitting.web.calculation.NavigationInfo;
import ch.unibas.fitting.web.calculation.OverviewPage;
import ch.unibas.fitting.web.application.calculation.CalculationManagementClient;
import ch.unibas.fitting.web.application.calculation.manager.StartDefinition;
import ch.unibas.fitting.web.web.PageNavigation;
import org.apache.commons.io.FileUtils;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StartDummyAlgoCommand {

    @Inject
    private IUserDirectory userDirectory;
    @Inject
    private Settings settings;

    @Inject
    private CalculationManagementClient calculationManagement;

    public void execute(String username) {

        var output_dir = new File(userDirectory.getUserBaseDir(username), "dummy_algo_output");
        try {
            if (output_dir.isDirectory())
                FileUtils.deleteDirectory(output_dir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        output_dir.mkdirs();

        var startResponse = calculationManagement.spawnTask(
                "Running test algorithm",
                username,
                new NavigationInfo(() -> PageNavigation.ToPage(OverviewPage.class)),
                createDummyAlgorithmStartDefinition(40, output_dir),
                createDummyAlgorithmStartDefinition(41, output_dir),
                createDummyAlgorithmStartDefinition(42, output_dir),
                createDummyAlgorithmStartDefinition(43, output_dir),
                createDummyAlgorithmStartDefinition(44, output_dir)
        );
        PageNavigation.ToProgressForCalculation(startResponse);
    }

    private StartDefinition createDummyAlgorithmStartDefinition(int param, File destination){
        Map<String, Object> params = new HashMap<>();
        params.put("someparam", param);

        var final_output = new File(destination, String.valueOf(param));
        final_output.mkdirs();

        return new StartDefinition("dummy_algorithm",
                params,
                final_output,
                new File(settings.getTestdataDir(), "dummy_algo_input.json"));
    }
}
