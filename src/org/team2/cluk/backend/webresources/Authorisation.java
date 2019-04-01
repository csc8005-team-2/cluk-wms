package org.team2.cluk.backend.webresources;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.StringReader;
import java.util.HashMap;

@Path("/")
public class Authorisation {
    // initialise hashmap for storing currently authorised users
    private static HashMap<String, String> userTokens = new HashMap<>();

    @Path("/login")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public String loginUser(String loginData) {
        JsonReader jsonReader = Json.createReader(new StringReader(loginData));
        JsonObject loginDataObject = jsonReader.readObject();
        jsonReader.close();

        String username = loginDataObject.getString("username");
        String password = loginDataObject.getString("password");

        // if auth successful, generate token and add to hashmap
        // to be completed

        // generic return for now
        return "ID token will be returned here";
    }

    @Path("/logout")
    @GET
    @Consumes("application/json")
    public Response logoutUser(@HeaderParam("Authorization") String idToken) {
        if (userTokens.containsKey(idToken)) {
            userTokens.remove(idToken);
            return Response.status(Response.Status.ACCEPTED).entity("LOGOUT_SUCCESSFUL").build();
        }
        return Response.status(Response.Status.NOT_FOUND).entity("USER_NEVER_LOGGED_IN").build();
    }
}
