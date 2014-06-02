/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.unibas.fieldcomp;

import ch.unibas.fieldcomp.exceptions.FieldcompFileRankException;
import ch.unibas.fieldcomp.exceptions.FieldcompParamsException;
import ch.unibas.fieldcomp.exceptions.FieldcompParamsShellException;
import ch.unibas.fieldcomp.exceptions.FieldcompParamsUnknownException;
import ch.unibas.fieldcomp.exceptions.FieldcompUnknownOutputFileType;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import static java.lang.Math.abs;
import static java.lang.Math.abs;
import static java.lang.Math.abs;
import static java.lang.Math.abs;
import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import java.util.Scanner;
import java.util.logging.Level;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 *
 * @author hedin
 */
public class Fieldcomp {

    private final static Logger logger = Logger.getLogger(Fieldcomp.class);

    //character strings
    private String cubefile, vdwfile, punfile, line1, line2/*, wrd, rnk*/;
    private final String basename;

    //allocatables
    private int[] ele_type, irank, jrank, pts;
    private float[][][] en, totener, diff;
    private double[] xs, ys, zs;
    private double[] x1, y1, z1;
    private double[] qu, qu1z, qu1y, qu1x;
    private double[] qu20, qu21c, qu21s, qu22c, qu22s;
    private double[] qu30, qu31c, qu31s, qu32c, qu32s;
    private double[] qu33c, qu33s, vdw;
    private boolean[][][] excl, sigma_range, near_vdw;

    //integers
    private int diffcnt, natoms;
    private int diffcnt_sigma, diffcnt_nvdw, diffcnt_farout;

    //double
    private float xstart, ystart, zstart, step_x, step_y, step_z, o, p, q, shell_i, shell_o;
    private float diffsum_sigma, diffsum_nvdw, diffsum_farout, diffperc_sigma,
            diffperc_nvdw, diffperc_farout, diffsum_sigma_sq;

    //double
    private double /*o, p, q, b2a,*/ a2b, chrg;
    private double que, qu1ze, qu1ye, qu1xe, qu20e, qu21ce, qu21se, qu22ce, qu22se;
    private double qu30e, qu31ce, qu31se, qu32ce, qu32se, qu33ce, qu33se;
    private double diffsum;
    private double diffperc;

    //logicals
    private boolean no_pics, sigma_only, cubeout;

    private Scanner s = null;
    private String inp = null;
    private String[] tokens = null;
    private final String delims = "\\s+";

    public static void main(String[] args) {

        // configure logger
        BasicConfigurator.configure();

        Fieldcomp fdc = null;

        try {
            fdc = new Fieldcomp(args);
            fdc.run();
        } catch (FieldcompParamsException | FieldcompFileRankException | FieldcompUnknownOutputFileType ex) {
            logger.warn("Please solve the error previously reported.");
        } catch (FileNotFoundException fex) {
            logger.warn("FileNotFoundException was detected : " + fex.getMessage());
        } catch (IOException iex) {
            logger.warn("IOException was detected : " + iex.getMessage());
        }

    }// end test main

