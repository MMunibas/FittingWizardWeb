package ch.unibas.fitting.web.application.task;

import ch.unibas.fitting.web.infrastructure.javaextensions.Function1;
import ch.unibas.fitting.web.infrastructure.javaextensions.Function2;
import io.vavr.control.Option;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.UUID;

/**
 * Created by mhelmer-mobile on 17.06.2016.
 */
@Deprecated
public interface IBackgroundTasks {
    <T> TaskHandle<T> spawnTask(String username,
                                String title,
                                Function1<ITaskContext, T> callable,
                                Function2<T, PageParameters, Class> nextPageCallback,
                                Class cancelPage,
                                Option<PageContext> originPage);

    <T> Option<TaskHandle> getHandle(UUID taskId);
    <T> Option<TaskHandle> getHandleForUser(String username);

    Option<Class> cancel(UUID taskId);

    void remove(TaskHandle th);
}
