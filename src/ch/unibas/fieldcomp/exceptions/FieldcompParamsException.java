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
public class FieldcompParamsException extends Exception {

    public FieldcompParamsException(Logger logger) {
        logger.error("Error detected with parameters passed to Fieldcomp : ");
    }

}
