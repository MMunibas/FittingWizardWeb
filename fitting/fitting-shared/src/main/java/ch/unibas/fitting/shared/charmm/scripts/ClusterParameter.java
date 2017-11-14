package ch.unibas.fitting.shared.charmm.scripts;

public class ClusterParameter {
    public final int ncpus;
    public final String clusterName;

    public ClusterParameter(int ncpus, String clusterName) {
        this.ncpus = ncpus;
        this.clusterName = clusterName;
    }
}
