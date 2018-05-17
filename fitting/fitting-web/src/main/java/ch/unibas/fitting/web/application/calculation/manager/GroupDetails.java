package ch.unibas.fitting.web.application.calculation.manager;

import ch.unibas.fitting.web.application.calculation.execution.RunDetails;
import ch.unibas.fitting.web.calculation.NavigationInfo;
import io.vavr.collection.List;

public class GroupDetails {
    public final String groupId;
    public final String title;
    public final NavigationInfo navigationInfo;
    public final List<RunDetails> runs;

    public GroupDetails(String groupId,
                        String title,
                        NavigationInfo navigationInfo,
                        List<RunDetails> runs){
        this.groupId = groupId;
        this.title = title;
        this.navigationInfo = navigationInfo;
        this.runs = runs;
    }
}
