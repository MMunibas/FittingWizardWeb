package ch.unibas.fitting.web.ljfit.fitting.step2.clusterparams;

import java.io.Serializable;

/**
 * Created by tschmidt on 23.06.2016.
 */
public class ClusterParameterViewModel implements Serializable {
    private Integer ncpus;

    public ClusterParameterViewModel(
            int ncpus) {
        this.ncpus = ncpus;
    }

    public Integer getNcpus() {
        return ncpus;
    }

    public void setNcpus(Integer ncpus) {
        this.ncpus = ncpus;
    }

}
