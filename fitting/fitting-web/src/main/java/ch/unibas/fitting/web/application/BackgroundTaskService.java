package ch.unibas.fitting.web.application;

import ch.unibas.fitting.shared.config.Settings;
import ch.unibas.fitting.shared.javaextensions.Action;
import ch.unibas.fitting.shared.javaextensions.Action1;
import ch.unibas.fitting.shared.javaextensions.Function2;
import org.apache.log4j.Logger;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by mhelmer-mobile on 16.06.2016.
 */
public class BackgroundTaskService implements IBackgroundTasks {

    private static final Logger LOGGER = Logger.getLogger(BackgroundTaskService.class);

    private final ExecutorService executor;
    private final HashMap<String, UUID> usernames = new HashMap<>();
    private final HashMap<UUID, ProgressPageTaskHandle> handles = new HashMap<>();

    @Inject
    public BackgroundTaskService(Settings settings) {
        int n = settings.getNumberOfBackgroundTaskThreads();
        executor = Executors.newFixedThreadPool(n);
    }

    @Override
    public synchronized <T> ProgressPageTaskHandle<T> execute(String username,
                                                              String title,
                                                              Callable<T> callable,
                                                              Function2<T, PageParameters, Class> nextPageCallback,
                                                              Class cancelPage) {
        Future<T> f = executor.submit(callable);
        ProgressPageTaskHandle handle = new ProgressPageTaskHandle<T>(username, title, f, nextPageCallback, cancelPage);
        LOGGER.debug("executing task for user [" + username + "] title [" + title + "] id [" + handle.getId() + "]");
        usernames.put(handle.getUsername(), handle.getId());
        handles.put(handle.getId(), handle);
        return handle;
    }

    @Override
    public synchronized <T> Optional<ProgressPageTaskHandle> getHandle(UUID taskId) {
        return handleFor(taskId);
    }

    @Override
    public synchronized <T> Optional<ProgressPageTaskHandle> getHandleForUser(String username) {
        UUID id = usernames.get(username);
        return handleFor(id);
    }

    @Override
    public synchronized Optional<Class> cancel(UUID taskId) {
        Optional<ProgressPageTaskHandle> h = handleFor(taskId);
        Class page = null;
        if (h.isPresent()) {
            LOGGER.debug("canceling task with id " + taskId);
            h.get().cancel();
            page = h.get().getCancelPage();
            removeReferences(h.get());
        }
        return Optional.ofNullable(page);
    }

    @Override
    public synchronized void remove(ProgressPageTaskHandle th) {
        removeReferences(th);
    }

    private Optional<ProgressPageTaskHandle> handleFor(UUID id) {
        return Optional.ofNullable(handles.get(id));
    }

    private void removeReferences(ProgressPageTaskHandle th) {
        LOGGER.debug("removing task id [" + th.getId() + "] username [" + th.getUsername() + "]");
        usernames.remove(th.getUsername());
        handles.remove(th.getId());
    }
}
