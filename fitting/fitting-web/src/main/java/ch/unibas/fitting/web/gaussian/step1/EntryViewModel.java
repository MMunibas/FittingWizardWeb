package ch.unibas.fitting.web.gaussian.step1;

import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * Created by martin on 05.06.2016.
 */
public class EntryViewModel implements Serializable {
    private String name;
    private String added;

    public EntryViewModel(String name, DateTime added) {
        this.name = name;
        this.added = added.toString("dd.MM.YYYY HH:mm:ss");
    }

    public String getName() {
        return name;
    }

    public String getAdded() {
        return added;
    }
}
