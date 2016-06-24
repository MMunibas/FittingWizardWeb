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
public class FieldcompParamsException extends Exception {

    public FieldcompParamsException(Logger logger) {
        logger.error("Error detected for parameters passed to Fieldcomp : ");
    }

}
