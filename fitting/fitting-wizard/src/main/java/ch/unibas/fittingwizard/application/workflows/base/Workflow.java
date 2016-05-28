/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fittingwizard.application.workflows.base;

/**
 * User: mhelmer
 * Date: 11.12.13
 * Time: 09:29
 */
public abstract class Workflow<TIn, TOut> {
    public abstract TOut execute(WorkflowContext<TIn> status);
}
