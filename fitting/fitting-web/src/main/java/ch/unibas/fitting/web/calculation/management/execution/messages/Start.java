package ch.unibas.fitting.web.calculation.management.execution.messages;

import ch.unibas.fitting.web.calculation.NavigationInfo;

public class Start {
    public String title;
    public String username;
    public StartDefinition[] starts;
    public NavigationInfo navigationInfo;

    public Start(String title, String username, NavigationInfo navigationInfo, StartDefinition... starts){
        this.starts = starts;
        this.title = title;
        this.username = username;
        this.navigationInfo = navigationInfo;
    }
}
