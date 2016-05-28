/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.presentation.fitting;

import javafx.scene.paint.Color;

/**
 * User: mhelmer
 * Date: 05.12.13
 * Time: 11:51
 */
public class ColorCoder {
    private final Color minColor;
    private final Color maxColor;

    /**
     * Initializes a default color coder from red to green;
     */
    public ColorCoder() {
        this(Color.rgb(0, 255, 0), Color.rgb(255, 0, 0));
    }

    public ColorCoder(Color minColor, Color maxColor) {
        this.minColor = minColor;
        this.maxColor = maxColor;
    }

    public Color getColor(double min, double max, double value) {
        return getColor(max - min, value - min);
    }

    public Color getColor(double max, double value) {
        if (value > max)
            throw new IllegalArgumentException("value must be less or equal to max");
        if (value < 0)
            throw new IllegalArgumentException("value must be greater than zero");

        Color color;
        if (value == 0.0)
            color = minColor;
        else if (value == max)
            color = maxColor;
        else {
            double minFactor = (max - value) / max;
            double maxFactor = value / max;
            double r = minColor.getRed() * minFactor + maxColor.getRed() * maxFactor;
            double g = minColor.getGreen() * minFactor + maxColor.getGreen() * maxFactor;
            double b = minColor.getBlue() * minFactor + maxColor.getBlue() * maxFactor;
            color = Color.color(r, g, b);
        }
        return color;
    }
}
