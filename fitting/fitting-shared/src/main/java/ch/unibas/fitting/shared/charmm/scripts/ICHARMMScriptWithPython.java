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
public interface ICHARMMScriptWithPython extends ICHARMMScript {

    // prepare python call
    void prepare_Python(CHARMM_Input input, File output);

}
