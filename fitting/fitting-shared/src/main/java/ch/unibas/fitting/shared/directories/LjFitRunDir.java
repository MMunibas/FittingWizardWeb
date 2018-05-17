package ch.unibas.fitting.shared.directories;

import java.io.File;

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

    public LjFitRunDir(String username, File directory) {
        super(username, directory);
        deltaG_dir = new File(getDirectory(), "deltaG");
        deltaG_dir.mkdirs();
        density_dir = new File(getDirectory(), "density");
        density_dir.mkdirs();

        gas_dir = new File(deltaG_dir, "gas");
        gas_vdw_dir  = new File(gas_dir, "vdw");
        gas_mtp_dir  = new File(gas_dir, "mtp");

        solv_dir = new File(deltaG_dir, "solv");
        solv_vdw_dir = new File(solv_dir, "vdw");
        solv_mtp_dir = new File(solv_dir, "mtp");
        gas_vdw_dir.mkdirs();
        gas_mtp_dir.mkdirs();
        solv_vdw_dir.mkdirs();
        solv_mtp_dir.mkdirs();
    }

    public File getDensity_dir() {
        return density_dir;
    }
    public File getSolventOutputFile() {
        return new File(density_dir, "pure_liquid.out");
    }
    public File getGasOutputFile() {
        return new File(density_dir, "gas_phase.out");
    }

    public File getDeltaG_dir() {
        return deltaG_dir;
    }
    public File getGasDir() {return gas_dir;}
    public File getGasVdwDir() {
        return gas_vdw_dir;
    }
    public File getGasMtpDir() {
        return gas_mtp_dir;
    }
    public File getGasVdwOutputFile() {
        return new File(gas_vdw_dir, "dg.out");
    }
    public File getGasMtpOutputFile() {
        return new File(gas_mtp_dir, "dg.out");
    }
    public File getSolvDir() {return solv_dir;}
    public File getSolvVdwDir() {
        return solv_vdw_dir;
    }
    public File getSolvMtpDir() {
        return solv_mtp_dir;
    }
    public File getSolvVdwOutputFile() {
        return new File(solv_vdw_dir, "dg.out");
    }
    public File getSolvMtpOutputFile() {
        return new File(solv_mtp_dir, "dg.out");
    }
    public File getRunInputJson() {return new File(getDirectory(), "fit_input.json");}
    public File getRunOutputJson() {return new File(getDirectory(),"fit_output.json");}
}
