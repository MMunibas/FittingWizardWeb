package ch.unibas.fitting.web.gaussian.fit;

import ch.unibas.fitting.shared.charges.ChargesFileGenerator;
import ch.unibas.fitting.shared.directories.FitOutputDir;
import ch.unibas.fitting.shared.directories.IUserDirectory;
import ch.unibas.fitting.shared.fitting.ChargeValue;
import ch.unibas.fitting.shared.fitting.Fit;
import ch.unibas.fitting.shared.fitting.InitialQ00;
import ch.unibas.fitting.shared.molecules.Molecule;
import ch.unibas.fitting.shared.scripts.fitmtp.FitMtpInput;
import ch.unibas.fitting.shared.scripts.fitmtp.FitMtpOutput;
import ch.unibas.fitting.shared.scripts.fitmtp.IFitMtpScript;
import ch.unibas.fitting.shared.workflows.gaussian.fit.CreateFit;
import ch.unibas.fitting.web.application.IAmACommand;
import ch.unibas.fitting.web.application.IBackgroundTasks;
import ch.unibas.fitting.web.application.PageContext;
import ch.unibas.fitting.web.application.TaskHandle;
import ch.unibas.fitting.web.gaussian.FitUserRepo;
import ch.unibas.fitting.web.gaussian.MoleculeUserRepo;
import ch.unibas.fitting.web.gaussian.fit.step1.ParameterPage;
import ch.unibas.fitting.web.gaussian.fit.step2.FittingResultsPage;
import ch.unibas.fitting.web.web.PageNavigation;
import io.vavr.control.Option;

import javax.inject.Inject;
import java.io.File;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

/**
 * Created by mhelmer-mobile on 19.06.2016.
 */
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
    private FitUserRepo fitRepo;
    @Inject
    private MoleculeUserRepo moleculeUserRepo;

    @Inject
    private CreateFit createFit;

    public void execute(String username,
                        PageContext context,
                        double convergence,
                        int rank,
                        boolean ignoreHydrogens,
                        LinkedHashSet<ChargeValue> chargeValues) {

        TaskHandle th = tasks.spawnTask(
                username,
                "MTP Fit",
                (ctx) -> {

                    int id = fitRepo.getNextFitId(username);

                    FitOutputDir fitOutputDir = userDirectory.getFitOutputDir(username);
                    fitOutputDir.removeFitResult(id);

                    File generatedCharges = chargesFileGenerator.generate(
                            fitOutputDir.getFitMtpOutputDir(),
                            "fit_" + id + "_generated_charges.txt",
                            chargeValues);

                    List<Molecule> molesForFit = moleculeUserRepo.loadAll(username);

                    FitMtpInput input = new FitMtpInput(
                            userDirectory.getMoleculesDir(username),
                            fitOutputDir,
                            id,
                            convergence,
                            rank,
                            ignoreHydrogens,
                            generatedCharges,
                            molesForFit);

                    FitMtpOutput result = fitScript.execute(input);

                    Fit fit = createFit.createFit(id,
                            rank,
                            result.getResultsFile(),
                            result.getOutputFile(),
                            new InitialQ00(chargeValues),
                            molesForFit);

                    fitRepo.save(username, fit);

                    return fit;
                },
                (fit, pp) -> {
                    pp.add("fit_id", fit.getId());
                    return FittingResultsPage.class;
                },
                ParameterPage.class,
                Option.of(context));

        PageNavigation.ToProgressForTask(th);
    }
}
