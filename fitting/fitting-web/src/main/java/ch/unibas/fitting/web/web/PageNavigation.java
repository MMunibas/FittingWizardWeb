package ch.unibas.fitting.web.web;

import ch.unibas.fitting.web.application.PageContext;
import ch.unibas.fitting.web.application.TaskHandle;
import ch.unibas.fitting.web.web.progress.ProgressPage;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class PageNavigation {

    public static void ToProgressForTask(TaskHandle handle) {
        ToPageWithParameter(ProgressPage.class, "task_id", String.valueOf(handle.getId()));
    }

    public static void ToPageWithParameter(Class page, String key, String value){
        PageParameters pp = new PageParameters();
        pp.add(key, value);
        RequestCycle.get().setResponsePage(page, pp);
    }

    public static void ToPage(Class page) {
        RequestCycle.get().setResponsePage(page);
    }

    public static void ToPage(PageContext context) {
        RequestCycle.get().setResponsePage(context.getPage(), context.getParameter());
    }
}
