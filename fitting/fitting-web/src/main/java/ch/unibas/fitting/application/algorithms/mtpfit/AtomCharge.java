package ch.unibas.fitting.application.algorithms.mtpfit;

public class AtomCharge {
    public final String atomLabel;
    public final int index;
    public final double charge;

    public AtomCharge(String atomLabel,
                      int index,
                      double charge) {
        this.atomLabel = atomLabel;
        this.index = index;
        this.charge = charge;
    }
}
