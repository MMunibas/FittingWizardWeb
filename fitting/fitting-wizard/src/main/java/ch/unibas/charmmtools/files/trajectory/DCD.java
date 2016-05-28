/*
 * Copyright (c) 2016, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */

package ch.unibas.charmmtools.files.trajectory;

import static ch.unibas.charmmtools.errors.IO_Errors.checkFortranIOerror;
import ch.unibas.charmmtools.exceptions.UnknownFileTypeException;
import ch.unibas.charmmtools.exceptions.UnsupportedVELDException;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import org.apache.log4j.Logger;


/**
 * This class provides methods for reading a CHARMM DCD binary coordinates file
 * 
 * @author hedin
 */
public final class DCD {
    
    protected static final Logger logger = Logger.getLogger(DCD.class);

    /** the type of file object used for reading the dcd*/
    private RandomAccessFile data = null;
    
    /** for converting little endian binary files to big endian for java */
    private Endianness convert = null;

    /**
     * at first read of the coordinates if there are some frozen atoms the number of
     * coordinates to read is different than for other frames
     */
    
    private boolean dcd_first_read = true;

    /** is CORD if only coordinates or VELD if velocities included (not supported yet) */
    private char HDR[] = new char[5];
    
    /** an array of integers containing flags and data describing the binary file */
    private int ICNTRL[] = new int[20];
    
    /** how many "title lines" in dcd */
    private int NTITLE;       
    
    /** each "title line" is 80 char long */
    private char TITLE[];                   

    /** a matrix of 6 real defining the periodic boundary conditions : only useful if QCRYS is not 0 */
    private double pbc[] = new double[6];

    /*content of ICNTRL : non detailed ones are 0 */
    
    /** ICNTRL(1)  number of frames in this dcd */
    private int NFILE;
    /** ICNTRL(2)  if restart, total number of frames before first print */
    private int NPRIV;
    /** ICNTRL(3)  frequency of writing dcd */
    private int NSAVC;
    /** ICNTRL(4)  number of steps ; note that NSTEP/NSAVC = NFILE */
    private int NSTEP;
    /** ICNTRL(8)  number of degrees of freedom */
    private int NDEGF;
    /** ICNTRL(9) is NATOM - NumberOfFreeAtoms : it is the number of frozen (i.e. not moving atoms) */
    private int FROZAT;
    /** ICNTRL(10) timestep in AKMA units but stored as a 32 bits integer !!! */
    private int DELTA4;
    /** ICNTRL(11) is 1 if CRYSTAL used */
    private int QCRYS;
    /** ICNTRL(20) is charmm version */
    private int CHARMV;

    /** Number of atoms */
    private int NATOM;      

    /** Number of free (moving) atoms */
    private int LNFREAT;
    /** Array storing indexes of moving atoms */
    private int FREEAT[];   

    /** coordinates stored in simple precision (IMPORTANT) */
    private float X[],Y[],Z[];

    /** size in bytes of whole file, header section, first frame and other frames */
    long fSize, headerSize, firstFrameSize, framesSize;


    /**
     * the constructor just requires the path to the dcd
     * @param fileName path to a dcd file
     */
    public DCD(String fileName) {

        try {
            data = new RandomAccessFile(fileName, "r");
            fSize = data.length();
        } catch (FileNotFoundException ex) {
//            System.err.println(ex.getMessage());
            logger.error("File not found: " + ex.getMessage());
        } catch (IOException ex) {
            logger.error("Error when reading dcd file: " + ex.getMessage());
        }

        convert = new Endianness();

        try {
            read_header();
            //automatically read the 2 first frames for obtaining *Size parameters
            //and go back to the end of the header section afterwards
            read_oneFrame();
            read_oneFrame();
            data.seek(headerSize);
            dcd_first_read = true;
        } catch (IOException ex) {
            logger.error("Error when reading dcd file: " + ex.getMessage());
        } catch (UnsupportedVELDException | UnknownFileTypeException ex) {
            logger.error(ex.getMessage());
        }

        print_header();
    }

