/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.gui.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.log4j.Logger;

/**
 * This class manages a connection to a mysql database for finding chemical compounds properties
 * @author hedin
 */
public class DB_interface {

    private static final Logger logger = Logger.getLogger(DB_interface.class);

    private String db_url = "jdbc:mysql://localhost:3306/fittingWizard";
    private String db_pass = "z6nNfrAhyxb2cLA8";
    private String db_user = "fittingWizard";

    private Connection connect = null;
//    private Statement statement = null;
//    private PreparedStatement preparedStatement = null;
//    private ResultSet resultSet = null;

    /**
     * Open connection to database using default connection
     */
    public DB_interface() {
        
        // prepare connection parameters and try to connect
        try {
            connect = DriverManager.getConnection(db_url, db_user, db_pass);
        } catch (SQLException ex) {
            logger.error("Error when connecting to Mysql database ! " + ex.getMessage());
        }

        // test statement to check if connection is OK
        try {
            Statement statement = connect.createStatement();
            ResultSet resultSet = statement.executeQuery("show tables");

            logger.info("Executing test query " + resultSet.getStatement().toString() + " on DB " + db_url);

        } catch (SQLException ex) {
            logger.error("Error when executing test statement 'show tables' on Mysql database !" + ex.getMessage());
        }

    }
    
    /**
     * Open connection to database
     */
    public DB_interface(String _url, String _pass, String _user) {

        this.db_url = _url;
        this.db_pass = _pass;
        this.db_user = _user;
        
        // prepare connection parameters and try to connect
        try {
            connect = DriverManager.getConnection(db_url, db_user, db_pass);
        } catch (SQLException ex) {
            logger.error("Error when connecting to Mysql database ! " + ex.getMessage());
        }

        // test statement to check if connection is OK
        try {
            Statement statement = connect.createStatement();
            ResultSet resultSet = statement.executeQuery("show tables");

            logger.info("Executing test query " + resultSet.getStatement().toString() + " on DB " + db_url);

        } catch (SQLException ex) {
            logger.error("Error when executing test statement 'show tables' on Mysql database !" + ex.getMessage());
        }

    }
    
    /**
     * Find data using compound name
     * @param name 
     */
    public void findByName(String name) {
        
    }
    
    /**
     * Find data using compound formula
     * @param formula 
     */
    public void findByFormula(String formula) {

    }

    /**
     * Find data using compound SMILES notation
     * @param smiles 
     */
    public void findBySMILES(String smiles) {

    }

    /**
     * Find data using compound mass + or - a threshold
     * @param target
     * @param threshold 
     */
    public void findByMASS(double target, double threshold) {

    }
}
