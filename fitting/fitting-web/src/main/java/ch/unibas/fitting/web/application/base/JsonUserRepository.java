package ch.unibas.fitting.web.application.base;

import com.google.gson.Gson;
import org.apache.log4j.Logger;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mhelmer-mobile on 15.06.2016.
 */
public abstract class JsonUserRepository<T> {
    protected final Logger logger;

    private Class<T> _clazz;
    private final Gson _gson;
    private final HashMap<String, List<String>>_data = new HashMap<>();

    protected JsonUserRepository() {
        logger = Logger.getLogger(getClass());
        _gson = creatGson();
        _clazz = (Class<T>) this.getClass();
    }

    public void save(String username, T obj) {
        String json = _gson.toJson(obj);
        if (!_data.containsKey(username))
            _data.put(username, new ArrayList<>());
        _data.get(username).add(json);
    }

    public List<T> loadAll(String username) {
        if (!_data.containsKey(username))
            return new ArrayList<T>(0);
        return _data.get(username)
                .stream()
                .map(s -> _gson.fromJson(s, _clazz))
                .collect(Collectors.toList());
    }

    protected Gson creatGson() {
        return new Gson();
    }
}
