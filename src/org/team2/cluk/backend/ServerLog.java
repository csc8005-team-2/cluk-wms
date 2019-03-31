package org.team2.cluk.backend;

import java.util.ArrayList;
import java.util.Date;

/**
 * Simple logging system for the backend. Static method used to ensure one log is running for one server instance.
 *
 * @author Matt Grabara
 * @version 31/03/2019
 */
class ServerLog {
    static ArrayList<String> log = new ArrayList<>();

    static void writeLog(String msg) {
        Date currentDateTime = new Date();
        String logEntry = "[" + currentDateTime + "] " + msg;
        log.add(logEntry);
        System.out.println(logEntry);
    }

    public static ArrayList<String> getLog() {
        return (ArrayList<String>) log.clone();
    }
}
