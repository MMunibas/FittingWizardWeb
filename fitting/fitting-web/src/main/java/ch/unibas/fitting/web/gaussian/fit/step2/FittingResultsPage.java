package ch.unibas.fitting.web.gaussian.fit.step2;

import ch.unibas.fitting.shared.fitting.Fit;
import ch.unibas.fitting.shared.presentation.gaussian.ColorCoder;
import ch.unibas.fitting.web.gaussian.FitUserRepo;
import ch.unibas.fitting.web.gaussian.fit.step1.FitViewModel;
import ch.unibas.fitting.web.gaussian.fit.step1.ParameterPage;
import ch.unibas.fitting.web.jsmol.JsMolHelper;
import ch.unibas.fitting.web.web.HeaderPage;
import com.google.inject.Inject;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
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
import java.util.Arrays;
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

    private List<String> _molecules = Arrays.asList("all", "co2", "ethanol");
    private IModel<String> selectedMolecule = Model.of("all");

    public FittingResultsPage(PageParameters pp) {

        String value = pp.get("fit_id").toString();
        if  (value != null)
            fitId = Integer.parseInt(value);

        rmse = Model.of();
        add(new Label("rmse", rmse));
        List<Fit> fits = fitUserRepo.loadAll(getCurrentUsername());

        if (fits.size() > 0) {
            if (fitId == null)
                fitId = fits.get(0).getId();
            initalizedFits(fits, fitId);
            _fitResults = loadFitResults(fits, fitId);
        }

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
                _fitResults = loadFitResults(fits, newSelection.getIndex());
            }
        });

        form.add(new DropDownChoice<String>("molecules",
                selectedMolecule,
                _molecules) {
            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }

            @Override
            protected void onSelectionChanged(String newSelection) {
                super.onSelectionChanged(newSelection);
            }
        });

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

                // Todo: add atom indices to highlight atoms in jsmol
                //JsMolHelper.addAtomsHighlightingMouseEvent(item, ...);
            }
        });

        add(new WebMarkupContainer("jsmol") {
            public boolean isVisible() {
                LOGGER.debug("visibility " + selectedMolecule + " " + selectedMolecule.getObject().equals("all"));
                if(selectedMolecule==null || selectedMolecule.getObject().equals("all")) {
                    return false;
                }
                return true;
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

    private List<FitResultViewModel> loadFitResults(List<Fit> fits, int selection) {

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

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(JavaScriptHeaderItem.forUrl("/javascript/jsmol/JSmol.min.js"));
        String filename = JsMolHelper.getXyzUrl(getCurrentUsername(), selectedMolecule.getObject());
        response.render(JavaScriptHeaderItem.forScript("var Info = {width: 400,height: 400,serverURL: \"http://chemapps.stolaf.edu/jmol/jsmol/php/jsmol.php\",use: \"HTML5\",j2sPath: \"/javascript/jsmol/j2s\",script: \"background black;load " + filename + "; selectionhalos on;select none;\",console: \"jmolApplet0_infodiv\"}", "jsmol_info"));
    }

}
