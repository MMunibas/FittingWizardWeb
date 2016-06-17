package ch.unibas.fitting.web.application;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
    public <T> TaskHandle<T> execute(String username, String title, Callable<T> callable) {
        Future<T> f = executor.submit(callable);
        TaskHandle handle = new TaskHandle<T>(username, title, f);
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
