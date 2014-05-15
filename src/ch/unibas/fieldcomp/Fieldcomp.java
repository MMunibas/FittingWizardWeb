/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.unibas.fieldcomp;

import ch.unibas.fieldcomp.exceptions.FieldcompFileRankException;
import ch.unibas.fieldcomp.exceptions.FieldcompParamsException;
import ch.unibas.fieldcomp.exceptions.FieldcompParamsShellException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hedin
 */
public final class Fieldcomp {

    //character strings
    private String cubefile, vdwfile, punfile, basename /*,line1, line2, wrd, rnk*/;

    //allocatables
    //String Arg[];
    private int[] ele_type, irank, jrank, pts;
    private float[][][] en, totener, diff;
    private double[] xr, yr, zr;
    private double[] xs, ys, zs;
    private double[] x1, y1, z1;
    private double[] qu, qu1z, qu1y, qu1x;
    private double[] qu20, qu21c, qu21s, qu22c, qu22s;
    private double[] qu30, qu31c, qu31s, qu32c, qu32s;
    private double[] qu33c, qu33s, vdw;
    private boolean[][][] excl, sigma_range, near_vdw;

    //integers
    //int nArgs;
    private int Error, io_error;
    //private int n0, n1, n2, n3;
    private int /*diffcnt, i, j, k,*/ natoms;
//    private int diffcnt_sigma, diffcnt_nvdw, diffcnt_farout;

    //float
    private float xstart, ystart, zstart, step_x, step_y, step_z, /*o, p, q,*/ shell_i, shell_o;
//    private float diffsum_sigma, diffsum_nvdw, diffsum_farout, diffperc_sigma,
//            diffperc_nvdw, diffperc_farout, diffsum_sigma_sq;

    //double
    private double xc, yc, zc, /*x, y, z,*/ o, p, q, r, a2b, b2a, chrg;
    //private double trax, tray, traz;
    private double /*que,*/ qu1ze, qu1ye, qu1xe, qu20e, qu21ce, qu21se, qu22ce, qu22se;
    private double qu30e, qu31ce, qu31se, qu32ce, qu32se, qu33ce, qu33se;
//    private double diffsum;
//    private double diffperc;

    //logicals
    private boolean no_pics, sigma_only, cubeout;

    private Scanner s = null;
    private String inp = null;
    private String[] tokens = null;
    private final String delims = "\\s+";

