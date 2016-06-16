/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.gaussian.fitting;

import ch.unibas.fitting.shared.fitting.ChargeValue;
import ch.unibas.fitting.shared.fitting.FitRepository;
import ch.unibas.fitting.shared.molecules.*;
import ch.unibas.fitting.shared.scripts.fitmtp.FitMtpInput;
import ch.unibas.fitting.shared.charges.ChargesFileGenerator;
import ch.unibas.fittingwizard.gaussian.MoleculeListPage;
import ch.unibas.fittingwizard.gaussian.base.ButtonFactory;
import ch.unibas.fittingwizard.gaussian.base.DefaultValues;
import ch.unibas.fittingwizard.gaussian.base.WizardPage;
import ch.unibas.fittingwizard.gaussian.base.dialog.OverlayDialog;
import java.io.File;
import java.util.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.apache.commons.lang.NotImplementedException;

/**
 * User: mhelmer
 * Date: 29.11.13
 * Time: 17:14
 */
public class FittingParameterPage extends WizardPage {

    private final File sessionDir;
    private final EditAtomTypeChargesDialog editAtomTypeChargesDialog;
    private final FitRepository fitRepository;
    private final MoleculeRepository moleculeRepository;

    @FXML
    private CheckBox chkIgnoreHydrogen;
    @FXML
    private TextField txtConvergence;
    @FXML
    private ComboBox<RankItem> cmbRank;

    public FittingParameterPage(FitRepository fitRepository,
                                MoleculeRepository moleculeRepository,
                                DefaultValues defaultValues,
                                File sessionDir,
                                EditAtomTypeChargesDialog editAtomTypeChargesDialog,
                                FitMtpInput fitMtpInput) {
        super("Fitting parameters");
        this.fitRepository = fitRepository;
        this.moleculeRepository = moleculeRepository;
        this.sessionDir = sessionDir;
        this.editAtomTypeChargesDialog = editAtomTypeChargesDialog;

        setupComboBox();
        if (fitMtpInput != null) {
            setupInitialValues(fitMtpInput.getRank(),
                    fitMtpInput.getConvergence(),
                    fitMtpInput.isIgnoreHydrogen());
        } else {
            setupInitialValues(defaultValues.getRank(),
                    defaultValues.getMonopoleConvergence(),
                    defaultValues.getIgnoreHydrogen());
        }
    }

    private void setupComboBox() {
        // TODO improve http://stackoverflow.com/questions/13368572/rendering-a-pojo-with-javafx-2s-combo-box-without-overriding-the-tostring-met
        cmbRank.getItems().clear();
        cmbRank.getItems().add(new RankItem("Point charges", 0));
        cmbRank.getItems().add(new RankItem("Point charges and dipoles", 1));
        cmbRank.getItems().add(new RankItem("Point charges, dipoles and quadrupoles", 2));
        cmbRank.autosize();
    }

    private void setupInitialValues(int rank, double convergence, boolean hydrogen) {
        RankItem itemToSelect = null;
        for (RankItem rankItem : cmbRank.getItems()) {
            if (rankItem.getRankValue() == rank) {
                itemToSelect = rankItem;
                break;
            }
        }
        cmbRank.getSelectionModel().select(itemToSelect);

        txtConvergence.setText(String.valueOf(convergence));
        chkIgnoreHydrogen.setSelected(hydrogen);
    }

    @Override
    protected void fillButtonBar() {
        Button backButton = ButtonFactory.createButtonBarButton("Go back to molecule list", new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                logger.info("Going back to molecule list.");
                navigateTo(MoleculeListPage.class);
            }
        });
        addButtonToButtonBar(backButton);

        Button startButton = ButtonFactory.createButtonBarButton("Start fitting", new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                logger.info("Starting fit.");

                MoleculeQueryService queryService = moleculeRepository.getQueryServiceForAllMolecules();
                File initalCharges = getInitalCharges(queryService);
                if (initalCharges != null) {
                    navigateTo(RunningFitPage.class, createFittingParameter(initalCharges, queryService.getMoleculeIds()));
                }
            }
        });
        addButtonToButtonBar(startButton);
    }

    private File getInitalCharges(MoleculeQueryService queryService) {
        LinkedHashSet<ChargeValue> userCharges = new LinkedHashSet<>();
        LinkedHashSet<AtomTypeId> atomTypesRequiringUserInput = new LinkedHashSet<>();

        List<Molecule> moleculesWithMissingUserCharges = queryService.findMoleculesWithMissingUserCharges();
        atomTypesRequiringUserInput.addAll(getAllAtomTypeIds(moleculesWithMissingUserCharges));

        boolean multipleMoleculesDefined = queryService.getNumberOfMolecules() > 1;
        if (multipleMoleculesDefined) {
            List<AtomTypeId> duplicates = queryService.findUnequalAndDuplicateAtomTypes();
            atomTypesRequiringUserInput.addAll(duplicates);
        }

        if (atomTypesRequiringUserInput.size() > 0) {
            LinkedHashSet<ChargeValue> editedValues = editAtomTypeChargesDialog.editAtomTypes(atomTypesRequiringUserInput);
            if (editedValues == null) {
                // TODO ... no nested return
                return null;
            }
            userCharges.addAll(editedValues);
        }

        // fill up with all other values in order to generate a correct charges file.
        // due to the set, the already edited values will not be replaced.
        LinkedHashSet<ChargeValue> allCharges = queryService.getUserChargesFromMoleculesWithCharges();
        for (ChargeValue charge : allCharges) {
            if (!userCharges.contains(charge)) {
                userCharges.add(charge);
            }
        }

        File initalChargesFile = generateInitialChargesFileFromUserCharges(userCharges);
        return initalChargesFile;
    }

    private LinkedHashSet<AtomTypeId> getAllAtomTypeIds(List<Molecule> molecules) {
        LinkedHashSet<AtomTypeId> allIds = new LinkedHashSet<>();
        for (Molecule molecule : molecules) {
            allIds.addAll(molecule.getAllAtomTypeIds());
        }
        return allIds;
    }

    private File generateInitialChargesFileFromUserCharges(LinkedHashSet<ChargeValue> chargeValues) {
        File chargesFile = new File(sessionDir, "output");
        File generatedFile = new ChargesFileGenerator().generate(chargesFile, "generated_charges.txt", chargeValues);
        return generatedFile;
    }

    private File generateInitalValuesFromScript() {
        // TODO generate inital values by script
        OverlayDialog.showError("Not implemented yet.", "This functionality is not yet implemented yet.");
        throw new NotImplementedException();
//        File chargesFile = new File(sessionDir, "output");
//        chargesFile = new File(chargesFile, "co2_t_charges.txt");
//
//        return chargesFile;
    }

    @Override
    public void initializeData() {
    }

    private FitMtpInput createFittingParameter(File initalChargesFile, List<MoleculeId> moleculesForFit) {
        try {
            double convergence = Double.parseDouble(txtConvergence.getText());
            int rank = cmbRank.getSelectionModel().getSelectedItem().getRankValue();
            boolean ignoreHydrongen = chkIgnoreHydrogen.isSelected();
            int id = fitRepository.getNextFitId();

            return new FitMtpInput(id, convergence, rank, ignoreHydrongen, initalChargesFile, moleculesForFit);
        } catch (Exception e) {
            OverlayDialog.showError("Invalid parameters", "The provided parameters are invalid.");
            throw e;
        }
    }

    private class RankItem {
        private final String title;
        private final int value;

        public RankItem(String title, int value) {
            this.title = title;
            this.value = value;
        }

        private String getTitle() {
            return title;
        }

        private int getRankValue() {
            return value;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
