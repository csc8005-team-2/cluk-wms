package org.team2.cluk.backend;

import com.sun.net.httpserver.HttpServer;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.team2.cluk.backend.tools.DbConnection;
import org.team2.cluk.backend.tools.ServerLog;
import org.team2.cluk.backend.webresources.*;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * Class containing main class used to start the server
 * <p><b>TO DO:</b></p>
 * <p><ol><li>
 *     Import DB settings from file
 * </li><li>
 *     Create way to exit application gracefully
 * </li><li>
 *     Export log to a file
 * </li></ol></p>
 *
 * @version 31/03/2019
 */
public class StartServer {
    public static void main (String[] args) {
        // http(s) server settings
        final String listeningUri = "http://localhost/"; // server will be accessible under this URI
        final int listeningPort = 9998; // server will be listening on this port
        // database connection settings
        String userName = "csc8005_team02";
        String password = "HogsGet(Text";
        String url = "jdbc:mysql://homepages.cs.ncl.ac.uk/csc8005_team02";
        // connect to the database
        DbConnection.connect(userName, password, url);
        // initialise server
        URI baseUri = UriBuilder.fromUri(listeningUri).port(listeningPort).build();
        ResourceConfig config = new ResourceConfig(CorsFilter.class, Warehouse.class, Restaurant.class, Authorisation.class);
        HttpServer server = JdkHttpServerFactory.createHttpServer(baseUri, config);
        ServerLog.writeLog("Server running on " + listeningUri + " listening on port " + listeningPort);
    }
}
