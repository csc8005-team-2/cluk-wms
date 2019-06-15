package org.team2.cluk.backend.webresources;

import org.team2.cluk.backend.tools.DbConnection;
import org.team2.cluk.backend.tools.JsonTools;
import org.team2.cluk.backend.tools.ServerLog;

import javax.json.*;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.sql.*;

/**
* Driver class including information about driver schedules and assigning orders
*/


@Path("/driver")

public class Driver {


	/** checks access type is manager
	connects to database
	gets today's date and the orderId of approved orders to be delivered in that same day
	gets the restaurant address of that same orderId from the Order table
	gets the restaurant address of the  region that matches the restaurant address above
	 @param idToken ID token assigned to the user
	@return array of addresses driver needs to visit
	*/

	@Path("/plot-route")
	@GET
	public static Response plotRoute(@HeaderParam("Authorization") String idToken) {

		if (!Authorisation.checkAccess(idToken, "driver")) {
			return Response.status(Response.Status.UNAUTHORIZED).entity("Cannot get permission").build();
		}

		//connects to database
		JsonArrayBuilder responseBuilder = Json.createArrayBuilder();
		Connection connection = DbConnection.getConnection();

		//get today's date
		java.util.Date orderDate = new java.util.Date();
		java.text.SimpleDateFormat date = new java.text.SimpleDateFormat("yyyy-MM-dd");
		String currentDate = date.format(orderDate.getTime());

		//get orderId from stockOrders table where the orderDeliveryDate is today's date and orderSatatus is approved for delivery.

		Statement statement = null;
		String query = "SELECT orderId " +
				"FROM StockOrders " +
				"WHERE orderDeliveryDate='" + currentDate + "' AND orderStatus LIKE 'approved'";
		try {
			statement = DbConnection.getConnection().createStatement();
			ResultSet rs = statement.executeQuery(query);

			while (rs.next()) {
				int orderIdStockOrders = rs.getInt("orderId");

				//get the restaurantAddress where the orderId from orders table is equal to the orderId from stockOrders table.

				Statement statement1 = null;
				String query1 = "SELECT restaurantAddress " +
						"FROM Orders " +
						"WHERE orderId ='" + orderIdStockOrders + "'";

				try {
					statement1 = DbConnection.getConnection().createStatement();
					ResultSet rs1 = statement1.executeQuery(query1);

					while (rs1.next()) {
						JsonObjectBuilder arrayEntryBuilder = Json.createObjectBuilder();
						String restaurantAddressOrdersTable = rs1.getString("restaurantAddress");
						arrayEntryBuilder.add("address", restaurantAddressOrdersTable);
						JsonObject arrayEntry = arrayEntryBuilder.build();
                   				responseBuilder.add(arrayEntry);
						}

						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}catch (SQLException e) {
			e.printStackTrace();
		}
		
		JsonArray response = responseBuilder.build();
		return Response.status(Response.Status.OK).entity(response.toString()).build();

	}

}
