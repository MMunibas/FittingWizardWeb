/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.gaussian.base;

import ch.unibas.fitting.shared.charmm.generate.CHARMM_InOut;
import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Generator_DGHydr;
import ch.unibas.charmmtools.gui.RunningCHARMM_DG;
import ch.unibas.charmmtools.gui.RunningCHARMM_DenVap;
import ch.unibas.charmmtools.gui.database.DB_View_Edit_add;
import ch.unibas.charmmtools.gui.loadOutput.CHARMM_GUI_LoadOutput;
import ch.unibas.charmmtools.gui.step1.mdAssistant.CHARMM_GUI_InputAssistant;
import ch.unibas.charmmtools.gui.step2.showOutput.CHARMM_GUI_ShowOutput;
import ch.unibas.charmmtools.gui.step3.showResults.CHARMM_GUI_ShowResults;
import ch.unibas.charmmtools.gui.step4.ParGrid.CHARMM_GUI_Fitgrid;
import ch.unibas.charmmtools.gui.topology.GenerateTopology;
import ch.unibas.charmmtools.scripts.CHARMMScript_DG_gas;
import ch.unibas.charmmtools.scripts.CHARMMScript_DG_solvent;
import ch.unibas.charmmtools.scripts.CHARMMScript_Den_Vap;
import ch.unibas.charmmtools.scripts.ICHARMMScript;
import ch.unibas.charmmtools.workflows.RunCHARMMWorkflow;
import ch.unibas.fitting.shared.config.Settings;
import ch.unibas.fitting.shared.directories.CharmmOutputDir;
import ch.unibas.fitting.shared.directories.FitOutputDir;
import ch.unibas.fitting.shared.directories.XyzDirectory;
import ch.unibas.fitting.shared.workflows.gaussian.MoleculeCreator;
import ch.unibas.fittingwizard.WhereToGo;
import ch.unibas.fittingwizard.gaussian.Visualization;
import ch.unibas.fitting.shared.directories.MoleculesDir;
import ch.unibas.fitting.shared.fitting.FitRepository;
import ch.unibas.fitting.shared.molecules.MoleculeRepository;
import ch.unibas.fitting.shared.scripts.babel.IBabelScript;
import ch.unibas.fitting.shared.scripts.export.IExportScript;
import ch.unibas.fitting.shared.scripts.fitmtp.FitMtpInput;
import ch.unibas.fitting.shared.scripts.fitmtp.IFitMtpScript;
import ch.unibas.fitting.shared.scripts.fittab.IFittabScript;
import ch.unibas.fitting.shared.scripts.lra.ILRAScript;
import ch.unibas.fitting.shared.scripts.multipolegauss.IMultipoleGaussScript;
import ch.unibas.fitting.shared.scripts.vmd.IVmdDisplayScript;
import ch.unibas.fitting.shared.tools.FitOutputParser;
import ch.unibas.fitting.shared.tools.GaussianLogModifier;
import ch.unibas.fitting.shared.tools.LPunParser;
import ch.unibas.fitting.shared.tools.Notifications;
import ch.unibas.fitting.shared.charges.ChargesFileParser;
import ch.unibas.fitting.shared.workflows.ExportFitWorkflow;
import ch.unibas.fitting.shared.workflows.RunFitWorkflow;
import ch.unibas.fitting.shared.workflows.gaussian.RunGaussianWorkflow;
import ch.unibas.fitting.shared.workflows.RunVmdDisplayWorkflow;
import ch.unibas.fitting.shared.scripts.babel.RealBabelScript;
import ch.unibas.fitting.shared.scripts.export.RealExportScript;
import ch.unibas.fitting.shared.scripts.fitmtp.RealFitMtpScript;
import ch.unibas.fitting.shared.scripts.fittab.RealFittabMarkerScript;
import ch.unibas.fitting.shared.scripts.lra.RealLRAScript;
import ch.unibas.fitting.shared.scripts.multipolegauss.RealMultipoleGaussScript;
import ch.unibas.fittingwizard.scripts.RealVmdDisplayScript;
import ch.unibas.fitting.shared.scripts.babel.MockBabelScript;
import ch.unibas.fitting.shared.scripts.export.MockExportScript;
import ch.unibas.fitting.shared.scripts.fitmtp.MockFitMtpScript;
import ch.unibas.fitting.shared.scripts.fittab.MockFittabMarkerScript;
import ch.unibas.fitting.shared.scripts.lra.MockLRAScript;
import ch.unibas.fitting.shared.scripts.multipolegauss.MockMultipoleGaussScript;
import ch.unibas.fittingwizard.gaussian.MoleculeListPage;
import ch.unibas.fittingwizard.gaussian.addmolecule.AtomChargesDto;
import ch.unibas.fittingwizard.gaussian.addmolecule.AtomTypeChargePage;
import ch.unibas.fittingwizard.gaussian.addmolecule.CoordinatesDto;
import ch.unibas.fittingwizard.gaussian.addmolecule.CoordinatesPage;
import ch.unibas.fittingwizard.gaussian.addmolecule.GaussCalculationDto;
import ch.unibas.fittingwizard.gaussian.addmolecule.GaussCalculationPage;
import ch.unibas.fittingwizard.gaussian.addmolecule.MultipoleGaussParameterDto;
import ch.unibas.fittingwizard.gaussian.addmolecule.MultipoleGaussParameterPage;
import ch.unibas.fittingwizard.gaussian.addmolecule.SelectCoordinateFilePage;
import ch.unibas.fittingwizard.gaussian.fitting.EditAtomTypeChargesDialog;
import ch.unibas.fittingwizard.gaussian.fitting.FitResultPage;
import ch.unibas.fittingwizard.gaussian.fitting.FittingParameterPage;
import ch.unibas.fittingwizard.gaussian.fitting.RunningFitPage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

