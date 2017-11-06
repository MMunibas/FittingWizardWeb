package ch.unibas.fitting.web.ljfit.services;

import ch.unibas.fitting.shared.charmm.web.CharmmResult;
import ch.unibas.fitting.shared.workflows.charmm.CharmmInputContainer;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Optional;

/**
 * Created by mhelmer on 27.06.2016.
 */
@Singleton
public class CharmmRepository {
    private HashMap<String, CharmmInputContainer> containerHashMap = new HashMap<>();
    private HashMap<String, CharmmResult> resultHashMap = new HashMap<>();

    public synchronized void saveContainer(String username, CharmmInputContainer container) {
        containerHashMap.put(username, container);
    }

    public synchronized Optional<CharmmInputContainer> getContainerFor(String username) {
        if (containerHashMap.containsKey(username))
            return Optional.of(containerHashMap.get(username));
        return Optional.empty();
    }

    public synchronized void removeContainer(String username) {
        containerHashMap.remove(username);
    }

    public synchronized void saveResult(String username, CharmmResult result) {
        resultHashMap.put(username, result);
    }

    public synchronized Optional<CharmmResult> getResultFor(String username) {
        if (resultHashMap.containsKey(username))
            return Optional.of(resultHashMap.get(username));
        return Optional.empty();
    }

    public synchronized void removeResultFor(String username) {
        resultHashMap.remove(username);
    }
}
