/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.molecules;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * User: mhelmer
 * Date: 25.11.13
 * Time: 14:39
 */
public class Atom {
    private StringProperty name = new SimpleStringProperty();
    private DoubleProperty x = new SimpleDoubleProperty();
    private DoubleProperty y = new SimpleDoubleProperty();
    private DoubleProperty z = new SimpleDoubleProperty();

    public Atom(String name, double x, double y, double z) {
        this.name.setValue(name);
        this.x.setValue(x);
        this.y.setValue(y);
        this.z.setValue(z);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public double getX() {
        return x.get();
    }

    public DoubleProperty xProperty() {
        return x;
    }

    public double getY() {
        return y.get();
    }

    public DoubleProperty yProperty() {
        return y;
    }

    public double getZ() {
        return z.get();
    }

    public DoubleProperty zProperty() {
        return z;
    }

    @Override
    public String toString() {
        return "Atom{" +
                "name=" + name.getValue() +
                ", x=" + x.getValue() +
                ", y=" + y.getValue() +
                ", z=" + z.getValue() +
                '}';
    }
}