    public static void main(String[] args) {

        Fieldcomp fdc = null;

        try {
            fdc = new Fieldcomp(args);
            fdc.run();
        } catch (FieldcompParamsException | FileNotFoundException | FieldcompFileRankException ex) {
            Logger.getLogger(Fieldcomp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Fieldcomp(String[] args) throws FieldcompParamsException{
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

        pts = new int[3];

        int nArgs = args.length;

        System.out.println("Parsing command line : ");
        for (String str : args) {
            System.out.print(str + " ");
        }
        System.out.println("\n");

        for (int it = 0; it < nArgs; it++) {
            //System.out.println(args[it]);
            switch (args[it].toLowerCase().trim()) {
                case "-cube":
                    cubefile = args[++it];
                    break;
                case "-vdw":
                    vdwfile = args[++it];
                    break;
                case "-pun":
                    punfile = args[++it];
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
                    shell_i = Float.valueOf(args[++it]);
                    break;
                case "-so":
                    shell_o = Float.valueOf(args[++it]);
                    break;

            } // end case
        } // end for on arguments

        //define basename using cubfile
        basename = cubefile.substring(0, cubefile.indexOf(".") - 1) + "_";

        //check inner and outer shell
        if (shell_i >= shell_o) {
            throw new FieldcompParamsShellException(shell_i, shell_o);
        }
    } // end ctor

    public void run() throws FileNotFoundException, FieldcompFileRankException {
        this.readCubefile();
        this.readVDWfile();
        this.readPUNfile();
        this.compute();
    }

    private void openFile(String fname) throws FileNotFoundException {
        s = new Scanner(new FileInputStream(new File(fname)));
        System.out.println("Opening file " + fname);
    }

    private void closeFile(String fname) {
        if (s.hasNext()) {
            System.out.println("Warning : file " + fname + " is closed but appears to still "
                    + "have unread data");
        }
        s.close();
        System.out.println("Closing file " + cubefile);
    }

    private void readCubefile() throws FileNotFoundException {
        // first open the cube file
        this.openFile(cubefile);

        //read(23,'(A)') line1
        //read(23,'(A)') line2
        String line1 = s.nextLine();
        String line2 = s.nextLine();

        //read(23,*) natoms, xstart, ystart, zstart
        inp = s.nextLine();
        tokens = inp.trim().split(delims);
        natoms = Integer.parseInt(tokens[0]);
        xstart = Float.valueOf(tokens[1]);
        ystart = Float.valueOf(tokens[2]);
        zstart = Float.valueOf(tokens[3]);

        //read(23,*) pts(1), step_x, o, p
        inp = s.nextLine();
        tokens = inp.trim().split(delims);
        pts[0] = Integer.parseInt(tokens[0]);
        step_x = Float.valueOf(tokens[1]);
        o = Double.valueOf(tokens[2]);
        p = Double.valueOf(tokens[3]);

        //read(23,*) pts(2), o, step_y, p
        inp = s.nextLine();
        tokens = inp.trim().split(delims);
        pts[1] = Integer.parseInt(tokens[0]);
        o = Double.valueOf(tokens[1]);
        step_y = Float.valueOf(tokens[2]);
        p = Double.valueOf(tokens[3]);

        //read(23,*) pts(3), o, p, step_z
        inp = s.nextLine();
        tokens = inp.trim().split(delims);
        pts[2] = Integer.parseInt(tokens[0]);
        o = Double.valueOf(tokens[1]);
        p = Double.valueOf(tokens[2]);
        step_z = Float.valueOf(tokens[3]);

        // Allocate all needed variables to natoms
        ele_type = new int[natoms];
        irank = new int[natoms];
        jrank = new int[natoms];
        //
        xr = new double[natoms];
        yr = new double[natoms];
        zr = new double[natoms];
        //
        xs = new double[natoms];
        ys = new double[natoms];
        zs = new double[natoms];
        //
        x1 = new double[natoms];
        y1 = new double[natoms];
        z1 = new double[natoms];
        //
        qu = new double[natoms];
        qu1x = new double[natoms];
        qu1y = new double[natoms];
        qu1z = new double[natoms];
        //
        qu20 = new double[natoms];
        qu21c = new double[natoms];
        qu21s = new double[natoms];
        qu22c = new double[natoms];
        qu22s = new double[natoms];
        //
        qu30 = new double[natoms];
        qu31c = new double[natoms];
        qu31s = new double[natoms];
        qu32c = new double[natoms];
        qu32s = new double[natoms];
        qu33c = new double[natoms];
        qu33s = new double[natoms];
        //
        vdw = new double[natoms];
        // Done allocating variables to natoms

        // reading extra data
        for (int i = 0; i < natoms; i++) {
            inp = s.nextLine();
            tokens = inp.trim().split(delims);
            ele_type[i] = Integer.parseInt(tokens[0]);
            chrg = Double.valueOf(tokens[1]);
            x1[i] = Double.valueOf(tokens[2]);
            y1[i] = Double.valueOf(tokens[3]);
            z1[i] = Double.valueOf(tokens[4]);
        }
        // more allocation now
        excl = new boolean[pts[2]][pts[1]][pts[0]];
        sigma_range = new boolean[pts[2]][pts[1]][pts[0]];
        near_vdw = new boolean[pts[2]][pts[1]][pts[0]];
        en = new float[pts[2]][pts[1]][pts[0]];
        totener = new float[pts[2]][pts[1]][pts[0]];
        diff = new float[pts[2]][pts[1]][pts[0]];

        System.out.println("Arrays allocated");

        // now read esp data from cube file
        System.out.println("pts 1 2 3 : " + pts[0] + " " + pts[1] + " " + pts[2]);

        int n3 = 0;
        for (int n1 = 0; n1 < pts[0]; n1++) {
            for (int n2 = 0; n2 < pts[1]; n2++) {
                n3 = 0;
                while (n3 < pts[2]) {
                    inp = s.nextLine();
                    tokens = inp.trim().split(delims);
                    for (String val : tokens) {
                        en[n3][n2][n1] = Float.valueOf(val);
//                        en[n3][n2][n1] = s.nextFloat();
                        n3++;
                    }
                }//end while
                //System.out.println(n1 + " " + n2 + " " + n3);
            }//end n2 loop
        }//end n1 loop

        System.out.println("ESP file properly read.");
        //we don't need anymore cube file so close it
        this.closeFile(cubefile);
    }// end readCubefile

    private void readVDWfile() throws FileNotFoundException {
        this.openFile(vdwfile);

        //Read .vdw file
        for (int i = 0; i < natoms; i++) {
            vdw[i] = s.nextDouble();
            jrank[i] = s.nextInt();
        }

        this.closeFile(vdwfile);
    }

    private void readPUNfile() throws FileNotFoundException, FieldcompFileRankException {
        this.openFile(punfile);

        //Read .pun file and transfer angstrom units to bohr
        inp = s.nextLine();
        inp = s.nextLine(); //3 comments lines 
        inp = s.nextLine();

        for (int i = 0; i < natoms; i++) {
            //first line
            inp = s.nextLine();//blank line
            inp = s.nextLine();
//            System.out.println(inp);
            tokens = inp.trim().split(delims);
            xs[i] = Double.valueOf(tokens[1]) * a2b;//angstroems to bohrs
            ys[i] = Double.valueOf(tokens[2]) * a2b;
            zs[i] = Double.valueOf(tokens[3]) * a2b;
            irank[i] = Integer.parseInt(tokens[5]);
            if (irank[i] != jrank[i]) {
                throw new FieldcompFileRankException(tokens[0]);
            }

            //second line
            inp = s.nextLine();
//            System.out.println(inp);
            tokens = inp.trim().split(delims);
            qu[i] = Double.valueOf(tokens[0]);

            //3rd line if required
            if (irank[i] != 0) {
                inp = s.nextLine();
                tokens = inp.trim().split(delims);
                qu1z[i] = Double.valueOf(tokens[0]);
                qu1x[i] = Double.valueOf(tokens[1]);
                qu1y[i] = Double.valueOf(tokens[2]);

                //4th line if required
                if (irank[i] != 1) {
                    inp = s.nextLine();
                    tokens = inp.trim().split(delims);
                    qu20[i] = Double.valueOf(tokens[0]);
                    qu21c[i] = Double.valueOf(tokens[1]);
                    qu21s[i] = Double.valueOf(tokens[2]);
                    qu22c[i] = Double.valueOf(tokens[3]);
                    qu22s[i] = Double.valueOf(tokens[4]);

                    //5th line if required
                    if (irank[i] != 2) {
                        inp = s.nextLine();
                        tokens = inp.trim().split(delims);
                        qu30[i] = Double.valueOf(tokens[0]);
                        qu31c[i] = Double.valueOf(tokens[1]);
                        qu31s[i] = Double.valueOf(tokens[2]);
                        qu32c[i] = Double.valueOf(tokens[3]);
                        qu32s[i] = Double.valueOf(tokens[4]);

                        inp = s.nextLine();
                        tokens = inp.trim().split(delims);
                        qu33c[i] = Double.valueOf(tokens[0]);
                        qu33s[i] = Double.valueOf(tokens[1]);
                    }
                }
            }

        }// end for loop on natoms
        this.closeFile(punfile);

    }// end of readPUNfile function

    private void compute() {
        // exclude point if within vdw radius of any atom and mark if close to vdw or within sigma range
        // Cycle if calculation is demanded for sigma range only
        double x, y, z;
        x = xstart - step_x;
        for (int n1 = 0; n1 < pts[0]; n1++) {
            x += step_x;
            y = ystart - step_y;
            for (int n2 = 0; n2 < pts[1]; n2++) {
                y += step_y;
                z = zstart - step_z;
                for (int n3 = 0; n3 < pts[2]; n3++) {
                    z += step_z;
                    for (int n0 = 0; n0 < natoms; n0++) {
                        o = vdw[n0] * vdw[n0];
                        p = shell_i * vdw[n0];
                        p *= p;
                        q = shell_o * vdw[n0];
                        q *= q;
                        r = (xs[n0] - x) * (xs[n0] - x) + (ys[n0] - y) * (ys[n0] - y)
                                + (zs[n0] - z) * (zs[n0] - z);
                        if (r <= o) {
                            excl[n3][n2][n1] = true;
                            continue;
                        } else if ((r >= o) && (r <= p)) {
                            near_vdw[n3][n2][n1] = true;
                            if (sigma_only == true) {
                                excl[n3][n2][n1] = true;
                                continue;
                            }
                        } else if ((r >= p) && (r <= q)) {
                            sigma_range[n3][n2][n1] = true;
                        }
                    }// natoms loop
                    if ((sigma_only == true) && (sigma_range[n3][n2][n1] == false)) {
                        excl[n3][n2][n1] = true;
                    }
                }//n3 loop
            }//n2 loop
        }//n1 loop

        double trax, tray, traz, que;
        //step through all grid points, calculate potentials from Multipoles
        for (int n0 = 0; n0 < natoms; n0++) {
            x = xstart - step_x;
            for (int n1 = 0; n1 < pts[0]; n1++) {
                x += step_x;
                y = ystart - step_y;
                for (int n2 = 0; n2 < pts[1]; n2++) {
                    y += step_y;
                    z = zstart - step_z;
                    for (int n3 = 0; n3 < pts[2]; n3++) {
                        z += step_z;
                        if (excl[n3][n2][n1] == true) {
                            continue;
                        }
                        r = sqrt((xs[n0] - x) * (xs[n0] - x) + (ys[n0] - y) * (ys[n0] - y)
                                + (zs[n0] - z) * (zs[n0] - z));
                        trax = -(xs[n0] - x) / r;
                        tray = -(ys[n0] - y) / r;
                        traz = -(zs[n0] - z) / r;
                        //qu(n0) is the charge on atom n0. The Potential due to this charge is calculated as (qu(n0))/(r)
                        que = qu[n0] / r;
                        //Contribution according to the monopole
                        if (irank[n0] == 0) {
                            totener[n3][n2][n1] += que;
                        } else {
                            /*
                             qu1[x,y,z]e are the components of the dipole vector. The potential due to the dipole is calculated as qu1[x,y,z](n0)/(r**2)*-delta[x,y,z]/r
                             (r**2) comes from the interaction between a dipole and a monopole
                             the other terms (-delta[x,y,z]/r) are there because the directionality of the dipole has to be taken account of, weighted by the contribution
                             of the single terms to the unit vector. (-delta[x,y,z]/r) scales to the unit vector.
                             */
                            qu1ze = qu1z[n0] / pow(r, 2) * traz;
                            qu1ye = qu1y[n0] / pow(r, 2) * tray;
                            qu1xe = qu1x[n0] / pow(r, 2) * trax;
                            if (irank[n0] != 1) {
                                totener[n3][n2][n1] += que + qu1ze + qu1xe + qu1ye;
                            } else {
                                //This is the contribution according to the quadrupole
                                qu20e = qu20[n0] / pow(r, 3) * 0.5 * (3 * traz * traz - 1);
                                qu21ce = qu21c[n0] / pow(r, 3) * pow(3, 0.5) * trax * traz;
                                qu21se = qu21s[n0] / pow(r, 3) * pow(3, 0.5) * tray * traz;
                                qu22ce = qu22c[n0] / pow(r, 3) * (0.5 * pow(3, 0.5) * (trax * trax - tray * tray));
                                qu22se = qu22s[n0] / pow(r, 3) * pow(3, 0.5) * trax * tray;

                                if (irank[n0] == 2) {
                                    totener[n3][n2][n1] += que + qu1ze + qu1xe + qu1ye + qu20e + qu21ce + qu21se + qu22ce + qu22se;
                                } else {
                                    //This is the contribution according to the octupole
                                    qu30e = qu30[n0] / pow(r, 4) * (5 * pow(traz, 3) - 3 * traz);
                                    qu31ce = qu31c[n0] / pow(r, 4) * 0.25 * 2.449409 * trax * (pow(traz, 2) - 1);
                                    qu31se = qu31s[n0] / pow(r, 4) * 0.25 * 2.449409 * tray * (pow(traz, 2) - 1);
                                    qu32ce = qu32c[n0] / pow(r, 4) * 0.5 * 3.872983 * traz * (pow(trax, 2) - pow(tray, 2));
                                    qu32se = qu32s[n0] / pow(r, 4) * 3.872983 * trax * tray * traz;
                                    qu33ce = qu33c[n0] / pow(r, 4) * 0.25 * 3.162278 * trax * (pow(trax, 2) - 3 * pow(tray, 2));
                                    qu33se = qu33s[n0] / pow(r, 4) * 0.25 * 3.162278 * tray * (3 * pow(trax, 2) - pow(tray, 2));
                                    totener[n3][n2][n1] += que + qu1ze + qu1xe + qu1ye + qu20e + qu21ce + qu21se + qu22ce + qu22se + qu30e
                                            + qu31ce + qu31se + qu32ce + qu32se + qu33ce + qu33se;
                                }//end of octopole contribution
                            }//end of quadrupole contribution
                        }//end of monopole contribution
                    }//n3 loop
                }//n2 loops
            }//ni loops
        }//natoms no loop

        //Analysis of the differences TODO
        int diffcnt = 0;
        int diffsum = 0;
        int diffperc = 0;
        int diffcnt_sigma = 0;
        int diffsum_sigma = 0;
        int diffperc_sigma = 0;
        int diffsum_sigma_sq = 0;
        int diffcnt_nvdw = 0;
        int diffsum_nvdw = 0;
        int diffperc_nvdw = 0;
        int diffcnt_farout = 0;
        int diffsum_farout = 0;
        int diffperc_farout = 0;


    }//end of compute 

}// end of class
