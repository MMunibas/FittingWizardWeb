package ch.unibas.fitting.web.gaussian.fit.step1;

import ch.unibas.fitting.shared.molecules.*;
import ch.unibas.fitting.shared.scripts.fitmtp.FitMtpInput;
import ch.unibas.fitting.web.web.HeaderPage;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by mhelmer-mobile on 17.06.2016.
 */
public class ParameterPage extends HeaderPage {

    private static class Rank {
        private String _name;
        private int _rank;

        public Rank(String name, int rank) {
            this._name = name;
            this._rank = rank;
        }

        public int getRank() {
            return _rank;
        }

        public String toString() {
            return _name;
        }
    }

    private static List<Rank> ranks = new ArrayList();
    static {
        ranks.add(new Rank("Point charges", 0));
        ranks.add(new Rank("Point charges and dipoles", 1));
        ranks.add(new Rank("Point charges, dipoles and quadrupoles", 2));
    }

    private Rank _rank = ranks.get(0);;
    private IModel<Double> _convergence = Model.of(0.1);
    private IModel<Boolean> _ignoreHydrogens = Model.of(false);

    private LinkedHashSet<AtomTypeId> getAllAtomTypeIds(List<Molecule> molecules) {
        LinkedHashSet<AtomTypeId> allIds = new LinkedHashSet<>();
        for (Molecule molecule : molecules) {
            allIds.addAll(molecule.getAllAtomTypeIds());
        }
        return allIds;
    }

    public ParameterPage() {

        ModalWindow modalWindow = new ModalWindow("modalWindow");

        modalWindow.setPageCreator(new ModalWindow.PageCreator() {
            @Override
            public Page createPage() {
                return new EnterChargesPage(modalWindow);
            }
        });

        modalWindow.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {

            @Override
            public void onClose(AjaxRequestTarget target) {
                //custom code…
                Logger.info("window closed");
            }
        });
        
        add(modalWindow);

        Form form = new Form("form");
        add(form);

        FeedbackPanel fp = new FeedbackPanel("feedback");
        fp.setOutputMarkupId(true);
        fp.setOutputMarkupPlaceholderTag(true);
        add(fp);

        NumberTextField ntf = new NumberTextField<>("convergence", _convergence);
        ntf.setStep(NumberTextField.ANY);
        ntf.setRequired(true);
        form.add(ntf);

        form.add(new DropDownChoice("rank", new PropertyModel(this, "_rank"), ranks));

        CheckBox hydro = new CheckBox("ignoreHydrogens", _ignoreHydrogens);
        form.add(hydro);

        form.add(new AjaxButton("start") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                target.add(fp);

                modalWindow.show(target);

                //XyzFile f = XyzFileParser.parse(_xyzFile);

                FitMtpInput input = new FitMtpInput(
                        0,
                        _convergence.getObject(),
                        _rank.getRank(),
                        _ignoreHydrogens.getObject(),
                        new File(""),
                        new ArrayList()
                );

//                FitMtpInput input = new FitMtpInput(
//                        _fitRepository.getNextFitId(),
//                        _convergence.getObject(),
//                        _rank.getRank(),
//                        _ignoreHydrogens.getObject(),
//                        initalCharges,
//                        queryService.getMoleculeIds()
//                );

                Logger.debug("FitMtpInput Parameters: " +
                        "convergence: " + _convergence.getObject() + ", " +
                        "rank: " + _rank.getRank() + ", " +
                        "ignoreHydrogens: " + _ignoreHydrogens.getObject());

                // TODO execute real script

//                TaskHandle th = _tasks.execute(getCurrentUsername(), () -> {
//                    Thread.sleep(5000);
//                    return "hello world!";
//                });
//
//                PageParameters pp = new PageParameters();
//                pp.add("task_id", th.getId());
//                pp.add("xyz_file", _xyzFile);

//                setResponsePage(ProgressPage.class, pp);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                super.onError(target, form);
                target.add(fp);
            }
        });
    }
}
