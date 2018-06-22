package ch.unibas.fitting.web.misc;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by martin on 05.06.2016.
 */
public class AdminPage extends HeaderPage {

    @Inject
    private SessionCounter counter;

    public AdminPage() {
        add(new DataView<Entry>("sessions", load())
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final Item<Entry> item)
            {
                Entry e = item.getModelObject();

                item.add(new Label("id", e.getId()));
                item.add(new Label("name", e.getName()));
                item.add(new Label("created", e.getCreated()));
            }
        });
    }

    private IDataProvider<Entry> load() {

        List<Entry> data = counter
                .getSessions()
                .stream()
                .map(s -> new Entry(s.getId(), s.getUsername(), s.getCreated()))
                .collect(Collectors.toList());

        return new ListDataProvider<>(data);
    }

    public static class Entry implements Serializable {
        private String id;
        private String name;
        private String created;

        public Entry(String id, String name, DateTime created) {
            this.id = id;
            this.name = name;
            this.created = created.toString("dd.MM.YYYY HH:mm:ss");
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getCreated() {
            return created;
        }
    }
}
