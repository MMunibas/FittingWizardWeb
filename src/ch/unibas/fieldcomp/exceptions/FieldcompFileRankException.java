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
public class FieldcompFileRankException extends Exception {

    public FieldcompFileRankException(Logger logger, String id) {
        logger.error("Error : irank[i] != jrank[i] for lpun atomid '" + id
                + "' ; Check rank in vdw and dma file !");
    }

}
