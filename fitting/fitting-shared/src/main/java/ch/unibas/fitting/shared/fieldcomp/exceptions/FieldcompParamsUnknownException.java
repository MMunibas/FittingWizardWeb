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
public class FieldcompParamsUnknownException extends FieldcompParamsException {

    public FieldcompParamsUnknownException(Logger logger, String param) {
        super(logger);
        logger.error("Unknown command line parameter '" + param + "'");
    }

    public FieldcompParamsUnknownException(Logger logger, String param, String value) {
        super(logger);
        logger.error("Unknown command line parameter '" + param + "' with value '" + value + "'");
    }

}
