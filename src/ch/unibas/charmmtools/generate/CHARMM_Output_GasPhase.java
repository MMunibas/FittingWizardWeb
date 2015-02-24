/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.generate;

import java.io.File;

/**
 *
 * @author hedin
 */
public class CHARMM_Output_GasPhase extends CHARMM_Output{

    public CHARMM_Output_GasPhase(File _charmmout) {
        super(_charmmout,"Gas Phase");
    }
    
}
