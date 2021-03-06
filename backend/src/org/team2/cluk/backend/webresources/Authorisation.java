package org.team2.cluk.backend.webresources;

import org.apache.commons.lang3.RandomStringUtils;
import org.team2.cluk.backend.tools.DbConnection;
import org.team2.cluk.backend.tools.JsonTools;
import org.team2.cluk.backend.tools.ServerLog;

import javax.json.*;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Authorisation class which handles accounts within the database
 * It enables account permissions for Restaurant, Warehouse and Driver
 */

@Path("/")
public class Authorisation {
    // initialise hashmap for storing currently authorised users
    private static HashMap<String, String> userTokens = new HashMap<>();
    private static HashSet<String> restaurantPermissions = new HashSet<>();
    private static HashSet<String> warehousePermissions = new HashSet<>();
    private static HashSet<String> driverPermissions = new HashSet<>();
    private static HashSet<String> managerPermissions = new HashSet<>();

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

   /**
    * Method to log in user to the system with specific username and password
    * Uses Json to get the correct username and password
    * If Json unsuccessful, the system will show "MISSING_USERNAME_OR_PASSWORD"
    * @param loginData   username and password
    * @return a successful log in
    */
    @Path("/login")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response loginUser(String loginData) {
        // create variable to check whether login was successful
        boolean loginSuccessful = false;

        // create response object
        Response.ResponseBuilder res = null;

        // parsing incoming json using javax.json library from Java EE
        JsonObject loginDataObject = JsonTools.parseObject(loginData);

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
        /* if (userTokens.containsValue(username)) {
            ServerLog.writeLog("User " + username + " already logged in. Rejecting login attempt.");
            return Response.status(Response.Status.CONFLICT).entity("USER_ALREADY_LOGGED_IN").build();
        } */

        ServerLog.writeLog("Generating password hash for " + username);
        String passwordHash = hashString(password, "SHA-256");

        // checking if authorisation successful

        // fetch current database connection
        Connection connection = DbConnection.getConnection();

        // initialise variable for storing work location
        String location = "";

        Statement statement = null;
        String query = "SELECT username, password, workLocation " +
                "FROM Accounts " +
                "WHERE username ='" + username + "'";
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            ServerLog.writeLog("Retrieved user's " + username + " data from database");
            if (rs.next()) {
                String storedHash = rs.getString("password");
                if (passwordHash.equals(storedHash)) {
                    loginSuccessful = true;
                    location = rs.getString("workLocation");
                    ServerLog.writeLog("Login successful! User " + username + " works at " + location);
                }
            }
        } catch (SQLException e) {
            ServerLog.writeLog("Error verifying user " + username + "credentials");
            res = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("CREDENTIAL_QUERY_ERROR");
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    ServerLog.writeLog("SQL exception occurred when closing SQL statement");
                }
            }
        }

        /* if authorization successful, generate token and add to hashmap
        generating token using Apache Common Lang library as per
        https://www.baeldung.com/java-random-string */
        if (loginSuccessful) {
            ServerLog.writeLog("Preparing output JSON for login " + username);
            String newIdToken;
            // generate tokens until unique token generated
            do {
                newIdToken = RandomStringUtils.randomAlphanumeric(256);
            } while (userTokens.containsKey(newIdToken));

            userTokens.put(newIdToken, username);

            // generate output JSON
            JsonObjectBuilder outputJsonBuilder = Json.createObjectBuilder();
            outputJsonBuilder.add("idToken", newIdToken);
            outputJsonBuilder.add("location", (location == null) ? "" : location);
            JsonObject outputJson = outputJsonBuilder.build();
            ServerLog.writeLog("Output JSON for " + username + " ready");
            // return token to the user on successful login
            res = Response.status(Response.Status.OK).entity(outputJson.toString());
        } else res = Response.status(Response.Status.UNAUTHORIZED).entity("WRONG_CREDENTIALS");

        return res.build();
    }



    /**
    * Method to log out user from the system
    * If successful, the system will show "LOGOUT_SUCCESSFUL"
    * If unsuccessful, the system will show "USER_NEVER_LOGGED_IN"
    * @param idToken for the username of a user in the system
    * @return a successful log out
    */
    @Path("/logout")
    @GET
    public Response logoutUser(@HeaderParam("Authorization") String idToken) {
        if (userTokens.containsKey(idToken)) {
            // retrieve username only for logging purposes
            String username = userTokens.get(idToken);
            // log out
            userTokens.remove(idToken);
            warehousePermissions.remove(idToken);
            restaurantPermissions.remove(idToken);
            driverPermissions.remove(idToken);
            managerPermissions.remove(idToken);

            ServerLog.writeLog("User " + username + " successfully logged out");
            JsonObject response = Json.createObjectBuilder().add("message", "LOGOUT_SUCCESSFUL").build();
            return Response.status(Response.Status.OK).entity(response.toString()).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("USER_NEVER_LOGGED_IN").build();
    }

    /* public static boolean checkAuthHeader(String authHeader) {
        if (userTokens.containsKey(authHeader))
            return true;
        return false;
    }

    //check permissions using authorization header and role
    public static boolean checkPermissions(String authHeader, Roles role) {
        switch (role) {
            case WAREHOUSE: if (warehousePermissions.contains(authHeader))
                return true;
            case RESTAURANT: if (restaurantPermissions.contains(authHeader))
                return true;
            case DRIVER: if (driverPermissions.contains(authHeader))
                return true;
        }
        return false;
    } */

   /**
    * Method to add an account to the system with a username and password
    * If successful, the system will show "ACCOUNT_CREATED"
    * If Json unsuccessful, the system will show "MISSING_NAME_USERNAME_OR_PASSWORD"
    * @param idToken for the username of the new user in the system
    * @param requestBody using Json to request to add a new account
    * @return the new account to the database
    */
    @Path("/accounts/add")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response addAccount(@HeaderParam("Authorization") String idToken, String requestBody) {
        Connection connection = DbConnection.getConnection();

        // parsing incoming json using javax.json library from Java EE
        JsonObject loginDataObject = JsonTools.parseObject(requestBody);

        // check if json is correct
        if (!loginDataObject.containsKey("username") || !loginDataObject.containsKey("password") || !loginDataObject.containsKey("name")) {
            ServerLog.writeLog("New account request without name, username or password arrived. Rejecting login attempt.");
            return Response.status(Response.Status.BAD_REQUEST).entity("MISSING_NAME_USERNAME_OR_PASSWORD").build();
        }

        // json correct, continue
        String username = loginDataObject.getString("username");
        String password = loginDataObject.getString("password");
        String name = loginDataObject.getString("name");

        Statement statement = null;
        String query = "INSERT INTO Accounts (name, username, password) SELECT '"+name+"', '"+username+"', SHA2('"+password+"', 256)";

        try{
            statement = connection.createStatement();
            statement.executeUpdate(query);
            ServerLog.writeLog("Account created for " +name+".\n");

        } catch (SQLException e ) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    ServerLog.writeLog("SQL exception occurred when closing SQL statement");
                }
            }
        }

        JsonObject response = Json.createObjectBuilder().add("message", "ACCOUNT_CREATED").build();
        return Response.status(Response.Status.OK).entity(response.toString()).build();
    }

   /**
    * Method to refresh the permissions for the accounts in the database
    * If unsuccessful, the system will show "Error verifying user " + username + "credentials"
    * @param username of an account
    */
    public static void refreshPermissions(String username) {
        // fetch current database connection
        Connection connection = DbConnection.getConnection();

        // initialise permissions
        boolean restaurant = false;
        boolean warehouse = false;
        boolean driver = false;
        boolean manager = false;

        Statement statement = null;
        String query = "SELECT restaurant, warehouse, driver, manager " +
                "FROM Accounts " +
                "WHERE username ='" + username + "'";
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()) {
                restaurant = rs.getBoolean("restaurant");
                warehouse = rs.getBoolean("warehouse");
                driver = rs.getBoolean("driver");
                manager = rs.getBoolean("manager");
            }
        } catch (SQLException e) {
            ServerLog.writeLog("Error verifying user " + username + " credentials");
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    ServerLog.writeLog("SQL exception occurred when closing SQL statement");
                }
            }
        }

        //add assigned tokens to permissions for Restaurant, Warehouse, Driver and Manager
        if (userTokens.containsValue(username)) {
            String assignedToken = "";

            for (Map.Entry<String, String> entry: userTokens.entrySet()) {
                if (entry.getValue().equals(username))
                    assignedToken = entry.getKey();
            }

            if (restaurant)
                restaurantPermissions.add(assignedToken);

            if (warehouse)
                warehousePermissions.add(assignedToken);

            if (driver)
                driverPermissions.add(assignedToken);

            if (manager)
                managerPermissions.add(assignedToken);
        }
    }

   /**
    * Method to set the account permissions
    * If successful, the system will show "PERMISSIONS_UPDATED"
    * If Json is unsuccessful, the system will show "PERMISSION_REQUEST_MISSPECIFIED"
    * @param idToken ID token assigned to the user
    * @param requestBody using a Json to request to change the permissions of the accounts
    * @return updated permissions to the database
    */
    @POST
    @Path("/accounts/set-permission")
    @Consumes("application/json")
    @Produces("application/json")
    public Response setPermissions(@HeaderParam("Authorization") String idToken, String requestBody) {
        Connection connection = DbConnection.getConnection();

        JsonObject requestJson = JsonTools.parseObject(requestBody);

        if (!(requestJson.containsKey("username") && requestJson.containsKey("restaurant") && requestJson.containsKey("warehouse") && requestJson.containsKey("driver") && requestJson.containsKey("manager")))
            return Response.status(Response.Status.BAD_REQUEST).entity("PERMISSION_REQUEST_MISSPECIFIED").build();

        String username = requestJson.getString("username");
        boolean restaurant = requestJson.getBoolean("restaurant");
        boolean warehouse = requestJson.getBoolean("warehouse");
        boolean driver = requestJson.getBoolean("driver");
        boolean manager = requestJson.getBoolean("manager");

        int rest =0; int ware = 0; int driv =0; int man =0;
        if(restaurant == true) {rest=1;}
        if(warehouse == true) {ware=1;}
        if(driver == true) {driv=1;}
        if(manager == true) {man=1;}

        Statement statement = null;
        String query = "UPDATE Accounts SET restaurant ="+rest+", warehouse ="+ware+", driver ="+driv+", manager ="+man+" WHERE username ='"+username+"'";

        try {
            statement = connection.createStatement();
            statement.executeUpdate(query);
            ServerLog.writeLog("Updated permissions for "+username);

        } catch (SQLException e ) {
            e.printStackTrace();
        } finally {
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (SQLException e) {
                        ServerLog.writeLog("SQL exception occurred when closing SQL statement");
                    }
                }
        }

        refreshPermissions(username);

        JsonObject response = Json.createObjectBuilder().add("message", "PERMISSIONS_UPDATED").build();
        return Response.status(Response.Status.OK).entity(response.toString()).build();
    }

    /**
    * Method which removes an account from the system
    * The system first logs out the user, and then deletes
    * If successful, the system will show "ACCOUNT_REMOVED"
    * If unsuccessful, the system will show "ACCOUNT_REMOVAL_ERROR"
     * @param idToken ID token assigned to the user
    * @param username for the account that will be removed
    * @return the database with the account removed from it
    */
    @Path("/accounts/remove")
    @GET
    public Response removeAccount(@HeaderParam("Authorization") String idToken, @HeaderParam("username") String username) {
        Response.ResponseBuilder res = null;

        // fetch dbConnection
        Connection connection = DbConnection.getConnection();

        // log out user from the system
        if (userTokens.containsValue(username)) {
            String assignedToken = "";

            for (Map.Entry<String, String> entry: userTokens.entrySet()) {
                if (entry.getValue().equals(username))
                    assignedToken = entry.getKey();
            }

            logoutUser(assignedToken);
        }

        // delete user account

        Statement statement = null;
        String query = "DELETE FROM Accounts WHERE username ='"+username+"'";

        try {
            statement = connection.createStatement();
            statement.executeUpdate(query);
            ServerLog.writeLog("Account for employee "+ username + " removed.");
            JsonObject resJson = Json.createObjectBuilder().add("message", "ACCOUNT_REMOVED").build();
            res = Response.status(Response.Status.OK).entity(resJson.toString());

        } catch (SQLException e ) {
            ServerLog.writeLog("Cannot delete user account " + username);
            res = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("ACCOUNT_REMOVAL_ERROR");
            e.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    ServerLog.writeLog("SQL exception occurred when closing SQL statement");
                }
            }
        }

        return res.build();
    }

   /**
    * Method to retrieve the staff information
    * @param idToken ID token assigned to the user
    * @return a Json array of the staff information
    * including id, name, username and if they are staff within restaurant, warehouse or a driver
    */
    @Path("/accounts/info")
    @GET
    @Produces("application/json")
    public Response getStaffInfo(@HeaderParam("Authorization") String idToken) {
        if (!Authorisation.checkAccess(idToken, "manager")) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Cannot get access").build();
        }

        Connection connection = DbConnection.getConnection();

        ServerLog.writeLog("Requested staff info");

        JsonArrayBuilder staffInfoBuilder = Json.createArrayBuilder();

        Statement statement = null;
        String query = "SELECT id, name, username, restaurant, warehouse, driver, manager, workLocation FROM Accounts";

        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            ServerLog.writeLog("Staff info retrieved");
            while(rs.next()){
                JsonObjectBuilder staffEntryBuilder = Json.createObjectBuilder();

                // all staff information included
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String username = rs.getString("username");
                String locationString = rs.getString("workLocation");
                String location = (locationString != null) ? locationString : "";

                int rest = rs.getInt("restaurant");
                boolean bRest = false;
                if(rest == 1) {bRest=true;}

                int ware = rs.getInt("warehouse");
                boolean bWare = false;
                if(ware == 1) {bWare=true;}

                int driv = rs.getInt("driver");
                boolean bDriv = false;
                if(driv == 1) {bDriv=true;}

                int mangr = rs.getInt("manager");
                boolean bMangr = false;
                if(mangr == 1) {bMangr=true;}

                staffEntryBuilder.add("id", id);
                staffEntryBuilder.add("name", name);
                staffEntryBuilder.add("username", username);
                staffEntryBuilder.add("restaurant", bRest);
                staffEntryBuilder.add("warehouse", bWare);
                staffEntryBuilder.add("driver", bDriv);
                staffEntryBuilder.add("manager", bMangr);
                staffEntryBuilder.add("location", location);

                staffInfoBuilder.add(staffEntryBuilder);
            }
        } catch (SQLException e ) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    ServerLog.writeLog("SQL exception occurred when closing SQL statement");
                }
            }
        }

        JsonArray staffInfo = staffInfoBuilder.build();
        return Response.status(Response.Status.OK).entity(staffInfo.toString()).build();
    }

   /**
    * Method which checks if an account has access to the warehouse, restaurant or driver
    * If an account does not have access, it will be unable to use the restricted functionality
    * @param idToken of an account
    * @return whether the account will have access or not
    */
    @Path("/accounts/check-access")
    @GET
    @Produces("application/json")
    public Response checkAccess(@HeaderParam("Authorization") String idToken) {
        ServerLog.writeLog("Refreshing user permissions");
        refreshPermissions(userTokens.get(idToken));

        ServerLog.writeLog("Building permissions array");
        JsonObjectBuilder permissionsTableBuilder = Json.createObjectBuilder();

        if (warehousePermissions.contains(idToken))
            permissionsTableBuilder.add("warehouse", true);
        else permissionsTableBuilder.add("warehouse", false);

        if (restaurantPermissions.contains(idToken))
            permissionsTableBuilder.add("restaurant", true);
        else permissionsTableBuilder.add("restaurant", false);

        if (driverPermissions.contains(idToken))
            permissionsTableBuilder.add("driver", true);
        else permissionsTableBuilder.add("driver", false);

        if (managerPermissions.contains(idToken))
            permissionsTableBuilder.add("manager", true);
        else permissionsTableBuilder.add("manager", false);

        JsonObject permissionsTable = permissionsTableBuilder.build();

        ServerLog.writeLog("Array built: " + permissionsTable.toString());

        return Response.status(Response.Status.OK).entity(permissionsTable.toString()).build();
    }

    /**
     * Overloaded checkAccess method for internal backend use
     * @param idToken   ID token of the user whose permissions are checked
     * @param level     level of access user is checked against
     * @return  true if user has this level of access, false otherwise or if level not specified
     */
    public static boolean checkAccess(String idToken, String level) {
    	refreshPermissions(userTokens.get(idToken));

        if (level.equals("restaurant") && restaurantPermissions.contains(idToken))
        	return true;

        if (level.equals("warehouse") && warehousePermissions.contains(idToken))
        	return true;

        if (level.equals("driver") && driverPermissions.contains(idToken))
        	return true;

        if (level.equals("manager") && managerPermissions.contains(idToken))
            return true;

        return false;
    }

    /**
    * method to check the work location for the accounts
    * @param idToken ID token of the user whose permissions are checked
    * @param name within Accounts
    * @return work location
    */
    @Path("/accounts/check-work-location")
    @GET
    @Produces("application/json")

    public Response checkWorkLocation(@HeaderParam("Authorization") String idToken, @HeaderParam("name") String name) {

    	Connection connection = DbConnection.getConnection();

        JsonArrayBuilder workLocationBuilder = Json.createArrayBuilder();

        Statement statement = null;
        String query = "SELECT workLocation FROM Accounts WHERE name = '"+name+"'";

        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);

            while(rs.next()){
                JsonObjectBuilder workEntryBuilder = Json.createObjectBuilder();

                String workLocation = rs.getString("workLocation");

                workEntryBuilder.add("message", workLocation);

                workLocationBuilder.add(workEntryBuilder);
            }
        } catch (SQLException e ) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    ServerLog.writeLog("SQL exception occurred when closing SQL statement");
                }
            }
        }

        JsonArray workLocation =  workLocationBuilder.build();
        return Response.status(Response.Status.OK).entity(workLocation.toString()).build();
    }


    /**
    * method to set the work location for employees
    * @param idToken   ID token of the user whose permissions are checked
    * @param requestBody JSON in form: {address: string}
    * @return work location
    */
    @POST
    @Path("/accounts/set-work-location")
    @Consumes("application/json")
    @Produces("application/json")
    public Response setWorkLocations(@HeaderParam("Authorization") String idToken, String requestBody) {
         Connection connection = DbConnection.getConnection();

         JsonObject requestJson = JsonTools.parseObject(requestBody);

         if (!(requestJson.containsKey("username") && requestJson.containsKey("address")))
             return Response.status(Response.Status.BAD_REQUEST).entity("SET_LOCATION_REQUEST_MISSPECIFIED").build();

         String username = requestJson.getString("username");
         String restaurantAddress = requestJson.getString("address");


         Statement statement = null;
         String query = "UPDATE Accounts SET workLocation='"+restaurantAddress+"' WHERE username ='"+username+"'";

         try {
             statement = connection.createStatement();
             statement.executeUpdate(query);
             ServerLog.writeLog("Updated work location for "+username);

         } catch (SQLException e ) {
             e.printStackTrace();
         } finally {
             if (statement != null) {
                 try {
                     statement.close();
                 } catch (SQLException e) {
                     ServerLog.writeLog("SQL exception occurred when closing SQL statement");
                 }
             }
         }

         JsonObject response = Json.createObjectBuilder().add("message", "LOCATION_SET").build();
         return Response.status(Response.Status.OK).entity(response.toString()).build();
     }
}
