package org.team2.cluk.backend;

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

    static void connect(String userName, String password, String url) {
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

    static void disconnect() {
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