/**
 * User: mhelmer Date: 26.11.13 Time: 11:31
 */
public class WizardPageFactory {

    private static final Logger logger = Logger.getLogger(WizardPageFactory.class);

    private Settings settings;
    private Visualization visualization;
    private Notifications notifications;
    private MoleculeRepository moleculeRepository;
    private FitRepository fitRepository;
    private DefaultValues defaultValues;
    private FitOutputDir fitOutputDir;
    private CharmmOutputDir charmmOutputDir;
    private MoleculesDir moleculesDir;
    private XyzDirectory xyzDirectory;
    private MoleculeCreator moleculeCreator;
    private LPunParser lPunParser;
    private EditAtomTypeChargesDialog editAtomTypeChargesDialog;

    private IMultipoleGaussScript gaussScript;
    private IBabelScript babelScript;
    private ILRAScript lraScript;
    private IFittabScript fittabMarkerScript;
    private IFitMtpScript fitMtpScript;
    private IExportScript exportScript;
    private IVmdDisplayScript vmdScript;
    private ICHARMMScript charmmScript_Den_Vap, charmmScript_DG_gas, charmmScript_DG_solvent, charmmScript_default;

    private RunFitWorkflow runFitWorkflow;
    private ExportFitWorkflow exportFitWorkflow;
    private RunGaussianWorkflow runGaussianWorkflow;
    private RunVmdDisplayWorkflow vmdDisplayWorkflow;
    private RunCHARMMWorkflow charmmWorkflow_Den_Vap, charmmWorkflow_DG, charmmWorkflow_other;

    public WizardPageFactory(Stage primaryStage) {
        initializeDependencies(primaryStage);
    }

    private void initializeDependencies(Stage primaryStage) {
        this.settings = Settings.loadConfig();
        File sessionDir = initializeCurrentSessionDirectory(settings.getDataDir());

        this.fitOutputDir = new FitOutputDir(sessionDir);
        this.charmmOutputDir = new CharmmOutputDir(sessionDir);
        this.moleculesDir = new MoleculesDir(settings.getMoleculeDir());
        this.xyzDirectory = new XyzDirectory(settings.getMoleculeDir());
        this.visualization = new Visualization(primaryStage);

        // TODO use repo to persist data into state dir
        this.moleculeRepository = new MoleculeRepository();
        this.fitRepository = new FitRepository(moleculeRepository);

        this.defaultValues = new DefaultValues(settings);
        // TODO fill from settings.
        notifications = new Notifications(settings);
//        notifications.sendTestMail();
//        notifications.sendLogMail();
        lPunParser = new LPunParser();
        moleculeCreator = new MoleculeCreator(lPunParser);
        initializeScripts();
        initializeWorkflows();

        editAtomTypeChargesDialog = new EditAtomTypeChargesDialog();
    }

