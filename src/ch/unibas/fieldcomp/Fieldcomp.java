/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.unibas.fieldcomp;

import ch.unibas.fieldcomp.exceptions.FieldcompParamsException;
import ch.unibas.fieldcomp.exceptions.FieldcompParamsShellException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
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
    private int diffcnt, /*i, j, k,*/ natoms;
    private int diffcnt_sigma, diffcnt_nvdw, diffcnt_farout;

    //float
    private float xstart, ystart, zstart, step_x, step_y, step_z, o, p, q, shell_i, shell_o;
    private float diffsum_sigma, diffsum_nvdw, diffsum_farout, diffperc_sigma,
            diffperc_nvdw, diffperc_farout, diffsum_sigma_sq;

    //double
    private double xc, yc, zc, x, y, z, r, a2b, b2a, chrg;
    private double trax, tray, traz;
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

        Fieldcomp fdc = null;

        try {
            fdc = new Fieldcomp(args);
            fdc.run();
        } catch (FieldcompParamsException | FileNotFoundException ex) {
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

    public void run() throws FileNotFoundException {
        this.readCubefile();
        this.readVDWfile();
        this.readPUNfile();
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

    private void readPUNfile() throws FileNotFoundException {
        this.openFile(punfile);

        //Read .pun file and transfer angstrom units to bohr
        this.closeFile(punfile);
    }

}// end of class
