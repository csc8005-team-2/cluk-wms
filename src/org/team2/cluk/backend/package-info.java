/**
 * Package containing StartServer class with main method.
 *
 * Backend can run on Oracle JDK and OpenJDK version 12.
 * Root permissions might be required to run the main method.
 *
 * Required dependencies are in lib folder. Depending on the IDE, it might be required to add them to the build/executino path manually.
 *
 * In order to start server, provide path to the configuration file after '--config' parameter.
 * Structure of the config file:
 * {"hostname": string (optional, localhost by default),
 * "port": number (optional, 80 or 443 by default, depending whether keystore provided),
 * "keystore": string (optional),
 * "keystorePassword: string (optional),
 * "dbURI": string,
 * "dbUsername": string,
 * "dbPassword": string}
 * where:
 * hostname - FQDN under which server will be available,
 * port - port number under which server will be available,
 * keystore - path to Java Keystore file containing SSL certificate; required for launching server in HTTPS mode,
 * keystorePassword - password to the Java Keystore file provided above,
 * dbURI - URI of the database in MySQL Connector/J format, e.g.: jdbc:mysql://example.com/cluk-schema,
 * dbUsername - username for database access,
 * dbPassword - password for database access.
 */
package org.team2.cluk.backend;
