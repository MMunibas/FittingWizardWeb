/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.generate.outputs;

import java.io.File;

/**
 *
 * @author hedin
 */
public class CHARMM_Output_DGHydr extends CHARMM_Output{

    public CHARMM_Output_DGHydr(File _charmmout) {
        super(_charmmout, "DeltaG of Hydration");
    }
    
}
