package ch.unibas.fitting.web.gaussian.addmolecule.step1;

import ch.unibas.fitting.web.gaussian.MoleculeUserRepo;
import ch.unibas.fitting.web.gaussian.RemoveMolecule;
import ch.unibas.fitting.web.gaussian.addmolecule.step2.UploadPage;
import ch.unibas.fitting.web.gaussian.addmolecule.step6.AtomTypesPage;
import ch.unibas.fitting.web.web.HeaderPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by martin on 29.05.2016.
 */
public class OverviewPage extends HeaderPage {

    @Inject
    private MoleculeUserRepo moleculeUserRepo;

    @Inject
    private RemoveMolecule removeMolecule;

    public OverviewPage() {

        add(new AjaxLink("upload") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(UploadPage.class);
            }
        });

        DataView<EntryViewModel> data = new DataView<EntryViewModel>("molecules", loadMolecuels()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<EntryViewModel> item) {
                EntryViewModel mol = item.getModelObject();
                item.add(new Label("name", mol.getName()));
                item.add(new Label("added", mol.getAdded()));

                item.add(new AjaxLink("edit") {

                    @Override
                    public IModel<?> getBody() {
                        return Model.of("Edit");
                    }

                    @Override
                    public void onClick(AjaxRequestTarget target) {

                        PageParameters pp = new PageParameters();
                        pp.add("molecule_name", mol.getName());
                        setResponsePage(AtomTypesPage.class, pp);
                    }
                });

                item.add(new AjaxLink("remove") {

                    @Override
                    public IModel<?> getBody() {
                        return Model.of("Remove");
                    }

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        removeMolecule.remove(getCurrentUsername(), mol.getName());

                        setResponsePage(OverviewPage.class);
                    }
                });
            }
        };
        add(data);

        add(new AjaxLink("startFit") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(ch.unibas.fitting.web.gaussian.fit.step1.ParameterPage.class);
            }

            @Override
            public boolean isVisible() {
                return data.getItemCount() > 0;
            }
        });
    }

    private IDataProvider<EntryViewModel> loadMolecuels() {

        return new ListDataProvider<EntryViewModel>() {
            @Override
            protected List getData() {
                List<EntryViewModel> moles = moleculeUserRepo.loadAll(getCurrentUsername())
                        .stream()
                        .map(molecule -> new EntryViewModel(molecule.getId().getName(), molecule.getCreated()))
                        .collect(Collectors.toList());
                return moles;
            }
        };
    }
}
