package ch.unibas.fitting.web.gaussian.fit.step2;

import ch.unibas.fitting.shared.directories.FitOutputDir;
import ch.unibas.fitting.shared.directories.IUserDirectory;
import ch.unibas.fitting.shared.fitting.Fit;
import ch.unibas.fitting.shared.fitting.FitResult;
import ch.unibas.fitting.shared.molecules.MoleculeId;
import ch.unibas.fitting.shared.presentation.gaussian.ColorCoder;
import ch.unibas.fitting.web.gaussian.FitUserRepo;
import ch.unibas.fitting.web.gaussian.MoleculeUserRepo;
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
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
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
    private IUserDirectory userDirectory;
    @Inject
    private ColorCoder colorCoder;
    @Inject
    private FitUserRepo fitUserRepo;

    private Integer fitId;
    private IModel<FitViewModel> selectedFit = Model.of();
    private IModel<ArrayList<FitViewModel>> fitVms = Model.of();
    private IModel<ArrayList<FitResultViewModel>> fitResults = Model.of();

    private IModel<String> selectedMolecule = Model.of();
    private IModel<ArrayList<String>> molecules = Model.of();

    public FittingResultsPage(PageParameters pp) {

        String value = pp.get("fit_id").toString();
        if  (value != null)
            fitId = Integer.parseInt(value);

        add(new Label("rmse", new PropertyModel<>(selectedFit, "rmse")));

        initializeFits(fitId, null);

        Form form = new Form("selections");
        add(form);

        form.add(new DropDownChoice<FitViewModel>("fitNumbers",
                selectedFit,
                fitVms,
                new ChoiceRenderer<>("index", "index")) {
            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }

            @Override
            protected void onSelectionChanged(FitViewModel newSelection) {
                if (newSelection != null)
                    initializeFits(newSelection.getIndex(), "ALL");
            }
        });

        form.add(new DropDownChoice<String>("molecules",
                selectedMolecule,
                molecules) {
            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }

            @Override
            protected void onSelectionChanged(String newSelection) {
                if (newSelection != null)
                    initializeFits(selectedFit.getObject().getIndex(), newSelection);
            }
        });

        add(new AjaxLink("back") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(ParameterPage.class);
            }
        });

        IModel fileModel = new AbstractReadOnlyModel(){
            public Object getObject() {
                FitOutputDir dir = userDirectory.getFitOutputDir(getCurrentUsername());
                java.io.File f = dir.getFitOutputFileRef(selectedFit.getObject().getIndex());
                return f;
            }
        };

        add(new DownloadLink("export", fileModel));

        add(new ListView<FitResultViewModel>("fitResults", fitResults) {
            @Override
            protected void populateItem(ListItem<FitResultViewModel> item) {
                FitResultViewModel mol = item.getModelObject();

                item.add(new Label("type", mol.getName()));

                item.add(createColoredLabel("Q00", mol));
                item.add(createColoredLabel("Q10", mol));
                item.add(createColoredLabel("Q11c", mol));
                item.add(createColoredLabel("Q11s", mol));
                item.add(createColoredLabel("Q20", mol));
                item.add(createColoredLabel("Q21c", mol));
                item.add(createColoredLabel("Q21s", mol));
                item.add(createColoredLabel("Q22c", mol));
                item.add(createColoredLabel("Q22s", mol));

                // Todo: add atom indices to highlight atoms in jsmol
                //JsMolHelper.addAtomsHighlightingMouseEvent(item, ...);
            }
        });

        add(new WebMarkupContainer("jsmol") {
            public boolean isVisible() {
                return selectedMolecule.getObject() != null &&
                        !selectedMolecule.getObject().equalsIgnoreCase("all");
            }
        });
    }

    private void initializeFits(final Integer fitId, String molecule) {
        List<Fit> allFits = fitUserRepo.loadAll(getCurrentUsername());

        if (allFits.isEmpty()) {
            fitResults.setObject(new ArrayList<>());
            fitVms.setObject(new ArrayList<>());
            return;
        }

        Fit selectedFit = null;
        if (fitId != null) {
            Optional<Fit> first = allFits.stream()
                    .filter(fit -> fit.getId() == fitId)
                    .findFirst();
            if (first.isPresent())
                selectedFit = first.get();
        }

        if (selectedFit == null)
            selectedFit = allFits.get(0);

        this.fitVms.setObject(allFits.stream()
                .map(FitViewModel::new)
                .collect(Collectors.toCollection(ArrayList<FitViewModel>::new)));

        Fit finalSelectedFit = selectedFit;
        Optional<FitViewModel> selectedVm = this.fitVms.getObject()
                .stream()
                .filter(fitViewModel -> fitViewModel.getIndex() == finalSelectedFit.getId())
                .findFirst();

        this.selectedFit.setObject(selectedVm.get());

        // init all molcules from the selected fit
        ArrayList<String> names = finalSelectedFit.getAllMoleculeIds()
                .stream()
                .map(moleculeId -> moleculeId.getName())
                .collect(Collectors.toCollection(ArrayList::new));
        names.add(0, "ALL");
        molecules.setObject(names);

        if (molecule == null)
            molecule = "ALL";
        selectedMolecule.setObject(molecule);

        List<FitResult> fitResults;
        if (molecule.equalsIgnoreCase("ALL"))
            fitResults = finalSelectedFit.getFitResults();
        else {
            String finalMolecule = molecule;
            fitResults = finalSelectedFit.getFitResults()
                    .stream()
                    .filter(fitResult -> fitResult.getMoleculeIds().contains(new MoleculeId(finalMolecule)))
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        this.fitResults.setObject(fitResults
                .stream()
                .map(fr -> createFitResultVM(fr, finalSelectedFit))
                .collect(Collectors.toCollection(ArrayList::new)));
    }

    private FitResultViewModel createFitResultVM(FitResult result, Fit fit) {
        return new FitResultViewModel(colorCoder, fit, result, new int[0]);
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

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(JavaScriptHeaderItem.forUrl("/javascript/jsmol/JSmol.min.js"));
        String filename = JsMolHelper.getXyzUrl(getCurrentUsername(), selectedMolecule.getObject());
        response.render(JavaScriptHeaderItem.forScript("var Info = {width: 400,height: 400,serverURL: \"http://chemapps.stolaf.edu/jmol/jsmol/php/jsmol.php\",use: \"HTML5\",j2sPath: \"/javascript/jsmol/j2s\",script: \"background black;load " + filename + "; selectionhalos on;select none;\",console: \"jmolApplet0_infodiv\"}", "jsmol_info"));
    }
}
