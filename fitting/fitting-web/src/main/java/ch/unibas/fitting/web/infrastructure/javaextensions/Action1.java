package ch.unibas.fitting.web.infrastructure.javaextensions;

@FunctionalInterface
public interface Action1<T1> {
    void execute(T1 param);
}
