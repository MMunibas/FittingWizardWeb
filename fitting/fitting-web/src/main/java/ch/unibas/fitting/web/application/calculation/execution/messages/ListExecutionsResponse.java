package ch.unibas.fitting.web.application.calculation.execution.messages;

import java.util.List;

public class ListExecutionsResponse {
    public List<ExecutionProgress> responses;
    public ListExecutionsResponse(List<ExecutionProgress> responses){
        this.responses = responses;
    }

}
