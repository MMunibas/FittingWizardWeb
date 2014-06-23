/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */

package ch.unibas.charmmtools.files;

/**
 *
 * @author hedin
 */
public abstract class RTF {

    protected int natom;
    /**
     * Inheriting classes have to redefine this allocator method if storage to arrays is required
     */
    protected abstract void allocate();
}
