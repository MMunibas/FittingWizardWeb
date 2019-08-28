package ch.unibas.fitting.web.mtpfit.fitting.step1;

import ch.unibas.fitting.application.algorithms.mtpfit.ChargeTypes;
import ch.unibas.fitting.application.algorithms.mtpfit.ChargeValue;
import ch.unibas.fitting.web.mtpfit.session.step2.UploadPage;
import ch.unibas.fitting.web.mtpfit.session.step6.ChargesViewModel;
import ch.unibas.fitting.web.mtpfit.commands.RemoveFitCommand;
import ch.unibas.fitting.web.mtpfit.commands.RunMtpFitCommand;
import ch.unibas.fitting.web.mtpfit.fitting.step2.FittingResultsPage;
import ch.unibas.fitting.web.mtpfit.services.ViewModelMapper;
import ch.unibas.fitting.web.misc.HeaderPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mhelmer-mobile on 17.06.2016.
 */
public class MtpFitSessionPage extends HeaderPage {

    private IModel<Integer> numChgs = Model.of(1);

    @Inject
    private RunMtpFitCommand runFit;
    @Inject
    private RemoveFitCommand removeFitCommand;

    @Inject
    private ViewModelMapper viewModelMapper;

    private EnterChargesPanel chargesPage;
    private List<ChargesViewModel> _userCharges;
    private List<FitViewModel> _fits;

    public MtpFitSessionPage() {

        ModalWindow chargesDialog = new ModalWindow("modalWindow");

        _fits = viewModelMapper.loadFits(getCurrentUsername()).toJavaList();
        _userCharges = viewModelMapper.loadUserCharges(getCurrentUsername()).toJavaList();

        chargesPage = new EnterChargesPanel(chargesDialog.getContentId(), chargesDialog, _userCharges);
        chargesDialog.setContent(chargesPage);
        chargesDialog.setCloseButtonCallback(target -> {
            LOGGER.info("window closed");
            return true;
        });
        chargesDialog.setWindowClosedCallback(target -> {
            if (allChargesFilled() && chargesPage.isWasSuccess())
                startFit();
        });
        add(chargesDialog);

        add(new AjaxLink("newSession") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(UploadPage.class);
            }
        });

        Form form = new Form("form");
        add(form);

        FeedbackPanel fp = new FeedbackPanel("feedback");
        fp.setOutputMarkupId(true);
        fp.setOutputMarkupPlaceholderTag(true);
        add(fp);

        NumberTextField ntf = new NumberTextField<>("numChgs", numChgs);
        ntf.setStep(NumberTextField.ANY);
        ntf.setMinimum(1);
        ntf.setRequired(true);
        form.add(ntf);

        form.add(new AjaxButton("start") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                target.add(fp);

                LOGGER.debug("Showing user charges dialog");
                LOGGER.debug("FitMtpInput Parameters: " +
                        "numChgs: " + numChgs.getObject());

                chargesDialog.show(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                super.onError(target, form);
                target.add(fp);
            }
        });

        form.add(new AjaxButton("showResults") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
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

                item.add(new AjaxLink("remove") {

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        removeFitCommand.remove(getCurrentUsername(), vm.getIndex());
                        setResponsePage(MtpFitSessionPage.class);
                    }
                });
            }
        });
    }

    private void startFit() {
        LOGGER.debug("Starting fit");

        LinkedHashSet<ChargeValue> charges = _userCharges.stream()
                .map(a -> new ChargeValue(a.getAtomType(), ChargeTypes.charge, a.getUserCharge(), a.getIndex()))
                .collect(Collectors.toCollection(() -> new LinkedHashSet<>()));

        runFit.executeNew(getCurrentUsername(),
                numChgs.getObject(),
                charges);
    }

    public boolean allChargesFilled() {
        return _userCharges.stream().allMatch(a -> a.isChargeDefined());
    }
}
