/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.charmm.scripts;

import ch.unibas.fitting.shared.config.Settings;
import ch.unibas.fitting.shared.directories.LjFitSessionDir;


public class CHARMMScript_DG_gas extends CHARMMScript_DG {

    public CHARMMScript_DG_gas(LjFitSessionDir ljFitSessionDir, Settings _settings) {
        super(ljFitSessionDir, _settings);
    }
}
