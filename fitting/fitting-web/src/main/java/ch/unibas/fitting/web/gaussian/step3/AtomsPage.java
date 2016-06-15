package ch.unibas.fitting.web.gaussian.step3;

import ch.unibas.fitting.shared.xyz.XyzAtom;
import ch.unibas.fitting.shared.xyz.XyzFile;
import ch.unibas.fitting.shared.xyz.XyzFileParser;
import ch.unibas.fitting.web.gaussian.step4.ParameterPage;
import ch.unibas.fitting.web.web.HeaderPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.Model;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by martin on 05.06.2016.
 */
public class AtomsPage extends HeaderPage {

    File _file;

    public AtomsPage(File xyzFile) {

        _file = xyzFile;

        add(new Label("filename", new Model<>(xyzFile.getName())));

        add(new DataView<AtomViewModel>("atoms", loadAtoms())
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<AtomViewModel> item)
            {
                AtomViewModel mol = item.getModelObject();

                item.add(new Label("name", mol.getName()));

                item.add(new Label("x", mol.getX()));
                item.add(new Label("y", mol.getY()));
                item.add(new Label("z", mol.getZ()));
            }
        });

        add(new AjaxLink("next") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(ParameterPage.class);
            }
        });
    }

    private IDataProvider<AtomViewModel> loadAtoms() {
        return new ListDataProvider<AtomViewModel>() {

            private List<AtomViewModel> _atoms;

            @Override
            protected List getData() {
                if (_atoms == null) {
                    XyzFile f = XyzFileParser.parse(_file);
                    _atoms = f.getAtoms()
                            .stream()
                            .map(a -> new AtomViewModel(a.getName(), a.getIndex(), a.getX(), a.getY(), a.getZ()))
                            .collect(Collectors.toList());
                }
                return _atoms;
            }
        };
    }
}
