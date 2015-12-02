/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.application.base;

import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 * User: mhelmer
 * Date: 03.12.13
 * Time: 14:45
 */
public abstract class MemoryRepository<T> {

    protected final Logger logger;

    protected MemoryRepository() {
        logger = Logger.getLogger(getClass());
    }

    private ArrayList<T> data = new ArrayList<>();

    public ArrayList<T> loadAll() {
        return data;
    }

    public void save(T molecule) {
        if (!data.contains(molecule))
            data.add(molecule);
    }

    public void remove(T molecule) {
        data.remove(molecule);
    }

}
