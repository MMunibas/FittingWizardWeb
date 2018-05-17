package ch.unibas.fitting.web.application.calculation.execution;

public enum RunState {
    Created,
    Initializing,
    Running,
    Failed,
    Canceled,
    Succeeded,
    Unknown
}
