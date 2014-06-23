/*
 * Copyright (c) 2014, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */

package ch.unibas.fieldcomp.exceptions;

import org.apache.log4j.Logger;

/**
 *
 * @author hedin
 */
public class FieldcompParamsShellException extends FieldcompParamsException {

    public FieldcompParamsShellException(Logger logger, double i, double o) {
        super(logger);
        logger.error("Inner shell has to be smaller than outer shell. Check '-si' and '-so' settings");
        logger.error("'-si' was : " + i);
        logger.error("'-so' was : " + o);
    }

}
