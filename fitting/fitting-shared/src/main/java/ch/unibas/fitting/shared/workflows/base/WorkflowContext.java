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
 * Time: 09:27
 */
public abstract class WorkflowContext<TParam> {
    public abstract void setCurrentStatus(String status);

    public abstract TParam getParameter();

    public static <TParam> WorkflowContext<TParam> withInput(final TParam param) {
        return new WorkflowContext<TParam>() {
            @Override
            public void setCurrentStatus(String status) {
            }

            @Override
            public TParam getParameter() {
                return param;
            }
        };
    }
}