    private void initializeScripts() {

        if (!settings.getMocksEnabled()) {
            // used to add molecules
            babelScript = new RealBabelScript();
            lraScript = new RealLRAScript(settings);
            fittabMarkerScript = new RealFittabMarkerScript(settings);

            // used for fitting
            fitMtpScript = new RealFitMtpScript(settings);
            exportScript = new RealExportScript(settings);
            vmdScript = new RealVmdDisplayScript(settings,
                    fitOutputDir.getFitMtpOutputDir(),
                    moleculesDir.getDirectory());
        } else {
            babelScript = new MockBabelScript(settings);
            lraScript = new MockLRAScript(settings);
            fittabMarkerScript = new MockFittabMarkerScript(settings);

            fitMtpScript = new MockFitMtpScript(settings);
            exportScript = new MockExportScript(settings);
        }

        if (settings.getUseGaussianMock()) {
            gaussScript = new MockMultipoleGaussScript(settings);
        } else {
            gaussScript = new RealMultipoleGaussScript(settings);
        }

        charmmScript_Den_Vap = new CHARMMScript_Den_Vap(charmmOutputDir, settings);
        charmmScript_DG_gas = new CHARMMScript_DG_gas(charmmOutputDir, settings);
        charmmScript_DG_solvent = new CHARMMScript_DG_solvent(charmmOutputDir, settings);
        charmmScript_default = new CHARMMScript_Den_Vap(charmmOutputDir, settings);
    }

    private void initializeWorkflows() {
        runFitWorkflow = new RunFitWorkflow(fitMtpScript,
                fitRepository,
                new ChargesFileParser(),
                new FitOutputParser());

        exportFitWorkflow = new ExportFitWorkflow(exportScript);

        runGaussianWorkflow = new RunGaussianWorkflow(gaussScript,
                babelScript,
                lraScript,
                fittabMarkerScript,
                new GaussianLogModifier(),
                notifications,
                moleculeCreator);

        vmdDisplayWorkflow = new RunVmdDisplayWorkflow(vmdScript, fitOutputDir.getFitMtpOutputDir());

        charmmWorkflow_Den_Vap = new RunCHARMMWorkflow(charmmScript_Den_Vap);
        charmmWorkflow_DG = new RunCHARMMWorkflow(charmmScript_DG_gas, charmmScript_DG_solvent);
        charmmWorkflow_other = new RunCHARMMWorkflow(charmmScript_default);
    }

