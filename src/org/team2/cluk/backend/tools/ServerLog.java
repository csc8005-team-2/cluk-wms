package org.team2.cluk.backend.tools;

import java.util.ArrayList;
import java.util.Date;

/**
 * ServerLog Class which provides a simple logging system for the backend
 * Static method used to ensure one log is running for one server instance
 *
 * @version 31/03/2019
 */
public class ServerLog {
    private static ArrayList<String> log = new ArrayList<>();

/**
 * Method to add a log for the system
 * @param msg message text
 */
    public static void writeLog(String msg) {
        Date currentDateTime = new Date();
        String logEntry = "[" + currentDateTime + "] " + msg;
        log.add(logEntry); // add log entry
        System.out.println(logEntry);
    }

    // create arraylist of log entries
    public static ArrayList<String> getLog() {
        return (ArrayList<String>) log.clone();
    }
}
