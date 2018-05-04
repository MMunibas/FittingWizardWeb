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

public class StartDummyLjFitCommand {

    private static final String taskName = "DummyLjFit";
    private static final String demoDataBaseDir = "D:\\_projects\\unibas\\FittingWizardWeb\\fitting\\data\\debugging-mode\\lj_fit\\uploaded\\";
    @Inject
    private CalculationManagementClient calculationManagement;

    public void execute(String username) {

        var startResponse = calculationManagement.spawnTask(
                taskName,
                username,
                new NavigationInfo(Option.of(OverviewPage.class)),
                createDummyLjFitStartDefinition("calc1",
                        "benzonitrile.rtf", "benzonitrile.pdb", "benzonitrile.par", "fit_4_benzonitrile.lpun", "solvent.pdb","pureliquid.pdb", 0.0, 1.0, 298.3,
                        0.1, 1.0, 1.0),
                createDummyLjFitStartDefinition("calc2",
                        "benzonitrile.rtf", "benzonitrile.pdb", "benzonitrile.par", "fit_4_benzonitrile.lpun", "solvent.pdb","pureliquid.pdb", 0.0, 1.0, 298.3,
                        0.2, 2.0, 2.0),
                createDummyLjFitStartDefinition("calc3",
                        "benzonitrile.rtf", "benzonitrile.pdb", "benzonitrile.par", "fit_4_benzonitrile.lpun", "solvent.pdb","pureliquid.pdb", 0.0, 1.0, 298.3,
                        0.3, 3.0, 3.0),
                createDummyLjFitStartDefinition("calc4",
                        "benzonitrile.rtf", "benzonitrile.pdb", "benzonitrile.par", "fit_4_benzonitrile.lpun", "solvent.pdb","pureliquid.pdb", 0.0, 1.0, 298.3,
                        0.4, 4.0, 4.0),
                createDummyLjFitStartDefinition("calc5",
                        "benzonitrile.rtf", "benzonitrile.pdb", "benzonitrile.par", "fit_4_benzonitrile.lpun", "solvent.pdb","pureliquid.pdb", 0.0, 1.0, 298.3,
                        0.5, 5.0, 5.0)
        );
        PageNavigation.ToProgressForTask(startResponse.taskId);
    }

    private StartDefinition createDummyLjFitStartDefinition(
            String title,
            String top, String slu, String par, String lpun, String slv, String pureliq, double lmb0, double lmb1, double T,
            double dlmb, double epsfac, double sigfac) {

        var algorithmName = "dummy_ljfit";

        Map<String, Object> params = new HashMap<>();
        // calc params
        params.put("top", top);
        params.put("slu", slu);
        params.put("par", par);
        params.put("lpun", lpun);
        params.put("slv", slv);
        params.put("pureliq", pureliq);
        params.put("lmb0", lmb0);
        params.put("lmb1", lmb1);
        params.put("T", T);

        // run params
        params.put("dlmb", dlmb);
        params.put("epsfac", epsfac);
        params.put("sigfac", sigfac);

        var fileArray = new File[] {
                new File(demoDataBaseDir+"benzonitrile.top"),
                new File(demoDataBaseDir+"benzonitrile.pdb"),
                new File(demoDataBaseDir+"benzonitrile.par"),
                new File(demoDataBaseDir+"fit_4_benzonitrile.lpun"),
                new File(demoDataBaseDir+"solvent.pdb"),
                new File(demoDataBaseDir+"pureliquid.pdb"),
                new File(demoDataBaseDir+"benzonitrile.rtf"),
        };

        return new StartDefinition(algorithmName, params, title, new File("d:\\temp\\downloadData\\"), fileArray);
    }
}
