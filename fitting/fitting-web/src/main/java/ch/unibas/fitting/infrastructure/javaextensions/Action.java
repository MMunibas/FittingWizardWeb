package ch.unibas.fitting.infrastructure.javaextensions;


import java.io.Serializable;

@FunctionalInterface
public interface Action extends Serializable {
    void execute();
}

