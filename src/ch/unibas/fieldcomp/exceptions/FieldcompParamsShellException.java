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
public class FieldcompParamsShellException extends FieldcompParamsException {

    public FieldcompParamsShellException(Logger logger, double i, double o) {
        super(logger);
        logger.error("Inner shell has to be smaller than outer shell. Check '-si' and '-so' settings");
        logger.error("'-si' was : " + i);
        logger.error("'-so' was : " + o);
    }

}
