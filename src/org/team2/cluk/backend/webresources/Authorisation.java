package org.team2.cluk.backend.webresources;

import org.apache.commons.lang3.RandomStringUtils;
import org.team2.cluk.backend.tools.DbConnection;
import org.team2.cluk.backend.tools.JsonTools;
import org.team2.cluk.backend.tools.Roles;
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

@Path("/")
public class Authorisation {
    // initialise hashmap for storing currently authorised users
    private static HashMap<String, String> userTokens = new HashMap<>();
    private static HashSet<String> restaurantPermissions = new HashSet<>();
    private static HashSet<String> warehousePermissions = new HashSet<>();
    private static HashSet<String> driverPermissions = new HashSet<>();

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
            return Response.status(Response.Status.OK).entity("LOGOUT_SUCCESSFUL").build();
        }
        return Response.status(Response.Status.NOT_FOUND).entity("USER_NEVER_LOGGED_IN").build();
    }

    public static boolean checkAuthHeader(String authHeader) {
        if (userTokens.containsKey(authHeader))
            return true;
        return false;
    }

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
    }

    @Path("/accounts/add")
    @POST
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

        return Response.status(Response.Status.OK).entity("ACCOUNT_CREATED").build();
    }

    public void refreshPermissions(String username) {
        // fetch current database connection
        Connection connection = DbConnection.getConnection();

        boolean restaurant = false;
        boolean warehouse = false;
        boolean driver = false;

        Statement statement = null;
        String query = "SELECT restaurant, warehouse, driver " +
                "FROM Accounts " +
                "WHERE username ='" + username + "'";
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()) {
                restaurant = rs.getBoolean("restaurant");
                warehouse = rs.getBoolean("warehouse");
                driver = rs.getBoolean("driver");
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
        }
    }

    @POST
    @Path("/accounts/set-permission")
    @Consumes("application/json")
    //Method to set account permissions.
    public Response setPermissions(@HeaderParam("Authorization") String idToken, String requestBody) {
        Connection connection = DbConnection.getConnection();

        JsonObject requestJson = JsonTools.parseObject(requestBody);

        if (!(requestJson.containsKey("username") && requestJson.containsKey("restaurant") && requestJson.containsKey("warehouse") && requestJson.containsKey("driver")))
            return Response.status(Response.Status.BAD_REQUEST).entity("PERMISSION_REQUEST_MISSPECIFIED").build();

        String username = requestJson.getString("username");
        boolean restaurant = requestJson.getBoolean("restaurant");
        boolean warehouse = requestJson.getBoolean("warehouse");
        boolean driver = requestJson.getBoolean("driver");

        int rest =0; int ware = 0; int driv =0;
        if(restaurant == true) {rest=1;}
        if(warehouse == true) {ware=1;}
        if(driver == true) {driv=1;}

        Statement statement = null;
        String query = "UPDATE Accounts SET restaurant ="+rest+", warehouse ="+ware+", driver ="+driv+" WHERE username ='"+username+"'";

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

        return Response.status(Response.Status.OK).entity("PERMISSIONS_UPDATED").build();
    }

    @Path("/accounts/remove")
    @GET
    //Method to remove account from database.
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
            res = Response.status(Response.Status.OK).entity("ACCOUNT_REMOVED");

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

    @Path("/accounts/info")
    @GET
    @Produces("application/json")
    //Method to see staff info
    public Response getStaffInfo(@HeaderParam("Authorization") String idToken) {
        Connection connection = DbConnection.getConnection();

        JsonArrayBuilder staffInfoBuilder = Json.createArrayBuilder();

        Statement statement = null;
        String query = "SELECT id, name, username, restaurant, warehouse, driver FROM Accounts";

        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);

            while(rs.next()){
                JsonObjectBuilder staffEntryBuilder = Json.createObjectBuilder();

                int id = rs.getInt("id");
                String name = rs.getString("name");
                String username = rs.getString("username");

                int rest = rs.getInt("restaurant");
                boolean bRest = false;
                if(rest == 1) {bRest=true;}

                int ware = rs.getInt("warehouse");
                boolean bWare = false;
                if(ware == 1) {bWare=true;}

                int driv = rs.getInt("driver");
                boolean bDriv = false;
                if(driv == 1) {bDriv=true;}

                staffEntryBuilder.add("id", id);
                staffEntryBuilder.add("name", name);
                staffEntryBuilder.add("username", username);
                staffEntryBuilder.add("restaurant", bRest);
                staffEntryBuilder.add("warehouse", bWare);
                staffEntryBuilder.add("driver", bDriv);

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

    @Path("/account/check-access")
    @GET
    @Produces("application/json")
    public Response checkAccess(@HeaderParam("Authorization") String idToken) {
        refreshPermissions(userTokens.get(idToken));

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

        JsonObject permissionsTable = permissionsTableBuilder.build();

        return Response.status(Response.Status.OK).entity(permissionsTable.toString()).build();
    }
}
