package ch.unibas.fitting.web.ljfit.ui.step2;

import ch.unibas.fitting.web.ljfit.ui.step1.CreateNewSessionPage;
import ch.unibas.fitting.web.ljfit.ui.step3.ViewFilesPage;
import ch.unibas.fitting.web.web.HeaderPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;

public class LjSessionPage extends HeaderPage {

    private GridRunPanel gridRunSetup;
    private SingleRunPanel singleRunSetup;

    public LjSessionPage() {

        add(new AjaxLink("newSession") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(CreateNewSessionPage.class);
            }
        });
        GridPanelParameter gridPanelParameter = new GridPanelParameter(1, 0.1,1,0.2);
        EpsilonSigmaPair singlePair = new EpsilonSigmaPair(1.0,1.0, true);
        ModalWindow gridRunDialogue = new ModalWindow("gridRunModalWindow");
        gridRunDialogue.setAutoSize(true);
        gridRunSetup = new GridRunPanel(gridRunDialogue.getContentId(), gridRunDialogue, gridPanelParameter);
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
        singleRunSetup = new SingleRunPanel(singleRunDialogue.getContentId(), singleRunDialogue, singlePair);
        singleRunDialogue.setContent(singleRunSetup);
        singleRunDialogue.setCloseButtonCallback(target -> true);
        add(singleRunDialogue);

        add(new AjaxLink("singleRunValues") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                LOGGER.debug("opening single run dialog");
                singleRunDialogue.show(target);
            }
        });

        List userList = Arrays.asList(
                new SingleRunResult[] {
                        new SingleRunResult(0.95, 0.95,-2.655,6.81544,-23.13735,-26.8116,7.83469,-16.32191,-18.97691,-7.03,17.0552,11.24,1.1184,1.22,19.140625,33.81655104,0.01032256,197.1631006),
                        new SingleRunResult(0.95, 0.95,-2.655,6.81544,-23.13735,-26.8116,7.83469,-16.32191,-18.97691,-7.03,17.0552,11.24,1.1184,1.22,19.140625,33.81655104,0.01032256,197.1631006),
                        new SingleRunResult(0.95, 0.95,-2.655,6.81544,-23.13735,-26.8116,7.83469,-16.32191,-18.97691,-7.03,17.0552,11.24,1.1184,1.22,19.140625,33.81655104,0.01032256,197.1631006)
                });

        add(new ListView("listview", userList) {
            protected void populateItem(ListItem item) {
                SingleRunResult singleResult = (SingleRunResult) item.getModelObject();
                item.add(new AjaxLink("resultPageLink") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        setResponsePage(ViewFilesPage.class);
                    }
                });

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
            }
        });
    }
}



