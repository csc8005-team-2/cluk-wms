package org.team2.cluk.backend.webresources;

import org.team2.cluk.backend.tools.DbConnection;
import org.team2.cluk.backend.tools.JsonTools;
import org.team2.cluk.backend.tools.ServerLog;

import javax.json.*;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.sql.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


/**
 * Warehouse Class which handles stocks information, sending orders to restaurant and assigning a driver to deliver the order
 *
 * @version 11/05/2019
 */

@Path("/warehouse")
public class Warehouse
{

    /**
    * method to get the stock names
    * @param idToken to check access for warehouse
    * @return all stock within the warehouse
    */
    @GET
    @Path("/get-stock-names")
    @Produces("application/json")
    public Response GetStockNames(@HeaderParam("Authorization") String idToken)
    {
        if (!Authorisation.checkAccess(idToken, "warehouse") && !Authorisation.checkAccess(idToken, "restaurant")) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Cannot get access").build();
        }

        ServerLog.writeLog("Requested names of stock items.");

        JsonArrayBuilder responseBuilder = Json.createArrayBuilder();
        // fetch current database connection
        Connection connection = DbConnection.getConnection();

        Statement statement = null;
        String query = "SELECT stockItem FROM Stock";

        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                JsonObjectBuilder arrayEntryBuilder = Json.createObjectBuilder();
                String stockItem = rs.getString("StockItem");
                arrayEntryBuilder.add("stockItem", stockItem);
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

    /**
     * Method which gets the total stock within a warehouse
     * If successful, system will show "StockItem: " + Quantity
     * @param address of the warehouse
     * @param idToken to check access for warehouse
     * @return the stock name and quantity (in units) held at the warehouse
     */
    @GET
    @Path("/get-total-stock")
    @Produces("application/json")
    public Response GetTotalStock(@HeaderParam("Authorization") String idToken, @HeaderParam("address") String address)
    {
	    if (!Authorisation.checkAccess(idToken, "warehouse")) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Cannot get access").build();
        }

        ServerLog.writeLog("Requested information on total stock in the warehouse at "+address);

        if (address.isBlank()) {
            ServerLog.writeLog("Rejected request as warehouse address not specified");
            return Response.status(Response.Status.BAD_REQUEST).entity("ADDRESS_BLANK_OR_NOT_PROVIDED").build();
        }

        JsonArrayBuilder responseBuilder = Json.createArrayBuilder();
        // fetch current database connection
        Connection connection = DbConnection.getConnection();

        Statement statement = null;
        String query = "SELECT stockItem, quantity, minQuantity " +
                       "FROM Inside " +            
                       "WHERE warehouseAddress='"+address+"'";

        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                JsonObjectBuilder arrayEntryBuilder = Json.createObjectBuilder();

                String stockItem = rs.getString("StockItem");
                int quantity = rs.getInt("Quantity");
                int minQty = rs.getInt("minQuantity");

                arrayEntryBuilder.add("stockItem", stockItem);
                arrayEntryBuilder.add("quantity", quantity);
                arrayEntryBuilder.add("belowRequired", (quantity < minQty) ? true : false);

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

    /**
     * Method to increase the warehouse stock by the quantity specified
     * Takes parameters of stock item and quantity
     * {stockItem: string, quantity: number}
     * If successful, system shows "STOCK_UPDATED"
     * If JSON request unsuccessful, system shows "Order entry misspecified. Skipping entry."
     * @param idToken to check access for warehouse
     * @param address of the warehouse
     * @param requestBody uses Json to request an array of the stock at the warehouse
     * @return the updated stock to the warehouse
     */
    @POST
    @Path("/update-stock")
    @Consumes("application/json")
    @Produces("application/json")
    public Response updateStock(@HeaderParam("Authorization") String idToken, @HeaderParam("address") String address, String requestBody) {
        if (!Authorisation.checkAccess(idToken, "warehouse")) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Cannot get access").build();
        }

        ServerLog.writeLog("Updating warehouse stock at " + address);
        // fetch db connection
        Connection connection = DbConnection.getConnection();

        // parse request body
        JsonArray stockToUpdate = JsonTools.parseArray(requestBody);

