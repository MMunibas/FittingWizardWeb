package ch.unibas.fitting.web.application.calculation;

import java.util.Random;
import java.util.UUID;

public class ActorUtils {

    private static Random random = new Random();

    public static String generateUniqueId(){
        return UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Returns the given name with a
     * @param name
     * @return
     */
    public static String nameWithUniqueSuffix(String name) {
        return String.format("%s_%s", name, generateUniqueId());
    }
}
