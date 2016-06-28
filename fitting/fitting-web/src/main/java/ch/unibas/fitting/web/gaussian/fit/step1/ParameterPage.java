package ch.unibas.fitting.web.gaussian.fit.step1;

import ch.unibas.fitting.shared.charges.ChargeTypes;
import ch.unibas.fitting.shared.fitting.ChargeValue;
import ch.unibas.fitting.shared.molecules.*;
import ch.unibas.fitting.web.gaussian.FitUserRepo;
import ch.unibas.fitting.web.gaussian.MoleculeUserRepo;
import ch.unibas.fitting.web.gaussian.fit.RunFitCommand;
import ch.unibas.fitting.web.web.progress.ProgressPage;
import ch.unibas.fitting.web.gaussian.addmolecule.step6.ChargesViewModel;
import ch.unibas.fitting.web.gaussian.fit.step2.FittingResultsPage;
import ch.unibas.fitting.web.web.HeaderPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    private Rank rank = ranks.get(0);;
    private IModel<Double> convergence = Model.of(0.1);
    private IModel<Boolean> ignoreHydrogens = Model.of(false);

    @Inject
    private MoleculeUserRepo userRepo;
    @Inject
    private FitUserRepo fitUserRepo;
    @Inject
    private RunFitCommand runFit;

    private EnterChargesPanel chargesPage;
    private List<ChargesViewModel> _atomsTypes;
    private List<FitViewModel> _fits;

    private LinkedHashSet<AtomType> getAllAtomTypeIds(List<Molecule> molecules) {
        LinkedHashSet<AtomType> allIds = new LinkedHashSet<>();
        for (Molecule molecule : molecules) {
            allIds.addAll(molecule.getAtomTypes());
        }
        return allIds;
    }

    public ParameterPage() {

        ModalWindow chargesDialog = new ModalWindow("modalWindow");

        _fits = loadFits();
        _atomsTypes = loadAtomTypes();

        chargesPage = new EnterChargesPanel(chargesDialog.getContentId(), chargesDialog, _atomsTypes);
        chargesDialog.setContent(chargesPage);
        chargesDialog.setCloseButtonCallback(target -> {
            LOGGER.info("window closed");
            return true;
        });
        chargesDialog.setWindowClosedCallback(target -> {
            if (allChargesFilled())
                startFit();
        });
        add(chargesDialog);

        Form form = new Form("form");
        add(form);

        FeedbackPanel fp = new FeedbackPanel("feedback");
        fp.setOutputMarkupId(true);
        fp.setOutputMarkupPlaceholderTag(true);
        add(fp);

        NumberTextField ntf = new NumberTextField<>("convergence", convergence);
        ntf.setStep(NumberTextField.ANY);
        ntf.setRequired(true);
        form.add(ntf);

        form.add(new DropDownChoice("rank", new PropertyModel(this, "rank"), ranks));

        CheckBox hydro = new CheckBox("ignoreHydrogens", ignoreHydrogens);
        form.add(hydro);

        form.add(new AjaxButton("start") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                target.add(fp);

                LOGGER.debug("Showing user charges dialog");
                LOGGER.debug("FitMtpInput Parameters: " +
                        "convergence: " + convergence.getObject() + ", " +
                        "rank: " + rank.getRank() + ", " +
                        "ignoreHydrogens: " + ignoreHydrogens.getObject());

                chargesDialog.show(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                super.onError(target, form);
                target.add(fp);
            }
        });

        form.add(new AjaxLink("showResults") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(FittingResultsPage.class);
            }

            @Override
            public boolean isVisible() {
                return _fits.size() > 0;
            }
        });

        add(new ListView<FitViewModel>("fits", _fits) {

            @Override
            protected void populateItem(ListItem<FitViewModel> item) {
                FitViewModel vm = item.getModelObject();

                item.add(new Label("index", Model.of(vm.getIndex())));
                item.add(new Label("created", Model.of(vm.getCreated())));
                item.add(new Label("rmse", Model.of(vm.getRmse())));
                item.add(new Label("rank", Model.of(vm.getRank())));

                item.add(new AjaxLink("remove") {

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        fitUserRepo.remove(getCurrentUsername(), vm.getIndex());
                        setResponsePage(ParameterPage.class);
                    }
                });
            }
        });
    }

    private List<FitViewModel> loadFits() {
        return fitUserRepo.loadAll(getCurrentUsername())
                .stream()
                .map(fit -> new FitViewModel(fit))
                .collect(Collectors.toList());
    }

    private void startFit() {
        LOGGER.debug("Starting fit");

        // TODO dont, if canceled

        LinkedHashSet<ChargeValue> charges = _atomsTypes.stream()
                .map(a -> new ChargeValue(new AtomTypeId(a.getName()), ChargeTypes.charge, a.getUserCharge()))
                .collect(Collectors.toCollection(() -> new LinkedHashSet<>()));

        UUID uuid = runFit.runFit(getCurrentUsername(),
                convergence.getObject(),
                rank.getRank(),
                ignoreHydrogens.getObject(),
                charges);
        PageParameters pp = new PageParameters();
        pp.add("task_id", uuid);
        setResponsePage(ProgressPage.class, pp);
    }

    private List<ChargesViewModel> loadAtomTypes() {
        List<Molecule> molecules = userRepo.loadAll(getCurrentUsername());
        return getAllAtomTypeIds(molecules)
                .stream()
                .map(atomType -> new ChargesViewModel(atomType.getId().getName(), atomType.getIndices(), atomType.getUserQ00()))
                .collect(Collectors.toList());
    }

    public boolean allChargesFilled() {
        return _atomsTypes.stream().allMatch(a -> a.isChargeDefined());
    }
}
