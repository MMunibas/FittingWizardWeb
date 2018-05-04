package ch.unibas.fitting.web.calculation.management.execution.messages;

import java.util.List;

public class ListExecutionsResponse {
    public List<ExecutionProgress> responses;
    public ListExecutionsResponse(List<ExecutionProgress> responses){
        this.responses = responses;
    }

}
