/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
//package ch.unibas.pythonBinding;
//
//import java.util.Enumeration;
//import java.util.Map;
//import java.util.Properties;
//import org.python.util.PythonInterpreter;
//
///**
// *
// * @author hedin
// */
//public class jythonTest {
//
//    /**
//     * @param args the command line arguments
//     */
//    public static void main(String[] args) {
//
//        Map<String, String> env = System.getenv();
//
//        for (Map.Entry<String, String> entry : env.entrySet()) {
//            System.out.println(entry.getKey() + " : " + entry.getValue());
//        }
//
//        Properties oldProps = System.getProperties();
//        Properties newProps = new Properties();
//        newProps.setProperty("python.path", "/usr/local/lib64/python2.7/site-packages/");
//        newProps.setProperty("java.library.path", env.get("LD_LIBRARY_PATH"));
//
//        Enumeration e = oldProps.propertyNames();
//        while (e.hasMoreElements()) {
//            String key = (String) e.nextElement();
//            System.out.println(key + " -- " + oldProps.getProperty(key));
//        }
//
//        PythonInterpreter.initialize(oldProps, newProps, null);
//        PythonInterpreter python = new PythonInterpreter();
////        python.execfile("/home/hedin/progra/workflowopt/scripts/check_rdkit_dependency.py");
//        python.execfile("/home/hedin/progra/workflowopt/scripts/check_scipy_dependency.py");
//
//    }//main
//
//
//}