    //private methods
    private void read_header() throws IOException, UnsupportedVELDException, UnknownFileTypeException {
        int fortcheck1, fortcheck2;

        //we are reading data corresponding to a "write(...) HDR,ICNTRL" fortran statement
        fortcheck1 = convert.little2big(data.readInt());                //consistency check 1
        for (int i = 0; i < 4; i++) {               //first data block written by fortran  : a character array of length 4.
            HDR[i] = (char) data.readByte();        //  !! char in fortran or C is 1 byte, but 2 for java !
        }

        HDR[4] = '\0';
        // if this is not a coordinates dcd but a velocities dcd stop the program
        // as it is not supported for the moment
        if (String.copyValueOf(HDR).equals("VELD\0")) {
            throw new UnsupportedVELDException();
        } else if (!String.copyValueOf(HDR).equals("CORD\0")) {
            throw new UnknownFileTypeException();
        }

        for (int i = 0; i < 20; i++) {
            ICNTRL[i] = convert.little2big(data.readInt());
        }
        fortcheck2 = convert.little2big(data.readInt());                //consistency check 2
        checkFortranIOerror(fortcheck1, fortcheck2);// if the 2 unsigned ints have a different value there was an I/O error
        
        NFILE = ICNTRL[0];
        NPRIV = ICNTRL[1];
        NSAVC = ICNTRL[2];
        NSTEP = ICNTRL[3];
        NDEGF = ICNTRL[7];
        FROZAT = ICNTRL[8];
        DELTA4 = ICNTRL[9];
        QCRYS = ICNTRL[10];
        CHARMV = ICNTRL[19];

        /* Several "lines" of title of length 80 are written to the dcd file by CHARMM */
        fortcheck1 = convert.little2big(data.readInt());
        NTITLE = convert.little2big(data.readInt());
        if (NTITLE == 0) {
            TITLE = new char[80 + 1];
            TITLE[0] = '\0';
        } else {
            TITLE = new char[NTITLE * 80 + 1];
            for (int i = 0; i < NTITLE; i++) {
                for (int j = 0; j < 80; j++) {
                    TITLE[i * 80 + j] = (char) data.readByte();
                }
            }
            TITLE[NTITLE * 80] = '\0';
        }
        fortcheck2 = convert.little2big(data.readInt());
        checkFortranIOerror(fortcheck1, fortcheck2);

        // reading number of atoms
        fortcheck1 = convert.little2big(data.readInt());
        NATOM = convert.little2big(data.readInt());
        fortcheck2 = convert.little2big(data.readInt());
        checkFortranIOerror(fortcheck1, fortcheck2);

        /* If some atoms of the MD or MC simulation are frozen (i.e. never moving ) it is useless to store their coordinates more than once.
         * In that case a list of Free atoms (moving ones) is written at the end of the header part of the dcd.
         */
        LNFREAT = (int) (NATOM - FROZAT);
        if (LNFREAT != NATOM) {
            FREEAT = new int[LNFREAT];
            fortcheck1 = convert.little2big(data.readInt());
            for (int i = 0; i < LNFREAT; i++) {
                FREEAT[i] = convert.little2big(data.readInt());
            }
            fortcheck2 = convert.little2big(data.readInt());
            checkFortranIOerror(fortcheck1, fortcheck2);
        }

        /* ------- */
        allocate();

        // save pointer to beginning of trajectory, also giving the size of the header section
        headerSize = data.getFilePointer();

    }// end of read_header()

    /**
     * Prints header (various info at beginning of dcd file)
     */
    private void print_header() {
        logger.debug("HDR :\t" + String.copyValueOf(HDR));
        logger.debug("Title :\t" + String.copyValueOf(TITLE));
        logger.debug("Frames :\t" + NFILE);
        logger.debug("Atoms :\t" + NATOM);
        logger.debug("Free Atoms :\t" + LNFREAT);
        logger.debug("Size of header section in Bytes : " + headerSize);
        logger.debug("Size of first frame in Bytes : " + firstFrameSize);
        logger.debug("Size of other frames in Bytes : " + framesSize);
    }

    /**
     * Allocating memory for storing coordinates
     */
    private void allocate() {
        X = new float[NATOM];
        Y = new float[NATOM];
        Z = new float[NATOM];
    }

