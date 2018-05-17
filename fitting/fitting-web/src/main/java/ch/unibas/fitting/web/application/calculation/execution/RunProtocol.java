package ch.unibas.fitting.web.application.calculation.execution;

public class RunProtocol {
    public static class GetDetails {
    }

    public static class GetDetailsResponse {
        public RunDetails details;
        public GetDetailsResponse(RunDetails details){
            this.details = details;
        }

    }

    public static class StatusChanged {
        public final RunDetails details;

        public StatusChanged(RunDetails details) {
            this.details = details;
        }
    }

    public static class Cancel {}
    public static class Cleanup {}
}
