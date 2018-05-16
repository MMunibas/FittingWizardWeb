package ch.unibas.fitting.web.gaussian.fit;

import ch.unibas.fitting.shared.charges.ChargesFileGenerator;
import ch.unibas.fitting.shared.directories.FitOutputDir;
import ch.unibas.fitting.shared.directories.IUserDirectory;
import ch.unibas.fitting.shared.directories.MoleculesDir;
import ch.unibas.fitting.shared.directories.MtpFitDir;
import ch.unibas.fitting.shared.fitting.ChargeValue;
import ch.unibas.fitting.shared.fitting.Fit;
import ch.unibas.fitting.shared.fitting.InitialQ00;
import ch.unibas.fitting.shared.scripts.fitmtp.FitMtpInput;
import ch.unibas.fitting.shared.scripts.fitmtp.FitMtpOutput;
import ch.unibas.fitting.shared.scripts.fitmtp.IFitMtpScript;
import ch.unibas.fitting.shared.workflows.ExportFitInput;
import ch.unibas.fitting.shared.workflows.ExportFitWorkflow;
import ch.unibas.fitting.shared.workflows.gaussian.fit.CreateFit;
import ch.unibas.fitting.web.application.IAmACommand;
import ch.unibas.fitting.web.application.calculation.CalculationManagementClient;
import ch.unibas.fitting.web.application.calculation.execution.messages.StartDefinition;
import ch.unibas.fitting.web.application.task.IBackgroundTasks;
import ch.unibas.fitting.web.application.task.PageContext;
import ch.unibas.fitting.web.application.task.TaskHandle;
import ch.unibas.fitting.web.calculation.NavigationInfo;
import ch.unibas.fitting.web.gaussian.addmolecule.step4.ParameterPage;
import ch.unibas.fitting.web.gaussian.addmolecule.step6.AtomTypesPage;
import ch.unibas.fitting.web.gaussian.fit.step1.MtpFitSessionPage;
import ch.unibas.fitting.web.gaussian.fit.step2.FittingResultsPage;
import ch.unibas.fitting.web.gaussian.services.MtpFitSessionRepository;
import ch.unibas.fitting.web.web.PageNavigation;
import io.vavr.collection.Array;
import io.vavr.control.Option;

import javax.inject.Inject;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashSet;

public class RunMtpFitCommand implements IAmACommand {
    @Inject
    private IBackgroundTasks tasks;
    @Inject
    private IUserDirectory userDirectory;
    @Inject
    private IFitMtpScript fitScript;
    @Inject
    private ChargesFileGenerator chargesFileGenerator;

    @Inject
    private ExportFitWorkflow exportFit;
    @Inject
    private CreateFit createFit;
    @Inject
    private MtpFitSessionRepository mtpFitRepo;

    @Inject
    private CalculationManagementClient client;

    public void executeNew(String username,
                           double convergence,
                           int rank,
                           boolean ignoreHydrogens,
                           LinkedHashSet<ChargeValue> chargeValues) {

        MtpFitDir mtpFitDir = userDirectory.getMtpFitDir(username);
        String calcId = mtpFitDir.readCalculationId().get();

        FitOutputDir fitOutputDir = mtpFitDir.createNextFitOutputDir();

        File generatedCharges = chargesFileGenerator.generate(
                fitOutputDir.getDirectory(),
                "generated_charges.txt",
                chargeValues);

        var params = new HashMap<String, Object>();
        params.put("mtp_fitting_table_filename", "mtpfittab.txt");
        params.put("mtp_fitting_charge_filename", generatedCharges.getName());

        params.put("mtp_fitting_threshold", convergence);
        params.put("mtp_fitting_rank", rank);
        params.put("mtp_fitting_flag_ignore_H", ignoreHydrogens);
        params.put("mtp_fit_number", fitOutputDir.getId());

        var response = client.spawnTask(
                "Running MTP fit",
                username,
                new NavigationInfo(
                        () -> PageNavigation.ToPageWithParameter(FittingResultsPage.class, "fit_id", String.valueOf(fitOutputDir.getId())),
                        () -> PageNavigation.ToPage(MtpFitSessionPage.class)),
                new StartDefinition(
                        "mtpfit_part2",
                        params,
                        fitOutputDir.getDirectory(),
                        Array.of(generatedCharges.getAbsoluteFile()).toJavaArray(File.class),
                        Option.of(calcId),
                        Option.of(() -> {
                            var molecules = mtpFitDir.getMoleculeDir().listAllMoleculeNames();

                            Fit fit = createFit.createFit(
                                    fitOutputDir.getId(),
                                    rank,
                                    new File(fitOutputDir.getDirectory(), "fit_results.txt"),
                                    new File(fitOutputDir.getDirectory(), "output.txt"),
                                    new InitialQ00(chargeValues),
                                    molecules);

                            mtpFitRepo.saveFitResult(username, fit);
                        })
                )
        );

        PageNavigation.ToProgressForCalculation(response);
    }

    @Deprecated
    public void execute(String username,
                        double convergence,
                        int rank,
                        boolean ignoreHydrogens,
                        LinkedHashSet<ChargeValue> chargeValues) {

        TaskHandle th = tasks.spawnTask(
                username,
                "MTP Fit",
                (ctx) -> {

                    MtpFitDir mtpFit = userDirectory.getMtpFitDir(username);
                    FitOutputDir fitOutputDir = mtpFit.createNextFitOutputDir();

                    File generatedCharges = chargesFileGenerator.generate(
                            fitOutputDir.getDirectory(),
                            "generated_charges.txt",
                            chargeValues);

                    MoleculesDir moleculesDir = mtpFit.getMoleculeDir();
                    FitMtpInput input = new FitMtpInput(
                            fitOutputDir,
                            convergence,
                            rank,
                            ignoreHydrogens,
                            generatedCharges,
                            moleculesDir.listAllMtpFitTabFiles());

                    FitMtpOutput result = fitScript.execute(input);

                    Fit fit = createFit.createFit(
                            fitOutputDir.getId(),
                            rank,
                            result.getResultsFile(),
                            result.getOutputFile(),
                            new InitialQ00(chargeValues),
                            moleculesDir.listAllMoleculeNames());

                    ExportFitInput exportInput = new ExportFitInput(
                            fitOutputDir,
                            moleculesDir,
                            fit);
                    exportFit.execute(exportInput);

                    mtpFitRepo.saveFitResult(username, fit);

                    return fit;
                },
                (fit, pp) -> {
                    pp.add("fit_id", fit.getId());
                    return FittingResultsPage.class;
                },
                MtpFitSessionPage.class,
                Option.of(new PageContext(MtpFitSessionPage.class)));

        PageNavigation.ToProgressForTask(th);
    }
}
