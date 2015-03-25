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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * This abstract class manages a connection to a database for finding chemical compounds properties
 *
 * @author hedin
 */
public abstract class DB_interface {

    protected static final Logger logger = Logger.getLogger(DB_interface.class);

    protected static final String colName = "name";
    protected static final String colFormula = "formula";
    protected static final String colSmiles = "smiles";
    protected static final String colMass = "mass";
    protected static final String colDensity = "density";
    protected static final String colHvap = "Hvap";
    protected static final String colGsolv = "Gsolv";

    protected Connection connect = null;

    /**
     * 
     * @param rset
     * @return 
     */
    protected List<DB_model> parseResultSet(ResultSet rset) {

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
     * @return 
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
            resultSet = statement.executeQuery("select `compounds`.name,`structure`.formula,"
                    + " `structure`.smiles, `prop`.mass, `prop`.density, `prop`.Hvap, `prop`.Gsolv"
                    + " from `compounds`,`prop`,`structure` where `compounds`.name like '%" + name + "%'"
                    + " and compounds.id=prop.id and compounds.id=structure.id");
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
     * @return 
     */
    public List<DB_model> findByFormula(String formula) {
        
        Statement statement = null;
        ResultSet resultSet = null;
        List<DB_model> modelList = null;
        
        try {
            //prepare statement and execute it
            statement = connect.createStatement();
            resultSet = statement.executeQuery("select `compounds`.name,`structure`.formula,"
                    + " `structure`.smiles, `prop`.mass, `prop`.density, `prop`.Hvap, `prop`.Gsolv"
                    + " from `compounds`,`prop`,`structure` where `structure`.formula like '%" + formula + "%'"
                    + " and compounds.id=prop.id and compounds.id=structure.id");
            modelList = parseResultSet(resultSet);
        } catch (SQLException ex) {
            logger.error("Error when executing SQL statement in findByFormula() ! " + ex.getMessage());
        }

        return modelList;
    }

    /**
     * Find data using compound SMILES notation
     *
     * @param smiles
     * @return 
     */
    public List<DB_model> findBySMILES(String smiles) {
        
        Statement statement = null;
        ResultSet resultSet = null;
        List<DB_model> modelList = null;
        
        try {
            //prepare statement and execute it
            statement = connect.createStatement();
            resultSet = statement.executeQuery("select `compounds`.name,`structure`.formula,"
                    + " `structure`.smiles, `prop`.mass, `prop`.density, `prop`.Hvap, `prop`.Gsolv"
                    + " from `compounds`,`prop`,`structure` where `structure`.smiles like '%" + smiles + "%'"
                    + " and compounds.id=prop.id and compounds.id=structure.id");
            modelList = parseResultSet(resultSet);
        } catch (SQLException ex) {
            logger.error("Error when executing SQL statement in findBySMILES() ! " + ex.getMessage());
        }

        return modelList;
    }

    /**
     * Find data using compound mass + or - a threshold
     *
     * @param target
     * @param threshold
     * @return 
     */
    public List<DB_model> findByMASS(double target, double threshold) {
        
        List<DB_model> res = new ArrayList<>();
        
        return res;
    }
}
