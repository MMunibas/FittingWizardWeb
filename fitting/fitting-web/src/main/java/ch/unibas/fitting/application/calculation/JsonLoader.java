package ch.unibas.fitting.application.calculation;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class JsonLoader {
    public static JsonObject getJsonObject(File file) throws IOException {
        try (var fr = new FileReader(file)) {
            try (var jr = new JsonReader(fr)) {
                var parser = new JsonParser();
                var obj = parser.parse(jr);
                return obj.getAsJsonObject();
            }
        }
    }
}
