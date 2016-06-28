package ch.unibas.fitting.web.gaussian.addmolecule.step6;

import ch.unibas.fitting.shared.molecules.Molecule;
import ch.unibas.fitting.web.gaussian.MoleculeUserRepo;
import ch.unibas.fitting.web.gaussian.addmolecule.step1.OverviewPage;
import ch.unibas.fitting.web.web.HeaderPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.RangeValidator;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by mhelmer-mobile on 15.06.2016.
 */
public class AtomTypesPage extends HeaderPage {

    private final String moleculeName;

    @Inject
    private MoleculeUserRepo moleculeUserRepo;

    private List<ChargesViewModel> types;

    public AtomTypesPage(PageParameters pp) {

        moleculeName = pp.get("molecule_name").toString();

        FeedbackPanel fp = new FeedbackPanel("feedback");
        fp.setOutputMarkupId(true);
        fp.setOutputMarkupPlaceholderTag(true);
        add(fp);

        Form form = new Form("form");
        add(form);

        form.add(new AjaxButton("next") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                // TODO set charges, only allow submit, if charger are present

                Optional<Molecule> mol = getMolecule();
                if (mol.isPresent()) {
                    types.forEach(charge -> mol.get().setUserCharge(charge.getName(), charge.getUserCharge()));
                    moleculeUserRepo.save(getCurrentUsername(), mol.get());
                }
                setResponsePage(OverviewPage.class);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(fp);
            }
        });

        types = loadAtomTypes();
        form.add(new DataView<ChargesViewModel>("charges", new ListDataProvider<>(types))
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<ChargesViewModel> item)
            {
                ChargesViewModel charge = item.getModelObject();

                item.add(new Label("name", charge.getName()));

                NumberTextField<Double> ntf = new NumberTextField<Double>("charge", new PropertyModel<>(charge, "userCharge"));
                ntf.add(RangeValidator.range(-99.0, 99.0));
                item.add(ntf);
                addAtomTypeHighlightingMouseEvent(item, charge);
            }
        });
    }

    private List<ChargesViewModel> loadAtomTypes() {
        Optional<Molecule> result = getMolecule();
        if (result.isPresent()) {
            return result.get().getAtomTypes()
                            .stream()
                            .map(atomType -> new ChargesViewModel(atomType.getId().getName(),
                                    atomType.getIndices(),
                                    atomType.getUserQ00()))
                            .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    private Optional<Molecule> getMolecule() {
        boolean pageOpenedWithMoleculeName = moleculeName != null;
        if (pageOpenedWithMoleculeName) {
            return moleculeUserRepo.load(getCurrentUsername(), moleculeName);
        }
        return Optional.empty();
    }

    private void addAtomTypeHighlightingMouseEvent(Item<ChargesViewModel> item, ChargesViewModel model) {
        String atomIdxString = createAtomSelectionString(model);
        item.add(new AttributeAppender("onmouseover", new Model("Jmol.script(jmolApplet0,\"select " + atomIdxString + "\")"),";"));
        item.add(new AttributeAppender("onmouseout", new Model("Jmol.script(jmolApplet0,\"select none\")"), ";"));
    }

    private String createAtomSelectionString(ChargesViewModel model) {
        int[] indices = model.getIndices();
        String atomIdxString = "";
        for (int i = 0; i < indices.length; i++) {
            atomIdxString += "atomIndex=" + indices[i];
            if (i < indices.length - 1) {
                atomIdxString += " OR ";
            }
        }
        return atomIdxString;
    }

    private String getXyzUrl(String moleculeName) {
        return "/data/" +
                getCurrentUsername() +
                "/xyz_files/" +
                moleculeName +
                ".xyz";
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(JavaScriptHeaderItem.forUrl("/javascript/jsmol/JSmol.min.js"));
        String filename = getXyzUrl(moleculeName);
        response.render(JavaScriptHeaderItem.forScript("var Info = {width: 400,height: 400,serverURL: \"http://chemapps.stolaf.edu/jmol/jsmol/php/jsmol.php\",use: \"HTML5\",j2sPath: \"/javascript/jsmol/j2s\",script: \"background black;load " + filename + "; selectionhalos on;select none;\",console: \"jmolApplet0_infodiv\"}", "jsmol_info"));
    }
}
