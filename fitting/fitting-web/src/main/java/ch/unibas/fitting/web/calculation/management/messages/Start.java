package ch.unibas.fitting.web.calculation.management.messages;

public class Start {
    public StartDefinition[] starts;

    public Start(StartDefinition... starts){
        this.starts = starts;
    }
}
