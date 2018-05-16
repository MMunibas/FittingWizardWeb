package ch.unibas.fitting.web.application.calculation.task.messages;

import ch.unibas.fitting.web.application.calculation.execution.messages.ExecutionProgress;
import ch.unibas.fitting.web.application.calculation.execution.messages.ListExecutionsResponse;

import java.util.List;
import java.util.stream.Collectors;

public class ListTaskExecutionsResponse {
    public List<ExecutionProgress> responses;

    public ListTaskExecutionsResponse(List<ListExecutionsResponse> answers) {
        responses = answers.stream().flatMap(x->x.responses.stream()).collect(Collectors.toList());
    }
}
