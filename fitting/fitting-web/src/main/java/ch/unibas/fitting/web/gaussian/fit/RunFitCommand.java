package ch.unibas.fitting.web.gaussian.fit;

import ch.unibas.fitting.shared.charges.ChargesFileGenerator;
import ch.unibas.fitting.shared.charges.ChargesFileParser;
import ch.unibas.fitting.shared.directories.FitOutputDir;
import ch.unibas.fitting.shared.directories.IUserDirectory;
import ch.unibas.fitting.shared.fitting.ChargeValue;
import ch.unibas.fitting.shared.fitting.Fit;
import ch.unibas.fitting.shared.fitting.InitialQ00;
import ch.unibas.fitting.shared.fitting.OutputAtomType;
import ch.unibas.fitting.shared.molecules.Molecule;
import ch.unibas.fitting.shared.scripts.fitmtp.FitMtpInput;
import ch.unibas.fitting.shared.scripts.fitmtp.FitMtpOutput;
import ch.unibas.fitting.shared.scripts.fitmtp.IFitMtpScript;
import ch.unibas.fitting.shared.tools.FitOutputParser;
import ch.unibas.fitting.web.application.IAmACommand;
import ch.unibas.fitting.web.application.IBackgroundTasks;
import ch.unibas.fitting.web.application.TaskHandle;
import ch.unibas.fitting.web.gaussian.FitUserRepo;
import ch.unibas.fitting.web.gaussian.MoleculeUserRepo;
import ch.unibas.fitting.web.gaussian.addmolecule.step1.OverviewPage;
import ch.unibas.fitting.web.gaussian.fit.step1.ParameterPage;
import ch.unibas.fitting.web.gaussian.fit.step2.FittingResultsPage;

import javax.inject.Inject;
import java.io.File;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

/**
 * Created by mhelmer-mobile on 19.06.2016.
 */
public class RunFitCommand implements IAmACommand {
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
    private MoleculeUserRepo userRepo;
    @Inject
    private ChargesFileParser chargesFileParser;
    @Inject
    private FitOutputParser fitOutputParser;

    public UUID runFit(String username,
                       double convergence,
                       int rank,
                       boolean ignoreHydrogens,
                       LinkedHashSet<ChargeValue> chargeValues) {

        FitOutputDir fitOutputDir = userDirectory.getFitOutputDir(username);
        // generate charges file
        File chargesFile = chargesFileGenerator.generate(fitOutputDir.getDirectory(),
                "generated_charges.txt",
                chargeValues);

        List<Molecule> molesForFit = userRepo.loadAll(username);

        FitMtpInput input = new FitMtpInput(
                userDirectory.getMoleculesDir(username),
                fitOutputDir,
                fitRepo.getNextFitId(username),
                convergence,
                rank,
                ignoreHydrogens,
                chargesFile,
                molesForFit);

        TaskHandle output = tasks.execute(username,
                "MTP Fit",
                () -> {
                    FitMtpOutput result = fitScript.execute(input);

                    double rmse = fitOutputParser.parseRmseValue(result.getOutputFile());

                    List<OutputAtomType> outputAtomTypes = chargesFileParser.parseOutputFile(result.getResultsFile());
                    File intialQsFile = input.getInitalChargesFile();

                    InitialQ00 initialQs = new ChargesFileParser().parseInitalCharges(intialQsFile);
                    // todo verify initial charges vs generated output

                    Fit fit = fitRepo.createFit(username, rmse, input.getRank(), outputAtomTypes, initialQs);

                    return fit;
                },
                (fit, pp) -> {
                    pp.add("fit_id", fit.getId());
                    return FittingResultsPage.class;
                }, ParameterPage.class);
        return output.getId();
    }
}
