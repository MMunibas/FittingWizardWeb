package ch.unibas.fitting.web.gaussian.jsmol;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tschmidt on 09.06.2016.
 */
public class JSMolPage extends WebPage {
    public JSMolPage() {

        List list = new ArrayList();

        for (int i=0; i < 10; ++i) {
            list.add(i);
        }

        final DataView dataView = new DataView("simple", new ListDataProvider(
                list)) {
            public void populateItem(final Item item) {
                final int index = (int) item.getModelObject();
                Label label = createLabelForAtom("id", index);
                item.add(label);
            }
        };

        add(dataView);
    }

    private Label createLabelForAtom(final String id, int atom_index) {
        Label label = new Label(id, "Atom " + atom_index);
        label.add(new AttributeAppender("onmouseover", new Model("Jmol.script(jmolApplet0,\"select atomIndex=" + atom_index + "\")"), ";"));
        label.add(new AttributeAppender("onmouseout", new Model("Jmol.script(jmolApplet0,\"select none\")"), ";"));
        return label;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(JavaScriptHeaderItem.forUrl("/javascript/jsmol/j2s/JSmol.min.js"));
        response.render(JavaScriptHeaderItem.forScript("var Info = {width: 400,height: 400,serverURL: \"http://chemapps.stolaf.edu/jmol/jsmol/php/jsmol.php\",use: \"HTML5\",j2sPath: \"/js/j2s\",console: \"jmolApplet0_infodiv\"}", "jsmol_info"));
    }
}
