package ch.unibas.fitting.web.gaussian.fit.step2;

import ch.unibas.fitting.web.application.directories.IUserDirectory;
import ch.unibas.fitting.web.gaussian.fit.step1.FitViewModel;
import ch.unibas.fitting.web.gaussian.fit.step1.MtpFitSessionPage;
import ch.unibas.fitting.web.gaussian.services.ViewModelMapper;
import ch.unibas.fitting.web.jsmol.JsMolHelper;
import ch.unibas.fitting.web.web.HeaderPage;
import com.google.inject.Inject;
import io.vavr.collection.Stream;
import javafx.scene.paint.Color;
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
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mhelmer-mobile on 17.06.2016.
 */
public class FittingResultsPage extends HeaderPage {

    @Inject
    private IUserDirectory userDirectory;
    @Inject
    private ViewModelMapper mapper;

    private String moleculeName = "";
    private IModel<File> lpunFile = Model.of();
    private IModel<FitViewModel> selectedFitId = Model.of();
    private IModel<List<FitViewModel>> fitIds = Model.ofList(new ArrayList<>());
    private IModel<List<FitResultViewModel>> fitResults = Model.ofList(new ArrayList<>());

    private DownloadLink downloadLink;

    public FittingResultsPage(PageParameters pp) {

        String value = pp.get("fit_id").toString();
        Integer initialFit = null;
        if  (value != null)
            initialFit = Integer.parseInt(value);

        add(new Label("rmse", new PropertyModel<>(selectedFitId, "rmse")));

        Form form = new Form("selections");
        add(form);

        form.add(new DropDownChoice<FitViewModel>("fitNumbers",
                selectedFitId,
                fitIds,
                new ChoiceRenderer<>("index", "index")) {
            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }

            @Override
            protected void onSelectionChanged(FitViewModel newSelection) {
                if (newSelection != null)
                    setFitSelection(newSelection.getIndex());
            }
        });

        add(new AjaxLink("goToSession") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(MtpFitSessionPage.class);
            }
        });

        form.add(downloadLink = new DownloadLink("download", lpunFile));
        downloadLink.setVisible(false);
        downloadLink.setOutputMarkupPlaceholderTag(true);

        add(new ListView<FitResultViewModel>("fitResults", fitResults) {
            @Override
            protected void populateItem(ListItem<FitResultViewModel> item) {
                FitResultViewModel mol = item.getModelObject();

                item.add(new Label("type", mol.getAtomType()));

                item.add(createColoredLabel("Q00", mol));
                item.add(createColoredLabel("Q10", mol));
                item.add(createColoredLabel("Q11c", mol));
                item.add(createColoredLabel("Q11s", mol));
                item.add(createColoredLabel("Q20", mol));
                item.add(createColoredLabel("Q21c", mol));
                item.add(createColoredLabel("Q21s", mol));
                item.add(createColoredLabel("Q22c", mol));
                item.add(createColoredLabel("Q22s", mol));

            }
        });

        add(new WebMarkupContainer("jsmol"));

        initializeFits(initialFit);
    }

    private void initializeFits(Integer fitId) {
        List<FitViewModel> allFits = mapper.loadFits(getCurrentUsername()).toJavaList();

        if (allFits.isEmpty()) {
            fitResults.setObject(new ArrayList<>());
            fitIds.setObject(new ArrayList<>());
            return;
        }
        moleculeName = userDirectory.getMtpFitDir(getCurrentUsername())
                .getMoleculeDir().getAnyMoleculeName();
        fitIds.setObject(allFits);

        if (fitId == null)
            fitId = allFits.get(0).getIndex();

        setFitSelection(fitId);
    }

    private void setFitSelection(int fitId) {
        Stream.ofAll(fitIds.getObject())
            .findLast(vm -> vm.getIndex() == fitId)
                .peek(vm -> {
                    selectedFitId.setObject(vm);

                    File lpun = userDirectory.getMtpFitDir(getCurrentUsername())
                            .getLpunFile(fitId)
                            .getOrElse((File) null);

                    if (lpun != null) {
                        lpunFile.setObject(lpun);
                        downloadLink.setVisible(true);
                    } else {
                        downloadLink.setVisible(false);
                    }

                    List<FitResultViewModel> results = mapper.loadFitResults(getCurrentUsername(), fitId).toJavaList();
                    fitResults.setObject(results);
                });
    }

    private Label createColoredLabel(String chargeType, FitResultViewModel fitResult) {
        var value = fitResult.getValue(chargeType);
        var color = fitResult.getColor(chargeType);
        Label label = new Label(chargeType);
        label.setDefaultModel(Model.of(value));
        label.add(new AttributeModifier("style", "background-color:" + color + ";"));
        return label;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(JavaScriptHeaderItem.forUrl("/javascript/jsmol/JSmol.min.js"));
        String filename = JsMolHelper.getXyzUrl(getCurrentUsername(), moleculeName);
        response.render(JavaScriptHeaderItem.forScript("var Info = {width: 400,height: 400,serverURL: \"http://chemapps.stolaf.edu/jmol/jsmol/php/jsmol.php\",use: \"HTML5\",j2sPath: \"/javascript/jsmol/j2s\",script: \"background black;load " + filename + "; selectionhalos on;select none;\",console: \"jmolApplet0_infodiv\"}", "jsmol_info"));
    }
}
