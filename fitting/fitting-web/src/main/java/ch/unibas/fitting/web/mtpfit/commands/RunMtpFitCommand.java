package ch.unibas.fitting.web.mtpfit.commands;

import ch.unibas.fitting.application.directories.FitOutputDir;
import ch.unibas.fitting.application.directories.IUserDirectory;
import ch.unibas.fitting.application.directories.MtpFitDir;
import ch.unibas.fitting.application.algorithms.mtpfit.ChargeValue;
import ch.unibas.fitting.application.algorithms.mtpfit.Fit;
import ch.unibas.fitting.application.algorithms.mtpfit.InitialQ00;
import ch.unibas.fitting.application.algorithms.ljfit.CreateFit;
import ch.unibas.fitting.application.IAmACommand;
import ch.unibas.fitting.application.algorithms.mtpfit.MtpResultsParser;
import ch.unibas.fitting.application.calculation.CalculationManagementClient;
import ch.unibas.fitting.application.calculation.manager.StartDefinition;
import ch.unibas.fitting.web.calculation.NavigationInfo;
import ch.unibas.fitting.web.mtpfit.fitting.step1.MtpFitSessionPage;
import ch.unibas.fitting.web.mtpfit.fitting.step2.FittingResultsPage;
import ch.unibas.fitting.application.algorithms.mtpfit.MtpFitSessionRepository;
import ch.unibas.fitting.web.misc.PageNavigation;
import io.vavr.control.Option;

import javax.inject.Inject;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashSet;

public class RunMtpFitCommand implements IAmACommand {
    @Inject
    private IUserDirectory userDirectory;

    @Inject
    private CreateFit createFit;
    @Inject
    private MtpFitSessionRepository mtpFitRepo;

    @Inject
    MtpResultsParser parser;
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

        var initalCharges = createCharges(chargeValues);

        var params = new HashMap<String, Object>();
        params.put("mtp_fitting_table_filename", "mtpfittab.txt");
        params.put("mtp_fitting_initial_charges", initalCharges);

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
                        new File[0],
                        Option.of(calcId),
                        Option.of(json -> {

                            var molecules = mtpFitDir.getMoleculeDir().listAllMoleculeNames();

                            var rmse = parser.getRmseValue(json.get());
                            var types = parser.getATomTypes(json.get());

                            Fit fit = createFit.createFit(
                                    fitOutputDir.getId(),
                                    rank,
                                    rmse,
                                    types,
                                    new InitialQ00(chargeValues),
                                    molecules);

                            mtpFitRepo.saveFitResult(username, fit);
                        }),
                        true
                )
        );

        PageNavigation.ToProgressForCalculation(response);
    }

    private HashMap<String, Double> createCharges(LinkedHashSet<ChargeValue> chargeValues) {
        var map = new HashMap<String, Double>();
        for (ChargeValue chargeLine : chargeValues) {
            map.put(String.format("%s_%s", chargeLine.getAtomType(), chargeLine.getMultipoleComponent()), chargeLine.getValue());
        }
        return map;
    }
}
