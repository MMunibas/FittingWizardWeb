/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.gui.database;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLITE_DB_interface extends DB_interface {

    private String db_url = "jdbc:sqlite:db/db.sqlite3";

    public SQLITE_DB_interface() {
        // prepare connection parameters and try to connect
        try {
            connect = DriverManager.getConnection(db_url);
        } catch (SQLException ex) {
            logger.error("Error when connecting to SQLite database ! " + ex.getMessage());
        }

        // test statement to check if connection is OK
        try {
            Statement statement = connect.createStatement();
            ResultSet resultSet = statement.executeQuery("PRAGMA encoding");

            logger.info("Executing test query " + resultSet.getStatement().toString() + " on DB " + db_url);

        } catch (SQLException ex) {
            logger.error("Error when executing test statement 'PRAGMA encoding' on SQLite database ! " + ex.getMessage());
        }
    }

    /**
     * 
     * @param _path 
     */
    public SQLITE_DB_interface(String _path) {
        this.db_url = _path;

        // prepare connection parameters and try to connect
        try {
            connect = DriverManager.getConnection(db_url);
        } catch (SQLException ex) {
            logger.error("Error when connecting to SQLite database ! " + ex.getMessage());
        }

        // test statement to check if connection is OK
        try {
            Statement statement = connect.createStatement();
            ResultSet resultSet = statement.executeQuery("PRAGMA encoding");

            logger.info("Executing test query " + resultSet.getStatement().toString() + " on DB " + db_url);

        } catch (SQLException ex) {
            logger.error("Error when executing test statement 'PRAGMA encoding' on SQLite database ! " + ex.getMessage());
        }
    }

}
