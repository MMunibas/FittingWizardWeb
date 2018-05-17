package ch.unibas.fitting.web.application.task;

import org.apache.wicket.request.mapper.parameter.PageParameters;

public class PageContext {
    private final Class page;
    private final PageParameters parameter;

    public PageContext(Class page) {
        this(page, new PageParameters());
    }

    public PageContext(Class page, String key, String value) {
        this(page, createParameter(key, value));
    }

    public PageContext(Class page, PageParameters parameter) {
        this.page = page;
        this.parameter = parameter;
    }

    private static PageParameters createParameter(String key, String value) {
        PageParameters p = new PageParameters();
        p.add(key, value);
        return p;
    }

    public Class getPage() {
        return page;
    }

    public PageParameters getParameter() {
        return parameter;
    }
}
