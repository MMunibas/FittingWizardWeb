package ch.unibas.fitting.web.application.calculation.execution.messages;

import ch.unibas.fitting.web.calculation.NavigationInfo;

public class Start {
    public final String title;
    public final String username;
    public final StartDefinition[] definitions;
    public NavigationInfo navigationInfo;

    public Start(String title,
                 String username,
                 NavigationInfo navigationInfo,
                 StartDefinition... definitions){
        this.definitions = definitions;
        this.title = title;
        this.username = username;
        this.navigationInfo = navigationInfo;
    }
}
