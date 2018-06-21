package ch.unibas.fitting.web.gaussian.services;

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
