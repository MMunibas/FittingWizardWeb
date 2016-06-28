package ch.unibas.fitting.web.application;

import ch.unibas.fitting.shared.javaextensions.Function2;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * Created by mhelmer-mobile on 17.06.2016.
 */
public interface IBackgroundTasks {
    <T> TaskHandle<T> execute(String username,
                              String title,
                              Callable<T> callable,
                              Function2<T, PageParameters, Class> nextPageCallback,
                              Class cancelPage);

    <T> Optional<TaskHandle> getHandle(UUID taskId);
    <T> Optional<TaskHandle> getHandleForUser(String username);

    Optional<Class> cancel(UUID taskId);

    void remove(TaskHandle th);
}
