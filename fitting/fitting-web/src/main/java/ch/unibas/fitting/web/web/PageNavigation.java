package ch.unibas.fitting.web.web;

import ch.unibas.fitting.web.application.ProgressPageTaskHandle;
import ch.unibas.fitting.web.web.progress.ProgressPage;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class PageNavigation {

    public static void ToProgressForTask(ProgressPageTaskHandle handle) {
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
}
