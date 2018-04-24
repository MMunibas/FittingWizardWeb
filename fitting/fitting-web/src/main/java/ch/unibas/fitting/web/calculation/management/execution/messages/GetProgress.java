package ch.unibas.fitting.web.calculation.management.execution.messages;

public class GetProgress {
    public final String calcId;

    public GetProgress(String calcId){
        this.calcId = calcId;
    }
}
