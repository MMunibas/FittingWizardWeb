package ch.unibas.fitting.web.gaussian;

import java.io.Serializable;

/**
 * Created by martin on 05.06.2016.
 */
public class AtomViewModel implements Serializable {
    private final String name;
    private final int index;
    private final double x;
    private final double y;
    private final double z;

    public AtomViewModel(String name, int index, double x, double y, double z) {
        this.name = name;
        this.index = index;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
}
