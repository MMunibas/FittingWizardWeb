package ch.unibas.fitting.web.ljfit.step1;

import java.io.Serializable;
import org.apache.wicket.markup.html.form.upload.FileUpload;

/**
 * Created by tschmidt on 23.06.2016.
 */
public class ExtraParameterViewModel implements Serializable {
    private Integer ncpusDeltaH;
    private Integer ncpusDeltaG;
    private String clusterName;

    public ExtraParameterViewModel(int ncpusDeltaH, int ncpusDeltaG, String clusterName) {
        this.ncpusDeltaH = ncpusDeltaH;
        this.ncpusDeltaG = ncpusDeltaG;
        this.clusterName = clusterName;
    }

    public Integer getNcpusDeltaH() {
        return ncpusDeltaH;
    }

    public void setNcpusDeltaH(Integer ncpusDeltaH) {
        this.ncpusDeltaH = ncpusDeltaH;
    }

    public Integer getNcpusDeltaG() {
        return ncpusDeltaG;
    }

    public void setNcpusDeltaG(Integer ncpusDeltaG) {
        this.ncpusDeltaG = ncpusDeltaG;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }
}
