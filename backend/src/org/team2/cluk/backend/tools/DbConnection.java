package org.team2.cluk.backend.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DbConnection Class for handling SQL connection for the backend
 *
 * @version 31/03/2019
 */

public class DbConnection {
    private static Connection connection = null;
    private static String _username = "";
    private static String _password = "";
    private static String _url = "";

   /**
    * Method to allow users to establish a connection to the database
    * Users input a username,password and url, all of type string
    * If successful, the system will show "Database connection established" and connect to the server
    * If it fails, the system will show "JDBC driver not found" or "Database connection error" or "Unrecognised exception encountered"
    * @param username database username
    * @param password database password
    * @param url    URI under which database is available in MySQL Connector/J format
    */
    public static void connect(String username, String password, String url) {
        try {
            // save login credentials so connection can be restored later
            _username = username;
            _password = password;
            _url = url;
            // importing MySQL driver as per MySQL website
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            connection = DriverManager.getConnection(url, username, password);
            ServerLog.writeLog("Database connection established");
        } catch (ClassNotFoundException e1) {
            ServerLog.writeLog("JDBC driver not found");
        } catch (SQLException e2) {
            ServerLog.writeLog("Database connection error");
        } catch (Exception e) {
            ServerLog.writeLog("Unrecognised exception encountered");
            e.printStackTrace();
        }
    }

   /**
    * Method to allow users to disconnect from the database
    * If it fails, the system will show "Exception occurred when disconnecting from the database"
    */
    public static void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            ServerLog.writeLog("Exception occurred when disconnecting from the database");
        }
    }

    /**
    * Accessor method to get the connection
    * @return connection to the database
    */
    public static Connection getConnection() {
        if (connection == null) {
            try {
                ServerLog.writeLog("Restoring database connection");
                connection = DriverManager.getConnection(_url, _username, _password);
            } catch (SQLException e) {
                ServerLog.writeLog("Cannot restore connection with database");
            }
        }
        return connection;
    }
}
