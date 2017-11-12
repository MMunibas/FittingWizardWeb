package ch.unibas.fitting.shared.javaextensions;

@FunctionalInterface
public interface Action1<T1> {
    void execute(T1 param);
}
