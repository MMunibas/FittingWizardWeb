package ch.unibas.fitting.web.gaussian.addmolecule.step3;

import ch.unibas.fitting.shared.directories.IUserDirectory;
import ch.unibas.fitting.shared.xyz.XyzFile;
import ch.unibas.fitting.shared.xyz.XyzFileParser;
import ch.unibas.fitting.web.gaussian.addmolecule.step4.ParameterPage;
import ch.unibas.fitting.web.jsmol.JsMolHelper;
import ch.unibas.fitting.web.web.HeaderPage;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
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

import javax.inject.Inject;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by martin on 05.06.2016.
 */
public class AtomsPage extends HeaderPage {

    private String moleculeName;
    private IModel<XyzFile> _xyzFile;

    @Inject
    private IUserDirectory userDirectory;

    public AtomsPage(PageParameters pp) {

        this.moleculeName = pp.get("molecule_name").toString();

        add(new Label("filename", Model.of(moleculeName != null ? moleculeName + ".xyz" : "no file defined")));

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
                JsMolHelper.addAtomsHighlightingMouseEvent(item, mol.getIndex());
            }
        });

        add(new AjaxLink("next") {
            @Override
            public void onClick(AjaxRequestTarget target) {

                PageParameters pp = new PageParameters();
                pp.add("molecule_name", moleculeName);

                setResponsePage(ParameterPage.class, pp);
            }
        });
    }

    private IDataProvider<AtomViewModel> loadAtoms() {
        return new ListDataProvider<AtomViewModel>() {

            private List<AtomViewModel> _atoms;

            @Override
            protected List getData() {
                if (_atoms == null && moleculeName != null) {

                    userDirectory
                            .getMtpFitDir(getCurrentUsername())
                            .getMoleculeDir()
                            .getXyzFile(moleculeName)
                            .peek(xyz -> {
                                _atoms = xyz.getAtoms()
                                        .map(a -> new AtomViewModel(a.getName(), a.getIndex(), a.getX(), a.getY(), a.getZ()))
                                        .collect(Collectors.toList());
                            });

                }
                return _atoms;
            }
        };
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(JavaScriptHeaderItem.forUrl("/javascript/jsmol/JSmol.min.js"));
        String filename = JsMolHelper.getXyzUrl(getCurrentUsername(), moleculeName);
        response.render(JavaScriptHeaderItem.forScript("var Info = {width: 400,height: 400,serverURL: \"http://chemapps.stolaf.edu/jmol/jsmol/php/jsmol.php\",use: \"HTML5\",j2sPath: \"/javascript/jsmol/j2s\",script: \"background black;load " + filename + "; selectionhalos on;select none;\",console: \"jmolApplet0_infodiv\"}", "jsmol_info"));
    }
}
