/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */

package ch.unibas.charmmtools.internals;

/**
 *
 * @author hedin
 */
public class Improper extends Dihedral {

    public Improper(Atom _a1, Atom _a2, Atom _a3, Atom _a4) {
        super(_a1, _a2, _a3, _a4);
    }

    public Improper(Atom _a1, Atom _a2, Atom _a3, Atom _a4, double _dihe) {
        super(_a1, _a2, _a3, _a4, _dihe);
    }

    public Improper(Atom _a1, Atom _a2, Atom _a3, Atom _a4, boolean _radian) {
        super(_a1, _a2, _a3, _a4, _radian);
    }

    public Improper(Atom _a1, Atom _a2, Atom _a3, Atom _a4, double _dihe, boolean _radian) {
        super(_a1, _a2, _a3, _a4, _dihe, _radian);
    }

}//end class
