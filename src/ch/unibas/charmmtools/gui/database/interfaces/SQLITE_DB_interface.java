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
import java.sql.SQLException;
import java.sql.Statement;

public class SQLITE_DB_interface extends DB_interface {

    private String db_url;

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
            statement.execute("PRAGMA foreign_keys=ON");
            
            logger.info("Executing test query " + statement.toString() + " on DB " + db_url);

        } catch (SQLException ex) {
            logger.error("Error when executing test statement 'PRAGMA foreign_keys=ON' on SQLite database ! " + ex.getMessage());
        }
    }

}
