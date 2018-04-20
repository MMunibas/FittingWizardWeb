package ch.unibas.fitting.web.calculation.management.messages;

import java.util.ArrayList;
import java.util.List;

public class ListExecutionsResponse {
    public List<ExecutionProgress> responses;
    public ListExecutionsResponse(Iterable<Object> responses){

        this.responses = new ArrayList<>();
        responses.forEach(x->this.responses.add((ExecutionProgress)x));
    }

}
