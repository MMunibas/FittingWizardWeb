/*
 * Copyright (c) 2015, Florent Hedin, Markus Meuwly, and the University of Basel
 * All rights reserved.
 *
 * The 3-clause BSD license is applied to this software.
 * see LICENSE.txt
 *
 */
package ch.unibas.charmmtools.gui.database.interfaces;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class MYSQL_DB_interface extends DB_interface {

    private String db_url;
    private String db_pass;
    private String db_user;

    /**
     * Open connection to database
     *
     * @param _url
     * @param _pass
     * @param _user
     */
    public MYSQL_DB_interface(String _url, String _user, String _pass) {

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
}
