package ch.unibas.fitting.web.gaussian.addmolecule;

import ch.unibas.fitting.web.application.directories.IUserDirectory;
import ch.unibas.fitting.web.application.directories.MtpFitDir;
import ch.unibas.fitting.web.application.IAmACommand;
import ch.unibas.fitting.web.application.calculation.CalculationService;
import ch.unibas.fitting.web.application.task.PageContext;
import ch.unibas.fitting.web.calculation.NavigationInfo;
import ch.unibas.fitting.web.application.calculation.CalculationManagementClient;
import ch.unibas.fitting.web.application.calculation.manager.StartDefinition;
import ch.unibas.fitting.web.gaussian.addmolecule.step4.ParameterPage;
import ch.unibas.fitting.web.gaussian.addmolecule.step6.AtomTypesPage;
import ch.unibas.fitting.web.web.PageNavigation;
import io.vavr.collection.Array;
import io.vavr.control.Option;

import javax.inject.Inject;
import java.io.File;
import java.util.HashMap;

/**
 * Created by mhelmer-mobile on 17.06.2016.
 */

public class RunMtpGenerateFilesCommand implements IAmACommand {

    @Inject
    private IUserDirectory userDir;

    @Inject
    private CalculationManagementClient client;
    @Inject
    private CalculationService calculationService;


    public void executeNew(String username,
                           String moleculeName,
                           PageContext pageContext,
                           Integer netCharge,
                           String quantum,
                           Integer nCores,
                           Integer multiplicity) {

        MtpFitDir mtpFitDir = userDir.getMtpFitDir(username);
        File moleculeFile = mtpFitDir.getMoleculeDir().getXyzFileFor(moleculeName);

        var calculationId = calculationService.createCalculation();
        mtpFitDir.writeCalculationId(calculationId);

        var params = new HashMap<String, Object>();
        params.put("mtp_gen_filename_xyz", moleculeFile.getName());
        params.put("mtp_gen_molecule_charge", netCharge);
        params.put("mtp_gen_molecule_multiplicity", multiplicity);
        params.put("mtp_gen_gaussian_input_commandline", quantum);
        params.put("mtp_gen_gaussian_num_cores", nCores);

        File moleculeDestinationDir = mtpFitDir
                .getMoleculeDir()
                .createMoleculeDir(moleculeName);

        var response = client.spawnTask(
                "Generate MTP files",
                username,
                new NavigationInfo(
                        () -> PageNavigation.ToPageWithParameter(AtomTypesPage.class, "molecule_name", moleculeName),
                        () -> PageNavigation.ToPage(ParameterPage.class),
                        pageContext),
                new StartDefinition(
                        "mtpfit_part1",
                        params,
                        moleculeDestinationDir,
                        Array.of(moleculeFile).toJavaArray(File.class),
                        Option.of(calculationId),
                        Option.none(),
                        true
                )
        );

        PageNavigation.ToProgressForCalculation(response);
    }
}
