package org.team2.cluk.backend.tools;

import java.util.ArrayList;
import java.util.Date;

/*
 * Simple logging system for the backend
 * Static method used to ensure one log is running for one server instance
 */

public class ServerLog {
    private static ArrayList<String> log = new ArrayList<>();

    public static void writeLog(String msg) {
        Date currentDateTime = new Date();
        String logEntry = "[" + currentDateTime + "] " + msg;
        log.add(logEntry);
        System.out.println(logEntry);
    }

    public static ArrayList<String> getLog() {
        return (ArrayList<String>) log.clone();
    }
}
