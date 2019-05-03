package org.team2.cluk.backend.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class for handling SQL connection for the backend
 *
 * @version 31/03/2019
 */
public class DbConnection {
    private static Connection connection = null;
/*
 * This method can help users to establish connection to the database.Users need to input username,password and url.They are all String type.
 * If every thing goes right the system will show "Database connection established".
 * If something goes wrong the system will show "JDBC driver not found" or "Database connection error" or "Unrecognised exception encountered".
 */
    public static void connect(String userName, String password, String url) {
        try {
            // importing MySQL driver as per MySQL website
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            connection = DriverManager.getConnection(url, userName, password);
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

    public static void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            ServerLog.writeLog("Exception occurred when disconnecting from the database");
        }
    }

    public static Connection getConnection() {
        return connection;
    }
}
