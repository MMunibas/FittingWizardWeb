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
public class FieldcompParamsShellException extends FieldcompParamsException {

    public FieldcompParamsShellException(float i, float o) {
        super();
        System.err.println("Inner shell has to be smaller than outer shell. Check '-si' and '-so' settings");
        System.err.println("'-si' was : " + i);
        System.err.println("'-so' was : " + o);
    }

}
