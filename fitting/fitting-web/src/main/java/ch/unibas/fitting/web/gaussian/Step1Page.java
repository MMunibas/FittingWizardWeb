package ch.unibas.fitting.web.gaussian;

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
public class Step1Page extends HeaderPage {
    public Step1Page() {

        add(new AjaxLink("upload") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(UploadPage.class);
            }
        });

        add(new DataView<Step1Entry>("molecules", loadMolecuels())
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<Step1Entry> item)
            {
                Step1Entry mol = item.getModelObject();
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

    private IDataProvider<Step1Entry> loadMolecuels() {

        List<Step1Entry> list = Arrays.asList(
            new Step1Entry("test", DateTime.now().minusDays(3)),
            new Step1Entry("test 2", DateTime.now().minusDays(2)),
            new Step1Entry("test 3", DateTime.now().plusDays(3))
        );

        return new ListDataProvider<>(list);
    }
}
