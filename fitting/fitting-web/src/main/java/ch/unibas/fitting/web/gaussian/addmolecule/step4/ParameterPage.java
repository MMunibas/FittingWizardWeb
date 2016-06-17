package ch.unibas.fitting.web.gaussian.addmolecule.step4;

import ch.unibas.fitting.shared.scripts.multipolegauss.MultipoleGaussInput;
import ch.unibas.fitting.shared.xyz.XyzFile;
import ch.unibas.fitting.shared.xyz.XyzFileParser;
import ch.unibas.fitting.web.application.IBackgroundTasks;
import ch.unibas.fitting.web.application.TaskHandle;
import ch.unibas.fitting.web.gaussian.addmolecule.step5.ProgressPage;
import ch.unibas.fitting.web.web.HeaderPage;
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
import java.io.File;

/**
 * Created by martin on 05.06.2016.
 */
public class ParameterPage extends HeaderPage {

    private IModel<Integer> _netCharge = Model.of(0);
    private IModel<String> _quantum = Model.of("#P MP2/aug-cc-PVDZ nosymm");
    private IModel<Integer> _nCores = Model.of(1);
    private IModel<Integer> _multiplicity = Model.of(1);

    private File _xyzFile;

    @Inject
    private IBackgroundTasks _tasks;

    public ParameterPage(PageParameters pp) {

        String xyzFile = pp.get("xyz_file").toString();
        if (xyzFile != null)
            _xyzFile = new File(xyzFile);

        Form form = new Form("form");
        add(form);

        FeedbackPanel fp =new FeedbackPanel("feedback");
        fp.setOutputMarkupId(true);
        fp.setOutputMarkupPlaceholderTag(true);
        add(fp);

        NumberTextField ntf = new NumberTextField<>("netcharge", _netCharge);
        ntf.add(RangeValidator.range(0, 10));
        ntf.setRequired(true);
        form.add(ntf);

        RequiredTextField quantum = new RequiredTextField("quantumdetails", _quantum);
        form.add(quantum);

        NumberTextField cores = new NumberTextField<>("numberOfCores", _nCores);
        cores.setRequired(true);
        cores.add(RangeValidator.range(0, 10));
        form.add(cores);

        NumberTextField multiplicity = new NumberTextField<>("multiplicity", _multiplicity);
        multiplicity.setRequired(true);
        multiplicity.add(RangeValidator.range(0, 10));
        form.add(multiplicity);

        form.add(new AjaxButton("start") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                target.add(fp);

                XyzFile f = XyzFileParser.parse(_xyzFile);

                MultipoleGaussInput input = new MultipoleGaussInput(
                        f.getMoleculeName(),
                        _netCharge.getObject(),
                        _quantum.getObject(),
                        _nCores.getObject(),
                        _multiplicity.getObject()
                );

                // TODO execute real gaussian script

                TaskHandle th = _tasks.execute(getCurrentUsername(), () -> {
                    Thread.sleep(5000);
                    return "hello world!";
                });

                PageParameters pp = new PageParameters();
                pp.add("task_id", th.getId());
                pp.add("xyz_file", _xyzFile);

                setResponsePage(ProgressPage.class, pp);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                super.onError(target, form);
                target.add(fp);
            }
        });
    }
}
