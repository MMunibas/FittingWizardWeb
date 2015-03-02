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
public class CHARMM_Output_PureLiquid extends CHARMM_Output{

    public CHARMM_Output_PureLiquid(File _charmmout) {
        super(_charmmout,"Pure Liquid");
    }
    
}
