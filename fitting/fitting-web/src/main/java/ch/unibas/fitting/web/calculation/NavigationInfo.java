package ch.unibas.fitting.web.calculation;

import ch.unibas.fitting.shared.javaextensions.Action;
import ch.unibas.fitting.web.application.task.PageContext;
import io.vavr.control.Option;

public class NavigationInfo
{
    public final Action doneCallback;
    public final Action cancelCallback;
    public final Option<PageContext> originPage;

    public NavigationInfo(Action doneCallback, Action cancelCallback, PageContext originPage) {
        this.doneCallback = doneCallback;
        this.cancelCallback = cancelCallback;
        this.originPage = Option.of(originPage);
    }

    public NavigationInfo(Action doneCallback, Action cancelCallback) {
        this(doneCallback, cancelCallback, null);
    }

    public NavigationInfo(Action doneCallback) {
        this(doneCallback, doneCallback);
    }
}
