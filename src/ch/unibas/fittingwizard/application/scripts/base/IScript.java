package ch.unibas.fittingwizard.application.scripts.base;

/**
 * User: mhelmer
 * Date: 28.11.13
 * Time: 14:30
 */
public interface IScript<TIn, TOut> {
    TOut execute(TIn input);
}
