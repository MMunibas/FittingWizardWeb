package ch.unibas.fitting.web.calculation.commands;

import ch.unibas.fitting.web.calculation.NavigationInfo;
import ch.unibas.fitting.web.calculation.OverviewPage;
import ch.unibas.fitting.web.calculation.management.CalculationManagementClient;
import ch.unibas.fitting.web.calculation.management.execution.messages.StartDefinition;
import ch.unibas.fitting.web.web.PageNavigation;
import io.vavr.control.Option;

import javax.inject.Inject;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class StartDummyAlgoCommand {

    private static final String taskName = "DummyAlgorithm";
    @Inject
    private CalculationManagementClient calculationManagement;

    public void execute(String username) {

        var startResponse = calculationManagement.spawnTask(
                taskName,
                username,
                new NavigationInfo(Option.of(OverviewPage.class)),
                createDummyAlgorithmStartDefinition(40, "calc1"),
                createDummyAlgorithmStartDefinition(41, "calc2"),
                createDummyAlgorithmStartDefinition(42, "calc3"),
                createDummyAlgorithmStartDefinition(43, "calc4"),
                createDummyAlgorithmStartDefinition(44, "calc5")
        );
        PageNavigation.ToProgressForCalculation(startResponse);
    }

    private StartDefinition createDummyAlgorithmStartDefinition(double param, String title){
        var algorithmName = "dummy_algorithm";
        Map<String, Object> params = new HashMap<>();
        params.put("someparam", param);
        var fileArray = new File[] {
                new File("C:\\Users\\eknecht\\Desktop\\somefile.json")
        };

        return new StartDefinition(algorithmName, params, new File("d:\\temp\\downloadData\\"), fileArray);
    }
}
