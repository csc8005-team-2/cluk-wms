import java.sql.*;

 public class Warehouse
{
    private String Address;
    
    public Warehouse(String address)
    {   
        Address = address;
    }

    //Outputs total stock held at the warehouse(units).
    public void GetTotalStock(Connection connection) throws SQLException
    {
        Statement statement = null;
        String query = "SELECT stockItem, quantity " +
                       "FROM Inside " +            
                       "WHERE warehouseAddress='"+Address+"'";
                       
        try {
        statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(query);
        while (rs.next()) {
            String StockItem = rs.getString("StockItem");
            int Quantity = rs.getInt("quantity");
            System.out.println(StockItem + ": " + Quantity);
        }
        } catch (SQLException e ) {
            e.printStackTrace();
        } finally {
            if (statement != null) {statement.close();}
        }                 
    }
    
    //Increases warehouse stock of item specified by quantity specified. Takes parameters for stockItem and quantity.
    public void UpdateStock(Connection connection, String stockItem, int quantity) throws SQLException
    {
        Statement statement = null;
        String query = "SELECT stockItem, quantity " +
                       "FROM Inside " +            
                       "WHERE stockItem='"+stockItem+"' AND warehouseAddress ='"+Address+"'";
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            
            rs.next();
            int Quantity = rs.getInt("quantity");
            System.out.println("Previos stock of "+stockItem + ": " + Quantity);
            int newQuantity = Quantity+quantity;
        
            Statement statement2 = null;
            String query2 = "UPDATE Inside " +
                            "SET quantity ='"+newQuantity+            
                            "' WHERE stockItem='"+stockItem+"' AND warehouseAddress ='"+Address+"'";
            try {
                statement2 = connection.createStatement();
                statement2.executeUpdate(query2);
                System.out.println("Updated stock of "+stockItem + ": " + newQuantity);
            } catch (SQLException e ) {
                e.printStackTrace();
            } finally {
                if (statement2 != null) {statement2.close();}
            }                     
        } catch (SQLException e ) {
            e.printStackTrace();
        } finally {
            if (statement != null) {statement.close();}
       }                                              
    }
    
    //Reduces warehouse stock levels determined by the stock requests in an order. Takes the orderId as a parameter.
    public void SendOrder(Connection connection, int orderId) throws SQLException
    {
    	//Check if order has already been delivered.
    	boolean orderFulfilled = false;
    	Statement statement = null;
    	String query = "SELECT orderStatus FROM StockOrders WHERE orderId ='"+orderId+"'";
    	try{
    		statement = connection.createStatement();
    		ResultSet rs = statement.executeQuery(query);
			
    		rs.next();
    		String orderStatus = rs.getString("orderStatus");
    		if(!orderStatus.equalsIgnoreCase("Pending")){
    			System.out.println("Order has already been fulfilled.");
    			orderFulfilled = true;
    		}
    	} catch (SQLException e ) {
    		e.printStackTrace();
    	} finally {
    		if (statement != null) {statement.close();}
    	}
    		
    	//Check if warehouse has enough stock to fulfil order.
    	boolean stockAvaliable = true;
    	int cQuantity=0; int iQuantity=0;
    	statement = null;
    	query = "SELECT quantity, stockItem "+
    			"FROM Contains WHERE orderId='"+orderId+"'";
    	try{
    		statement = connection.createStatement();
    		ResultSet rs = statement.executeQuery(query);
		
    		while(rs.next()){
    			cQuantity = rs.getInt("quantity");
    			String cStock= rs.getString("stockItem");
    			
    			Statement innerStatement =null;
    			String innerQuery = "SELECT quantity FROM Inside WHERE stockItem ='"+cStock+"' AND warehouseAddress ='"+Address+"'";
    			try{
    				innerStatement = connection.createStatement();
    				ResultSet innerRs = innerStatement.executeQuery(innerQuery);
    				
    				innerRs.next();
    				iQuantity = innerRs.getInt("quantity");
    			}catch (SQLException e ) {
    	    		e.printStackTrace();
    	    	} finally {
    	    		if (innerStatement != null) {innerStatement.close();}
    	    	}
    	    	
    			if(cQuantity > iQuantity){
    				System.out.println("Order cannot be fulfilled. Warehouse stock too low.");
    	    		stockAvaliable = false;
				}
    		}
    	} catch (SQLException e ) {
			e.printStackTrace();
		} finally {
			if (statement != null) {statement.close();}
		}
    	
    	//If passed previous checks update stock levels.
    	if(stockAvaliable==true && orderFulfilled==false) {
    		statement = null;
    		query = "SELECT stockItem, quantity " +
					"FROM Contains " +            
					"WHERE orderId='"+orderId+"'";
    		try {
				statement = connection.createStatement();
				ResultSet rs = statement.executeQuery(query);
				while(rs.next()){
					int quantity = rs.getInt("quantity");
					String stockItem = rs.getString("StockItem");
					
					Statement innerStatement = null;
					String innerQuery = "UPDATE Inside " +
                        				"SET quantity = quantity-"+quantity+           
                        				" WHERE stockItem='"+stockItem+"' AND warehouseAddress ='"+Address+"'";
					try {
						innerStatement = connection.createStatement();
						innerStatement.executeUpdate(innerQuery);
					} catch (SQLException e ) {
						e.printStackTrace();
					} finally {
						if (innerStatement != null) {innerStatement.close();}
					}                     
				}
    		} catch (SQLException e ) {
				e.printStackTrace();
			} finally {
				if (statement != null) {statement.close();}
			}   
    		
    		//Update database to mark order as out for delivery.
    		statement = null;
			query = "UPDATE StockOrders "+
					"SET orderStatus = 'Out for delivery' "+
					"WHERE orderId='"+orderId+"'";
			try {
				statement = connection.createStatement();
				statement.executeUpdate(query);
			} catch (SQLException e ) {
				e.printStackTrace();
			} finally {
				if (statement != null) {statement.close();}
			}                     
    	}
    }
    
    //Checks the warehouse stock is above the minimum level.
    public void minStockCheck(Connection connection) throws SQLException
    {
        Statement statement = null;
        String query = "SELECT stockItem, quantity, minQuantity " +
                       "FROM Inside " +            
                       "WHERE warehouseAddress='"+Address+"'";
                       
        try {
        statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(query);
        while (rs.next()) {
            String StockItem = rs.getString("StockItem");
            int Quantity = rs.getInt("quantity");
            int minQuantity = rs.getInt("minQuantity");

            if(Quantity < minQuantity){
            	int deficit = minQuantity - Quantity;
            	System.out.println("Current stock of "+ StockItem +" is below minimum stock levels by "+deficit+".");
            	this.UpdateStock(connection, StockItem, 100);
            }    
        }
        } catch (SQLException e ) {
            e.printStackTrace();
        } finally {
            if (statement != null) {statement.close();}
        }                 
    }
    
    //Allows the warehouse stock minimums to be set.
    public void updateMinStock(Connection connection, String stockItem, int min) throws SQLException
    {
        Statement statement = null;
        String query = "UPDATE Inside SET minQuantity ="+min+" WHERE stockItem='"+stockItem+"' AND warehouseAddress ='"+Address+"'";
                       
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

    //Assigns an order to a driver(basic) may require expanding based on driver class.
    public void assignOrderToDriver(Connection connection, int orderId, String driverId) throws SQLException{
    	try {

		String query = "INSERT INTO SentBy(orderId,driverId)VALUES (?,?)";
    	PreparedStatement statement= connection.prepareStatement(query);
		statement.setInt(1,orderId);
		statement.setString(2, driverId);
		statement.execute();
		
    	}catch (SQLException e ) {
    		e.printStackTrace();
    	}           
    }
    
    
    //Added functionality discussed on 29/03/2019. Currently untested. Will test (02/04//2019).
    //////////////////////////////////////////////////////////////////////////////////////////
    
    
    //Method to get current minimum stock levels.
    public void getMinStock(Connection connection) throws SQLException {
    	
    	Statement statement = null;
    	String query = "SELECT stockItem, minQuantity from Inside WHERE warehouseAddresss ='"+this.Address+"'";
    	try {
    		 statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query);
             
             while(rs.next()) {
            	 String stockItem = rs.getString("stockItem");
            	 int minQuantity = rs.getInt("minQuantity");
            	 System.out.print("Stock Item: "+stockItem+" Current minimum stock level: "+minQuantity);
             }
    	} catch (SQLException e ) {
            e.printStackTrace();
        } finally {
            if (statement != null) {statement.close();}
        }                 
    }
    
    //Method to get currently pending orders.
    public void getCurrentPendingOrders(Connection connection) throws SQLException {
    	
    	//Gets orderId and date/time for orders with status pending.
    	Statement statement = null;
    	String query = "SELECT orderId, orderDateTime FROM StockOrders WHERE orderStatus='Pending'";
    	try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            
            while(rs.next()) {
            	int orderId = rs.getInt("orderId");
            	Date dateTime = rs.getDate("orderDateTime");
            	
            	//Gets address of restaurant for each order.
            	Statement innerStatement = null;
            	String innerQuery = "SELECT restautantAddress FROM Orders WHERE orderId="+orderId;
            	
            	try {
            		innerStatement = connection.createStatement();
                    ResultSet innerRs = statement.executeQuery(innerQuery);
                    rs.next();
                    String restaurant = innerRs.getString("restaurantAddress");
                    System.out.print("Restaurant: "+restaurant+" Order ID: "+orderId+" Date/Time ordered: "+dateTime+" Status: Pending");
    
            	}catch (SQLException e ) {
                    e.printStackTrace();
                } finally {
                    if (innerStatement != null) {innerStatement.close();}
                }                 
            	
            	//Gets contents of the order.
            	innerStatement = null;
            	innerQuery = "SELECT quantity, stockItem FROM Contains WHERE orderId="+orderId;
            	
            	try {
            		innerStatement = connection.createStatement();
                    ResultSet innerRs = statement.executeQuery(innerQuery);
                    System.out.print("Order contains: \n");
                    
                    while(rs.next()) {
                    	String stockItem = innerRs.getString("stockItem");
                    	int quantity = innerRs.getInt("quantity");
                    	System.out.print(stockItem+": "+quantity);
                    }
            	}catch (SQLException e ) {
                    e.printStackTrace();
                } finally {
                    if (innerStatement != null) {innerStatement.close();}
                }   
            }    
    	} catch (SQLException e ) {
            e.printStackTrace();
        } finally {
            if (statement != null) {statement.close();}
        }                 
    }
}