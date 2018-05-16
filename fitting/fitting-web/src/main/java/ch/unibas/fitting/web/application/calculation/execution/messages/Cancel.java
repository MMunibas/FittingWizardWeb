package ch.unibas.fitting.web.application.calculation.execution.messages;

public class Cancel {
    public final String calcId;

    public Cancel(String calcId){
        this.calcId = calcId;
    }
    public Cancel(){
        this.calcId = null;
    }
}
