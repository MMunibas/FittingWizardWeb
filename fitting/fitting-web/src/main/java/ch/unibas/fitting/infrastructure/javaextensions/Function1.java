package ch.unibas.fitting.infrastructure.javaextensions;

@FunctionalInterface
public interface Function1<T1, R> {
    R apply(T1 p1);
}