    public <T extends WizardPage> WizardPage create(Class<T> type, Object parameter) {
        WizardPage page;
        logger.info("Type is : " + type.toString());
        try {
            if (type == MoleculeListPage.class) {
                page = new MoleculeListPage(visualization,
                        moleculeRepository,
                        fitRepository,
                        moleculesDir);
            } // ADD MOLECULE PAGES
            else if (type == SelectCoordinateFilePage.class) {
                File dto = (File) parameter;
                page = new SelectCoordinateFilePage(dto);
            } else if (type == CoordinatesPage.class) {
                CoordinatesDto dto = throwIfParameterIsNull(parameter);
                page = new CoordinatesPage(visualization, moleculesDir, dto);
            } else if (type == MultipoleGaussParameterPage.class) {
                MultipoleGaussParameterDto dto = throwIfParameterIsNull(parameter);
                page = new MultipoleGaussParameterPage(defaultValues,
                        moleculesDir,
                        xyzDirectory,
                        dto);
            } else if (type == AtomTypeChargePage.class) {
                AtomChargesDto dto = throwIfParameterIsNull(parameter);
                page = new AtomTypeChargePage(moleculeRepository,
                        moleculesDir,
                        xyzDirectory,
                        moleculeCreator,
                        lPunParser,
                        visualization,
                        dto);
            } else if (type == GaussCalculationPage.class) {
                GaussCalculationDto dto = throwIfParameterIsNull(parameter);
                page = new GaussCalculationPage(runGaussianWorkflow, dto);
//                page = new ProgressWithQstatTest();
            } // GAUSSIAN FITTING PAGES
            else if (type == FittingParameterPage.class) {
                FitMtpInput dto = (FitMtpInput) parameter;
                page = new FittingParameterPage(fitRepository,
                        moleculeRepository,
                        defaultValues,
                        moleculesDir,
                        fitOutputDir,
                        editAtomTypeChargesDialog,
                        dto);
            } else if (type == RunningFitPage.class) {
                FitMtpInput dto = throwIfParameterIsNull(parameter);
                page = new RunningFitPage(runFitWorkflow, dto);
            } else if (type == FitResultPage.class) {
                page = new FitResultPage(fitOutputDir,
                        moleculesDir,
                        moleculeRepository,
                        fitRepository,
                        visualization,
                        exportFitWorkflow,
                        vmdDisplayWorkflow);
            } // CHARMM FITTING PAGES
            else if (type == RunningCHARMM_DenVap.class) {
                List<CHARMM_InOut> ioList = throwIfParameterIsNull(parameter);
                page = new RunningCHARMM_DenVap(charmmWorkflow_Den_Vap, ioList);
            } else if (type == RunningCHARMM_DG.class) {
                List<CHARMM_Generator_DGHydr> dgList = throwIfParameterIsNull(parameter);
                page = new RunningCHARMM_DG(charmmWorkflow_DG, dgList);
            } else if (type == CHARMM_GUI_InputAssistant.class) {
                if (parameter == null) {
                    page = new CHARMM_GUI_InputAssistant(charmmWorkflow_Den_Vap);
                } else {
//                    List<CHARMM_InOut> ioList = throwIfParameterIsNull(parameter);
//                    page = new CHARMM_GUI_InputAssistant(charmmWorkflow_Den_Vap, ioList);
                    List<File> ioList = throwIfParameterIsNull(parameter);
                    page = new CHARMM_GUI_InputAssistant(charmmWorkflow_Den_Vap, ioList);
                }
            } else if (type == CHARMM_GUI_ShowOutput.class) {
                List<CHARMM_InOut> ioList = throwIfParameterIsNull(parameter);
                page = new CHARMM_GUI_ShowOutput(charmmWorkflow_Den_Vap, ioList);
            } else if (type == CHARMM_GUI_ShowResults.class) {
                List<CHARMM_InOut> ioList = throwIfParameterIsNull(parameter);
                page = new CHARMM_GUI_ShowResults(charmmWorkflow_Den_Vap, ioList);
            } else if (type == CHARMM_GUI_Fitgrid.class) {
                page = new CHARMM_GUI_Fitgrid(charmmWorkflow_other);
            } else if (type == DB_View_Edit_add.class) {
                page = new DB_View_Edit_add(settings);
            } else if (type == WhereToGo.class) {
                page = new WhereToGo();
            } else if (type == GenerateTopology.class) {
                page = new GenerateTopology(charmmWorkflow_other);
            } else if (type == CHARMM_GUI_LoadOutput.class) {
                page = new CHARMM_GUI_LoadOutput();
            } // MISC
            else {
                page = type.newInstance();
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not create instance of page " + type.getSimpleName(), e);
        }
        return page;
    }

    private <T> T throwIfParameterIsNull(Object parameter) {
        T casted = (T) parameter;
        if (casted == null) {
            throw new RuntimeException("Expected parameter object is null.");
        }
        return casted;
    }

    private File initializeCurrentSessionDirectory(File dataDir) {
        String sessionName = getSessionName();
        File sessionDir = new File(dataDir, sessionName);
        logger.info("Creating session directory " + sessionDir.getAbsolutePath());

        boolean directoryCreated = sessionDir.mkdirs();

        if (!directoryCreated) {
            throw new RuntimeException("Could not create session directory.");
        }

        return sessionDir;
    }

    private String getSessionName() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        return format.format(new Date());
    }

    public Settings getSettings() {
        return settings;
    }
}
