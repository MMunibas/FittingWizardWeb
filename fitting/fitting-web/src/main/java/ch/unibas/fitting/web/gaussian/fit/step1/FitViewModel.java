package ch.unibas.fitting.web.gaussian.fit.step1;

import ch.unibas.fitting.shared.fitting.Fit;
import org.joda.time.DateTime;

/**
 * Created by mhelmer on 20.06.2016.
 */
public class FitViewModel {

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

    public DateTime getCreated() {
        return created;
    }

    public double getRmse() {
        return rmse;
    }

    public int getRank() {
        return rank;
    }
}
