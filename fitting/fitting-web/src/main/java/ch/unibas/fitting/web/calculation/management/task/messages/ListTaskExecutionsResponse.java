package ch.unibas.fitting.web.calculation.management.task.messages;

import ch.unibas.fitting.web.calculation.management.execution.messages.ExecutionProgress;
import ch.unibas.fitting.web.calculation.management.execution.messages.ListExecutionsResponse;

import java.util.List;
import java.util.stream.Collectors;

public class ListTaskExecutionsResponse {
    public List<ExecutionProgress> responses;

    public ListTaskExecutionsResponse(List<ListExecutionsResponse> answers) {
        responses = answers.stream().flatMap(x->x.responses.stream()).collect(Collectors.toList());
    }
}
