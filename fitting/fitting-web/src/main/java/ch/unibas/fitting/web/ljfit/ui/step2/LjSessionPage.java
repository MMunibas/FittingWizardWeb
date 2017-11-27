package ch.unibas.fitting.web.ljfit.ui.step2;

import ch.unibas.fitting.shared.workflows.ljfit.LjFitRun;
import ch.unibas.fitting.web.ljfit.services.LjFitRepository;
import ch.unibas.fitting.web.ljfit.ui.step1.CreateNewSessionPage;
import ch.unibas.fitting.web.ljfit.ui.step2.clusterparams.ClusterParameterPanel;
import ch.unibas.fitting.web.ljfit.ui.step2.clusterparams.ClusterParameterViewModel;
import ch.unibas.fitting.web.ljfit.ui.step2.run.RunLjFitsCommand;
import ch.unibas.fitting.web.ljfit.ui.step3.ViewFilesPage;
import ch.unibas.fitting.web.web.HeaderPage;
import io.vavr.collection.List;
import io.vavr.control.Option;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import javax.inject.Inject;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class LjSessionPage extends HeaderPage {

    @Inject
    private LjFitRepository ljFitRepository;
    @Inject
    private RunLjFitsCommand runLjFitsCommand;

    private ListModel<SingleRunResult> runResults = new ListModel<>();

    private GridRunPanel gridRunSetup;
    private SingleRunPanel singleRunSetup;
    private ClusterParameterPanel clusterParameterPanel;

    private ClusterParameterViewModel clusterParameter;

    public LjSessionPage() {
        this.clusterParameter = new ClusterParameterViewModel(8, "beethoven");

        add(new AjaxLink("newSession") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(CreateNewSessionPage.class);
            }
        });

        ModalWindow clusterWindow = new ModalWindow("clusterParameterWindow");
        clusterWindow.showUnloadConfirmation(false);
        clusterWindow.setAutoSize(true);
        clusterWindow.setContent(new ClusterParameterPanel(
                clusterWindow.getContentId(),
                clusterWindow,
                clusterParameter
        ));
        clusterWindow.setCloseButtonCallback(target -> true);
        add(clusterWindow);

        add(new AjaxLink("showClusterParameter") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                LOGGER.debug("opening single run dialog");
                clusterWindow.show(target);
            }
        });

        GridPanelParameter gridPanelParameter = new GridPanelParameter(1, 0.1,1,0.1);
        ModalWindow gridRunDialogue = new ModalWindow("gridRunModalWindow");
        gridRunDialogue.showUnloadConfirmation(false);
        gridRunDialogue.setAutoSize(true);
        gridRunSetup = new GridRunPanel(
                gridRunDialogue.getContentId(),
                gridPanelParameter,
                getCurrentUsername(),
                runLjFitsCommand,
                clusterParameter);
        gridRunDialogue.setContent(gridRunSetup);
        gridRunDialogue.setCloseButtonCallback(target -> true);
        add(gridRunDialogue);
        add(new AjaxLink("gridRunValues") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                LOGGER.debug("opening grid run dialogue");
                gridRunDialogue.show(target);
            }
        });

        ModalWindow singleRunDialogue = new ModalWindow("singleRunModalWindow");
        singleRunSetup = new SingleRunPanel(
                singleRunDialogue.getContentId(),
                getCurrentUsername(),
                runLjFitsCommand,
                clusterParameter);
        singleRunDialogue.setContent(singleRunSetup);
        singleRunDialogue.showUnloadConfirmation(false);
        singleRunDialogue.setCloseButtonCallback(target -> true);
        add(singleRunDialogue);

        add(new AjaxLink("singleRunValues") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                LOGGER.debug("opening single run dialog");
                singleRunDialogue.show(target);
            }
        });

        add(new ListView("listview", runResults) {
            protected void populateItem(ListItem item) {
                SingleRunResult singleResult = (SingleRunResult) item.getModelObject();

                item.add(new AjaxLink("resultPageLink") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        PageParameters pp = new PageParameters();
                        pp.add("run_dir", singleResult.getDirName());
                        setResponsePage(ViewFilesPage.class, pp);
                    }
                });

                if (singleResult.hasLowestScore())
                    item.add(new AttributeAppender("style", "font-weight:bold;"));
                if (!singleResult.wasSuccessful)
                    item.add(new AttributeAppender("style", "background-color:red;"));

                NumberFormat oneDecimal = new DecimalFormat("0.0");
                NumberFormat twoDecimal = new DecimalFormat("0.00");
                NumberFormat threeDecimal = new DecimalFormat("0.000");

                item.add(new Label("eps", twoDecimal.format(singleResult.get_eps())));
                item.add(new Label("sigma", twoDecimal.format(singleResult.get_sigma())));
                item.add(new Label("score", oneDecimal.format(singleResult.get_Score())));
                item.add(new Label("deltaG", oneDecimal.format(singleResult.get_deltaG())));
                item.add(new Label("deltaH", oneDecimal.format(singleResult.get_deltaH())));
                item.add(new Label("density", threeDecimal.format(singleResult.get_density())));
                item.add(new Label("calcdeltaG", twoDecimal.format(singleResult.get_calcdeltaG())));
                item.add(new Label("expdeltaG", twoDecimal.format(singleResult.get_expdeltaG())));
                item.add(new Label("calcdeltaH", twoDecimal.format(singleResult.get_calcdeltaH())));
                item.add(new Label("expdeltaH", twoDecimal.format(singleResult.get_expdeltaH())));
                item.add(new Label("calcdensity", twoDecimal.format(singleResult.get_calcdensity())));
                item.add(new Label("expdensity", twoDecimal.format(singleResult.get_expdensity())));
                item.add(new Label("VDWGAS", twoDecimal.format(singleResult.get_VDWGAS())));
                item.add(new Label("ELEGAS", twoDecimal.format(singleResult.get_MTPGAS())));
                item.add(new Label("ELESOL", twoDecimal.format(singleResult.get_MTPSOL())));
                item.add(new Label("VDWSOL", twoDecimal.format(singleResult.get_VDWSOL())));
                item.add(new Label("GASTOTAL", twoDecimal.format(singleResult.get_GASTOTAL())));
                item.add(new Label("SOLTOTAL", twoDecimal.format(singleResult.get_SOLTOTAL())));

                item.add(new AjaxLink("deleteRun") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        ljFitRepository.deleteRunDir(getCurrentUsername(), singleResult.getDirName());
                        setResponsePage(LjSessionPage.class);
                    }
                });
            }
        });
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        List<LjFitRun> all = ljFitRepository.listRuns(getCurrentUsername());

        Option<Double> minScore = all.flatMap(r -> r.result)
                .map(r -> r.score)
                .min();

        List<SingleRunResult> runs =  all
                .map(run -> new SingleRunResult(run, minScore))
                .toList();

        ljFitRepository.loadSessionForUser(getCurrentUsername())
                .flatMap(ljFitSession -> {
                    gridRunSetup.lambda.setObject(ljFitSession.getSessionParameter().lambdaSpacing);
                    singleRunSetup.lambda.setObject(ljFitSession.getSessionParameter().lambdaSpacing);
                   return null;
                });

        runResults.setObject(runs.toJavaList());
    }
}



