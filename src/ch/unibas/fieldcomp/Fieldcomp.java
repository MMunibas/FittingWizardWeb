/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.unibas.fieldcomp;

import ch.unibas.fieldcomp.exceptions.FieldcompParamsException;
import ch.unibas.fieldcomp.exceptions.FieldcompParamsShellException;

/**
 *
 * @author hedin
 */
public final class Fieldcomp {

    //character strings
    String cubefile, vdwfile, punfile, basename, line1, line2, wrd, rnk;

    //allocatables
    //String Arg[];
    int ele_type[], irank[], jrank[], pts[];
    float en[][][], totener[][][], diff[][][];
    double xr[], yr[], zr[];
    double xs[], ys[], zs[];
    double x1[], y1[], z1[];
    double qu[], qu1z[], qu1y[], qu1x[];
    double qu20[], qu21c[], qu21s[], qu22c[], qu22s[];
    double qu30[], qu31c[], qu31s[], qu32c[], qu32s[];
    double qu33c[], qu33s[], vdw[];
    boolean excl[], sigma_range[], near_vdw[];

    //integers
    //int nArgs;
    int Error, io_error;
    int n0, n1, n2, n3;
    int diffcnt, i, j, k, natoms;
    int diffcnt_sigma, diffcnt_nvdw, diffcnt_farout;

    //float
    float xstart, ystart, zstart, step_x, step_y, step_z, o, p, q, shell_i, shell_o;
    float diffsum_sigma, diffsum_nvdw, diffsum_farout, diffperc_sigma, diffperc_nvdw, diffperc_farout, diffsum_sigma_sq;

    //double
    double xc, yc, zc, x, y, z, r, a2b, b2a, chrg;
    double trax, tray, traz;
    double que, qu1ze, qu1ye, qu1xe, qu20e, qu21ce, qu21se, qu22ce, qu22se;
    double qu30e, qu31ce, qu31se, qu32ce, qu32se, qu33ce, qu33se;
    double diffsum;
    double diffperc;

    //logicals
    boolean no_pics, sigma_only, cubeout;

    public Fieldcomp(String Arg[]) throws FieldcompParamsException {
        //Conversion parameters form Angstrom to Bohr and vice versa
        a2b = 1.889726;
        b2a = 0.52917720859;

        //Factors defining the shells for anaylsis of MEP deviation
        shell_i = 1.66f;
        shell_o = 2.2f;

        //Read input from commandline
        no_pics = true;
        sigma_only = false;
        cubeout = false;

        int nArgs = Arg.length;

        for (int it = 1; it < nArgs; it++) {
            switch (Arg[it].toLowerCase().trim()) {
                case "-cube":
                    cubefile = Arg[++it];
                    break;
                case "-vdw":
                    vdwfile = Arg[++it];
                    break;
                case "-pun":
                    punfile = Arg[++it];
                    break;
                case "-pics":
                    no_pics = false;
                    break;
                case "-sigma_only":
                    sigma_only = true;
                    break;
                case "-cubeout":
                    cubeout = true;
                    break;
                case "-si":
                    wrd = Arg[++it];
                    shell_i = Float.valueOf(wrd);
                    break;
                case "-so":
                    wrd = Arg[++it];
                    shell_o = Float.valueOf(wrd);
                    break;

            } // end case
        } // end for on arguments

        //define basename using cubfile
        //basename = cubefile(1:index(cubefile,'.')-1)//'_'
        //check inner and outer shell
        if (shell_i >= shell_o) {
            throw new FieldcompParamsShellException(shell_i, shell_o);
        }

    } // end ctor

    public void readCubefile() {

    }

}
