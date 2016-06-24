/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */

package ch.unibas.fitting.shared.fieldcomp.exceptions;

import org.apache.log4j.Logger;

/**
 *
 * @author hedin
 */
public class FieldcompUnknownOutputFileType extends Exception {

    public FieldcompUnknownOutputFileType(Logger logger, String mode) {
        logger.error("Unknown OutputFile mode '" + mode + "'");
        logger.error("It should be one of 'gausscube' , 'mtpcube'  , 'diffcube'");
    }
}
