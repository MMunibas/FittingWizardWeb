package ch.unibas.fitting.web.calculation.management.messages;

public class Cancel {
    public final String calcId;

    public Cancel(String calcId){
        this.calcId = calcId;
    }
}
