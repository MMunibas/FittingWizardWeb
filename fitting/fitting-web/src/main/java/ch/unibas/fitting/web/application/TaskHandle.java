package ch.unibas.fitting.web.application;

import ch.unibas.fitting.shared.javaextensions.Function1;
import ch.unibas.fitting.shared.javaextensions.Function2;
import ch.unibas.fitting.web.web.WizardPage;
import org.apache.log4j.Logger;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Function;

/**
 * Created by mhelmer-mobile on 17.06.2016.
 */
public class TaskHandle<T> implements ITaskContext {

    private static final Logger LOGGER = Logger.getLogger(TaskHandle.class);

    private final String title;
    private final Function1<ITaskContext, T> callable;

    private final Function2<T, PageParameters, Class> nextPageCallback;
    private final Class cancelPage;

    private final String username;
    private final UUID id = UUID.randomUUID();
    private final DateTime startTime = DateTime.now();

    private String status;
    private Future<T> future;

    public TaskHandle(String username,
                      String title,
                      Function1<ITaskContext, T> callable,
                      Function2<T, PageParameters, Class> nextPageCallback,
                      Class cancelPage) {
        this.username = username;
        this.title = title;
        this.callable = callable;
        this.nextPageCallback = nextPageCallback;
        this.cancelPage = cancelPage;
    }

    public void submit(ExecutorService executor) {
        future = executor.submit(() -> {
            LOGGER.debug("Started task for user [" + username + "] title [" + title + "] id [" + id + "]");

            status = "Started";
            T result = callable.apply(this);
            status = "Succeeded";

            LOGGER.debug("Succeeded task for user [" + username + "] title [" + title + "] id [" + id + "]");
            return result;
        });
        status = "Queued for execution";
        LOGGER.debug("Submitted task for user [" + username + "] title [" + title + "] id [" + id + "]");
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

    public Class getCancelPage() {
        return cancelPage;
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
