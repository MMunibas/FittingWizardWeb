package ch.unibas.fitting.infrastructure.javaextensions;

import io.vavr.collection.List;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class FutureUtils {

    public static <T> List<T> aggregate(List<CompletionStage<T>> futures) {
        var array = futures.map(s -> s.toCompletableFuture());
        return CompletableFuture.allOf(array.toJavaList().toArray(new CompletableFuture[0]))
                .thenApply(x -> array.map(f -> f.join()))
                .join();
    }

}
