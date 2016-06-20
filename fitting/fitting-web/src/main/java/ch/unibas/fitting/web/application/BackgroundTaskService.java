package ch.unibas.fitting.web.application;

import ch.unibas.fitting.shared.javaextensions.Function2;
import ch.unibas.fitting.web.web.WizardPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by mhelmer-mobile on 16.06.2016.
 */
public class BackgroundTaskService implements IBackgroundTasks {

    private final ExecutorService executor;
    private final HashMap<String, UUID> usernames = new HashMap<>();
    private final HashMap<UUID, TaskHandle> handles = new HashMap<>();

    public BackgroundTaskService() {
        executor = Executors.newFixedThreadPool(2);
    }

    @Override
    public <T> TaskHandle<T> execute(String username,
                                     String title,
                                     Callable<T> callable,
                                     Function2<T, PageParameters, Class> nextPageCallback) {
        Future<T> f = executor.submit(callable);
        TaskHandle handle = new TaskHandle<T>(username, title, f, nextPageCallback);
        usernames.put(handle.getUsername(), handle.getId());
        handles.put(handle.getId(), handle);
        return handle;
    }

    @Override
    public <T> TaskHandle<T> getHandle(UUID taskId) {
        return handles.get(taskId);
    }

    @Override
    public <T> TaskHandle<T> getHandleForUser(String username) {
        UUID id = usernames.get(username);
        if (id != null)
            return handles.get(id);
        else
            return null;
    }

    @Override
    public void cancel(UUID taskId) {
        TaskHandle h = handles.get(taskId);
        h.cancel();
        usernames.remove(h.getUsername());
        handles.remove(taskId);
    }
}
