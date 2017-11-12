package ch.unibas.fitting.shared.infrastructure;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joda.time.DateTime;

public class JsonSerializer {
    private final Gson gson;

    public JsonSerializer() {
        gson = new GsonBuilder()
                .serializeSpecialFloatingPointValues()
                .registerTypeAdapter(DateTime.class, new DateTimeConverter())
                .create();
    }

    public String toJson(Object src) {
        return gson.toJson(src);
    }

    public <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }
}
