package ch.unibas.fitting.web.calculation.management.execution.messages;

public class Start {
    public String title;
    public StartDefinition[] starts;

    public Start(String title, StartDefinition... starts){
        this.starts = starts;
        this.title = title;
    }
}
