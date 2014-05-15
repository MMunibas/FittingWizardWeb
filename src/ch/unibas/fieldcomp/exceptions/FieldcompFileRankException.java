/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.unibas.fieldcomp.exceptions;

/**
 *
 * @author hedin
 */
final public class FieldcompFileRankException extends Exception {

    public FieldcompFileRankException(String id) {
        System.err.println("Error : irank[i] != jrank[i] for lpun atomid '" + id
                + "' ; Check rank in vdw and dma file !");
        System.exit(-1);
    }

}
