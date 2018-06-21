package ch.unibas.fitting.web.gaussian.fit.step1;

import ch.unibas.fitting.web.application.algorithms.mtp.Fit;
import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * Created by mhelmer on 20.06.2016.
 */
public class FitViewModel implements Serializable {

    private int index;
    private DateTime created;
    private double rmse;
    private int rank;

    public FitViewModel(Fit fit) {
        index = fit.getId();
        created = fit.getCreated();
        rmse = fit.getRmse();
        rank = fit.getRank();
    }

    public int getIndex() {
        return index;
    }

    public String getCreated() {
        return created.toString("dd.MM.YYYY HH:mm:ss");
    }

    public double getRmse() {
        return rmse;
    }

    public int getRank() {
        return rank;
    }
}
