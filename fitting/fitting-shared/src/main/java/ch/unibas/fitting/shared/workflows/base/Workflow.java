/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.workflows.base;

/**
 * User: mhelmer
 * Date: 11.12.13
 * Time: 09:29
 */
public interface Workflow<TIn, TOut> {
    TOut execute(WorkflowContext<TIn> status);
}
