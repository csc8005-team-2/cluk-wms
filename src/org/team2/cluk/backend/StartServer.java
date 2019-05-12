package org.team2.cluk.backend;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.team2.cluk.backend.tools.DbConnection;
import org.team2.cluk.backend.tools.JsonTools;
import org.team2.cluk.backend.tools.ServerLog;
import org.team2.cluk.backend.webresources.*;

import javax.json.JsonObject;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;
import javax.ws.rs.core.UriBuilder;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URI;
import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * StartServer Class which is the main class used to start the server
 *
 * @version 31/03/2019
 */
public class StartServer {
    private static void printHelp() {
        final String helpInfo = "CLUK Warehouse Management System\n" +
                "Backend Module, version 1.0\n\n" +
                "In order to start server, provide path to the configuration file after '--config' parameter.\n\n" +
                "Structure of the config file:\n" +
                "{\"hostname\": string /*optional, localhost by default*/,\n" +
                "\"port\" number /*optional, 80 or 443 by default, depending whether keystore provided*/,\n" +
                "\"keystore\": string /*optional*/,\n" +
                "\"keystorePassword\": string /*optional*/,\n" +
                "\"dbURI\": string,\n" +
                "\"dbUsername\": string,\n" +
                "\"dbPassword\": string}\n\n" +
                "where:\n" +
                "hostname - FQDN under which server will be available,\n" +
                "port - port number under which server will be available,\n" +
                "keystore - path to Java Keystore file containing SSL certificate; required for launching server in HTTPS mode,\n" +
                "keystorePassword - password to the Java Keystore file provided above,\n" +
                "dbURI - URI of the database in MySQL Connector/J format, e.g.: jdbc:mysql://example.com/cluk-schema,\n" +
                "dbUsername - username for database access,\n" +
                "dbPassword - password for database access.\n";

        System.out.println(helpInfo);

    }

    public static void main(String[] args) {
        boolean useSsl = false;

        // initialise URI and port
        final String listeningUri;
        final int listeningPort;

        // process arguments from args
        ArrayList<String> rawArguments = new ArrayList<>(Arrays.asList(args));

        // convert to lower case
        ArrayList<String> arguments = new ArrayList<>();
        for (String argument: rawArguments) {
            arguments.add(argument.toLowerCase());
        }

        // check if config file provided
        if (!arguments.contains("--config") || arguments.indexOf("--config") == arguments.size()-1) {
            ServerLog.writeLog("No configuration file provided");
            printHelp();
            System.exit(0);
        }

        // get config file path
        final String configFilePath = arguments.get(arguments.indexOf("--config")+1);

        // load config file
        final Scanner configFile;
        final StringBuilder configBuilder = new StringBuilder();
        try {
            configFile = new Scanner(new FileReader(configFilePath));
            // read configuration to a string
            while (configFile.hasNext()) {
                configBuilder.append(configFile.nextLine());
            }
        } catch (FileNotFoundException e) {
            ServerLog.writeLog("Configuration file not found in path specified");
            System.exit(0);
        }

        // parse configuration string as a JSON
        JsonObject config = JsonTools.parseObject(configBuilder.toString());

        if (config.containsKey("keystore"))
            useSsl = true;

        final String hostname = (config.containsKey("hostname")) ? config.getString("hostname") : "localhost";
        final int port = (config.containsKey("port")) ? config.getInt("port") : ((useSsl) ? 443 : 80);

        // set up database connection
        if (config.containsKey("dbURI") && config.containsKey("dbUsername") && config.containsKey("dbPassword")) {
            String userName = config.getString("dbUsername");
            String password = config.getString("dbPassword");
            String uri = config.getString("dbURI");
            // connect to the database
            DbConnection.connect(userName, password, uri);
        } else {
            ServerLog.writeLog("Configuration file contains incomplete database connection settings");
            System.exit(0);
        }

        // initialise server resources
        ResourceConfig resourceConfig = new ResourceConfig(CorsFilter.class, Warehouse.class, Restaurant.class, Driver.class, Authorisation.class);

        // build base URI based on whether SSL connection desired
        if (!useSsl) {
            listeningUri = "http://" + hostname + "/"; // server will be accessible under this URI
            listeningPort = port; // server will be listening on this port
        } else {
            // by default, launch ssl server
            listeningUri = "https://" + hostname + "/"; // server will be accessible under this URI
            listeningPort = port; // server will be listening on this port
        }
        final URI baseUri = UriBuilder.fromUri(listeningUri).port(listeningPort).build();

        // if no SSL desired, launch server
        if (!useSsl) {
            HttpServer server = JdkHttpServerFactory.createHttpServer(baseUri, resourceConfig);
        } else {

            // HTTPS server settings
            try {
                // load keystore file
                InputStream jksStream = new FileInputStream(config.getString("keystore"));

                // create SSL context
                SSLContext sslContext = SSLContext.getInstance("TLS");

                String keystorePassword = (config.containsKey("keystorePassword"))
                        ? config.getString("keystorePassword") : "";

                // initialise the keystore
                KeyStore keyStore = KeyStore.getInstance("JKS");
                keyStore.load (jksStream, keystorePassword.toCharArray());

                // setup the key manager factory
                KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
                keyManagerFactory.init(keyStore, keystorePassword.toCharArray());

                // setup the trust manager factory
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
                trustManagerFactory.init(keyStore);

                // initialise SSL context
                sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

                // create instance of HttpServer with SSL context
                HttpServer httpServer = JdkHttpServerFactory.createHttpServer(baseUri, resourceConfig, sslContext, false);

                // downcasting HttpServer to HttpsServer
                if (httpServer instanceof HttpsServer) {
                    HttpsServer server = (HttpsServer) httpServer;

                    // configure HTTPS server
                    server.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
                        public void configure(HttpsParameters params) {
                            SSLContext c = getSSLContext();

                            // get default parameters
                            SSLParameters sslParams = c.getDefaultSSLParameters();

                            params.setSSLParameters(sslParams);
                        }
                    });

                    server.start();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        ServerLog.writeLog("Server running on " + listeningUri + " listening on port " + listeningPort);
        ServerLog.writeLog("Press Ctrl+C to stop server.");
    }
}
