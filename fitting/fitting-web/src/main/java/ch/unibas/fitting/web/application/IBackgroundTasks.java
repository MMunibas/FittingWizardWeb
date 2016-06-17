package ch.unibas.fitting.web.application;

import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * Created by mhelmer-mobile on 17.06.2016.
 */
public interface IBackgroundTasks {
    <T> TaskHandle<T> execute(String username, String title, Callable<T> callable);

    <T> TaskHandle getHandle(UUID taskId);
    <T> TaskHandle<T> getHandleForUser(String username);

    void cancel(UUID taskId);
}
