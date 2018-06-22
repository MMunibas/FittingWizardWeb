package ch.unibas.fitting.web.calculation;

import ch.unibas.fitting.infrastructure.javaextensions.Action;
import io.vavr.control.Option;

public class NavigationInfo
{
    public final Action doneCallback;
    public final Action cancelCallback;

    public NavigationInfo(Action doneCallback, Action cancelCallback) {
        this.doneCallback = doneCallback;
        this.cancelCallback = cancelCallback;
    }

    public NavigationInfo(Action doneCallback) {
        this(doneCallback, doneCallback);
    }
}
