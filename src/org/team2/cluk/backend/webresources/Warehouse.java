package org.team2.cluk.backend.webresources;

import org.team2.cluk.backend.tools.DbConnection;
import org.team2.cluk.backend.tools.JsonTools;
import org.team2.cluk.backend.tools.ServerLog;

import javax.json.*;
import javax.validation.constraints.Positive;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;


/*
 * Warehouse Class which handles stocks information, sending orders to restaurant and assigning a driver to deliver the order
 *
 * @version 11/05/2019
 */

@Path("/warehouse")
public class Warehouse
{

    /*
     * Method which gets the total stock within a warehouse
     * If successful, system will show "StockItem: " + Quantity
     * @param address of the warehouse
     * @return the stock name and quantity (in units) held at the warehouse
     */
    @GET
    @Path("/get-total-stock")
    @Produces("application/json")
    public void GetTotalStock(@HeaderParam("warehouse") String address)
    {
        Statement statement = null;
        String query = "SELECT stockItem, quantity " +
                       "FROM Inside " +            
                       "WHERE warehouseAddress='"+address+"'";
                       
        try {
	// connect to db
        statement = DbConnection.getConnection().createStatement();
        ResultSet rs = statement.executeQuery(query);
        while (rs.next()) {
            String StockItem = rs.getString("StockItem");
            int Quantity = rs.getInt("quantity");
            System.out.println(StockItem + ": " + Quantity);
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
    }

    /*
     * Method to increase the warehouse stock by the quantity specified
     * Takes parameters of stock item and quantity
     * {stockItem: string, quantity: number}
     * If successful, system shows "STOCK_UPDATED"
     * If JSON request unsuccessful, system shows "Order entry misspecified. Skipping entry."
     * @param address of the warehouse
     * @param requestBody uses Json to request an array of the stock at the warehouse
     * @return the updated stock to the warehouse
     */
    @POST
    @Path("/update-stock")
    @Consumes("application/json")
    public Response updateStock(@HeaderParam("warehouse") String address, String requestBody)
    {
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

        return Response.status(Response.Status.OK).entity("STOCK_UPDATED").build();
    }

    /*
     * Method which sends an order from a Warehouse to a Restaurant
     * It reduces warehouse stock levels determined by the stock requests in the order, taking the order ID as a parameter
     * The system checks whether the order has enough stock quantities to provide the order
     * If successful, the system shows "ORDER_SENT"
     * If unsuccessful due to not enough stock in warehouse, the system shows "STOCK_TOO_LOW"
     * @param address of the warehouse
     * @param orderId including the stock items and quantity which are to be sent
     * @return database update showing the order is out for delivery
     */
    @GET
    @Path("/send-order")
    public Response sendOrder(@HeaderParam("address") String address, @HeaderParam("orderId") int orderId)
    {
        // fetch current db connection
        Connection connection = DbConnection.getConnection();

    	// check if order has already been delivered
    	boolean orderFulfilled = false;
    	Statement statement = null;
    	String query = "SELECT orderStatus FROM StockOrders WHERE orderId ='"+orderId+"'";
    	try{
    		statement = connection.createStatement();
    		ResultSet rs = statement.executeQuery(query);
			
    		rs.next();
    		String orderStatus = rs.getString("orderStatus");
    		if(!orderStatus.equalsIgnoreCase("Pending")){
    			ServerLog.writeLog("Order has already been fulfilled.");
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

        // update database to mark order as out for delivery
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
        return Response.status(Response.Status.OK).entity("ORDER_SENT").build();
    }

   /*
    * Method which gets the minimum amount of each stock item within the warehouse 
    * If successful, system will show "Stock Item: "+stockItem+" Current minimum stock level: "+minQuantity
    * If unsuccessful, system will show "ERROR_QUERYING_MIN_STOCK_LEVEL"
    * @param address of the warehouse
    * @return the stock items and their minimum amount 
    */
    @Path("/get-min-stock")
    @POST
    @Consumes("application/json")
    public Response getMinStock(@HeaderParam("address") String address) {
        Response.ResponseBuilder res = null;
	    // fetch db connection
        Connection connection = DbConnection.getConnection();

	    
        Statement statement = null;
        String query = "SELECT stockItem, minQuantity from Inside WHERE warehouseAddress ='"+address+"'";
        try {
		// create Json array builder 
            JsonArrayBuilder minStockBuilder = Json.createArrayBuilder();
            JsonArray minStock = minStockBuilder.build();

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

   /*
    * Method which checks that the Warehouse stock is above the minimum quantity level
    * If method is successful and there are stock items below the minimum level, system will show "Current stock of "+ stockItem +" is below minimum stock levels by "+ deficit
    * If method is successful and no stock items are below the minimum level, system will show "ENOUGH_STOCK"
    * @param address of the warehouse
    * @return a list of stock and quantity which is below the minimum level for that warehouse
    */
    @GET
    @Path("/min-stock-check")
    public Response minStockCheck(@HeaderParam("address") String address)
    {
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

	// if no stock is below minimum quantity level
        if (lackingStockArrayBuilder.build().toArray().length==0) // none added to Json array
            return Response.status(Response.Status.OK).entity("ENOUGH_STOCK").build();

        return updateStock(address, lackingStockArrayBuilder.build().toString());
    }

   /*
    * Method which allows the Warehouse stock minimum levels to be set
    * If successful, the system will show "MIN_STOCK_VALUE_UPDATED"
    * If unsuccessful request, the system will show "REQUEST_MISSPECIFIED"
    * @param address of the Warehouse
    * @param requestBody to request to update stock minimum levels
    * @return adding the updated minimum stock levels to the database
    */
    @Path("/update-min-stock")
    @POST
    @Consumes("application/json")
    public Response updateMinStock(@HeaderParam("address") String address, String requestBody)
    {
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

        return Response.status(Response.Status.OK).entity("MIN_STOCK_VALUE_UPDATED").build();
    }

   /*
    * Method which assigns an order to a driver which needs to be delivered from a warehouse to a restaurant 
    * If successful, system will show "ORDER_ASSIGNED"
    * If unsuccessful, system will show "ORDER_ASSIGNMENT_ERROR"
    * @param orderId which includes the correct stock quantities required to be delivered to the restaurant 
    * @param driverId of the delivery driver
    * @return the driver id which is assigned to an order ID
    */
    @GET
    @Path("/assign-to-driver")
    public Response assignOrderToDriver(@HeaderParam("orderId") int orderId, @HeaderParam("driverId") String driverId){
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
    	res = Response.status(Response.Status.OK).entity("ORDER_ASSIGNED");

    	return res.build();
    }
	
   /*
    * method to get the currently pending orders to go from a warehouse to a restaurant
    * Includes the date and time for the orders
    * @return order entries which currently have the status "pending"
    */
    @GET
    @Path("/get-pending-orders")
    public Response getCurrentPendingOrders() {
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
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);

            JsonObjectBuilder orderEntry = Json.createObjectBuilder();

            while(rs.next()) {
                int orderId = rs.getInt("StockOrders.orderId");
                Date dateTime = rs.getDate("StockOrders.orderDateTime");
                String address = rs.getString("Orders.restaurantAddress");

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
}
