package ch.unibas.fitting.web.gaussian.addmolecule.step4;

import ch.unibas.fitting.web.application.IBackgroundTasks;
import ch.unibas.fitting.web.application.PageContext;
import ch.unibas.fitting.web.gaussian.addmolecule.RunGaussianCommand;
import ch.unibas.fitting.web.gaussian.addmolecule.step1.OverviewPage;
import ch.unibas.fitting.web.web.HeaderPage;
import ch.unibas.fitting.web.web.PageNavigation;
import ch.unibas.fitting.web.welcome.WelcomePage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.RangeValidator;

import javax.inject.Inject;

/**
 * Created by martin on 05.06.2016.
 */
public class ParameterPage extends HeaderPage {

    private IModel<Integer> _netCharge = Model.of(0);
    private IModel<String> _quantum = Model.of("#P MP2/aug-cc-PVDZ nosymm");
    private IModel<Integer> _nCores = Model.of(8);
    private IModel<Integer> _multiplicity = Model.of(1);

    private final String moleculeName;

    @Inject
    private RunGaussianCommand runGaussian;

    public ParameterPage(PageParameters pp) {

        this.moleculeName = pp.get("molecule_name").toString();
        if (moleculeName == null)
            PageNavigation.ToPage(OverviewPage.class);

        Form form = new Form("form");
        add(form);

        FeedbackPanel fp =new FeedbackPanel("feedback");
        fp.setOutputMarkupId(true);
        fp.setOutputMarkupPlaceholderTag(true);
        add(fp);

        NumberTextField ntf = new NumberTextField<>("netcharge", _netCharge);
        ntf.add(RangeValidator.range(-10, 10));
        ntf.setRequired(true);
        form.add(ntf);

        RequiredTextField quantum = new RequiredTextField("quantumdetails", _quantum);
        form.add(quantum);

        NumberTextField cores = new NumberTextField<>("numberOfCores", _nCores);
        cores.setRequired(true);
        cores.add(RangeValidator.range(1, 128));
        form.add(cores);

        NumberTextField multiplicity = new NumberTextField<>("multiplicity", _multiplicity);
        multiplicity.setRequired(true);
        multiplicity.add(RangeValidator.range(0, 10));
        form.add(multiplicity);

        form.add(new AjaxButton("start") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                target.add(fp);

                PageContext pageContext = new PageContext(
                        ParameterPage.class,
                        pp
                );

                runGaussian.execute(getCurrentUsername(),
                        moleculeName,
                        pageContext,
                        _netCharge.getObject(),
                        _quantum.getObject(),
                        _nCores.getObject(),
                        _multiplicity.getObject());
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                super.onError(target, form);
                target.add(fp);
            }
        });
    }
}
