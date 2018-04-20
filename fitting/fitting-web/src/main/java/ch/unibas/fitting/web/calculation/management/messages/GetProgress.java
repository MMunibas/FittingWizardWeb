package ch.unibas.fitting.web.calculation.management.messages;

public class GetProgress {
    public final String calcId;

    public GetProgress(String calcId){
        this.calcId = calcId;
    }
}
