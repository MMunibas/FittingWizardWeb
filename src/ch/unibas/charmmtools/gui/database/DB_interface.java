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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 * This class manages a connection to a mysql database for finding chemical compounds properties
 *
 * @author hedin
 */
public class DB_interface {

    private static final Logger logger = Logger.getLogger(DB_interface.class);

    private String db_url = "jdbc:mysql://localhost:3306/fittingWizard";
    private String db_pass = "z6nNfrAhyxb2cLA8";
    private String db_user = "fittingWizard";

//    private static final String byNameQuery = "select `compounds`.name, `structure`.formula, `structure`.smiles,"
//            + " `prop`.mass, `prop`.density, `prop`.Hvap, `prop`.Gsolv from `compounds`,`prop`,`structure`where `compounds`.name like '%?%'";

    private static final String colName = "name";
    private static final String colFormula = "formula";
    private static final String colSmiles = "smiles";
    private static final String colMass = "mass";
    private static final String colDensity = "density";
    private static final String colHvap = "Hvap";
    private static final String colGsolv = "Gsolv";

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
            logger.error("Error when executing test statement 'show tables' on Mysql database ! " + ex.getMessage());
        }

    }

    /**
     * Open connection to database
     *
     * @param _url
     * @param _pass
     * @param _user
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
            logger.error("Error when executing test statement 'show tables' on Mysql database ! " + ex.getMessage());
        }

    }

    private List<DB_model> parseResultSet(ResultSet rset) {

        List<DB_model> parsedList = new ArrayList<>();

        try {
            // then parse result
            while (rset.next()) {
                String name = rset.getString(colName);
                String formula = rset.getString(colFormula);
                String smiles = rset.getString(colSmiles);
                String mass = rset.getString(colMass);
                String density = rset.getString(colDensity);
                String dh = rset.getString(colHvap);
                String dg = rset.getString(colGsolv);
                parsedList.add(new DB_model(name, formula, smiles, mass, density, dh, dg));
            }
        } catch (SQLException ex) {
            logger.error("Error when parsing ResultSet of SQL statement in parseResultSet() ! " + ex.getMessage());
        }

        return parsedList;
    }

    /**
     * Find data using compound name
     *
     * @param name
     */
    public List<DB_model> findByName(String name) {

//        PreparedStatement statement = null;
        Statement statement = null;
        ResultSet resultSet = null;
        List<DB_model> modelList = null;
        
        try {
            //prepare statement and execute it
//            statement = connect.prepareStatement(byNameQuery);
//            statement.setString(1, name);
            statement = connect.createStatement();
            resultSet = statement.executeQuery("select `compounds`.name,`structure`.formula, `structure`.smiles, `prop`.mass, `prop`.density, `prop`.Hvap, `prop`.Gsolv from `compounds`,`prop`,`structure` where `compounds`.name like '%" + name + "%'");
            modelList = parseResultSet(resultSet);
        } catch (SQLException ex) {
            logger.error("Error when executing SQL statement in findByName() ! " + ex.getMessage());
        }



        return modelList;
    }

    /**
     * Find data using compound formula
     *
     * @param formula
     */
    public List<DB_model> findByFormula(String formula) {
        List<DB_model> res = new ArrayList<>();
        return res;
    }

    /**
     * Find data using compound SMILES notation
     *
     * @param smiles
     */
    public List<DB_model> findBySMILES(String smiles) {
        List<DB_model> res = new ArrayList<>();
        return res;
    }

    /**
     * Find data using compound mass + or - a threshold
     *
     * @param target
     * @param threshold
     */
    public List<DB_model> findByMASS(double target, double threshold) {
        List<DB_model> res = new ArrayList<>();
        return res;
    }
}
