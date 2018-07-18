package ch.unibas.fitting.infrastructure.javaextensions;

import java.io.Serializable;

@FunctionalInterface
public interface Action1<T1> extends Serializable {
    void execute(T1 param);
}
