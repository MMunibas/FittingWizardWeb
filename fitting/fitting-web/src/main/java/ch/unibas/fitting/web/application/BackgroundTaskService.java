package ch.unibas.fitting.web.application;

import ch.unibas.fitting.shared.config.Settings;
import ch.unibas.fitting.shared.javaextensions.Function1;
import ch.unibas.fitting.shared.javaextensions.Function2;
import io.vavr.control.Option;
import org.apache.log4j.Logger;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by mhelmer-mobile on 16.06.2016.
 */
@Singleton
public class BackgroundTaskService implements IBackgroundTasks {

    private static final Logger LOGGER = Logger.getLogger(BackgroundTaskService.class);

    private final ExecutorService executor;
    private final HashMap<String, UUID> usernames = new HashMap<>();
    private final HashMap<UUID, TaskHandle> handles = new HashMap<>();

    @Inject
    public BackgroundTaskService(Settings settings) {
        int n = settings.getNumberOfBackgroundTaskThreads();
        executor = Executors.newFixedThreadPool(n);
    }

    @Override
    public synchronized <T> TaskHandle<T> spawnTask(String username,
                                                    String title,
                                                    Function1<ITaskContext, T> callable,
                                                    Function2<T, PageParameters, Class> nextPageCallback,
                                                    Class cancelPage) {

        TaskHandle handle = new TaskHandle<T>(username, title, callable, nextPageCallback, cancelPage);
        handle.submit(executor);
        usernames.put(handle.getUsername(), handle.getId());
        handles.put(handle.getId(), handle);
        return handle;
    }

    @Override
    public synchronized <T> Option<TaskHandle> getHandle(UUID taskId) {
        return handleFor(taskId);
    }

    @Override
    public synchronized <T> Option<TaskHandle> getHandleForUser(String username) {
        UUID id = usernames.get(username);
        return handleFor(id);
    }

    @Override
    public synchronized Option<Class> cancel(UUID taskId) {
        Option<TaskHandle> h = handleFor(taskId);

        return h.peek(th -> {
            LOGGER.debug("canceling task with id " + taskId);
            th.cancel();
            removeReferences(h.get());
        }).map(th -> th.getCancelPage());
    }

    @Override
    public synchronized void remove(TaskHandle th) {
        removeReferences(th);
    }

    private Option<TaskHandle> handleFor(UUID id) {
        return Option.of(handles.get(id));
    }

    private void removeReferences(TaskHandle th) {
        LOGGER.debug("removing task id [" + th.getId() + "] username [" + th.getUsername() + "]");
        usernames.remove(th.getUsername());
        handles.remove(th.getId());
    }
}
