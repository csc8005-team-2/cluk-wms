package org.team2.cluk.backend.webresources;

import org.apache.commons.lang3.RandomStringUtils;
import org.team2.cluk.backend.tools.DbConnection;
import org.team2.cluk.backend.tools.ServerLog;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.StringReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

@Path("/")
public class Authorisation {
    // initialise hashmap for storing currently authorised users
    private static HashMap<String, String> userTokens = new HashMap<>();

    // used code from https://stackoverflow.com/questions/3103652/hash-string-via-sha-256-in-java
    private static String hashString(String plainText, String hashAlgorithm) {
        MessageDigest md;
        String returnHexHash = "";

        try {
            md = MessageDigest.getInstance(hashAlgorithm);
            // Change this to UTF-16 if needed
            md.update(plainText.getBytes(StandardCharsets.UTF_8));
            byte[] digest = md.digest();
            returnHexHash = String.format("%064x", new BigInteger(1, digest));
        } catch (NoSuchAlgorithmException e) {
            ServerLog.writeLog("Error when hashing string. Hashing algorithm " + hashAlgorithm + "not found");
        }

        return returnHexHash;
    }

    @Path("/login")
    @POST
    @Consumes("application/json")
    public Response loginUser(String loginData) {
        // create variable to check whether login was successful
        boolean loginSuccessful = false;

        // create response object
        Response res = null;

        // parsing incoming json using javax.json library from Java EE
        JsonReader jsonReader = Json.createReader(new StringReader(loginData));
        JsonObject loginDataObject = jsonReader.readObject();
        jsonReader.close();

        // check if json is correct
        if (!loginDataObject.containsKey("username") || !loginDataObject.containsKey("password")) {
            ServerLog.writeLog("Login request without username or password arrived. Rejecting login attempt.");
            return Response.status(Response.Status.BAD_REQUEST).entity("MISSING_USERNAME_OR_PASSWORD").build();
        }

        // json correct, continue
        String username = loginDataObject.getString("username");
        String password = loginDataObject.getString("password");

        ServerLog.writeLog("User " + username + " tries to log in");

        // check if user already logged in
        if (userTokens.containsValue(username)) {
            ServerLog.writeLog("User " + username + " already logged in. Rejecting login attempt.");
            return Response.status(Response.Status.CONFLICT).entity("USER_ALREADY_LOGGED_IN").build();
        }

        String passwordHash = hashString(password, "SHA-256");
        // checking if authorisation successful

        // fetch current database connection
        Connection connection = DbConnection.getConnection();

        Statement statement = null;
        String query = "SELECT username, password " +
                "FROM Accounts " +
                "WHERE username ='" + username + "'";
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()) {
                String storedHash = rs.getString("password");
                if (passwordHash.equals(storedHash))
                    loginSuccessful = true;
            }
        } catch (SQLException e) {
            ServerLog.writeLog("Error verifying user " + username + "credentials");
            res = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("CREDENTIAL_QUERY_ERROR").build();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    ServerLog.writeLog("SQL exception occurred when closing SQL statement");
                }
            }
        }


        // if auth successful, generate token and add to hashmap
        // generating token using Apache Common Lang library as per
        // https://www.baeldung.com/java-random-string
        if (loginSuccessful) {
            String newIdToken;
            // generate tokens until unique token generated
            do {
                newIdToken = RandomStringUtils.randomAlphanumeric(256);
            } while (userTokens.containsKey(newIdToken));

            userTokens.put(newIdToken, username);

            // return token to the user on successful login
            res = Response.status(Response.Status.OK).entity(newIdToken).build();
        } else res = Response.status(Response.Status.UNAUTHORIZED).entity("WRONG_CREDENTIALS").build();

        return res;
    }

    @Path("/logout")
    @GET
    public Response logoutUser(@HeaderParam("Authorization") String idToken) {
        if (userTokens.containsKey(idToken)) {
            userTokens.remove(idToken);
            return Response.status(Response.Status.ACCEPTED).entity("LOGOUT_SUCCESSFUL").build();
        }
        return Response.status(Response.Status.NOT_FOUND).entity("USER_NEVER_LOGGED_IN").build();
    }
}
