package ch.unibas.fitting.web.mtpfit.commands;

import ch.unibas.fitting.application.directories.IUserDirectory;
import ch.unibas.fitting.application.directories.MtpFitDir;
import ch.unibas.fitting.application.IAmACommand;
import ch.unibas.fitting.application.calculation.CalculationService;
import ch.unibas.fitting.web.calculation.NavigationInfo;
import ch.unibas.fitting.application.calculation.CalculationManagementClient;
import ch.unibas.fitting.application.calculation.manager.StartDefinition;
import ch.unibas.fitting.web.mtpfit.session.step4.ParameterPage;
import ch.unibas.fitting.web.mtpfit.session.step6.AtomTypesPage;
import ch.unibas.fitting.web.misc.PageNavigation;
import io.vavr.collection.Array;
import io.vavr.control.Option;

import javax.inject.Inject;
import java.io.File;
import java.util.HashMap;
import java.util.ArrayList;

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

    public void execute(String username,
                        String moleculeName,
                        String axisFileName,
                        Integer netCharge,
                        String quantum,
                        Integer nCores,
                        Integer multiplicity) {

        MtpFitDir mtpFitDir = userDir.getMtpFitDir(username);
        File moleculeFile = mtpFitDir.getMoleculeDir().getXyzFileFor(moleculeName);
        File fitDestination = mtpFitDir.getMoleculeDir().getSessionDir();
        File axisFile = new File(fitDestination,axisFileName);

        var fileArray = new ArrayList<>();
        fileArray.add(moleculeFile);
        fileArray.add(axisFile);

        var calculationId = calculationService.createCalculation();
        mtpFitDir.writeCalculationId(calculationId);

        var params = new HashMap<String, Object>();
        params.put("mtp_gen_filename_xyz", moleculeFile.getName());
        params.put("mtp_gen_molecule_charge", netCharge);
        params.put("mtp_gen_molecule_multiplicity", multiplicity);
        params.put("mtp_gen_gaussian_input_commandline", quantum);
        params.put("mtp_gen_gaussian_num_cores", nCores);
        params.put("dcm_axis_filename", axisFile.getName());

        File moleculeDestinationDir = mtpFitDir
                .getMoleculeDir()
                .createMoleculeDir(moleculeName);

        var response = client.spawnCalculationGroup(
                "Step1: Fit Atomic Charge Models to MEP",
                username,
                new NavigationInfo(
                        () -> PageNavigation.ToPageWithParameter(AtomTypesPage.class, "molecule_name", moleculeName),
                        () -> PageNavigation.ToPage(ParameterPage.class)),
                new StartDefinition(
                        "mtpfit_part1",
                        params,
                        moleculeDestinationDir,
                        fileArray.toArray(new File[0]),
                        Option.of(calculationId),
                        Option.none(),
                        true
                )
        );

        PageNavigation.ToProgressForCalculation(response);
    }
}
