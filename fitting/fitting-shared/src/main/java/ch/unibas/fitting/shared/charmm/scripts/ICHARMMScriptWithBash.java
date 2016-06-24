/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.charmm.scripts;

import ch.unibas.fitting.shared.charmm.generate.inputs.CHARMM_Input;
import java.io.File;


/**
 *
 * @author hedin
 */
public interface ICHARMMScriptWithBash extends ICHARMMScript {

    // prepare python call
    public void prepare_Bash(CHARMM_Input input, File output);

}
