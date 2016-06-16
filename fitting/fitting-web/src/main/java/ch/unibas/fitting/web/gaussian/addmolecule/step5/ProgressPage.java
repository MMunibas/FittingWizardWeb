package ch.unibas.fitting.web.gaussian.addmolecule.step5;

import ch.unibas.fitting.web.gaussian.addmolecule.step6.AtomTypesPage;
import ch.unibas.fitting.web.web.HeaderPage;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.time.Duration;

/**
 * Created by mhelmer-mobile on 15.06.2016.
 */
public class ProgressPage extends HeaderPage {

    private IModel<String> _text;

    public ProgressPage() {

        _text = Model.of("Just started ...");

        final Label label = new Label("progress", _text);
        label.setOutputMarkupId(true);
        add(label);

        add(new AbstractAjaxTimerBehavior(Duration.seconds(2)) {

            private int _count;

            @Override
            protected void onTimer(AjaxRequestTarget target) {
                _count++;
                _text.setObject("We are running since " + _count + " seconds");
                target.add(label);
                if (_count > 5) {
                    stop(target);
                    setResponsePage(AtomTypesPage.class);
                }
            }
        });
    }
}
