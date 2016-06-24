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
public class FieldcompFileRankException extends Exception {

    public FieldcompFileRankException(Logger logger, String id) {
        logger.error("Error : irank[i] != jrank[i] for lpun atomid '" + id
                + "' ; Check rank in vdw and dma file !");
    }

}