    public Fieldcomp(String[] args) throws FieldcompParamsException{
        //Conversion parameters form Angstrom to Bohr and vice versa
        a2b = 1.889726;
        //b2a = 0.52917720859;

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
        System.out.println("%n");

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

                default:
                    throw new FieldcompParamsUnknownException(Fieldcomp.logger, args[it]);

            } // end case
        } // end for on arguments

        //define basename using cubfile
        basename = cubefile.substring(0, cubefile.indexOf(".")) + "_";

        //check inner and outer shell
        if (shell_i >= shell_o) {
            throw new FieldcompParamsShellException(Fieldcomp.logger, shell_i, shell_o);
        }
    } // end ctor

    public void run() throws FileNotFoundException, FieldcompFileRankException, FieldcompUnknownOutputFileType, IOException {

        this.readCubefile();
        this.readVDWfile();
        this.readPUNfile();
        this.compute();
        this.print();

        if (cubeout) {
            this.writeOutFiles("gausscube", "Electrostatic potential from Total SCF Density                        ");
            this.writeOutFiles("mtpcube", "Electrostatic potential from Atomic Multipoles                        ");
            this.writeOutFiles("diffcube", "Difference between ab-initio and MTP Electrostatic Potential          ");
        }
    }

    private void openFile(String fname) throws FileNotFoundException {
        s = new Scanner(new FileInputStream(new File(fname)));
        logger.info("Opening file " + fname);
    }

    private void closeFile(String fname) {
        if (s.hasNext()) {
            logger.info("Warning : file " + fname + " is closed but appears to still "
                    + "have unread data");
        }
        s.close();
        logger.info("Closing file " + cubefile);
    }

    private void readCubefile() throws FileNotFoundException {
        // first open the cube file
        this.openFile(cubefile);

        //read(23,'(A)') line1
        //read(23,'(A)') line2
        line1 = s.nextLine();
        line2 = s.nextLine();

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
        o = Float.valueOf(tokens[2]);
        p = Float.valueOf(tokens[3]);

        //read(23,*) pts(2), o, step_y, p
        inp = s.nextLine();
        tokens = inp.trim().split(delims);
        pts[1] = Integer.parseInt(tokens[0]);
        o = Float.valueOf(tokens[1]);
        step_y = Float.valueOf(tokens[2]);
        p = Float.valueOf(tokens[3]);

        //read(23,*) pts(3), o, p, step_z
        inp = s.nextLine();
        tokens = inp.trim().split(delims);
        pts[2] = Integer.parseInt(tokens[0]);
        o = Float.valueOf(tokens[1]);
        p = Float.valueOf(tokens[2]);
        step_z = Float.valueOf(tokens[3]);

        // Allocate all needed variables to natoms
        ele_type = new int[natoms];
        irank = new int[natoms];
        jrank = new int[natoms];
        //
//        xr = new double[natoms];
//        yr = new double[natoms];
//        zr = new double[natoms];
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

        logger.info("Arrays allocated");

        // now read esp data from cube file
//        System.out.println("pts 1 2 3 : " + pts[0] + " " + pts[1] + " " + pts[2]);

        int n3 = 0;
        for (int n1 = 0; n1 < pts[0]; n1++) {
            for (int n2 = 0; n2 < pts[1]; n2++) {
                n3 = 0;
                while (n3 < pts[2]) {
                    inp = s.nextLine();
                    tokens = inp.trim().split(delims);
                    for (String val : tokens) {
                        en[n3][n2][n1] = Float.valueOf(val);
//                        en[n3][n2][n1] = s.nextDouble();
                        n3++;
                    }
                }//end while
                //System.out.println(n1 + " " + n2 + " " + n3);
            }//end n2 loop
        }//end n1 loop

        if (sigma_only == false) {
            logger.info("ESP file properly read.");
        }

        //we don't need anymore cube file so close it
        this.closeFile(cubefile);
    }// end readCubefile

    private void readVDWfile() throws FileNotFoundException {
        this.openFile(vdwfile);

        //Read .vdw file
        for (int i = 0; i < natoms; i++) {
            vdw[i] = s.nextDouble();
            jrank[i] = s.nextInt();
//            System.out.println(vdw[i] + " " + jrank[i]);
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
//            System.out.println(xs[i] + " " + ys[i] + " " + zs[i]);
            irank[i] = Integer.parseInt(tokens[5]);
            if (irank[i] != jrank[i]) {
                throw new FieldcompFileRankException(Fieldcomp.logger, tokens[0]);
            }

            //second line
            inp = s.nextLine();
//            System.out.println(inp);
            tokens = inp.trim().split(delims);
            qu[i] = Double.valueOf(tokens[0]);

            //System.out.println(irank[i]);

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
//            System.out.println(qu[i] + " " + qu1z[i] + " " + qu1x[i] + " " + qu1y[i] + " " + qu20[i] + " " + qu21c[i] + " " + qu21s[i] + " " + qu22c[i] + " " + qu22s[i]);
        }// end for loop on natoms
        this.closeFile(punfile);

    }// end of readPUNfile function

    private void compute() {
        // exclude point if within vdw radius of any atom and mark if close to vdw or within sigma range
        // Cycle if calculation is demanded for sigma range only
        double x, y, z;
        double r;
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
                        o = (float) pow(vdw[n0], 2);
                        p = (float) pow(shell_i * vdw[n0], 2);
                        q = (float) pow(shell_o * vdw[n0], 2);
                        r = pow((xs[n0] - x), 2) + pow((ys[n0] - y), 2) + pow((zs[n0] - z), 2);
//                        o = vdw[n0] * vdw[n0];
//                        p = (shell_i * vdw[n0]) * (shell_i * vdw[n0]);
//                        q = (shell_o * vdw[n0]) * (shell_o * vdw[n0]);
//                        r = (xs[n0] - x) * (xs[n0] - x)
//                                + (ys[n0] - y) * (ys[n0] - y)
//                                + (zs[n0] - z) * (zs[n0] - z);
                        //System.err.println(o + " " + p + " " + q + " " + r);
//                        System.err.format("%10.3f %10.3f %10.3f %10.3f%n", o, p, q, r);
                        if (r < o) {
                            excl[n3][n2][n1] = true;
                            continue;
                        } else if ((r > o) && (r < p)) {
                            near_vdw[n3][n2][n1] = true;
                            if (sigma_only == true) {
                                excl[n3][n2][n1] = true;
                                continue;
                            }
                        } else if ((r > p) && (r < q)) {
                            sigma_range[n3][n2][n1] = true;
                        }
                    }// natoms loop
                    if ((sigma_only == true) && (sigma_range[n3][n2][n1] == false)) {
                        excl[n3][n2][n1] = true;
                    }
                }//n3 loop
            }//n2 loop
        }//n1 loop

        double trax, tray, traz;
        double r2, r3, r4;
        final double sq3 = sqrt(3.0);
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
                        r = sqrt(pow((xs[n0] - x), 2) + pow((ys[n0] - y), 2)
                                + pow((zs[n0] - z), 2));
                        r2 = r * r;
                        r3 = r2 * r;
                        r4 = r2 * r2;
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
                            qu1ze = qu1z[n0] / r2 * traz;
                            qu1ye = qu1y[n0] / r2 * tray;
                            qu1xe = qu1x[n0] / r2 * trax;
                            if (irank[n0] == 1) {
                                totener[n3][n2][n1] += que + qu1ze + qu1xe + qu1ye;
                            } else {
                                //This is the contribution according to the quadrupole
                                qu20e = qu20[n0] / r3 * 0.5 * (3.0 * traz * traz - 1.0);
                                qu21ce = qu21c[n0] / r3 * sq3 * trax * traz;
                                qu21se = qu21s[n0] / r3 * sq3 * tray * traz;
                                qu22ce = qu22c[n0] / r3 * (0.5 * sq3 * (trax * trax - tray * tray));
                                qu22se = qu22s[n0] / r3 * sq3 * trax * tray;

                                if (irank[n0] == 2) {
                                    totener[n3][n2][n1] += que + qu1ze + qu1xe + qu1ye + qu20e + qu21ce + qu21se + qu22ce + qu22se;
                                } else {
                                    //This is the contribution according to the octupole
                                    qu30e = qu30[n0] / r4 * (5 * pow(traz, 3) - 3.0 * traz);
                                    qu31ce = qu31c[n0] / r4 * 0.25 * 2.449409 * trax * (pow(traz, 2) - 1.0);
                                    qu31se = qu31s[n0] / r4 * 0.25 * 2.449409 * tray * (pow(traz, 2) - 1.0);
                                    qu32ce = qu32c[n0] / r4 * 0.5 * 3.872983 * traz * (pow(trax, 2) - pow(tray, 2));
                                    qu32se = qu32s[n0] / r4 * 3.872983 * trax * tray * traz;
                                    qu33ce = qu33c[n0] / r4 * 0.25 * 3.162278 * trax * (pow(trax, 2) - 3.0 * pow(tray, 2));
                                    qu33se = qu33s[n0] / r4 * 0.25 * 3.162278 * tray * (3.0 * pow(trax, 2) - pow(tray, 2));
                                    totener[n3][n2][n1] += que + qu1ze + qu1xe + qu1ye + qu20e + qu21ce + qu21se + qu22ce + qu22se + qu30e
                                            + qu31ce + qu31se + qu32ce + qu32se + qu33ce + qu33se;
                                }//end of octopole contribution
                            }//end of quadrupole contribution
                        }//end of monopole contribution
                    }//n3 loop
                }//n2 loops
            }//ni loops
        }//natoms no loop

        //Analysis of the differences
        diffcnt = 0;
        diffsum = 0;
        diffperc = 0;
        diffcnt_sigma = 0;
        diffsum_sigma = 0;
        diffperc_sigma = 0;
        diffsum_sigma_sq = 0;
        diffcnt_nvdw = 0;
        diffsum_nvdw = 0;
        diffperc_nvdw = 0;
        diffcnt_farout = 0;
        diffsum_farout = 0;
        diffperc_farout = 0;

        for (int n1 = 0; n1 < pts[0]; n1++) {
            for (int n2 = 0; n2 < pts[1]; n2++) {
                for (int n3 = 0; n3 < pts[2]; n3++) {

                    if (excl[n3][n2][n1] == true) {
                        diff[n3][n2][n1] = 0.f;
                    } else if (near_vdw[n3][n2][n1] == true) {
                        diffcnt_nvdw += 1;
                        diffcnt += 1;
                        diff[n3][n2][n1] = abs(totener[n3][n2][n1] - en[n3][n2][n1]);
                        diffsum_nvdw += diff[n3][n2][n1];
                        diffsum += diff[n3][n2][n1];
                        diffperc_nvdw += diff[n3][n2][n1] / abs(en[n3][n2][n1]);
                        diffperc += diff[n3][n2][n1] / abs(en[n3][n2][n1]);
                    } else if (sigma_range[n3][n2][n1] == true) {
                        diffcnt_sigma += 1;
                        diffcnt += 1;
                        diff[n3][n2][n1] = abs(totener[n3][n2][n1] - en[n3][n2][n1]);
                        diffsum_sigma += diff[n3][n2][n1];
                        diffsum_sigma_sq += pow(diff[n3][n2][n1], 2);
                        diffsum += diff[n3][n2][n1];
                        diffperc_sigma += diff[n3][n2][n1] / abs(en[n3][n2][n1]);
                        diffperc += diff[n3][n2][n1] / abs(en[n3][n2][n1]);
                    } else {
                        diffcnt_farout += 1;
                        diffcnt += 1;
                        diff[n3][n2][n1] = abs(totener[n3][n2][n1] - en[n3][n2][n1]);
                        diffsum_farout += diff[n3][n2][n1];
                        diffsum += diff[n3][n2][n1];
                        diffperc_farout += diff[n3][n2][n1] / abs(en[n3][n2][n1]);
                        diffperc += diff[n3][n2][n1] / abs(en[n3][n2][n1]);
                    }

                }//n3
            }//n2
        }//n1

