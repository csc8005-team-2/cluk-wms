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

@Path("/warehouse")
public class Warehouse
{
	
	@GET
    @Path("/get-stock-names")
    @Produces("application/json")
    //Outputs the names of the stock items.
    public Response GetStockNames(@HeaderParam("Authorization") String idToken)
    {
	    
	    // if (checkAccess(warehousePermissions)) {
		    
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

    @GET
    @Path("/get-total-stock")
    @Produces("application/json")
    //Outputs total stock held at the warehouse(units).
    public Response GetTotalStock(@HeaderParam("Authorization") String idToken, @HeaderParam("address") String address)
    {
	    // if (checkAccess(warehousePermissions)) {
	
        ServerLog.writeLog("Requested information on total stock in the warehouse at "+address);

        if (address.isBlank()) {
            ServerLog.writeLog("Rejected request as warehouse address not specified");
            return Response.status(Response.Status.BAD_REQUEST).entity("ADDRESS_BLANK_OR_NOT_PROVIDED").build();
        }

        JsonArrayBuilder responseBuilder = Json.createArrayBuilder();
        // fetch current database connection
        Connection connection = DbConnection.getConnection();

        Statement statement = null;
        String query = "SELECT stockItem, quantity " +
                       "FROM Inside " +            
                       "WHERE warehouseAddress='"+address+"'";

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

    /**
     * {stockItem: string, quantity: number}
     * @param address
     * @param requestBody
     * @return
     * @throws SQLException
     */
    @POST
    @Path("/update-stock")
    @Consumes("application/json")
    @Produces("application/json")
    //Increases warehouse stock of item specified by quantity specified. Takes parameters for stockItem and quantity.
    public Response updateStock(@HeaderParam("Authorization") String idToken, @HeaderParam("address") String address, String requestBody)
    {
	    // if (checkAccess(managerPermissions)) {
	    
        ServerLog.writeLog("Updating warehouse stock at " + address);
        // fetch db connection
        Connection connection = DbConnection.getConnection();

        // parse request body
        JsonArray stockToUpdate = JsonTools.parseArray(requestBody);

        for (JsonValue entry: stockToUpdate) {
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

    
    
    //Decline order method. Unsure if correct json usage.
    @GET
    @Path("/approve-order")
    @Produces("application/json")
    public Response approveOrder(@HeaderParam("Authorization") String idToken, @HeaderParam("orderId") String _orderId)
    {
    	int orderId = Integer.parseInt(_orderId);
   	Response.ResponseBuilder res = null;
   	Connection connection = DbConnection.getConnection();
   		
   	Statement statement = null;
   	String query = "UPDATE StockOrders Set orderStatus = 'Approved' WHERE orderId = " + orderId;
   		
   	try {
   		statement = connection.createStatement();
   		statement.executeUpdate(query);
   		ServerLog.writeLog("Order: "+orderId +" Approved");
   			
   		res = Response.status(Response.Status.OK).entity("APPROVED_ORDER");
   		
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

    //Decline order method. Unsure if correct json usage.
    @GET
    @Path("/decline-order")
    @Produces("application/json")
    public Response declineOrder(@HeaderParam("Authorization") String idToken, @HeaderParam("orderId") String _orderId)
    {
    	int orderId = Integer.parseInt(_orderId);
   	Response.ResponseBuilder res = null;
   	Connection connection = DbConnection.getConnection();
   		
   	Statement statement = null;
   	String query = "UPDATE StockOrders Set orderStatus = 'Declined' WHERE orderId = " + orderId;
   		
   	try {
   		statement = connection.createStatement();
   		statement.executeUpdate(query);
   		ServerLog.writeLog("Order: "+orderId +" Declined");
   			
   		res = Response.status(Response.Status.OK).entity("DECLINED_ORDER");
   		
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
    
	
	
    @GET
    @Path("/send-order")
    @Produces("application/json")
    //Reduces warehouse stock levels determined by the stock requests in an order. Takes the orderId as a parameter.
    public Response sendOrder(@HeaderParam("Authorization") String idToken, @HeaderParam("address") String address, @HeaderParam("orderId") String _orderId)
    {
	    // if (checkAccess(warehousePermissions)) {
	    
        int orderId = Integer.parseInt(_orderId);
        // fetch current db connection
        Connection connection = DbConnection.getConnection();

    	//Check if order has already been delivered.
    	boolean orderFulfilled = false;
    	Statement statement = null;
    	String query = "SELECT orderStatus FROM StockOrders WHERE orderId ='"+orderId+"'";
    	try{
    		statement = connection.createStatement();
    		ResultSet rs = statement.executeQuery(query);
			
    		rs.next();
    		String orderStatus = rs.getString("orderStatus");
    		if(!orderStatus.equalsIgnoreCase("Approved")){
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

    	if (!orderFulfilled)
    	    return Response.status(Response.Status.FORBIDDEN).entity("ORDER_ALREADY_FULFILLED").build();


    	// retrieve order contents
        HashMap<String, Integer> orderedStock = new HashMap<>();

    	//Check if warehouse has enough stock to fulfil order.
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

                innerRs.next();
                int iQuantity = innerRs.getInt("quantity");

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

    	//If passed previous checks update stock levels.

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

        //Update database to mark order as out for delivery.
        statement = null;
        query = "UPDATE StockOrders "+
                "SET orderStatus = 'Out for delivery' "+
                "WHERE orderId='"+orderId+"'";
        try{
            statement = connection.createStatement();
            statement.executeUpdate(query);
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
        JsonObject responseJson = Json.createObjectBuilder().add("message", "ORDER_SENT").build();
        return Response.status(Response.Status.OK).entity(responseJson.toString()).build();
		
    }

    @Path("/get-min-stock")
    @GET
    @Consumes("application/json")
    public Response getMinStock(@HeaderParam("Authorization") String idToken, @HeaderParam("address") String address) {
        Response.ResponseBuilder res = null;
        Connection connection = DbConnection.getConnection();
	    
	    // if (checkAccess(warehousePermissions)) {
		    
        Statement statement = null;
        String query = "SELECT stockItem, minQuantity from Inside WHERE warehouseAddress ='"+address+"'";
        try {
            JsonArrayBuilder minStockBuilder = Json.createArrayBuilder();
            JsonArray minStock = minStockBuilder.build();

            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);

            while(rs.next()) {
                JsonObjectBuilder stockEntryBuilder = Json.createObjectBuilder();

                String stockItem = rs.getString("stockItem");
                int minQuantity = rs.getInt("minQuantity");

                stockEntryBuilder.add("stockItem", stockItem);
                stockEntryBuilder.add("quantity", minQuantity);
                minStockBuilder.add(stockEntryBuilder);

                ServerLog.writeLog("Stock Item: "+stockItem+" Current minimum stock level: "+minQuantity+"\n");
            }

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

    @GET
    @Path("/min-stock-check")
    @Produces("application/json")
    //Checks the warehouse stock is above the minimum level.
    public Response minStockCheck(@HeaderParam("Authorization") String idToken, @HeaderParam("address") String address)
    {
	    
	    // if (checkAccess(managerPermissions)) {
	
        Connection connection = DbConnection.getConnection();

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

            if(quantity < minQuantity){
            	int deficit = minQuantity - quantity;

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

    //Allows the warehouse stock minimums to be set.
    @Path("/update-min-stock")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response updateMinStock(@HeaderParam("Authorization") String idToken, @HeaderParam("address") String address, String requestBody)
    {
	    // if (checkAccess(managerPermissions)) {
		    
        // fetch db connection
        Connection connection = DbConnection.getConnection();

        JsonObject stockObject = JsonTools.parseObject(requestBody);

        // check if request is correct
        if (!stockObject.containsKey("stockItem") || !stockObject.containsKey("quantity"))
            return Response.status(Response.Status.BAD_REQUEST).entity("REQUEST_MISSPECIFIED").build();

        String stockItem = stockObject.getString("stockItem");
        int min = stockObject.getInt("quantity");

        Statement statement = null;
        String query = "UPDATE Inside SET minQuantity ="+min+" WHERE stockItem='"+stockItem+"' AND warehouseAddress ='"+address+"'";
                       
        try {
        	statement = connection.createStatement();
        	statement.executeUpdate(query);
        	ServerLog.writeLog("Minimum stock levels updated to: " + min + " at " + address);
        
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

    @GET
    @Path("/assign-to-driver")
    @Produces("application/json")
    //Assigns an order to a driver(basic) may require expanding based on driver class.
    public Response assignOrderToDriver(@HeaderParam("Authorization") String idToken, @HeaderParam("orderId") String _orderId, @HeaderParam("driverId") String driverId){
        
	    // if (checkAccess(managerPermissions)) {
	    
	int orderId = Integer.parseInt(_orderId);
        Response.ResponseBuilder res = null;
	    

        // fetch current dbConnection
        Connection connection = DbConnection.getConnection();

        String query = "INSERT INTO SentBy(orderId,driverId)VALUES (?,?)";
        PreparedStatement statement = null;

    	try {
    	    statement = connection.prepareStatement(query);
		    statement.setInt(1,orderId);
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
		    
		    
    }

    @GET
    @Path("/get-pending-orders")
    @Produces("application/json")
    //Method to get currently pending orders.
    public Response getCurrentPendingOrders(@HeaderParam("Authorization") String idToken) {
	    
	    // if (checkAccess(warehousePermissions)) {

        Response.ResponseBuilder res = null;

        Connection connection = DbConnection.getConnection();

        JsonArrayBuilder pendingOrdersBuilder = Json.createArrayBuilder();

        //Gets orderId and date/time for orders with status pending.
        Statement statement = null;
        String query = "SELECT StockOrders.orderId, StockOrders.orderDateTime, Orders.restaurantAddress " +
                "FROM StockOrders, Orders WHERE StockOrders.orderStatus='Pending' AND Orders.orderId=StockOrders.orderId";
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);

            JsonObjectBuilder orderEntry = Json.createObjectBuilder();

            while(rs.next()) {
                int orderId = rs.getInt("StockOrders.orderId");
                Date dateTime = rs.getDate("StockOrders.orderDateTime");
                String address = rs.getString("Orders.restaurantAddress");

                orderEntry.add("orderId", orderId);
                orderEntry.add("dateTime", dateTime.toString());
                orderEntry.add("address", address);

                //Gets contents of the order.
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
}
