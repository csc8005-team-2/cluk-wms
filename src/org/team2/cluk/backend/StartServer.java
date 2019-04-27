package org.team2.cluk.backend;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.team2.cluk.backend.tools.DbConnection;
import org.team2.cluk.backend.tools.ServerLog;
import org.team2.cluk.backend.webresources.*;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;
import javax.ws.rs.core.UriBuilder;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;

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
        boolean noSsl = false;

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

        // determine if --noSsl argument given
        if (arguments.contains("--nossl")) {
            noSsl = true;
        }

        // initialise key path
        final String keyStorePath =
                (!noSsl && arguments.contains("--keystore") && arguments.indexOf("--keystore") != arguments.size()-1)
                        ? arguments.get(arguments.indexOf("--keystore")+1) : "";

        // get keystore password
        final String keyStorePassword =
                (!noSsl && arguments.contains("--password") && arguments.indexOf("--password") != arguments.size()-1)
                        ? arguments.get(arguments.indexOf("--password")+1) : "";

        // initialise server resources
        ResourceConfig config = new ResourceConfig(CorsFilter.class, Warehouse.class, Restaurant.class, Authorisation.class);

        // build base URI based on whether SSL connection desired
        if (noSsl) {
            listeningUri = "http://localhost/"; // server will be accessible under this URI
            listeningPort = 80; // server will be listening on this port
        } else {
            // by default, launch ssl server
            listeningUri = "https://localhost/"; // server will be accessible under this URI
            listeningPort = 443; // server will be listening on this port
        }
        final URI baseUri = UriBuilder.fromUri(listeningUri).port(listeningPort).build();

        // if no SSL desired, launch server
        if (noSsl) {
            HttpServer server = JdkHttpServerFactory.createHttpServer(baseUri, config);
        } else {

            // HTTPS server settings
            try {
                // load keystore file
                InputStream jksStream = new FileInputStream(keyStorePath);

                // create SSL context
                SSLContext sslContext = SSLContext.getInstance("TLS");

                // initialise the keystore
                KeyStore keyStore = KeyStore.getInstance("JKS");
                keyStore.load (jksStream, keyStorePassword.toCharArray());

                // setup the key manager factory
                KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
                keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());

                // setup the trust manager factory
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
                trustManagerFactory.init(keyStore);

                // initialise SSL context
                sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

                // create instance of HttpServer with SSL context
                HttpServer httpServer = JdkHttpServerFactory.createHttpServer(baseUri, config, sslContext, false);

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

        // database connection settings
        String userName = "csc8005_team02";
        String password = "HogsGet(Text";
        String url = "jdbc:mysql://homepages.cs.ncl.ac.uk/csc8005_team02";
        // connect to the database
        DbConnection.connect(userName, password, url);
    }
}