        for (JsonValue entry : stockToUpdate) {
            // check if array entry is a JSON
            if (!(entry instanceof JsonObject)) {
                ServerLog.writeLog("Order entry misspecified. Skipping entry.");
                continue;
            }

            JsonObject entryObj = (JsonObject) entry;

            // check if JSON correctly specified
            if (!(entryObj.containsKey("stockItem") && entryObj.containsKey("quantity"))) {
                ServerLog.writeLog("Order entry misspecified. Skipping entry.");
                continue;
            }

            String stockItem = entryObj.getString("stockItem");
            int requestedQuantity = entryObj.getInt("quantity");

            // retrieve current level of given stock
            int currentQuantity = 0;

            Statement statement = null;
            String query = "SELECT stockItem, quantity " +
                    "FROM Inside " +
                    "WHERE stockItem='" + stockItem + "' AND warehouseAddress ='" + address + "'";
            try {
                statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(query);

                while (rs.next()) {
                    currentQuantity = rs.getInt("quantity");
                    System.out.println("Previous stock of " + stockItem + ": " + currentQuantity + " at " + address);
                }

            } catch (SQLException e) {
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

            // update stock to the new level
            int newQuantity = currentQuantity + requestedQuantity;

            statement = null;
            query = "UPDATE Inside " +
                    "SET quantity ='" + newQuantity +
                    "' WHERE stockItem='" + stockItem + "' AND warehouseAddress ='" + address + "'";
            try {
                statement = connection.createStatement();
                statement.executeUpdate(query);
                ServerLog.writeLog("Updated stock of " + stockItem + ": " + newQuantity + " at " + address);
            } catch (SQLException e) {
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
        }

        JsonObject responseJson = Json.createObjectBuilder().add("message", "STOCK_UPDATED").build();

        return Response.status(Response.Status.OK).entity(responseJson.toString()).build();
    }


	    
	    
	/**
    * method to approve order from the warehouse to the restaurant
    * @param idToken to check access to warehouse and restaurant
    * @param _orderId of the stock order
    * @return approved order
    */
    @GET
    @Path("/approve-order")
    @Produces("application/json")
    public Response approveOrder(@HeaderParam("Authorization") String idToken, @HeaderParam("orderId") String _orderId)
    {
	    
	    if (!Authorisation.checkAccess(idToken, "warehouse")) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Cannot get access").build();
        }
	    
    	int orderId = Integer.parseInt(_orderId);
   	Response.ResponseBuilder res = null;
   	Connection connection = DbConnection.getConnection();
   		
   	Statement statement = null;
   	String query = "UPDATE StockOrders Set orderStatus = 'Approved' WHERE orderId = " + orderId;
   		
   	try {
   		statement = connection.createStatement();
   		statement.executeUpdate(query);
   		ServerLog.writeLog("Order: "+orderId +" Approved");

        JsonObject resJson= Json.createObjectBuilder().add("message", "APPROVED_ORDER").build();

   		res = Response.status(Response.Status.OK).entity(resJson.toString());
   		
   	 } catch (SQLException e) {
   		ServerLog.writeLog("SQL exception occurred when executing query");
   		e.printStackTrace();
   		res = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("SQL Exception occurred when executing query");
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
    * method to decline order.
     * @param idToken to check access to warehouse and restaurant
     * @param _orderId of the stock order
    * @return declined order
    */
    @GET
    @Path("/decline-order")
    @Produces("application/json")
    public Response declineOrder(@HeaderParam("Authorization") String idToken, @HeaderParam("orderId") String _orderId)
    {
	    
	    if (!Authorisation.checkAccess(idToken, "warehouse")) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Cannot get access").build();
        }
	    
    	int orderId = Integer.parseInt(_orderId);
   	Response.ResponseBuilder res = null;
   	Connection connection = DbConnection.getConnection();
   		
   	Statement statement = null;
   	String query = "UPDATE StockOrders Set orderStatus = 'Declined' WHERE orderId = " + orderId;
   		
   	try {
   		statement = connection.createStatement();
   		statement.executeUpdate(query);
   		ServerLog.writeLog("Order: "+orderId +" Declined");

   		JsonObject resJson= Json.createObjectBuilder().add("message", "DECLINED_ORDER").build();
   		res = Response.status(Response.Status.OK).entity(resJson.toString());
   		
   	 } catch (SQLException e) {
   		ServerLog.writeLog("SQL exception occurred when executing query");
   		e.printStackTrace();
   		res = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("SQL Exception occurred when executing query");
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
     * Method which sends an order from a Warehouse to a Restaurant
     * It reduces warehouse stock levels determined by the stock requests in the order, taking the order ID as a parameter
     * The system checks whether the order has enough stock quantities to provide the order
     * If successful, the system shows "ORDER_SENT"
     * If unsuccessful due to not enough stock in warehouse, the system shows "STOCK_TOO_LOW"
     * @param idToken to check access for warehouse
     * @param address of the warehouse
     * @param _orderId including the stock items and quantity which are to be sent
     * @return database update showing the order is out for delivery
     */
    @GET
    @Path("/send-order")
    //Reduces warehouse stock levels determined by the stock requests in an order. Takes the orderId as a parameter.
    public Response sendOrder(@HeaderParam("Authorization") String idToken, @HeaderParam("address") String address, @HeaderParam("orderId") String _orderId)
    {
	     if (!Authorisation.checkAccess(idToken, "warehouse") && !Authorisation.checkAccess(idToken, "restaurant")) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Cannot get access").build();
        }

        int orderId = Integer.parseInt(_orderId);
        // fetch current db connection
        Connection connection = DbConnection.getConnection();

    	// check if order has already been delivered
    	boolean orderFulfilled = false;
    	Statement statement = null;
    	String query = "SELECT orderStatus FROM StockOrders WHERE orderId ='"+orderId+"'";
    	try{
    		statement = connection.createStatement();
    		ResultSet rs = statement.executeQuery(query);

    		String orderStatus = "";

    		while (rs.next()) {
                orderStatus = rs.getString("orderStatus");
            }
    		if(!orderStatus.equalsIgnoreCase("Delivered")){
    			ServerLog.writeLog("Order has not been approved or has already been fulfilled.");
    			orderFulfilled = true;
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

	// if order hasnt been fulfilled
    	if (!orderFulfilled)
    	    return Response.status(Response.Status.FORBIDDEN).entity("ORDER_ALREADY_FULFILLED").build();


    	// retrieve order contents
        HashMap<String, Integer> orderedStock = new HashMap<>();

    	// check if warehouse has enough stock to fulfil order
    	boolean stockAvailable = true;

    	statement = null;
    	query = "SELECT quantity, stockItem "+
    			"FROM Contains WHERE orderId='"+orderId+"'";
    	try{
    		statement = connection.createStatement();
    		ResultSet rs = statement.executeQuery(query);
		
    		while(rs.next()){
                int cQuantity=0;

    			cQuantity = rs.getInt("quantity");
    			String cStock= rs.getString("stockItem");

    			orderedStock.put(cStock, cQuantity);
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

    	// check if enough stock is available to fulfill the order
    	for (Map.Entry<String, Integer> orderedItem: orderedStock.entrySet()) {
    	    String cStock = orderedItem.getKey();
    	    int cQuantity = orderedItem.getValue();

            statement =null;
            query = "SELECT quantity FROM Inside WHERE stockItem ='"+cStock+"' AND warehouseAddress ='"+address+"'";
            try{
                statement = connection.createStatement();
                ResultSet innerRs = statement.executeQuery(query);
                int iQuantity = -1;

                while (innerRs.next()) {
                    iQuantity = innerRs.getInt("quantity");
                }

                if(cQuantity > iQuantity){
                    ServerLog.writeLog("Order cannot be fulfilled. Warehouse stock too low.");
                    stockAvailable = false;
                }

            }catch (SQLException e ) {
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
        }

    	if (!stockAvailable)
    	    return Response.status(Response.Status.FORBIDDEN).entity("STOCK_TOO_LOW").build();

    	// if passed previous checks update stock levels

        for (Map.Entry<String, Integer> orderEntry: orderedStock.entrySet()) {
            String stockItem = orderEntry.getKey();
            int quantity = orderEntry.getValue();

            statement = null;
            query = "UPDATE Inside " +
                    "SET quantity = quantity-" + quantity +
                    " WHERE stockItem='" + stockItem + "' AND warehouseAddress ='" + address + "'";
            try {
                statement = connection.createStatement();
                statement.executeUpdate(query);
            } catch (SQLException e) {
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
        }


        JsonObject responseJson = Json.createObjectBuilder().add("message", "ORDER_SENT").build();
        return Response.status(Response.Status.OK).entity(responseJson.toString()).build();

    }

   /**
    * Method which gets the minimum amount of each stock item within the warehouse
    * If successful, system will show "Stock Item: "+stockItem+" Current minimum stock level: "+minQuantity
    * If unsuccessful, system will show "ERROR_QUERYING_MIN_STOCK_LEVEL"
    * @param idToken to check access for warehouse
    * @param address of the warehouse
    * @return the stock items and their minimum amount
    */
    @Path("/get-min-stock")
    @GET
    @Consumes("application/json")
    public Response getMinStock(@HeaderParam("Authorization") String idToken, @HeaderParam("address") String address) {
        Response.ResponseBuilder res = null;
	    // fetch db connection
        Connection connection = DbConnection.getConnection();

	      if (!Authorisation.checkAccess(idToken, "warehouse")) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Cannot get access").build();
        }

        Statement statement = null;
        String query = "SELECT stockItem, minQuantity from Inside WHERE warehouseAddress ='"+address+"'";
        JsonArrayBuilder minStockBuilder = Json.createArrayBuilder();
        try {

            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);

            while(rs.next()) {
                JsonObjectBuilder stockEntryBuilder = Json.createObjectBuilder();

                String stockItem = rs.getString("stockItem");
                int minQuantity = rs.getInt("minQuantity");

		// add stock and quantity to Json array
                stockEntryBuilder.add("stockItem", stockItem);
                stockEntryBuilder.add("quantity", minQuantity);
                minStockBuilder.add(stockEntryBuilder);

                ServerLog.writeLog("Stock Item: "+stockItem+" Current minimum stock level: "+minQuantity+"\n");
            }
            JsonArray minStock = minStockBuilder.build();
            res = Response.status(Response.Status.OK).entity(minStock.toString());
        } catch (SQLException e ) {
            res = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("ERROR_QUERYING_MIN_STOCK_LEVEL");
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
    * Method which checks that the Warehouse stock is above the minimum quantity level
    * If method is successful and there are stock items below the minimum level, system will show "Current stock of "+ stockItem +" is below minimum stock levels by "+ deficit
    * If method is successful and no stock items are below the minimum level, system will show "ENOUGH_STOCK"
    * @param idToken to check access for warehouse
    * @param address of the warehouse
    * @return a list of stock and quantity which is below the minimum level for that warehouse
    */
    @GET
    @Path("/min-stock-check")
    @Produces("application/json")
    public Response minStockCheck(@HeaderParam("Authorization") String idToken, @HeaderParam("address") String address)
    {

	  if (!Authorisation.checkAccess(idToken, "warehouse")) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Cannot get access").build();
        }

	// fetch db connection
        Connection connection = DbConnection.getConnection();

	// create Json array builder
        JsonArrayBuilder lackingStockArrayBuilder = Json.createArrayBuilder();

        Statement statement = null;
        String query = "SELECT stockItem, quantity, minQuantity " +
                       "FROM Inside " +            
                       "WHERE warehouseAddress='"+address+"'";
                       
        try {
        statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(query);
        while (rs.next()) {
            String stockItem = rs.getString("StockItem");
            int quantity = rs.getInt("quantity");
            int minQuantity = rs.getInt("minQuantity");

            // if quantity below the minimum
            if(quantity < minQuantity){
            	int deficit = minQuantity - quantity;

		// add stock information to the Json array
            	JsonObjectBuilder stockEntry = Json.createObjectBuilder();
            	stockEntry.add("stockItem", stockItem);
            	stockEntry.add("quantity", deficit);

            	lackingStockArrayBuilder.add(stockEntry.build());
            	System.out.println("Current stock of "+ stockItem +" is below minimum stock levels by "+deficit+".");
            }    
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

        if (lackingStockArrayBuilder.build().toArray().length==0)
            return Response.status(Response.Status.OK).entity("{}").build(); // produce empty json if enough stock

        return updateStock(idToken, address, lackingStockArrayBuilder.build().toString());


    }

   /**
    * Method which allows the Warehouse stock minimum levels to be set
    * If successful, the system will show "MIN_STOCK_VALUE_UPDATED"
    * If unsuccessful request, the system will show "REQUEST_MISSPECIFIED"
    * @param idToken to check access for warehouse
    * @param address of the Warehouse
    * @param requestBody to request to update stock minimum levels
    * @return adding the updated minimum stock levels to the database
    */
    @Path("/update-min-stock")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response updateMinStock(@HeaderParam("Authorization") String idToken, @HeaderParam("address") String address, String requestBody)
    {
        ServerLog.writeLog("Requested update of minimum stock required at " + address);
	   if (!Authorisation.checkAccess(idToken, "warehouse")) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Cannot get access").build();
        }

	   ServerLog.writeLog("User authorised to make change");
        // fetch db connection
        Connection connection = DbConnection.getConnection();

        ServerLog.writeLog("Parsing request body");
        JsonObject stockObject = JsonTools.parseObject(requestBody);

        ServerLog.writeLog("Request body parsed");
        // check if request is correct
        if (!stockObject.containsKey("stockItem") || !stockObject.containsKey("quantity"))
            return Response.status(Response.Status.BAD_REQUEST).entity("REQUEST_MISSPECIFIED").build();

        ServerLog.writeLog("Request correctly specified: " + stockObject.toString());

        ServerLog.writeLog("Further parsing JSON");

        String stockItem = stockObject.getString("stockItem");
        String minQtyStr = stockObject.getString("quantity");
        int minQty = Integer.parseInt(minQtyStr);

        ServerLog.writeLog("Update requested for " + stockItem + " to " + minQty);

        Statement statement = null;
    String query = "UPDATE Inside SET minQuantity ="+minQty+" WHERE stockItem='"+stockItem+"' AND warehouseAddress ='"+address+"'";
                       
        try {
        	statement = connection.createStatement();
        	statement.executeUpdate(query);
        	ServerLog.writeLog("Minimum stock levels updated to: " + minQty + " at " + address);
        
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

        JsonObject responseJson = Json.createObjectBuilder().add("message", "MIN_STOCK_VALUE_UPDATED").build();

        return Response.status(Response.Status.OK).entity(responseJson.toString()).build();

    }

   /**
    * Method which assigns an order to a driver which needs to be delivered from a warehouse to a restaurant
    * If successful, system will show "ORDER_ASSIGNED"
    * If unsuccessful, system will show "ORDER_ASSIGNMENT_ERROR"
    * @param idToken to check access for warehouse
    * @param orderId which includes the correct stock quantities required to be delivered to the restaurant
    * @param driverId of the delivery driver
    * @return the driver id which is assigned to an order ID
    */
    /* @GET
    @Path("/assign-to-driver")
    @Produces("application/json")
    public static Response assignOrderToDriver(@HeaderParam("Authorization") String idToken, @HeaderParam("orderId") String _orderId, @HeaderParam("driverId") String driverId){

	     if (!Authorisation.checkAccess(idToken, "warehouse") || !Authorisation.checkAccess(idToken, "driver")) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Cannot get access").build();
        }

	int orderId = Integer.parseInt(_orderId);
        Response.ResponseBuilder res = null;

        // fetch current dbConnection
        Connection connection = DbConnection.getConnection();

        String query = "INSERT INTO SentBy(orderId,driverId)VALUES (?,?)";
        PreparedStatement statement = null;

    	try {
    	    statement = connection.prepareStatement(query);
		    statement.setInt(1,orderId); // order assignment to driver
		    statement.setString(2, driverId);
		    statement.execute();
    	}catch (SQLException e ) {
    		e.printStackTrace();
    		res = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("ORDER_ASSIGNMENT_ERROR");
    	}
    	finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    ServerLog.writeLog("SQL exception occurred when closing SQL statement");
                }
            }
        }
    	JsonObject responseJson = Json.createObjectBuilder().add("message", "ORDER_ASSIGNED").build();
    	res = Response.status(Response.Status.OK).entity(responseJson.toString());

    	return res.build();
    } */

   /**
    * method to get the currently pending orders to go from a warehouse to a restaurant
    * Includes the date and time for the orders
    * @param idToken to check access for warehouse
    * @return order entries which currently have the status "pending"
    */
    @GET
    @Path("/get-pending-orders")
    @Produces("application/json")
    public Response getCurrentPendingOrders(@HeaderParam("Authorization") String idToken) {
	    ServerLog.writeLog("Requested list of pending orders");

	    if (!Authorisation.checkAccess(idToken, "warehouse")) {
	        ServerLog.writeLog("No permissions to view pending orders");
            return Response.status(Response.Status.UNAUTHORIZED).entity("Cannot get access").build();
        }

        Response.ResponseBuilder res = null;

	// fetch db connection
        Connection connection = DbConnection.getConnection();

	// create Json array builder
        JsonArrayBuilder pendingOrdersBuilder = Json.createArrayBuilder();

        // gets orderId and date/time for orders with status pending
        Statement statement = null;
        String query = "SELECT StockOrders.orderId, StockOrders.orderDateTime, Orders.restaurantAddress " +
                "FROM StockOrders, Orders WHERE StockOrders.orderStatus='Pending' AND Orders.orderId=StockOrders.orderId";
        try {
            ServerLog.writeLog("Querying database for pending orders");
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);

            JsonObjectBuilder orderEntry = Json.createObjectBuilder();

            while(rs.next()) {
                int orderId = rs.getInt("StockOrders.orderId");
                Date dateTime = rs.getDate("StockOrders.orderDateTime");
                String address = rs.getString("Orders.restaurantAddress");

                ServerLog.writeLog("Pending order ID: " + orderId);

		// add to Json array
                orderEntry.add("orderId", orderId);
                orderEntry.add("dateTime", dateTime.toString());
                orderEntry.add("address", address);

                // gets contents of the order
                JsonArrayBuilder orderContents = Json.createArrayBuilder();
                Statement innerStatement = null;
                String innerQuery = "SELECT quantity, stockItem FROM Contains WHERE orderId="+orderId;

                try {
                    innerStatement = connection.createStatement();
                    ResultSet innerRs = innerStatement.executeQuery(innerQuery);

                    while(innerRs.next()) {
                        String stockItem = innerRs.getString("stockItem");
                        int quantity = innerRs.getInt("quantity");
                        JsonObjectBuilder stockEntry = Json.createObjectBuilder();

			// add stock to the Json array
                        stockEntry.add("stockItem", stockItem);
                        stockEntry.add("quantity", quantity);

                        orderContents.add(stockEntry);
                    }
                }catch (SQLException e ) {
                    e.printStackTrace();
                }finally {
                    if (innerStatement != null) {
                        try {
                            innerStatement.close();
                        } catch (SQLException e) {
                            ServerLog.writeLog("SQL exception occurred when closing SQL statement");
                        }
                    }
                }

                orderEntry.add("contents", orderContents);

                pendingOrdersBuilder.add(orderEntry);
            }

            res = Response.status(Response.Status.OK).entity(pendingOrdersBuilder.build().toString());

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

        return res.build();
		}

        /**
	* method that outputs data to plot graphs for stock sent by warehouse
	* @param idToken to check access for the warehouse
	* @param stockItem which is then plotted on graph
	* @param type   time interval to use for the plot
	* @return data to plot graphs
	*/
	@GET
	@Path("/warehouse-graph")
	@Produces("application/json")
	public Response warehouseGraph(@HeaderParam("Authorization") String idToken, @HeaderParam("stockItem") String stockItem, @HeaderParam("type") String type)
        {

		 if (!Authorisation.checkAccess(idToken, "warehouse")) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Cannot get access").build();
        }

		ServerLog.writeLog("Requested warehouse graphing data.");
		JsonArrayBuilder responseBuilder = Json.createArrayBuilder();
		Connection connection = DbConnection.getConnection();

        Statement statement = null;
        String query = "SELECT restaurantAddress FROM Restaurants";

        try {
        	statement = connection.createStatement();
        	ResultSet rs = statement.executeQuery(query);

        	while (rs.next()) {
        		String restaurant = rs.getString("restaurantAddress");

        		Statement statement2 = null;
                String query2 = "SELECT orderId FROM Orders where restaurantAddress ='"+restaurant+"'";
                int total=0;

                try {
                	statement2 = connection.createStatement();
                	ResultSet rs2 = statement2.executeQuery(query2);

                	while (rs2.next()) {
                		int orderId = rs2.getInt("orderId");

                		java.util.Date orderDate = new java.util.Date();
                    	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                    	String currentTime = sdf.format(orderDate);

                    	Calendar cal = Calendar.getInstance();

                    	if(type.equalsIgnoreCase("year")) {
                    		cal.add(Calendar.YEAR, -1);
                    	}else if (type.equalsIgnoreCase("month")) {
                    		cal.add(Calendar.MONTH, -1);
                    	}else if (type.equalsIgnoreCase("week")) {
                    		cal.add(Calendar.DAY_OF_MONTH, -7);
                    	}else if (type.equalsIgnoreCase("day")) {
                    		cal.add(Calendar.DAY_OF_MONTH, -1);
                    	}

                    	java.util.Date result = cal.getTime();
                    	String timeBack = sdf.format(result);


                		Statement statement3 = null;
                        String query3 = "SELECT orderId FROM StockOrders where orderId ="+orderId+" AND (orderDeliveryDate BETWEEN '"+timeBack+"' AND '"+currentTime+"')";

                        try {
                        	statement3 = connection.createStatement();
                        	ResultSet rs3 = statement3.executeQuery(query3);

                        	while (rs3.next()) {
                        		orderId = rs3.getInt("orderId");


                        		Statement statement4 = null;
                                String query4 = "SELECT quantity FROM Contains where orderId ="+orderId+" AND stockItem = '"+stockItem+"'";

                                try {
                                	statement4 = connection.createStatement();
                                	ResultSet rs4 = statement4.executeQuery(query4);
                                	int quantity=0;

                                	while (rs4.next()) {
                                		quantity = rs4.getInt("quantity");
                                		total=total+quantity;
                                	}

                                } catch (SQLException e ) {
                                    e.printStackTrace();
                                } finally {
                                    if (statement4 != null) {statement4.close();}
                                }
                        	}
                        } catch (SQLException e ) {
                            e.printStackTrace();
                        } finally {
                            if (statement3 != null) {statement3.close();}
                        }
                	}
                } catch (SQLException e ) {
                    e.printStackTrace();
                } finally {
                    if (statement2 != null) {statement2.close();}
                }

                JsonObjectBuilder arrayEntryBuilder = Json.createObjectBuilder();
                arrayEntryBuilder.add("name", restaurant);
                arrayEntryBuilder.add("value", total);

                JsonObject arrayEntry = arrayEntryBuilder.build();
                responseBuilder.add(arrayEntry);

        	}
        }catch (SQLException e ) {
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

        JsonArray response = responseBuilder.build();

        return Response.status(Response.Status.OK).entity(response.toString()).build();

    }


	/**
	* method to get a warehouse list
	* @param idToken check access for manager
	* @return a list of all the warehouses
	*/
	@GET
        @Path("/get-list")
        @Produces("application/json")
        public Response getWarehouseList(@HeaderParam("Authorization") String idToken) {

            if (!Authorisation.checkAccess(idToken, "manager")) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("Cannot get access").build();
            }


            JsonArrayBuilder responseBuilder = Json.createArrayBuilder();
            // fetch current database connection
            Connection connection = DbConnection.getConnection();

            Statement statement = null;
            String query = "SELECT warehouseAddress " +
                    "FROM Warehouse ";
            try {
                statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(query);
                while (rs.next()) {
                    JsonObjectBuilder arrayEntryBuilder = Json.createObjectBuilder();

                    String warehouseAddress = rs.getString("WarehouseAddress");

                    arrayEntryBuilder.add("address", warehouseAddress);

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
}
