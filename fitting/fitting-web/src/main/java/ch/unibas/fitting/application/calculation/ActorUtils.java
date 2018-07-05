package ch.unibas.fitting.application.calculation;

import java.util.UUID;

public class ActorUtils {

    public static String generateUniqueId(){
        return UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Returns the given name with a
     * @param name
     * @return
     */
    public static String uniqueName(String name) {
        return String.format("%s_%s", name, generateUniqueId());
    }
}