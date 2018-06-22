package ch.unibas.fitting.infrastructure;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.vavr.control.Option;
import io.vavr.gson.VavrGson;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class JsonSerializer {
    private final Gson gson;

    public JsonSerializer() {

        GsonBuilder builder = new GsonBuilder()
                .serializeSpecialFloatingPointValues()
                .registerTypeAdapter(DateTime.class, new DateTimeConverter());
        VavrGson.registerAll(builder);
        gson = builder.create();
    }

    public String toJson(Object src) {
        return gson.toJson(src);
    }

    public <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    public void writeJsonFile(File file, Object src) {
        String json = toJson(src);
        try {
            FileUtils.write(file, json, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException("save json file failed " + file, e);
        }
    }

    public  <T> Option<T> readJsonFile(File file, Class<T> classOfT) {
        if (!file.isFile())
            return Option.none();
        try {
            String content = FileUtils.readFileToString(file, Charset.defaultCharset());
            T result = fromJson(content, classOfT);
            return Option.of(result);
        } catch (Exception e) {
            throw new RuntimeException("Could not deserialize file " + file, e);
        }
    }
}
