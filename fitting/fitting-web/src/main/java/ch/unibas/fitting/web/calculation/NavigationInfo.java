package ch.unibas.fitting.web.calculation;

import io.vavr.control.Option;
import org.apache.wicket.request.component.IRequestablePage;

public class NavigationInfo
{
    public Option<Class<? extends IRequestablePage>> previousPage = Option.none();
    public Option<Class<? extends IRequestablePage>> returnPage = Option.none();

    public NavigationInfo(Option<Class<? extends IRequestablePage>> previousPage, Option<Class<? extends IRequestablePage>> returnPage) {
        this.previousPage = previousPage;
        this.returnPage = returnPage;
    }
    public NavigationInfo(Option<Class<? extends IRequestablePage>> returnPage) {
        this.previousPage = Option.none();
        this.returnPage = returnPage;
    }
    public NavigationInfo() {
        this.previousPage = Option.none();
        this.returnPage = Option.none();
    }
}
