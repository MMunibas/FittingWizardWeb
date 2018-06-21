package ch.unibas.fitting.web.application.task;

import ch.unibas.fitting.web.infrastructure.javaextensions.Function1;
import ch.unibas.fitting.web.infrastructure.javaextensions.Function2;
import org.apache.log4j.Logger;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created by mhelmer-mobile on 17.06.2016.
 */
public class TaskHandle<T> implements ITaskContext {

    private static final Logger LOGGER = Logger.getLogger(TaskHandle.class);

    private final Function1<ITaskContext, T> callable;

    private final Function2<T, PageParameters, Class> nextPageCallback;
    private final Class cancelPage;

    private final String username;
    private final UUID id = UUID.randomUUID();
    private final DateTime startTime = DateTime.now();

    private String status;
    private Future<T> future;

    private final TaskContext taskContext;

    public TaskHandle(String username,
                      TaskContext taskContext,
                      Function1<ITaskContext, T> callable,
                      Function2<T, PageParameters, Class> nextPageCallback,
                      Class cancelPage) {
        this.username = username;
        this.taskContext = taskContext;
        this.callable = callable;
        this.nextPageCallback = nextPageCallback;
        this.cancelPage = cancelPage;
    }

    public void submit(ExecutorService executor) {
        future = executor.submit(() -> {
            LOGGER.debug("Started task for user [" + username + "] title [" + taskContext.getTitle() + "] id [" + id + "]");

            status = "Started";
            T result = callable.apply(this);
            status = "Succeeded";

            LOGGER.debug("Succeeded task for user [" + username + "] title [" + taskContext.getTitle() + "] id [" + id + "]");
            return result;
        });
        status = "Queued for execution";
        LOGGER.debug("Submitted task for user [" + username + "] title [" + taskContext.getTitle() + "] id [" + id + "]");
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
        return taskContext.getTitle();
    }

    public Class getCancelPage() {
        return cancelPage;
    }

    public TaskContext getTaskContext() {
        return taskContext;
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

    public Duration getRunningTime() {
        Duration diff = Duration.millis(DateTime.now().getMillis() - getStartTime().getMillis());
        return diff;
    }

    public boolean hasError() {
        Throwable e = getException();
        return e != null && !(e instanceof InterruptedException);
    }

    public String getStatus() {
        return status;
    }

    @Override
    public synchronized void setStatus(String status) {
        this.status = status;
    }
}
