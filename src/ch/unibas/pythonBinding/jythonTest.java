/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.unibas.pythonBinding;

import org.python.util.PythonInterpreter;
import org.python.core.*;

/**
 *
 * @author hedin
 */
public class jythonTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        PythonInterpreter python = new PythonInterpreter();

        int number1 = 10;
        int number2 = 32;

        python.set("number1", new PyInteger(number1));
        python.set("number2", new PyInteger(number2));
        python.exec("number3 = number1+number2");
        PyObject number3 = python.get("number3");
        System.out.println("val : " + number3.toString());
    }


}
