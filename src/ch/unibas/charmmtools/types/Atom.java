/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */

package ch.unibas.charmmtools.types;

/**
 *
 * @author hedin
 */
public class Atom {

    private int id;
    private String symbol;

    public Atom(int _id) {
        this.id = _id;
    }

    public Atom(int _id, String _symbol) {
        this.id = _id;
        this.symbol = _symbol;
    }

}//end class
