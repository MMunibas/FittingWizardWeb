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
public class FieldcompParamsException extends Exception {

    public FieldcompParamsException() {
        System.err.println("Error detected with parameters passed to Fieldcomp : ");
    }

}
