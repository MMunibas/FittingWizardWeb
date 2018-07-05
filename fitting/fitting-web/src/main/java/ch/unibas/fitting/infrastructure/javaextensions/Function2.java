package ch.unibas.fitting.infrastructure.javaextensions;

@FunctionalInterface
public interface Function2<One, Two, Return> {
    Return apply(One one, Two two);
}
