package ch.unibas.fitting.web.gaussian;

import ch.unibas.fitting.web.web.HeaderPage;
import ch.unibas.fitting.web.web.WizardPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.Model;

import java.util.Arrays;
import java.util.List;

/**
 * Created by martin on 05.06.2016.
 */
public class AtomsPage extends HeaderPage {
    public AtomsPage() {

        add(new Label("filename", new Model<String>("test")));

        add(new DataView<AtomViewModel>("atoms", loadAtoms())
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<AtomViewModel> item)
            {
                AtomViewModel mol = item.getModelObject();

                item.add(new Label("index", mol.getIndex()));
                item.add(new Label("name", mol.getName()));

                item.add(new Label("x", mol.getX()));
                item.add(new Label("y", mol.getY()));
                item.add(new Label("z", mol.getZ()));
            }
        });

        add(new AjaxLink("next") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(UploadPage.class);
            }
        });
    }

    private IDataProvider<AtomViewModel> loadAtoms() {
        List<AtomViewModel> list = Arrays.asList(
                new AtomViewModel("A", 0, 1.029, -0.822, 0.0771),
                new AtomViewModel("B", 1, 1.029, -0.822, 0.0771),
                new AtomViewModel("C", 2, 1.029, -0.822, 0.0771)
        );
        return new ListDataProvider<>(list);
    }
}
