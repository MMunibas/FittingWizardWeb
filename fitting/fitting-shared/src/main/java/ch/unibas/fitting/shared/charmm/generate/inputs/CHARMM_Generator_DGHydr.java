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
import ch.unibas.fitting.shared.scripts.base.PythonScriptRunner;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * DG of hydration charmm calculation ; gas system
 *
 * @author hedin
 */
public class CHARMM_Generator_DGHydr implements CHARMM_InOut {
    
    protected String solu_cor, solu_top;
    protected String solv_cor, solv_top;
    protected String par, lpun;
    protected String ti_type;
    protected double l_min, l_space, l_max;
    protected String whoami;
    
    protected PythonScriptRunner runner = null;
//    protected final File workDir = new File("test");
    protected File myDir = null;

    private File output = null;
    
    protected static final Logger logger = Logger.getLogger(CHARMM_Generator_DGHydr.class);
    
    public CHARMM_Generator_DGHydr(String _solu_cor, String _solu_top,
            String _par, String _lpun,
            String _ti_type, double _l_min, double _l_space, double _l_max, File _mydir) {
        
        this.solu_cor = _solu_cor;
        this.solv_cor = null;
        this.solu_top = _solu_top;
        this.solv_top = null;
        this.par = _par;
        this.lpun = _lpun;
        this.ti_type = _ti_type;
        this.l_min = _l_min;
        this.l_space = _l_space;
        this.l_max = _l_max;
        this.myDir = _mydir;
        
        this.runner = new PythonScriptRunner();
        this.runner.setWorkingDir(this.myDir);
        
        whoami = "gas_" + ti_type;
    }
    
    public CHARMM_Generator_DGHydr(String _solu_cor, String _solv_cor,
            String _solu_top, String _solv_top,
            String _par, String _lpun,
            String _ti_type, double _l_min, double _l_space, double _l_max, File _mydir) {
        
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
        this.myDir = _mydir;
        
        this.runner = new PythonScriptRunner();
        this.runner.setWorkingDir(this.myDir);
        
        whoami = "solvent_" + ti_type;
    }
    
    public CHARMM_Generator_DGHydr(File _out, String _type){
        output = _out;
        whoami = _type;
    }
    
    private void copyAndFixPaths() {
        try {
            FileUtils.copyFileToDirectory(new File(solu_cor), myDir);
            
            if (this.solv_cor != null) {
                FileUtils.copyFileToDirectory(new File(solv_cor), myDir);
            }
            
            FileUtils.copyFileToDirectory(new File(solu_top), myDir);
            
            if (this.solv_top != null) {
                FileUtils.copyFileToDirectory(new File(solv_top), myDir);
            }
            
            FileUtils.copyFileToDirectory(new File(par), myDir);
            
            FileUtils.copyFileToDirectory(new File(lpun), myDir);
            
        } catch (IOException | NullPointerException ex) {
            logger.error("An error append while copying files to subdirectory " + this.myDir.getAbsolutePath() + " : " + ex.getMessage());
        }
        
        this.solu_cor = new File(solu_cor).getName();
        if (this.solv_cor != null) {
            this.solv_cor = new File(solv_cor).getName();
        }
        this.solu_top = new File(solu_top).getName();
        if (this.solv_top != null) {
            this.solv_top = new File(solv_top).getName();
        }
        this.par = new File(par).getName();
        this.lpun = new File(lpun).getName();
    }

    protected void genInputPythonGas(boolean genOnly) {
        List<String> args = new ArrayList<>();
        args.clear();
        
        args.add("--ti");
        args.add(this.ti_type);
        args.add("--tps");
        args.add(this.solu_top);
        args.add("--slu");
        args.add(this.solu_cor);
        args.add("--par");
        args.add(this.par);
        args.add("--lpun");
        args.add(this.lpun);
        
        if (genOnly) {
            args.add("--chm");
            args.add("../../scripts/charmm");
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
        
        File script = new File("scripts/charmm-ti/perform-ti.py");
        //File output = null;

        if (genOnly) {
            output = new File(myDir + "/dg_gen_" + this.ti_type + "_gas" + ".out");
            logger.info("OUTPUT file from perform-ti generate is : " + output.getAbsolutePath());
        } else {
            output = new File(myDir + "/dg_run_" + this.ti_type + "_gas" + ".out");
            logger.info("OUTPUT file from perform-ti running  is : " + output.getAbsolutePath());
        }
        
        int returnCode;
////        if (genOnly) {
        returnCode = runner.exec(script, args, output);
//        } else {
//        returnCode = runner.exec(script, args);
//        }


    }
    
    protected void genInputPythonSolvent(boolean genOnly) {
        List<String> args = new ArrayList<>();
        args.clear();
        
        args.add("--ti");
        args.add(this.ti_type);
        args.add("--tps");
        args.add(this.solu_top);
//        args.add("--top");
//        args.add(this.solv_top);
        args.add("--slu");
        args.add(this.solu_cor);
        args.add("--slv");
        args.add(this.solv_cor);
        args.add("--par");
        args.add(this.par);
        args.add("--lpun");
        args.add(this.lpun);
        if (genOnly) {
            args.add("--chm");
            args.add("../../scripts/charmm");
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
        
        File script = new File("scripts/charmm-ti/perform-ti.py");
        //File output = null;

        if (genOnly) {
            output = new File(myDir + "/dg_gen_" + this.ti_type + "_solv" + ".out");
            logger.info("OUTPUT file from perform-ti generate is : " + output.getAbsolutePath());
        } else {
            output = new File(myDir + "/dg_run_" + this.ti_type + "_solv" + ".out");
            logger.info("OUTPUT file from perform-ti running  is : " + output.getAbsolutePath());
        }
        
        int returnCode;
//        if (genOnly) {
        returnCode = runner.exec(script, args, output);
//        } else {
//        returnCode = runner.exec(script, args);
//        }
    }

    /**
     * @return the myFiles
     */
    public List<File> listOutputFiles() {
        String[] exts = {"inp"};
        return FileUtils.listFiles(myDir, exts, false)
                .stream()
                .collect(Collectors.toList());
    }
    
    public void generate() {
        this.copyAndFixPaths();
        
        if (this.solv_cor != null) {
            this.genInputPythonSolvent(true);
        } else {
            this.genInputPythonGas(true);
        }
        
    }
    
    public void run() {
        
        if (this.solv_cor != null) {
            this.genInputPythonSolvent(false);
        } else {
            this.genInputPythonGas(false);
        }
        
    }

    /**
     * @return the whoami
     */
    public String Whoami() {
        return whoami;
    }
    
    @Override
    public String getText() {
        String content = "";
        try {
            content = new String(Files.readAllBytes(Paths.get(output.getAbsolutePath())));
        } catch (IOException ex) {
            logger.error("Error while reading simulation output file : " + ex);
        }
        return content;
    }
    
    @Override
    public String getType() {
        return whoami;
    }
    
    @Override
    public File getWorkDir() {
        return new File(myDir.getAbsolutePath());
    }
    
}
