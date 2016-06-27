/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.charmm.generate.inputs;

import ch.unibas.fitting.shared.charmm.generate.CHARMM_InOut;
import ch.unibas.fitting.shared.config.Settings;
import ch.unibas.fitting.shared.scripts.base.PythonScriptRunner;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

/**
 * DG of hydration charmm calculation ; gas system
 *
 * @author hedin
 */
public class CHARMM_Generator_DGHydr implements CHARMM_InOut {
    
    protected File solu_cor, solu_top;
    protected File solv_cor, solv_top;
    protected File par, lpun;
    protected String ti_type;
    protected double l_min, l_space, l_max;
    protected String whoami;
    private final Settings settings;

    protected PythonScriptRunner runner = null;

    protected File baseDir = null;

    private File output = null;
    
    protected static final Logger LOGGER = Logger.getLogger(CHARMM_Generator_DGHydr.class);
    
    public CHARMM_Generator_DGHydr(File _solu_cor,
                                   File _solu_top,
                                   File _par,
                                   File _lpun,
                                   String _ti_type,
                                   double _l_min,
                                   double _l_space,
                                   double _l_max,
                                   File _baseDir,
                                   Settings settings) {
        this.solu_cor = _solu_cor;
        this.settings = settings;
        this.solv_cor = null;
        this.solu_top = _solu_top;
        this.solv_top = null;
        this.par = _par;
        this.lpun = _lpun;
        this.ti_type = _ti_type;
        this.l_min = _l_min;
        this.l_space = _l_space;
        this.l_max = _l_max;
        this.baseDir = _baseDir;
        
        this.runner = new PythonScriptRunner();
        this.runner.setWorkingDir(this.baseDir);
        
        whoami = "gas_" + ti_type;
    }
    
    public CHARMM_Generator_DGHydr(File _solu_cor,
                                   File _solv_cor,
                                   File _solu_top,
                                   File _solv_top,
                                   File _par,
                                   File _lpun,
                                   String _ti_type,
                                   double _l_min,
                                   double _l_space,
                                   double _l_max,
                                   File _mydir,
                                   Settings settings) {
        this.solu_cor = _solu_cor;
        this.solv_cor = _solv_cor;
        this.solu_top = _solu_top;
        this.solv_top = _solv_top;
        this.par = _par;
        this.lpun = _lpun;
        this.ti_type = _ti_type;
        this.l_min = _l_min;
        this.l_space = _l_space;
        this.l_max = _l_max;
        this.baseDir = _mydir;
        this.settings = settings;

        this.runner = new PythonScriptRunner();
        this.runner.setWorkingDir(this.baseDir);
        
        whoami = "solvent_" + ti_type;
    }
    
    public CHARMM_Generator_DGHydr(File _out,
                                   String _type,
                                   Settings settings){
        output = _out;
        whoami = _type;
        this.settings = settings;
    }
    
    private void copyAndFixPaths() {
        try {
            FileUtils.copyFileToDirectory(solu_cor, baseDir);
            
            if (this.solv_cor != null) {
                FileUtils.copyFileToDirectory(solv_cor, baseDir);
            }
            
            FileUtils.copyFileToDirectory(solu_top, baseDir);
            
            if (this.solv_top != null) {
                FileUtils.copyFileToDirectory(solv_top, baseDir);
            }
            
            FileUtils.copyFileToDirectory(par, baseDir);
            
            FileUtils.copyFileToDirectory(lpun, baseDir);
            
        } catch (IOException | NullPointerException ex) {
            LOGGER.error("An error append while copying files to subdirectory " + this.baseDir.getAbsolutePath() + " : " + ex.getMessage());
        }
    }

