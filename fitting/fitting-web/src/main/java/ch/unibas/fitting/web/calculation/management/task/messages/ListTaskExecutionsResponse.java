package ch.unibas.fitting.web.calculation.management.task.messages;

import ch.unibas.fitting.web.calculation.management.execution.messages.ExecutionProgress;

import java.util.List;

public class ListTaskExecutionsResponse {
    public List<ExecutionProgress> responses;
    public ListTaskExecutionsResponse(Iterable<Object> responses){
            responses.forEach(x->this.responses.addAll(((ListTaskExecutionsResponse)x).responses));
    }

}
