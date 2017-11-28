package ch.unibas.fitting.shared.directories;

import org.joda.time.DateTime;

import java.io.File;
import java.time.Instant;

/**
 * Created by tschmidt on 24.06.2016.
 */
public class LjFitRunDir extends FittingDirectory {

    private final File deltaG_dir;
    private final File density_dir;

    private final File gas_dir;
    private final File gas_vdw_dir;
    private final File gas_mtp_dir;

    private final File solv_dir;
    private final File solv_vdw_dir;
    private final File solv_mtp_dir;
    private final DateTime created;

    public LjFitRunDir(String username, File directory, long time) {
        super(username, directory);
        created = new DateTime(time);
        deltaG_dir = new File(getDirectory(), "deltaG");
        density_dir = new File(getDirectory(), "density");
        density_dir.mkdirs();
        deltaG_dir.mkdirs();

        gas_dir = new File(deltaG_dir, "gas_" + time);
        gas_vdw_dir  = new File(gas_dir, "vdw");
        gas_mtp_dir  = new File(gas_dir, "mtp");

        solv_dir = new File(deltaG_dir, "solv_" + time);
        solv_vdw_dir = new File(solv_dir, "vdw");
        solv_mtp_dir = new File(solv_dir, "mtp");
        gas_vdw_dir.mkdirs();
        gas_mtp_dir.mkdirs();
        solv_vdw_dir.mkdirs();
        solv_mtp_dir.mkdirs();
    }

    public File getDeltaG_dir() {
        return deltaG_dir;
    }

    public File getDensity_dir() {
        return density_dir;
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

    public File getRunInputJson() {return new File(getDirectory(), "input.json");}
    public File getRunOutputJson() {return new File(getDirectory(),"output.json");}

    public DateTime getCreated() {
        return created;
    }
}
