package ch.scs.unibas.fittingwizard.application.workflows.base;

/**
 * User: mhelmer
 * Date: 11.12.13
 * Time: 09:29
 */
public abstract class Workflow<TIn, TOut> {
    public abstract TOut execute(WorkflowContext<TIn> status);
}
