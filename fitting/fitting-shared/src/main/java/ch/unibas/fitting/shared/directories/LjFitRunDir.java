package ch.unibas.fitting.shared.directories;

import java.io.File;
import java.time.Instant;

/**
 * Created by tschmidt on 24.06.2016.
 */
public class LjFitRunDir extends FittingDirectory {

    private File gas_dir;
    private File gas_vdw_dir;
    private File gas_mtp_dir;

    private File solv_dir;
    private File solv_vdw_dir;
    private File solv_mtp_dir;

    public LjFitRunDir(String username, File directory) {
        this(username, directory, Instant.now().getEpochSecond());
    }

    public LjFitRunDir(String username, File directory, long time) {
        super(username, directory);

        gas_dir = new File(getDirectory(), "gas_" + time);
        gas_vdw_dir  = new File(gas_dir, "vdw");
        gas_mtp_dir  = new File(gas_dir, "mtp");

        solv_dir = new File(getDirectory(), "solv_" + time);
        solv_vdw_dir = new File(solv_dir, "vdw");
        solv_mtp_dir = new File(solv_dir, "mtp");
        gas_vdw_dir.mkdirs();
        gas_mtp_dir.mkdirs();
        solv_vdw_dir.mkdirs();
        solv_mtp_dir.mkdirs();
    }

    public File getGasDir() {return gas_dir;}

    public File getGasVdwDir() {
        return gas_vdw_dir;
    }

    public File getGasMtpDir() {
        return gas_mtp_dir;
    }

    public File getSolvDir() {return solv_dir;}

    public File getSolvVdwDir() {
        return solv_vdw_dir;
    }

    public File getSolvMtpDir() {
        return solv_mtp_dir;
    }

    public File getRunJson() {return new File(getDirectory(),"run.json");}
}
