/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.charges;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: mhelmer
 * Date: 11.12.13
 * Time: 18:12
 */
public class ChargeTypes {
    public static final String charge;
    public static final List<String> dipole;
    public static final List<String> quadripole;
    public static final List<String> all;

    static {
        charge = "Q00";
        dipole = Arrays.asList( "Q10", "Q1C", "Q1S" );
        quadripole =Arrays.asList( "Q20", "Q21C", "Q21S", "Q22C", "Q22S" );

        all = new ArrayList<>();
        all.add(charge);
        all.addAll(dipole);
        all.addAll(quadripole);
    }
}
