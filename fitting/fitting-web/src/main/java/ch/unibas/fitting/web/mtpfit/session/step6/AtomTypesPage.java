package ch.unibas.fitting.web.mtpfit.session.step6;

import ch.unibas.fitting.web.mtpfit.fitting.step1.MtpFitSessionPage;
import ch.unibas.fitting.application.algorithms.mtpfit.AtomCharge;
import ch.unibas.fitting.application.algorithms.mtpfit.MtpFitSessionRepository;
import ch.unibas.fitting.application.algorithms.mtpfit.UserCharges;
import ch.unibas.fitting.web.mtpfit.services.ViewModelMapper;
import ch.unibas.fitting.web.jsmol.JsMolHelper;
import ch.unibas.fitting.web.misc.HeaderPage;
import io.vavr.collection.List;
import io.vavr.collection.Stream;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import javax.inject.Inject;

/**
 * Created by mhelmer-mobile on 15.06.2016.
 */
public class AtomTypesPage extends HeaderPage {

    @Inject
    private ViewModelMapper mapper;
    @Inject
    private MtpFitSessionRepository mtpFitRepo;

    private final String moleculeName;
    private List<ChargesViewModel> charges;

    public AtomTypesPage(PageParameters pp) {

        moleculeName = pp.get("molecule_name").toString();

        FeedbackPanel fp = new FeedbackPanel("feedback");
        fp.setOutputMarkupId(true);
        fp.setOutputMarkupPlaceholderTag(true);
        add(fp);

        Form form = new Form("form");
        add(form);

        form.add(new AjaxButton("save") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if (moleculeName != null && charges.size() > 0) {

                    List<AtomCharge> userCharges = Stream.ofAll(charges)
                            .filter(vm -> vm.getUserCharge() != null)
                            .map(c -> new AtomCharge(
                                    c.getAtomType(),
                                    c.getIndex(),
                                    c.getUserCharge()))
                            .toList();

                    mtpFitRepo.saveUserCharges(
                            getCurrentUsername(),
                            new UserCharges(moleculeName, userCharges));

                    setResponsePage(MtpFitSessionPage.class);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(fp);
            }
        });

        charges = loadAtomTypes();
        form.add(new DataView<ChargesViewModel>("charges",
                new ListDataProvider<>(charges.toJavaList()))
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<ChargesViewModel> item)
            {
                ChargesViewModel charge = item.getModelObject();

                item.add(new Label("name", charge.getAtomType()));

                NumberTextField<Double> ntf = new NumberTextField<Double>("charge", new PropertyModel<>(charge, "userCharge"));
                ntf.setStep(NumberTextField.ANY);
                item.add(ntf);
                JsMolHelper.addAtomsHighlightingMouseEvent(item, charge.getHighlightIndices());
            }
        });
    }

    private List<ChargesViewModel> loadAtomTypes() {
        if (moleculeName != null) {
            return mapper.loadUserCharges(getCurrentUsername(), moleculeName);
        }
        return List.empty();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(JavaScriptHeaderItem.forUrl("/javascript/jsmol/JSmol.min.js"));
        String filename = JsMolHelper.getXyzUrl(getCurrentUsername(), moleculeName);
        response.render(JavaScriptHeaderItem.forScript("var Info = {width: 400,height: 400,serverURL: \"http://chemapps.stolaf.edu/jmol/jsmol/php/jsmol.php\",use: \"HTML5\",j2sPath: \"/javascript/jsmol/j2s\",script: \"background black;load " + filename + "; selectionhalos on;select none;\",console: \"jmolApplet0_infodiv\"}", "jsmol_info"));
    }
}
