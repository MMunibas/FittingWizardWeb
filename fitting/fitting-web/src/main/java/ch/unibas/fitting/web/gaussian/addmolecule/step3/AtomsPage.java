package ch.unibas.fitting.web.gaussian.addmolecule.step3;

import ch.unibas.fitting.shared.xyz.XyzFile;
import ch.unibas.fitting.shared.xyz.XyzFileParser;
import ch.unibas.fitting.web.gaussian.addmolecule.step4.ParameterPage;
import ch.unibas.fitting.web.web.HeaderPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by martin on 05.06.2016.
 */
public class AtomsPage extends HeaderPage {

    private File _file;
    private IModel<XyzFile> _xyzFile;

    public AtomsPage(PageParameters pp) {

        String file = pp.get("xyz_file").toString();
        if (file != null)
            _file = new File(file);

        add(new Label("filename", Model.of(_file != null ? _file.getName() : "no file defined")));

        add(new DataView<AtomViewModel>("atoms", loadAtoms())
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<AtomViewModel> item)
            {
                AtomViewModel mol = item.getModelObject();

                Label label = createClickableAtomLabel("name", mol.getName(), mol.getIndex());
                item.add(label);

                item.add(new Label("x", mol.getX()));
                item.add(new Label("y", mol.getY()));
                item.add(new Label("z", mol.getZ()));
            }
        });

        add(new AjaxLink("next") {
            @Override
            public void onClick(AjaxRequestTarget target) {

                PageParameters pp = new PageParameters();
                pp.add("xyz_file", _file);

                setResponsePage(ParameterPage.class, pp);
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

    private Label createClickableAtomLabel(final String id, String atom_name, int atom_index) {
        Label label = new Label(id, atom_name);
        label.add(new AttributeAppender("onmouseover", new Model("Jmol.script(jmolApplet0,\"select atomIndex=" + atom_index + "\")"), ";"));
        label.add(new AttributeAppender("onmouseout", new Model("Jmol.script(jmolApplet0,\"select none\")"), ";"));
        return label;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(JavaScriptHeaderItem.forUrl("/javascript/jsmol/JSmol.min.js"));
        String filename = convertFileToJavascriptStylePath(_file);
        response.render(JavaScriptHeaderItem.forScript("var Info = {width: 400,height: 400,serverURL: \"http://chemapps.stolaf.edu/jmol/jsmol/php/jsmol.php\",use: \"HTML5\",j2sPath: \"/javascript/jsmol/j2s\",script: \"background black;load " + filename + "; selectionhalos on;select none;\",console: \"jmolApplet0_infodiv\"}", "jsmol_info"));
    }

    private String convertFileToJavascriptStylePath(File file) {
        return "/" + file.toString().replace("\\", "/");
    }
}
