/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.unibas.fieldcomp.exceptions;

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
