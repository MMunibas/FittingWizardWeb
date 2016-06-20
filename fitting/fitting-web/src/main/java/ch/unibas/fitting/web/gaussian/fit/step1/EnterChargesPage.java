package ch.unibas.fitting.web.gaussian.fit.step1;

import ch.unibas.fitting.shared.molecules.AtomTypeId;
import ch.unibas.fitting.shared.molecules.Molecule;
import ch.unibas.fitting.shared.molecules.MoleculeQueryService;
import ch.unibas.fitting.web.gaussian.MoleculeUserRepo;
import ch.unibas.fitting.web.web.WizardPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mhelmer-mobile on 17.06.2016.
 */
public class EnterChargesPage extends WizardPage {

    private List<AtomTypesViewModel> _atomsTypes;

    private final MoleculeUserRepo repo;

    public EnterChargesPage(ModalWindow window, MoleculeUserRepo repo) {
        this.repo = repo;
        Form form = new Form("form");
        add(form);

        FeedbackPanel fp = new FeedbackPanel("feedback");
        fp.setOutputMarkupId(true);
        fp.setOutputMarkupPlaceholderTag(true);
        add(fp);

        _atomsTypes = loadAtomTypes();

        form.add(new ListView<AtomTypesViewModel>("atomTypes", _atomsTypes) {
            @Override
            protected void populateItem(ListItem<AtomTypesViewModel> item) {
                AtomTypesViewModel mol = item.getModelObject();

                item.add(new Label("type", mol.getAtomTypeName()));
                NumberTextField chargeField = new NumberTextField("charge", new PropertyModel<String>(mol, "getCharge"));
                chargeField.setStep(NumberTextField.ANY);
                chargeField.setRequired(true);
                item.add(chargeField);
            }
        });

        form.add(new AjaxButton("start") {
            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                target.add(fp);

                for (AtomTypesViewModel atom : _atomsTypes) {
                    Logger.debug("Charge for atom " + atom.getAtomTypeName() + " " + atom.getCharge());
                }

                window.close(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                super.onError(target, form);
                target.add(fp);
            }
        });
    }

    private List<AtomTypesViewModel> loadAtomTypes() {

        // TODO: get the real data

        List<Molecule> molecules = repo.loadAllI(getCurrentUsername());

        MoleculeQueryService qs = new MoleculeQueryService(molecules);

        List _atoms = new ArrayList();
        _atoms.add(new AtomTypesViewModel(new AtomTypeId("O1C2O1")));
        _atoms.add(new AtomTypesViewModel(new AtomTypeId("C2O1O1")));

        return _atoms;
    }
}
