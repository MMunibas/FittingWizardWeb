package ch.unibas.fitting.web.application;

import ch.unibas.fitting.shared.javaextensions.Function2;
import ch.unibas.fitting.web.web.WizardPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.joda.time.DateTime;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;

/**
 * Created by mhelmer-mobile on 17.06.2016.
 */
public class TaskHandle<T> {

    private String title;
    private final Future<T> future;
    private Function2<T, PageParameters, Class> nextPageCallback;
    private final UUID id;
    private final DateTime startTime;
    private String username;

    public TaskHandle(String username,
                      String title,
                      Future<T> future,
                      Function2<T, PageParameters, Class> nextPageCallback) {
        this.username = username;
        this.title = title;
        this.future = future;
        this.nextPageCallback = nextPageCallback;
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

    public Function2<T, PageParameters, Class> getNextPageCallback() {
        return nextPageCallback;
    }

    public Throwable getException() {
        try {
            future.get();
        } catch (ExecutionException e) {
            return e.getCause();
        } catch (InterruptedException e) {
            return e;
        }
        return null;
    }
}
