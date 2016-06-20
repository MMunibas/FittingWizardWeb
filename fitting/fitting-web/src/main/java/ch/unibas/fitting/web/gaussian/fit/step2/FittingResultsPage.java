package ch.unibas.fitting.web.gaussian.fit.step2;

import ch.unibas.fitting.shared.fitting.Fit;
import ch.unibas.fitting.shared.presentation.gaussian.ColorCoder;
import ch.unibas.fitting.web.gaussian.FitUserRepo;
import ch.unibas.fitting.web.gaussian.fit.step1.FitViewModel;
import ch.unibas.fitting.web.gaussian.fit.step1.ParameterPage;
import ch.unibas.fitting.web.web.HeaderPage;
import com.google.inject.Inject;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by mhelmer-mobile on 17.06.2016.
 */
public class FittingResultsPage extends HeaderPage {

    @Inject
    private ColorCoder colorCoder;
    @Inject
    private FitUserRepo fitUserRepo;

    private Integer fitId;
    private IModel<FitViewModel> selectedFit;
    private List<FitViewModel> _fits;

    private List<FitResultViewModel> _fitResults;

    private IModel<Double> rmse;

    private List<String> _molecules;
    private IModel<String> selectedMolecule;

    public FittingResultsPage(PageParameters pp) {

        String value = pp.get("fit_id").toString();
        if  (value != null)
            fitId = Integer.parseInt(value);
        else
            fitId = 0;

        rmse = Model.of();
        add(new Label("rmse", rmse));
        List<Fit> fits = fitUserRepo.loadAll(getCurrentUsername());

        initalizedFits(fits, fitId);
        _fitResults = lodFitResults(fits, fitId);

        Form form = new Form("selections");
        add(form);

        form.add(new DropDownChoice<FitViewModel>("fitNumbers",
                selectedFit,
                _fits,
                new ChoiceRenderer<>("index", "index")) {
            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }

            @Override
            protected void onSelectionChanged(FitViewModel newSelection) {
                List<Fit> fits = fitUserRepo.loadAll(getCurrentUsername());
                _fitResults = lodFitResults(fits, newSelection.getIndex());
            }
        });

//        form.add(new DropDownChoice<FitViewModel>("molecules",
//                selectedMolecule,
//                _molecules) {
//            @Override
//            protected boolean wantOnSelectionChangedNotifications() {
//                return true;
//            }
//
//            @Override
//            protected void onSelectionChanged(FitViewModel newSelection) {
//                super.onSelectionChanged(newSelection);
//            }
//        });

        add(new AjaxLink("back") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(ParameterPage.class);
            }
        });

        add(new ListView<FitResultViewModel>("fitResults", _fitResults) {
            @Override
            protected void populateItem(ListItem<FitResultViewModel> item) {
                FitResultViewModel mol = item.getModelObject();

                item.add(new Label("type", mol.getName()));

                item.add(createColoredLabel("Q00", mol));
                item.add(createColoredLabel("Q10", mol));
                item.add(createColoredLabel("Q1C", mol));
                item.add(createColoredLabel("Q1S", mol));
                item.add(createColoredLabel("Q20", mol));
                item.add(createColoredLabel("Q21C", mol));
                item.add(createColoredLabel("Q21S", mol));
                item.add(createColoredLabel("Q22C", mol));
                item.add(createColoredLabel("Q22S", mol));
            }
        });
    }

    private void initalizedFits(List<Fit> fits, int fitId) {
        _fits = fits.stream()
                .map(fit -> new FitViewModel(fit))
                .collect(Collectors.toList());
        Optional<FitViewModel> selected = _fits.stream().filter(fitViewModel -> fitViewModel.getIndex() == fitId)
                .findFirst();
        if (selected.isPresent())
            selectedFit = Model.of(selected.get());
    }

    private Label createColoredLabel(String chargeType, FitResultViewModel fitResult) {
        FitResultViewModel.FitValue fitValue = fitResult.getFitValueFor(chargeType);
        Label label = new Label(chargeType);
        if (fitValue == null) {
            label.add(new AttributeModifier("style", "background-color:white;"));
        } else {
            label.setDefaultModel(Model.of(fitValue.getValue()));
            label.add(new AttributeModifier("style", "background-color:" + fitValue.getColor() + ";"));
        }
        return label;
    }

    private List<FitResultViewModel> lodFitResults(List<Fit> fits, int selection) {

        Optional<Fit> first = fits.stream()
                .filter(fit -> fit.getId() == selection)
                .findFirst();

        List<FitResultViewModel> list;
        if  (first.isPresent()) {
            Fit fit  = first.get();
            rmse.setObject(fit.getRmse());
            list = first.get().getFitResults()
                    .stream()
                    .map(fr -> new FitResultViewModel(colorCoder, fit, fr))
                    .collect(Collectors.toList());
        } else {
            list = new ArrayList<>();
        }
        return list;
    }
}
