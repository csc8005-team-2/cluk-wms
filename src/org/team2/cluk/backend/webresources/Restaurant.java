package org.team2.cluk.backend.webresources;

import org.team2.cluk.backend.tools.DbConnection;
import org.team2.cluk.backend.tools.JsonTools;
import org.team2.cluk.backend.tools.ServerLog;

import javax.json.*;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@Path("/restaurant")
public class Restaurant {

    /**
     * Handles request for total stock units at a given restaurant given as "address" in the request header
     * @param restaurantAddress address of the restaurant provided in the request header as "address"
     * @return  JSON array with all stock stored in that restaurant
     */
    @GET
    @Path("/get-total-stock")
	@Produces("application/json")
    public Response getTotalStock(@HeaderParam("address") String restaurantAddress) {

		ServerLog.writeLog("Requested information on total stock in the restaurant at "+restaurantAddress);

    	if (restaurantAddress.isBlank()) {
    		ServerLog.writeLog("Rejected request as restaurant address not specified");
    		return Response.status(Response.Status.BAD_REQUEST).entity("ADDRESS_BLANK_OR_NOT_PROVIDED").build();
		}

		JsonArrayBuilder responseBuilder = Json.createArrayBuilder();
    	// fetch current database connection
		Connection connection = DbConnection.getConnection();

        Statement statement = null;
        String query = "SELECT stockItem, quantity " +
                "FROM Within " +
                "WHERE restaurantAddress ='" + restaurantAddress+"'";
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
				JsonObjectBuilder arrayEntryBuilder = Json.createObjectBuilder();

                String stockItem = rs.getString("StockItem");
                int quantity = rs.getInt("Quantity");

                arrayEntryBuilder.add("stockItem", stockItem);
                arrayEntryBuilder.add("quantity", quantity);

				JsonObject arrayEntry = arrayEntryBuilder.build();
				responseBuilder.add(arrayEntry);
            }
        } catch (SQLException e) {
        	ServerLog.writeLog("SQL exception occurred when executing query");
            e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("SQL Exception occurred when executing query").build();
        } finally {
            if (statement != null) {
            	try {
            		statement.close();
				} catch (SQLException e) {
            		ServerLog.writeLog("SQL exception occurred when closing SQL statement");
				}
            }
        }
		JsonArray response = responseBuilder.build();

        return Response.status(Response.Status.OK).entity(response.toString()).build();
    }

    @GET
    @Path("/receive-order")
	//this method updates the stock for a restaurant when it has received an order.
    public Response receiveOrder(@HeaderParam("address") String restaurantAddress, @HeaderParam("order-id") int orderId) {
    	ServerLog.writeLog("Requested receiving order " + orderId + " at " + restaurantAddress);

    	boolean processedCorrectly = true;

    	Response.ResponseBuilder response = null;
        // fetch current database connection
        Connection connection = DbConnection.getConnection();

        // variable for logging purposes only
		String restaurant = "";

    	//Checking order is being sent to the correct restaurant.
    	boolean correctRestaurant=false;
    	Statement statement = null;
    	String query = "SELECT restaurantAddress FROM Orders WHERE orderId ='" + orderId + "'";
    	try {
    		statement = connection.createStatement();
    		ResultSet rs = statement.executeQuery(query);

    		// check if order found
    		if (!rs.next()) {
    			ServerLog.writeLog("Order " + orderId + "not found");
    			response = Response.status(Response.Status.NOT_FOUND).entity("ORDER_NOT_FOUND");
    			return response.build();
			}

    		// if order found, continue
    		restaurant = rs.getString("restaurantAddress");
    		if(restaurant.equals(restaurantAddress)) {
				ServerLog.writeLog("New order received at: " + restaurantAddress + ". Order ID: "+ orderId);
				correctRestaurant = true;
    		}
    	} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (statement != null) {
			    try {
			        statement.close();
			    } catch(SQLException e) {
                    ServerLog.writeLog("SQL exception occurred when closing SQL statement");
                }
			}
		}

    	// if check not passed, return response already
		if (!correctRestaurant) {
			ServerLog.writeLog("The order submitted is not for this restaurant. The correct "+
					"delivery location is " + restaurant + ".");
			response = Response.status(Response.Status.FORBIDDEN).entity("WRONG_RESTAURANT");
			return response.build();
		}

		// if check passed, move on
		ServerLog.writeLog("Order ID " + orderId + " matches with requested location: " + restaurantAddress);

    	// retrieving conversion table
		ServerLog.writeLog("Fetching unit conversion table");
		HashMap<String, Integer> unitConversion = new HashMap<>();

		statement = null;
		query= "SELECT stockItem, unitSize from Stock";

		try{
			statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(query);
			while (rs.next()) {
				String stockItem = rs.getString("stockItem");
				int unitSize = rs.getInt("unitSize");
				unitConversion.put(stockItem, unitSize);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (statement != null) {
				try {
					ServerLog.writeLog("Unit conversion table retrieved successfully");
					statement.close();
				} catch(SQLException e) {
					ServerLog.writeLog("SQL exception occurred when closing SQL statement");
				}
			}
		}

		// retrieve order content
		ServerLog.writeLog("Retrieving order content");
		HashMap<String, Integer> receivedOrder = new HashMap<>();

		statement = null;
		query = "SELECT stockItem, quantity FROM Contains WHERE orderId ='" +orderId+"'";

		try {
			statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(query);
			while (rs.next()) {
				int quantityToAdd = rs.getInt("quantity");
				String stockItem = rs.getString("stockItem");

				// put order items in a hashmap, perform conversion to restaurant unit sizes
				int conversionRate = unitConversion.get(stockItem);
				receivedOrder.put(stockItem, quantityToAdd * conversionRate);
				ServerLog.writeLog("Received order " + orderId + " contains " + stockItem + ": " + quantityToAdd);
			}
			ServerLog.writeLog("Order " + orderId + " contents received");
		} catch (SQLException e) {
			ServerLog.writeLog("SQL exception occurred when querying received order");
			e.printStackTrace();
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("RECEIVED_ORDER_QUERY_ERROR");
			processedCorrectly = false;
		} finally {
			if (statement != null) {
				try {
					statement.close();
				}
				catch (SQLException e) {
					ServerLog.writeLog("SQL exception occurred when closing SQL statement");
				}
			}
		}

		// get current levels of ordered stock in the restaurant
		HashMap<String, Integer> currentStock = new HashMap<>();

		for (Map.Entry<String, Integer> entry: receivedOrder.entrySet()) {
			String stockItem = entry.getKey();
			ServerLog.writeLog("Retrieving current level of " + stockItem + " at " + restaurantAddress);
			statement = null;
			query = "SELECT quantity FROM Within WHERE restaurantAddress ='" + restaurantAddress + "'" +
					"AND stockItem ='" + stockItem + "'";
			try {
				statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(query);
				int previousQuantity = 0;

				// if restaurant had that item earlier, update previous quantity
				while (resultSet.next()) {
					previousQuantity = resultSet.getInt("quantity");
				}
				currentStock.put(entry.getKey(), previousQuantity);
				ServerLog.writeLog("Previous stock quantity: " + previousQuantity + " for stock: " + stockItem + " at restaurant: " + restaurantAddress);

			} catch (SQLException e) {
				ServerLog.writeLog("SQL exception occurred when querying current stock levels");
				e.printStackTrace();
				response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("CURRENT_STOCK_LEVELS_QUERY_ERROR");
				processedCorrectly = false;
			} finally {
				if (statement != null) {
					try {
						statement.close();
					} catch (SQLException e) {
						ServerLog.writeLog("SQL exception occurred when closing SQL statement");
					}
				}
			}
		}

    	// now, add order items to restaurant stock
		for (Map.Entry<String, Integer> entry: receivedOrder.entrySet()) {
			// retrieve stock item
			String stockItem = entry.getKey();
			// calculate new quantity
			int quantityToAdd = entry.getValue();
			int previousQuantity = currentStock.get(stockItem);
			int newQuantity = quantityToAdd + previousQuantity;

			statement = null;
			String updateQuery = "UPDATE Within SET quantity ='" + newQuantity +"'" + "WHERE stockItem ='" + stockItem +"'";
			try {
				statement = connection.createStatement();
				statement.executeUpdate(updateQuery);
				ServerLog.writeLog("Updated stock of " + stockItem + ": " + newQuantity + " at restaurant: " + restaurantAddress);
			} catch (SQLException e) {
				ServerLog.writeLog("Error when updating stock at restaurant: " + restaurantAddress);
				e.printStackTrace();
				response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("UPDATE_STOCK_ERROR");
				processedCorrectly = false;
			} finally {
				if (statement != null) {
					try {
						statement.close();
					} catch (SQLException e) {
						ServerLog.writeLog("SQL exception occurred when closing SQL statement");
					}
				}
			}
		}

		if (processedCorrectly) {
			ServerLog.writeLog("Order " + orderId + " received in full by restaurant " + restaurantAddress);
			response = Response.status(Response.Status.OK).entity("ORDER_RECEIVED");
		}

    	return response.build();
    }

	/**
	 * Method executed when client requests custom order at the endpoint "/restaurant/request-order/custom".
	 * Body of the request should be JSON array with each entry of form:
	 * {stockItem: string, quantity: number}
	 * @param restaurantAddress	value of address header specifying restaurant where order shall be delivered
	 * @param strOrderContents	stringified JSON array containing order details
	 * @return	202 ORDER_ACCEPTED - even if order contains entries not following the specification, they are removed from the order
	 */
    @Path("/request-order/custom")
	@POST
	@Consumes("application/json")
    //Creates an order for any number of items. Numbers of each item required are passed as parameters.  Parameters are in alphabetical order.
    public Response requestCustomOrder(@HeaderParam("address") String restaurantAddress, String strOrderContents) {

    	// fetch current db connection
    	Connection connection = DbConnection.getConnection();

    	// parse request body to retrieve contents
		JsonArray orderContents = JsonTools.parseArray(strOrderContents);

    	java.util.Date orderDate = new java.util.Date();
    	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	String currentTime = sdf.format(orderDate);
    	int orderId=0;

    	// Creating Order in StockOrders Table.
    	String query = "INSERT INTO StockOrders (orderDateTime, orderStatus) VALUES (?, ?)";
    			    	    	
    	try{
    		PreparedStatement pstmt = connection.prepareStatement(query);
    		pstmt.setString(1, currentTime);
    		pstmt.setString(2,"Out for delivery"); //manual set to out for delivery
    		pstmt.executeUpdate();
	    		
    	} catch (SQLException e ) {
    		e.printStackTrace();
    	} 

    	// Getting the orderId of the order we just created.
    	Statement statement = null;
    	query = "SELECT orderId FROM StockOrders WHERE orderDateTime ='" +currentTime+"'"; 
    	
    	try { 
    		statement = connection.createStatement();
    		ResultSet rs = statement.executeQuery(query);
    		while (rs.next()) {
    			orderId = rs.getInt("orderId");
    			ServerLog.writeLog("Created order " + orderId + " for restaurant at " + restaurantAddress);
			}
    	}catch (SQLException e ) {
    		e.printStackTrace();
    	} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				ServerLog.writeLog("SQL exception occurred when closing SQL statement");
			}
		}

    	// Add Order ID and restaurant address to orders table.
    	query = "INSERT INTO Orders (restaurantAddress, orderId) VALUES (?, ?)";
		PreparedStatement pstmt = null;
    	try{
			pstmt = connection.prepareStatement(query);
    		pstmt.setString(1, restaurantAddress);
    		pstmt.setInt(2, orderId); //same orderId as above
    		pstmt.executeUpdate();
    	}catch (SQLException e ) {
    		e.printStackTrace();
    	}finally {
			try {
				pstmt.close();
			} catch (SQLException e) {
				ServerLog.writeLog("SQL exception occurred when closing SQL statement");
			}
		}
    	
    	//Getting stock items from Stock Table
		pstmt = null;

    	for (JsonValue orderEntryValue: orderContents) {
    		if (!(orderEntryValue instanceof JsonObject)) {
    			ServerLog.writeLog("Order entry misspecified");
    			continue;
    		}

    		JsonObject orderEntry = (JsonObject) orderEntryValue;

    		if (!orderEntry.containsKey("stockItem") || !orderEntry.containsKey("quantity")) {
    			ServerLog.writeLog("Order entry misspecified. Skipping this entry");
    			continue;
			}

    		String stockItem = orderEntry.getString("stockItem");
    		int quantity = orderEntry.getInt("quantity");

    		query = "INSERT INTO Contains (orderId, quantity, stockItem) VALUES (?, ?, ?)";
    		pstmt = null;

    		try {
				pstmt = connection.prepareStatement(query);
    			pstmt.setInt(1, orderId);
    			pstmt.setInt(2, quantity);
    			pstmt.setString(3, stockItem);
    			pstmt.executeUpdate();
    			ServerLog.writeLog("Order " + orderId + " contains " + stockItem + " at quantity " + quantity);
    		} catch (SQLException e ) {
    			e.printStackTrace();
    		} finally {
    			try {
    				pstmt.close();
    			} catch (SQLException e) {
    				ServerLog.writeLog("SQL exception occurred when closing SQL statement");
    			}
    		}
    	}

    	ServerLog.writeLog("Order " + orderId + " has been accepted");
    	return Response.status(Response.Status.ACCEPTED).entity("ORDER_ACCEPTED").build();
    }


	/**
	 * Requests standard order for a restaurant. Fetches what are the standard order items and quantities and
	 * calls requestCustomOrder to order them.
	 * @param restaurantAddress	address of the destination restaurant, provided in "address" header parameter
	 * @return	same responses as for requestCustomOrder
	 */
	@Path("/request-order")
	@GET
    public Response requestStandardOrder(@HeaderParam("address") String restaurantAddress) {

    	// fetch db connection
		Connection connection = DbConnection.getConnection();

		// create JsonArrayBuilder to contain the contents of standard order
		JsonArrayBuilder standardOrderArrayBuilder = Json.createArrayBuilder();

    	//Getting standard quantities from Stock Table
		Statement statement = null;
    	String query = "SELECT stockItem, typicalUnitsOrdered FROM Stock";
    	//selecting all the stock for a standard order
    	try {
    		statement = connection.createStatement();
    		ResultSet rs = statement.executeQuery(query);
    		while (rs.next()) {
				JsonObjectBuilder orderEntryBuilder = Json.createObjectBuilder();

    			String stockItem = rs.getString("stockItem");
    			int typicalUnits = rs.getInt("typicalUnitsOrdered");

    			orderEntryBuilder.add("stockItem", stockItem);
    			orderEntryBuilder.add("quantity", typicalUnits);

    			JsonObject orderEntry = orderEntryBuilder.build();
    			standardOrderArrayBuilder.add(orderEntry);
    		}
    	} catch (SQLException e ) {
    		e.printStackTrace();
    	} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				ServerLog.writeLog("SQL exception occurred when closing SQL statement");
			}
    	}

    	JsonArray standardOrderArray = standardOrderArrayBuilder.build();

		return requestCustomOrder(restaurantAddress, standardOrderArray.toString());
    }
	    			    		   


    //Checks stock at restaurant is above the minimum stock level.
	@Path("/min-stock-check")
	@GET
	@Produces("application/json")
    public Response minStockCheck(@HeaderParam("address") String restaurantAddress) {
		// fetch db connection
		Connection connection = DbConnection.getConnection();

		// create JsonArrayBuilder
		JsonArrayBuilder stockArrayBuilder = Json.createArrayBuilder();

    	Statement statement = null;
    	String query = "SELECT stockItem, quantity, minQuantity " +
    				   "FROM Within " +            
                       "WHERE restaurantAddress='"+restaurantAddress+"'";
                           
    	try {
    		statement = connection.createStatement();
    		ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
				JsonObjectBuilder stockEntryBuilder = Json.createObjectBuilder();

                String stockItem = rs.getString("StockItem");
                int quantity = rs.getInt("quantity");
                int minQuantity = rs.getInt("minQuantity");

                stockEntryBuilder.add("stockItem", stockItem);
                stockEntryBuilder.add("quantity", quantity);
                stockEntryBuilder.add("minQuantity", minQuantity);

                if(quantity < minQuantity){
                	int deficit = minQuantity - quantity;
                	ServerLog.writeLog("Current stock of "+ stockItem +" at "+ restaurantAddress + " is below minimum stock levels by "+deficit+".");
                }

                JsonObject stockEntry = stockEntryBuilder.build();
                stockArrayBuilder.add(stockEntry);
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
    	JsonArray stockArray = stockArrayBuilder.build();

    	return Response.status(Response.Status.OK).entity(stockArray.toString()).build();
	}

        /*
    //Allows the minimum stock level to be changed.
    public void updateMinStock(Connection connection, String stockItem, int min) throws SQLException
        {
            Statement statement = null;
            String query = "UPDATE Within SET minQuantity ="+min+" WHERE stockItem='"+stockItem+"' AND restaurantAddress ='"+restaurantAddress+"'";
                           
            try {
            	statement = connection.createStatement();
            	statement.executeUpdate(query);
            	System.out.println("Minimum stock levels updated to: " + min);
            
            } catch (SQLException e ) {
                e.printStackTrace();
            } finally {
                if (statement != null) {statement.close();}
            }                 
        }
    	
    //Allows a restaurant to use stock by creating meal items.
	public void createMeal(Connection connection, String meal) throws SQLException {
		
		//Check that there is enough stock at the restaurant to make the meal item.
		boolean enoughStock=true;
		
		Statement statement = null;
		String query = "SELECT stockItem, quantity FROM MadeWith WHERE mealId ='"+meal+"'";
		try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while(rs.next()){
            	String stockItem = rs.getString("stockItem");
            	int quantity = rs.getInt("quantity");
            	
            	Statement innerstatement = null;
                String innerquery = "SELECT quantity FROM Within WHERE stockItem='"+stockItem+"' AND restaurantAddress ='"+this.restaurantAddress+"'" ;
                try {
                	innerstatement = connection.createStatement();
                	ResultSet rs2 = innerstatement.executeQuery(innerquery);
                	rs2.next();
                	int oldQuantity = rs2.getInt("quantity");
                	
                	if(oldQuantity<quantity){
                		System.out.print(meal + " cannot be made. Restaurant stock too low. \n");
                		enoughStock=false;
                	}
                	
				} catch (SQLException e ) {
					e.printStackTrace();
				} finally {
					if (innerstatement != null) {innerstatement.close();}
				}                     
            } 
		}catch (SQLException e ) {
			e.printStackTrace();
		} finally {
			if (statement != null) {statement.close();}
		} 
		
		//If enough stock update quantities.	
		if(enoughStock == true){
		
		statement = null;
    	query = "SELECT stockItem, quantity FROM MadeWith WHERE mealId ='"+meal+"'";
    	
    	try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while(rs.next()){
            	String stockItem = rs.getString("stockItem");
            	int quantity = rs.getInt("quantity");
            	
            	
            	Statement innerstatement = null;
                String innerquery = "UPDATE Within set quantity =quantity-"+quantity+" WHERE stockItem='"+stockItem+"' AND restaurantAddress ='"+this.restaurantAddress+"'";
                try {
                	innerstatement = connection.createStatement();
                	innerstatement.executeUpdate(innerquery);
				} catch (SQLException e ) {
					e.printStackTrace();
				} finally {
					if (innerstatement != null) {innerstatement.close();}
				}                     
            }
            System.out.print("Item: "+meal+" created.\n");
    	} catch (SQLException e ) {
    		e.printStackTrace();
    	} finally {
    		if (statement != null) {statement.close();}
    	} 
		}
	}

 */
}    