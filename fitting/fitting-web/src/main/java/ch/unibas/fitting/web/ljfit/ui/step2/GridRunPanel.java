package ch.unibas.fitting.web.ljfit.ui.step2;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.GridView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.time.Duration;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;


public class GridRunPanel extends Panel {

    private List<EpsilonSigmaPair> epsilonSigmaPairCandidates = new ArrayList<>();

    public GridRunPanel(String id, ModalWindow window, GridPanelParameter gridPanelParameter) {
        super(id);

        epsilonSigmaPairCandidates.add(new EpsilonSigmaPair(1.0, 1.0, true));

        Form inputForm = new Form("inputForm");
        add(inputForm);

        NumberTextField numberEpsField = new NumberTextField<>("numberEps", new PropertyModel(gridPanelParameter, "number_eps"));
        numberEpsField.setStep(2);
        numberEpsField.setMinimum(1);
        numberEpsField.setRequired(true);
        inputForm.add(numberEpsField);

        TextField deltaEpsField = new TextField("deltaEps", new PropertyModel(gridPanelParameter, "delta_eps"));
        inputForm.add(deltaEpsField);

        NumberTextField numberSigmaField = new NumberTextField<>("numberSigma", new PropertyModel(gridPanelParameter, "number_sigma"));
        numberSigmaField.setStep(2);
        numberSigmaField.setMinimum(1);
        numberSigmaField.setRequired(true);
        inputForm.add(numberSigmaField);

        TextField deltaSigmaField = new TextField("deltaSigma", new PropertyModel(gridPanelParameter, "delta_sigma"));
        inputForm.add(deltaSigmaField);

        Form choiceForm = new Form("choiceForm");
        add(choiceForm);

        choiceForm.add(new AjaxButton("runGrid") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> choiceForm) {
                // TODO: read out epsilonSigmaPairCandidates
                int a = 1;
                for(EpsilonSigmaPair candidate : epsilonSigmaPairCandidates) {
                    if(candidate.getSelected()) {
                        a++;
                    }
                };
                window.close(target);
            }
        });

        WebMarkupContainer listContainer = new WebMarkupContainer("choiceContainer");
        listContainer.setOutputMarkupId(true);

        ListView gridListView = new ListView("gridListview", epsilonSigmaPairCandidates) {
            protected void populateItem(ListItem item) {
                EpsilonSigmaPair singleResult = (EpsilonSigmaPair) item.getModelObject();
                item.add(new Label("choiceEps", singleResult.getEps()));
                item.add(new Label("choiceSigma", singleResult.getSigma()));
                item.add(new CheckBox("selectedCheckbox", new PropertyModel(singleResult, "selected")));
            }
        };

        listContainer.add(gridListView);
        choiceForm.add(listContainer);

        inputForm.add(new AjaxButton("generateGrid") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> inputForm) {
                epsilonSigmaPairCandidates = calculateSigmaEpsilon(gridPanelParameter.getNumber_eps(),
                        gridPanelParameter.getNumber_sigma(), gridPanelParameter.getDelta_eps(), gridPanelParameter.getDelta_sigma());
                listContainer.replace(new ListView("gridListview", epsilonSigmaPairCandidates) {
                    protected void populateItem(ListItem item) {
                        EpsilonSigmaPair singleResult = (EpsilonSigmaPair) item.getModelObject();
                        item.add(new Label("choiceEps", singleResult.getEps()));
                        item.add(new Label("choiceSigma", singleResult.getSigma()));
                        item.add(new CheckBox("selectedCheckbox", new PropertyModel(singleResult, "selected")));
                    }
                });

                target.add(listContainer);
            }
        });
    }


    private List<EpsilonSigmaPair> calculateSigmaEpsilon(int n_eps, int n_sigma, Double delta_eps, Double delta_sigma){
        List<EpsilonSigmaPair> pairList = new ArrayList<>();

        int nEpsOneSide = (int) Math.floor(n_eps/2);
        int nSigmaOneSide = (int) Math.floor(n_sigma/2);

        for (int i=-nEpsOneSide; i<=nEpsOneSide; i++){
            for (int j=-nSigmaOneSide; j<=nSigmaOneSide; j++){
                if (i==0) {
                    if (j == 0) {
                        pairList.add(new EpsilonSigmaPair(1.0,1.0, true));
                    } else {
                        pairList.add(new EpsilonSigmaPair(1.0,1.0+j*delta_sigma, true));
                    }
                }
                else {
                    if (j == 0) {
                        pairList.add(new EpsilonSigmaPair(1.0+i*delta_eps,1.0, true));
                    } else {
                        pairList.add(new EpsilonSigmaPair(1.0+i*delta_eps,1.0+j*delta_sigma,true));
                    }
                }
            }
        }
        return pairList;
    }


};
