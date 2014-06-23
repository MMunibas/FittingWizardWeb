/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.application.scripts.base;

/**
 * User: mhelmer
 * Date: 28.11.13
 * Time: 14:30
 */
public interface IScript<TIn, TOut> {
    TOut execute(TIn input);
}
