/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.generate.inputs;

import ch.unibas.fittingwizard.infrastructure.base.PythonScriptRunner;
import ch.unibas.fittingwizard.infrastructure.base.ResourceUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * DG of hydration charmm calculation ; gas system
 *
 * @author hedin
 */
public class CHARMM_Generator_DGHydr{

    protected String solu_cor, solu_top;
    protected String solv_cor, solv_top;
    protected String par, lpun;
    protected String ti_type;
    protected double l_min, l_space, l_max;
    protected String whoami;

    protected PythonScriptRunner runner = null;
//    protected final File currDir = new File(".");
    protected File myDir = null;
    
    private List<File> myFiles = new ArrayList<>();

    protected static final Logger logger = Logger.getLogger(CHARMM_Generator_DGHydr.class);

    public CHARMM_Generator_DGHydr(String _solu_cor, String _solu_top,
            String _par, String _lpun,
            String _ti_type, double _l_min, double _l_space, double _l_max) {

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

        this.myDir = new File("./test/gas_" + ti_type);
        this.myDir.mkdirs();

        this.runner = new PythonScriptRunner();
        this.runner.setWorkingDir(this.myDir);

        whoami="gas_" + ti_type;
        
        this.generate();

    }

    public CHARMM_Generator_DGHydr(String _solu_cor, String _solv_cor,
            String _solu_top, String _solv_top,
            String _par, String _lpun,
            String _ti_type, double _l_min, double _l_space, double _l_max) {

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

        this.myDir = new File("./test/solvent_" + ti_type);
        this.myDir.mkdirs();

        this.runner = new PythonScriptRunner();
        this.runner.setWorkingDir(this.myDir);

        whoami="solvent_" + ti_type;
        
        this.generate();
        
    }

    private void copyAndFixPaths() {
        try {
            FileUtils.copyFileToDirectory(new File(solu_cor), myDir);
            if(this.solv_cor != null) FileUtils.copyFileToDirectory(new File(solv_cor), myDir);
            FileUtils.copyFileToDirectory(new File(solu_top), myDir);
            if(this.solv_top != null) FileUtils.copyFileToDirectory(new File(solv_top), myDir);
            FileUtils.copyFileToDirectory(new File(par), myDir);
            FileUtils.copyFileToDirectory(new File(lpun), myDir);
        } catch (IOException | NullPointerException ex) {
            logger.error("An error append while copying files to subdirectory " + this.myDir.getAbsolutePath() + " : " + ex.getMessage());
        }

        String folderPath = new File("./test").getAbsolutePath();

        this.solu_cor = ResourceUtils.getRelativePath(solu_cor,folderPath);
        if(this.solv_cor != null) this.solv_cor = ResourceUtils.getRelativePath(solv_cor,folderPath);
        this.solu_top = ResourceUtils.getRelativePath(solu_top,folderPath);
        if(this.solv_top != null) this.solv_top = ResourceUtils.getRelativePath(solv_top,folderPath);
        this.par = ResourceUtils.getRelativePath(par,folderPath);
        this.lpun = ResourceUtils.getRelativePath(lpun,folderPath);
    }

    private void genInputPythonGas(boolean genOnly) {
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
        args.add("--chm");
        args.add("../../scripts/charmm");
        args.add("--lmb");
        args.add(Double.toString(this.l_min));
        args.add(Double.toString(this.l_space));
        args.add(Double.toString(this.l_max));
        
        if(genOnly) args.add("--generate");

        File script = new File("scripts/charmm-ti/perform-ti.py");
        File output = new File("test/gen_dg_inp_" + this.ti_type + "_gas" + ".out");

        int returnCode;
        if (genOnly)
            returnCode = runner.exec(script, args, output);
        else
            returnCode = runner.exec(script, args);
        
        String[] exts = {"inp"};
        myFiles.addAll(FileUtils.listFiles(myDir, exts, false));
    }

    private void genInputPythonSolvent(boolean genOnly) {
        List<String> args = new ArrayList<>();
        args.clear();

        args.add("--ti");
        args.add(this.ti_type);
        args.add("--tps");
        args.add(this.solu_top);
        args.add("--top");
        args.add(this.solv_top);
        args.add("--slu");
        args.add(this.solu_cor);
        args.add("--slv");
        args.add(this.solv_cor);
        args.add("--par");
        args.add(this.par);
        args.add("--lpun");
        args.add(this.lpun);
        args.add("--chm");
        args.add("../../scripts/charmm");
        args.add("--lmb");
        args.add(Double.toString(this.l_min));
        args.add(Double.toString(this.l_space));
        args.add(Double.toString(this.l_max));
        
        if(genOnly) args.add("--generate");

        File script = new File("scripts/charmm-ti/perform-ti.py");
        File output = new File("test/gen_dg_inp_" + this.ti_type + "_solvent" + ".out");

        int returnCode;
        if (genOnly)
            returnCode = runner.exec(script, args, output);
        else
            returnCode = runner.exec(script, args);
            
        
        String[] exts = {"inp"};
        myFiles.addAll(FileUtils.listFiles(myDir, exts, false));
    }

    /**
     * @return the myFiles
     */
    public List<File> getMyFiles() {
        return myFiles;
    }

    private void generate(){
        this.copyAndFixPaths();
        
        if(this.solv_cor != null)
            this.genInputPythonSolvent(true);
        else
            this.genInputPythonGas(true);
        
    }
 
    public void run(){
        
        if(this.solv_cor != null)
            this.genInputPythonSolvent(false);
        else
            this.genInputPythonGas(false);
        
    }

    /**
     * @return the whoami
     */
    public String Whoami() {
        return whoami;
    }


}
