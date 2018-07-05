package ch.unibas.fitting.web.misc;

import ch.unibas.fitting.application.calculation.manager.CalculationProtocol;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class PageNavigation {

    public static void ToPageWithParameter(Class page, String key, String value){
        PageParameters pp = new PageParameters();
        pp.add(key, value);
        RequestCycle.get().setResponsePage(page, pp);
    }

    public static void ToPage(Class page) {
        RequestCycle.get().setResponsePage(page);
    }


    public static void ToProgressForCalculation(CalculationProtocol.StartResponse response) {
        ToPageWithParameter(ProgressPage.class, "group_id", String.valueOf(response.groupId));
    }

    public static void ToProgressForCalculation(String groupId) {
        ToPageWithParameter(ProgressPage.class, "group_id", String.valueOf(groupId));
    }
}
