/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.fitting.shared.charmm.generate;

import java.io.File;

/**
 *
 * @author hedin
 */
public interface CHARMM_InOut {
    public String getText();
    public String getType();
    public File getWorkDir();
}
