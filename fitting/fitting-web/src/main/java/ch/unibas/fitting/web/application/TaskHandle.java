package ch.unibas.fitting.web.application;

import org.joda.time.DateTime;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by mhelmer-mobile on 17.06.2016.
 */
public class TaskHandle<T> {

    private String title;
    private final Future<T> future;
    private final UUID id;
    private final DateTime startTime;
    private String username;

    public TaskHandle(String username, String title, Future<T> future) {
        this.username = username;
        this.title = title;
        this.future = future;
        this.id = UUID.randomUUID();
        this.startTime = DateTime.now();
    }

    public T getResult() {
        try {
            return future.get();
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public UUID getId() {
        return id;
    }

    public boolean isDone() {
        return future.isDone();
    }

    public boolean wasSuccessful() {
        try {
            future.get();
        } catch (ExecutionException e) {
            return false;
        } catch (InterruptedException e) {
            return false;
        }
        return true;
    }

    public void cancel() {
        future.cancel(true);
    }

    public String getUsername() {
        return username;
    }

    public String getTitle() {
        return title;
    }
}
