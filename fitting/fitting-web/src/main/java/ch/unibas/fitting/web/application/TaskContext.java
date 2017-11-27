package ch.unibas.fitting.web.application;

import io.vavr.control.Option;

public class TaskContext {
    private final String title;
    private final Option<PageContext> originPage;
    private final PageContext cancelPage;

    public TaskContext(
            String title,
            Option<PageContext> originPage,
            PageContext cancelPage) {
        this.title = title;
        this.originPage = originPage;
        this.cancelPage = cancelPage;
    }

    public String getTitle() {
        return title;
    }

    public PageContext getCancelPage() {
        return cancelPage;
    }

    public Option<PageContext> getOriginPage() {
        return originPage;
    }
}