    protected void genInputPythonGas(boolean genOnly) {
        List<String> args = new ArrayList<>();

        args.add("--ti");
        args.add(this.ti_type);
        args.add("--tps");
        args.add(this.solu_top.getName());
        args.add("--slu");
        args.add(this.solu_cor.getName());
        args.add("--par");
        args.add(this.par.getName());
        args.add("--lpun");
        args.add(this.lpun.getName());
        
        if (genOnly) {
            args.add("--chm");
            args.add(FilenameUtils.normalize(settings.getCharmmScriptDir().getAbsolutePath()));
        } else {
            args.add("--rem");
            args.add("verdi");
            args.add("--num");
            args.add("8");
        }
        
        args.add("--lmb");
        args.add(Double.toString(this.l_min));
        args.add(Double.toString(this.l_space));
        args.add(Double.toString(this.l_max));
//        args.add("--nst");
//        args.add("5000");
//        args.add("--neq");
//        args.add("1000");

        if (genOnly) {
            args.add("--generate");
        }
        
        File script = new File(settings.getScriptsDir(), "charmm-ti/perform-ti.py");

        runner.exec(script, args, output);
    }
    
    protected void genInputPythonSolvent(boolean genOnly) {
        List<String> args = new ArrayList<>();
        args.clear();
        
        args.add("--ti");
        args.add(this.ti_type);
        args.add("--tps");
        args.add(this.solu_top.getName());
//        args.add("--top");
//        args.add(this.solv_top);
        args.add("--slu");
        args.add(this.solu_cor.getName());
        args.add("--slv");
        args.add(this.solv_cor.getName());
        args.add("--par");
        args.add(this.par.getName());
        args.add("--lpun");
        args.add(this.lpun.getName());
        if (genOnly) {
            args.add("--chm");
            args.add(FilenameUtils.normalize(settings.getCharmmScriptDir().getAbsolutePath()));
        } else {
            args.add("--rem");
            args.add("studix");
            args.add("--num");
            args.add("8");
        }
        
        args.add("--lmb");
        args.add(Double.toString(this.l_min));
        args.add(Double.toString(this.l_space));
        args.add(Double.toString(this.l_max));
//        args.add("--nst");
//        args.add("5000");
//        args.add("--neq");
//        args.add("1000");

        if (genOnly) {
            args.add("--generate");
        }
        
        File script = new File(settings.getScriptsDir(), "charmm-ti/perform-ti.py");

        runner.exec(script, args, output);
    }

    /**
     * @return the myFiles
     */
    public List<File> listOutputFiles() {
        String[] exts = {"inp"};
        return FileUtils.listFiles(baseDir, exts, false)
                .stream()
                .collect(Collectors.toList());
    }
    
    public void generate() {
        this.copyAndFixPaths();
        
        if (isSolvent()) {
            this.genInputPythonSolvent(true);
        } else {
            this.genInputPythonGas(true);
        }
    }
    
    public void run() {
        if (isSolvent()) {
            this.genInputPythonSolvent(false);
        } else {
            this.genInputPythonGas(false);
        }
    }

    private boolean isSolvent() {
        return this.solv_cor != null;
    }

    public String Whoami() {
        return whoami;
    }
    
    @Override
    public String getText() {
        return getOutput(output);
    }

    public String getGenOutput() {
        File file;
        if (isSolvent()) {
            file = new File(baseDir + "/dg_gen_" + this.ti_type + "_solv" + ".out");
            LOGGER.debug("OUTPUT file from perform-ti generate is : " + file.getAbsolutePath());
        } else {
            file = new File(baseDir + "/dg_gen_" + this.ti_type + "_gas" + ".out");
            LOGGER.debug("OUTPUT file from perform-ti generate is : " + file.getAbsolutePath());
        }
        return getOutput(file);
    }

    public String getRunOutput() {
        File file;
        if (isSolvent()) {
            file = new File(baseDir + "/dg_run_" + this.ti_type + "_solv" + ".out");
            LOGGER.debug("OUTPUT file from perform-ti running  is : " + file.getAbsolutePath());
        } else {
            file = new File(baseDir + "/dg_run_" + this.ti_type + "_gas" + ".out");
            LOGGER.debug("OUTPUT file from perform-ti running  is : " + file.getAbsolutePath());
        }
        return getOutput(file);
    }

    private String getOutput(File file) {
        String content = "";
        try {
            content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
        } catch (IOException ex) {
            LOGGER.error("Error while reading simulation output file : " + ex);
        }
        return content;
    }
    
    @Override
    public String getType() {
        return whoami;
    }
    
    @Override
    public File getWorkDir() {
        return new File(baseDir.getAbsolutePath());
    }
}