//        System.out.println(diffcnt + " " + diffsum + " " + diffperc + " " + diffcnt_sigma + " " + diffsum_sigma);
//        System.out.println(diffperc_sigma + " " + diffsum_sigma_sq + " " + diffcnt_nvdw + " " + diffsum_nvdw);
//        System.out.println(diffperc_nvdw + " " + diffcnt_farout + " " + diffsum_farout + " " + diffperc_farout);

    }//end of compute

    // print to terminal computed properties
    private void print() {
        if (sigma_only == true) {
            logger.info("diffsum_sigma_sq/diffcnt_sigma = " + diffsum_sigma_sq / (double) diffcnt_sigma);
        } else {
            logger.info("Analysis of total space");
            logger.info("sum of differences: " + diffsum * 2625.5 + " kJ/mol");
            logger.info("difference average: " + diffsum * 2625.5 / (double) diffcnt + " kJ/mol");
            logger.info("difference percentage: " + (diffperc / (double) diffcnt) * 100.0 + " %");
//            System.out.println();
            logger.info("Analysis of space between vdW-Surface and " + shell_i + " * vdW-Surface");
            logger.info("sum of differences: " + diffsum_nvdw * 2625.5 + " kJ/mol");
            logger.info("difference average: " + diffsum_nvdw * 2625.5 / (double) diffcnt_nvdw + " kJ/mol");
            logger.info("difference percentage: " + (diffperc_nvdw / (double) diffcnt_nvdw) * 100.0 + " %");
//            System.out.println();
            logger.info("Analysis of space between " + shell_i + " * vdW-Surface - " + shell_o + " * vdw-Surface");
            logger.info("sum of differences: " + diffsum_sigma * 2625.5 + " kJ/mol");
            logger.info("difference average: " + diffsum_sigma * 2625.5 / (double) diffcnt_sigma + " kJ/mol");
            logger.info("difference percentage: " + (diffperc_sigma / (double) diffcnt_sigma) * 100.0 + " %");
//            System.out.println();
            logger.info("Analysis of space outside " + shell_o + " * vdW-Surface");
            logger.info("sum of differences: " + diffsum_farout * 2625.5 + " kJ/mol");
            logger.info("difference average: " + diffsum_farout * 2625.5 / (double) diffcnt_farout + " kJ/mol");
            logger.info("difference percentage: " + (diffperc_farout / (double) diffcnt_farout) * 100.0 + " %");
        }
    }// end of print

    // generic method for writing results to new cube files
    private void writeOutFiles(String mode, String description) throws FieldcompUnknownOutputFileType, IOException {

        float matrix[][][] = null;

        switch (mode.toLowerCase().trim()) {
            case "gausscube":
                matrix = en;
                break;

            case "mtpcube":
                matrix = totener;
                break;

            case "diffcube":
                matrix = new float[pts[2]][pts[1]][pts[0]];
                for (int n1 = 0; n1 < pts[0]; n1++) {
                    for (int n2 = 0; n2 < pts[1]; n2++) {
                        for (int n3 = 0; n3 < pts[2]; n3++) {
                            matrix[n3][n2][n1] = en[n3][n2][n1] - totener[n3][n2][n1];
                        }
                    }
                }
                break;

            default:
                throw new FieldcompUnknownOutputFileType(Fieldcomp.logger, mode);
        }//end of switch filling matrix[][][] properly (copy or computation)

        BufferedWriter of = null;
        String line = "";

        //Write File with Gaussian ESPs with point in sigma range only
        String newname = basename.trim() + mode.trim() + ".cube";
        logger.info("Writing new file '" + newname + "'");
        of = new BufferedWriter(new FileWriter(newname));

        //Write header
        of.write(String.format(" %s%n", line1));
        of.write(String.format(" %s%n", description));
        of.write(String.format("%5d%12.6f%12.6f%12.6f%n", natoms, xstart, ystart, zstart));
        of.write(String.format("%5d%12.6f%s%n", pts[0], step_x, "    0.000000    0.000000"));
        of.write(String.format("%5d%s%12.6f%s%n", pts[1], "    0.000000", step_y, "	0.000000"));
        of.write(String.format("%5d%s%12.6f%n", pts[2], "    0.000000    0.000000", step_z));
        for (int n1 = 0; n1 < natoms; n1++) {
            of.write(
                    String.format("%5d%12.6f%12.6f%12.6f%12.6f%n", ele_type[n1], (float) ele_type[n1], x1[n1], y1[n1], z1[n1])
            );
        }

        //Write numbers
        for (int n1 = 0; n1 < pts[0]; n1++) {
            for (int n2 = 0; n2 < pts[1]; n2++) {
                for (int n3 = 0; n3 < pts[2]; n3++) {
                    if (excl[n3][n2][n1] == false && near_vdw[n3][n2][n1] == false && sigma_range[n3][n2][n1] == true) {
                        of.write(String.format(" %12.5E", matrix[n3][n2][n1]));
                    } else {
                        of.write(String.format("%s", "  0.00000E+00"));
                    }//end if
                    if ((n3 + 1) % 6 == 0 && n3 < pts[2]) {
                        of.write(String.format("%n"));
                    }
                }//for n3
                of.write(String.format("%n"));
            }//for n2
        }//for n1

        of.close();

    }// end of writeOutFiles

}// end of class
