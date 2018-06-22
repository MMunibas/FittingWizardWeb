package ch.unibas.fitting.web.calculation;

import ch.unibas.fitting.infrastructure.javaextensions.Action;
import io.vavr.control.Option;

/**
 * Allows to define how the {@link ch.unibas.fitting.web.misc.ProgressPage} behaves
 * when the continue button is clicked. This is used to navigate to the result page
 * or go back to the input page.
 */
public class NavigationInfo
{
    /**
     * Callback executed when at least one calculation was successful.
     */
    public final Action doneCallback;
    /**
     * Callback executed when all calculations were unsuccessful.
     */
    public final Action cancelCallback;

    public NavigationInfo(Action doneCallback, Action cancelCallback) {
        this.doneCallback = doneCallback;
        this.cancelCallback = cancelCallback;
    }

    public NavigationInfo(Action doneCallback) {
        this(doneCallback, doneCallback);
    }
}
