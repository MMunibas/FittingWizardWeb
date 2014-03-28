package ch.scs.unibas.fittingwizard.application.tools.charges;

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
