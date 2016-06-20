package ch.unibas.fitting.web.gaussian.fit.step2;

import ch.unibas.fitting.shared.molecules.AtomTypeId;
import ch.unibas.fitting.web.web.HeaderPage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mhelmer-mobile on 17.06.2016.
 */
public class FittingResultsPage extends HeaderPage {

    private List<FitResultViewModel> _fitResults;

    public FittingResultsPage() {
        _fitResults = lodFitResults();

        add(new ListView<FitResultViewModel>("fitResults", _fitResults) {
            @Override
            protected void populateItem(ListItem<FitResultViewModel> item) {
                FitResultViewModel mol = item.getModelObject();

                item.add(new Label("type", mol.getAtomTypeName()));

                item.add(createColoredLabel("Q00", mol.getQ00()));
                item.add(createColoredLabel("Q10", mol.getQ10()));
                item.add(createColoredLabel("Q1C", mol.getQ1C()));
                item.add(createColoredLabel("Q1S", mol.getQ1S()));
                item.add(createColoredLabel("Q20", mol.getQ20()));
                item.add(createColoredLabel("Q21C", mol.getQ21C()));
                item.add(createColoredLabel("Q21S", mol.getQ21S()));
                item.add(createColoredLabel("Q22C", mol.getQ22C()));
                item.add(createColoredLabel("Q22S", mol.getQ22S()));
            }
        });
    }

    private Label createColoredLabel(String id, Double value) {
        Label label = new Label(id, value);
        if (value == null) {
            label.add(new AttributeModifier("style", "background-color:white;"));
        } else if(value > 0) {
            label.add(new AttributeModifier("style", "background-color:green;"));
        } else {
            label.add(new AttributeModifier("style", "background-color:red;"));
        }
        return label;
    }

    private List<FitResultViewModel> lodFitResults() {

        // TODO: get the real data

        List results = new ArrayList();
        FitResultViewModel results1 = new FitResultViewModel(new AtomTypeId("O1C2O1"));
        results1.setQ00(-0.1916);
        results1.setQ10(-0.2327);
        results1.setQ20(-0.8251);
        results.add(results1);

        FitResultViewModel results2 = new FitResultViewModel(new AtomTypeId("C2O1O1"));
        results2.setQ00(0.1916);
        results2.setQ10(-0.0001);
        results2.setQ20(-0.751);
        results.add(results2);

        return results;
    }

}
