/*
 * Copyright (c) 2016, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.lpuntools;

/**
 * this class is for modelling the content of a lpun file
 * @author hedin
 */
public class lpun {
    
    /*
                            MTP coefficients

    MTP interactions up to, and including, rank 2 (quadrupole) are implemented.
    All MTP coefficients are expressed in spherical coordinates:
    * 1 component  for the monopole (i.e., partial charge): Q_00
    * 3 components for the dipole: Q_10, Q_11c, Q_11s
    * 5 components for the quadrupole: Q_20, Q_21c, Q_21s, Q_22c, Q_22s

    The partial charges *replace* the charges read from a topology/PSF file.

    The MTPL module computes all interactions *except* the charge-charge
    interactions, which need to be taken care of by other modules (e.g., PME).  

    Any atom can be given an MTP rank between 0 (i.e., monopole) and 2 (i.e.,
    quadrupole). Different ranks on different atoms can be used within the same
    simulation.

    MTP coefficients must be expressed in a modified PUN file that describes both
    the MTP parameters on every atomic site and the associated local axis
    system. All atoms must be specified in the same file, using the same atom ID
    (and order) as in the psf file.  The structure of the file consists is:
    1. 3 lines for title/comments (unused)
    2. atom ID; position x; position y; position z; "Rank"; rank
    3. "LRA:"; lra; neighbor 1; neighbor 2; neighbor 3; neighbor 4
    4. Q_00
    5. Q_10; Q_11c; Q_11s
    6. Q_20; Q_21c; Q_21s; Q_22c; Q_22s
    7. *empty line*
    8. *Repeat from step 2. to include more atoms*

    where ";" corresponds to a blank space; 'lra' is the type of local reference
    axis system, which can be one of the following: 'c3v', 'int', 'ter' (see
    [Kramer, Gedeck, Meuwly, JCC _33_ (2012)]) as well as 'lin' for linear/diatomic
    molecules (all of them without the single quotes); neighbor # is the atom ID of
    a neighbor used to define the local axis system ('0' means no neighbor); and
    Q_xx are the spherical MTP coefficients expressed in the local axis system.  

    The order and number of neighbors for each type of axis system is described in
    Kramer et al., except for 'lin', where an atom has one neighbor if it's at a
    terminal position and two neighbors (with the same priority rules as in Kramer
    et al.) if it's an internal atom.  The positions x, y, and z are not used in the
    present implementation.  The atom ID starts at 1 for the first particle, not 0.

    The following is an example of such a file for a single water molecule

    # LPUN file for a single water molecule
    # MTP interactions expressed in the local axis system
    #
    1 O2HH 1.418 2.967 2.166 Rank 2
    LRA: int 3 2 0 0
    -0.4188
    0.00 -0.00 -0.4358
    -0.9632 0.00 0.00 0.4747 0.00

    2 HO2H 2.185 3.54 2.157 Rank 2
    LRA: ter 1 3 0 0
    0.2094
    -0.0279 0.00 0.00
    0.17852 -0.0132 0.00 0.0278 0.00

    3 HO2H 0.677 3.56 2.287 Rank 2
    LRA: ter 1 2 0 0
    0.2094
    -0.0279 -0.00 0.00
    0.17852 -0.0132 0.00 0.0278 0.00
    */
    
    private String[] titles = new String[3];
    
    private int atomID;
    private double x,y,z;
    
    private String lra;
    private int neighb, neighb2, neighb3, neighb4;
    
    private double q00;
    
    private double q10, q11c, q11s;
    
    private double q20, q21c, q21s, q22c, q22s;
    
    
    
}
