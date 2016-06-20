package ch.unibas.fitting.shared.javaextensions;

@FunctionalInterface
public interface Function2<One, Two, Return> {
    public Return apply(One one, Two two);
}