    /**
     * Read one record (frame) of the trajectory dcd
     * @throws IOException Thrown if problem happens when reading file
     */
    public void read_oneFrame() throws IOException {
        int fortcheck1, fortcheck2;
        int siz = (dcd_first_read) ? NATOM : LNFREAT;

        long start = data.getFilePointer();

        // first read the crystal PBCs if needed
        if (QCRYS == 1) {
            fortcheck1 = convert.little2big(data.readInt());
            for (int i = 0; i < 6; i++) {
                pbc[i] = convert.little2big(data.readDouble());
            }
            fortcheck2 = convert.little2big(data.readInt());
            checkFortranIOerror(fortcheck1, fortcheck2);
        }

        
        // then read coordinates : first vector X, then Y and Z
        if (dcd_first_read || (LNFREAT == NATOM)) {
            //The first frame of the dcd always contains all the atoms

            // X
            fortcheck1 = convert.little2big(data.readInt());
            for (int i = 0; i < NATOM; i++) {
                X[i] = convert.little2big(data.readFloat());
            }
            fortcheck2 = convert.little2big(data.readInt());
            checkFortranIOerror(fortcheck1, fortcheck2);

            // Y
            fortcheck1 = convert.little2big(data.readInt());
            for (int i = 0; i < NATOM; i++) {
                Y[i] = convert.little2big(data.readFloat());
            }
            fortcheck2 = convert.little2big(data.readInt());
            checkFortranIOerror(fortcheck1, fortcheck2);

            // Z
            fortcheck1 = convert.little2big(data.readInt());
            for (int i = 0; i < NATOM; i++) {
                Z[i] = convert.little2big(data.readFloat());
            }
            fortcheck2 = convert.little2big(data.readInt());
            checkFortranIOerror(fortcheck1, fortcheck2);

        } else {
            // if not the first frame, only free atoms are stored for saving space if (LNFREAT != NATOM)

            // X
            fortcheck1 = convert.little2big(data.readInt());
            for (int i = 0; i < siz; i++) {
                X[FREEAT[i] - 1] = convert.little2big(data.readFloat());
            }
            fortcheck2 = convert.little2big(data.readInt());
            checkFortranIOerror(fortcheck1, fortcheck2);

            // Y
            fortcheck1 = convert.little2big(data.readInt());
            for (int i = 0; i < siz; i++) {
                Y[FREEAT[i] - 1] = convert.little2big(data.readFloat());
            }
            fortcheck2 = convert.little2big(data.readInt());
            checkFortranIOerror(fortcheck1, fortcheck2);

            // Z
            fortcheck1 = convert.little2big(data.readInt());
            for (int i = 0; i < siz; i++) {
                Z[FREEAT[i] - 1] = convert.little2big(data.readFloat());
            }
            fortcheck2 = convert.little2big(data.readInt());
            checkFortranIOerror(fortcheck1, fortcheck2);
        }

        if (dcd_first_read) {
            dcd_first_read = false;
            firstFrameSize = data.getFilePointer() - start;
        }
        else {
            framesSize = data.getFilePointer() - start;
        }


    } // read_oneFrame()

    /**
     * Moves file reading pointer to a given place in file , useful for skipping frames
     * @param destination A byte position in the dcd where to jump
     * @throws IOException Thrown if problem happens when reading file
     */
    public void moveToFrame(int destination) throws IOException {

        if (destination <= 0 || destination > this.NFILE) {
            throw new IOException("Error : Trying to move to a frame which does not exist : value = " + destination + " and the number of frame of this dcd is " + this.NFILE);
        }
        else {
            long moveto = 0l;

            if (destination == 1) {
                moveto = headerSize;
            } else if (destination >= 2) {
                moveto = headerSize + firstFrameSize + ((long) destination - 2l) * framesSize;
            }

            if (moveto <= fSize) {
                data.seek(moveto);
            } else {
                throw new EOFException("Error : Trying to move to Byte " + moveto + " which is out of the bounds of this file.");
            }

        }// else if on error destination


    } //end of moveToFrame

    /**
     * Get number of frames
     * @return the NFILE
     */
    public int getNFILE() {
        return NFILE;
    }

    /**
     * check if dcd already had one frame read
     * @return the dcd_first_read
     */
    public boolean isDcd_first_read() {
        return dcd_first_read;
    }

    /**
     * Get number title 'lines'
     * @return the NTITLE
     */
    public int getNTITLE() {
        return NTITLE;
    }

    /**
     * Get number of frames already performed before thi dcd
     * @return the NPRIV
     */
    public int getNPRIV() {
        return NPRIV;
    }

    /**
     * Get save frequency
     * @return the NSAVC
     */
    public int getNSAVC() {
        return NSAVC;
    }

    /**
     * Get number of steps in simulation
     * @return the NSTEP
     */
    public int getNSTEP() {
        return NSTEP;
    }

    /**
     * Get number of degrees of freedom
     * @return the NDEGF
     */
    public int getNDEGF() {
        return NDEGF;
    }

    /**
     * Get number of frozen atoms
     * @return the FROZAT
     */
    public int getFROZAT() {
        return FROZAT;
    }

    /**
     * Get time step
     * 
     * TODO: need to convert it to real
     * 
     * @return the DELTA4
     */
    public int getDELTA4() {
        return DELTA4;
    }

    /**
     * Get whether crystal was used in CHARMM
     * 
     * @return the QCRYS
     */
    public int getQCRYS() {
        return QCRYS;
    }

    /**
     * Get the CHARMM version
     * 
     * @return the CHARMV
     */
    public int getCHARMV() {
        return CHARMV;
    }

    /**
     * Get number of atoms
     * 
     * @return the NATOM
     */
    public int getNATOM() {
        return NATOM;
    }

    /**
     * Get number of free atoms moving (not frozen)
     * 
     * @return the LNFREAT
     */
    public int getLNFREAT() {
        return LNFREAT;
    }

    /**
     * Get X coordinates for a record as array
     * 
     * @return the X
     */
    public float[] getX() {
        return X;
    }

    /**
     * Get Y coordinates for a record as array
     * @return the Y
     */
    public float[] getY() {
        return Y;
    }

    /**
     * Get Z coordinates for a record as array
     * @return the Z
     */
    public float[] getZ() {
        return Z;
    }


}
