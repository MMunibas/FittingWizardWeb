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
public class FieldcompUnknownOutputFileType extends Exception {

    public FieldcompUnknownOutputFileType(Logger logger, String mode) {
        logger.error("Unknown OutputFile mode '" + mode + "'");
        logger.error("It should be one of 'gausscube' , 'mtpcube'  , 'diffcube'");
    }
}
