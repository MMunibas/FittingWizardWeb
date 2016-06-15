package ch.unibas.fitting.web.gaussian.step1;

import ch.unibas.fitting.web.gaussian.step2.UploadPage;
import ch.unibas.fitting.web.web.HeaderPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.List;

/**
 * Created by martin on 29.05.2016.
 */
public class OverviewPage extends HeaderPage {
    public OverviewPage() {

        add(new AjaxLink("upload") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(UploadPage.class);
            }
        });

        add(new DataView<EntryViewModel>("molecules", loadMolecuels())
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<EntryViewModel> item)
            {
                EntryViewModel mol = item.getModelObject();
//                item.add(new ActionPanel("actions", item.getModel()));
                item.add(new Label("name", mol.getName()));
                item.add(new Label("added", mol.getAdded()));


//                item.add(AttributeModifier.replace("class", new AbstractReadOnlyModel<String>()
//                {
//                    private static final long serialVersionUID = 1L;
//
//                    @Override
//                    public String getObject()
//                    {
//                        return (item.getIndex() % 2 == 1) ? "even" : "odd";
//                    }
//                }));
            }
        });
    }

    private IDataProvider<EntryViewModel> loadMolecuels() {

        return new ListDataProvider<EntryViewModel>() {
            @Override
            protected List getData() {
                return super.getData();
            }
        };
    }
}
