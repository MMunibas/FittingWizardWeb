/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.charmm.generate.inputs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * gas_phase charmm calculation
 extends the abstract CHARMM_Input class
 * @author hedin
 */
public class CHARMM_Input_PureLiquid extends CHARMM_Input {

    /**
     * If content of the field has to be directly written to a file we use a BufferedWriter type
     *
     * @param _cor
     * @param _top
     * @param _par
     * @param _outf
     * @throws java.io.IOException
     */
    public CHARMM_Input_PureLiquid(File _cor,
                                   File _top,
                                   File _par,
                                   File _outf) throws IOException {
        super(_cor, _top, _par, _outf, "Pure Liquid");
        
        this.expectedFormats.clear();
        this.expectedFormats.add(".cor");
    }

    /**
     * If content of the field has to be directly written to a file we use a BufferedWriter type
     * Requires also a lpun file when MTP module is used
     *
     * @param _cor
     * @param _top
     * @param _par
     * @param _lpun
     * @param _outf
     * @throws java.io.IOException
     */
    public CHARMM_Input_PureLiquid(File _cor,
                                   File _top,
                                   File _par,
                                   File _lpun,
                                   File _outf) {
        super(_cor, _top, _par, _lpun, _outf, "Pure Liquid");
        
        this.expectedFormats.clear();
        this.expectedFormats.add(".cor");
    }

    public void generate() {
        try {
            writer = new BufferedWriter(new FileWriter(out));
            //build the input file by calling all the print_* sections
            this.build();

            writer.close();
        } catch (IOException e) {
            throw new RuntimeException("CHARMM_Input_PureLiquid ctor failed", e);
        }
    }
    
    @Override
    protected void build() throws IOException{
        
        //prepare and print title
        this.print_title();

        //prepare and print io section
        this.print_ioSection();
        this.print_corSection();
        this.print_crystalSection();

        //prepare non bonded parameters line and print it
        this.print_nbondsSection();

        this.print_ShakeSection();

        //add section with lpun file
        this.print_lpunfile();

        this.print_MiniSection();
        this.print_DynaSection();
        this.print_StopSection();
    }
    
    /**
     * Creates the header part of charmm input file, i.e. containing a title and bomlev and prnlev parameters
     *
     * @throws IOException
     */
    @Override
    protected void print_title() throws IOException {
        this.title += "* CHARMM input file for " + cor.getName() + "\n";
        this.title += "* Pure Liquid simulation with MTPs \n";
        this.title += "* generated on " + d.toString() + "\n";
        this.title += "* by user " + System.getProperty("user.name") + " on machine " + System.getProperty("os.name") + " " + System.getProperty("os.arch") + " " + System.getProperty("os.version") + "\n";
        this.title += "*\n";
        //then print it
        writer.write(this.title + "\n");
        //print error level and print level
        writer.write("bomlev " + this.bomlev + "\n");
        writer.write("prnlev " + this.prnlev + "\n\n");
    }
    
    @Override
    protected void print_crystalSection() throws IOException{
        writer.write("CRYSTAL DEFI CUBIC 28. 28. 28. 90. 90. 90." + "\n");
        writer.write("CRYSTAL BUILD nope 0" + "\n");
        writer.write("image byres xcen 0.0 ycen 0.0 zcen 0.0 sele all end" + "\n\n");
    }   

    /**
     * Creates the section where nonbonded parameters are defined
     *
     * @throws IOException
     */
    @Override
    protected void print_nbondsSection() throws IOException {
        writer.write("! Non bonded parameters" + "\n");
        writer.write("NBONDS ATOM EWALD PMEWALD KAPPA 0.43  -" + "\n");
        writer.write("\t" + "FFTX 32 FFTY 32 FFTZ 32 ORDER 4 -" + "\n");
        writer.write("\t" + "CUTNB 14.0  CTOFNB 12.0 CTONNB 10.0 -" + "\n");
        writer.write("\t" + "LRC VDW VSWITCH -" + "\n");
        writer.write("\t" + "INBFRQ -1 IMGFRQ -1" + "\n\n");
    }

    @Override
    protected void print_lpunfile() throws IOException {
        writer.write("OPEN UNIT 40 CARD READ NAME -" + "\n");
        writer.write(lpun.getName() + "\n");
        writer.write("MTPL MTPUNIT 40 ron2 10 roff2 12 ron3 9 roff3 11 -" + "\n");
        writer.write("\t" + "ron4 8 roff4 10 ron5 7 roff5 9" + "\n");
        writer.write("CLOSE UNIT 40" + "\n\n");
    }
  
    @Override
    protected void print_MiniSection() throws IOException {
        writer.write("scalar mass stat" + "\n");
        writer.write("calc pmass = int ( ?stot  /  50.0 )" + "\n");
        writer.write("calc tmass = @pmass * 10" + "\n\n");
        
        writer.write("mini sd nstep 1000  " + "\n\n");
        writer.write("mini abnr nstep 100 " + "\n\n");
        
        writer.write("calc tmin = 298 - 200.0" + "\n");
    }

    @Override
    protected void print_DynaSection() throws IOException {
        writer.write(   "dyna leap verlet start -                    ! use leap-frog verlet integrator\n" +
                        "   timestep 0.001 nstep 40000 nprint 1000 - ! run 10K steps @ 1 fs time-steps\n" +
                        "   firstt @tmin finalt 298 tbath 298 -      ! heat from @tmin K to 298 K (200 K)\n" +
                        "   ihtfrq 1000 teminc 5 ieqfrq 0 -          ! heat the system 5K every 2500 steps\n" +
                        "   iasors 1 iasvel 1 iscvel 0 ichecw 0 -    ! assign velocities via a Gaussian\n" +
                        "   ntrfrq 500 -                             ! stop rotation and translation\n" +
                        "   iseed  11033 -                           ! pick a random seed for the\n" +
                        "   echeck 100.0                             ! If energy changes more than 100\n\n");
        
        writer.write(   "dyna leap cpt nstep 40000 timestep 0.001 -\n" +
                        "  nprint 100 nsavc 100 iuncrd 50 ntrfrq 200 -\n" +
                        "  iprfrq 50000 inbfrq -1 imgfrq 50 ihtfrq 0 -\n" +
                        "  ieqfrq 0 -\n" +
                        "  pint pconst pref 1 pgamma 5 pmass @pmass -\n" +
                        "  hoover reft 298 tmass @tmass firstt 298\n\n");
        
        writer.write(   "dyna leap nstep 40000 timestep 0.001 -\n" +
                        "  nprint 100 nsavc 100 iuncrd 50 ntrfrq 200 -\n" +
                        "  iprfrq 40000 inbfrq -1 imgfrq 50 ihtfrq 0 -\n" +
                        "  ieqfrq 0 -\n" +
                        "  cpt pint pconst pref 1 pgamma 0 pmass @pmass -\n" +
                        "  hoover reft 298 tmass @tmass\n\n");
    }
}
