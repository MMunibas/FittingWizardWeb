package ch.unibas.fitting.web.application.calculation.task.messages;

import akka.actor.ActorRef;

import java.util.List;

public class ListTasksResponse {
    public List<ActorRef> tasks;
    public ListTasksResponse(List<ActorRef> tasks){
        this.tasks = tasks;
    }
}
