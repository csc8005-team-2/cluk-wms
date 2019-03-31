package org.team2.cluk.backend.webresources;

import java.sql.*;
import java.util.*;

public class RestaurantResource {

    private String restaurantAddress;

    public RestaurantResource() {
        restaurantAddress = "";
    }

    public RestaurantResource(String name) {
        this.restaurantAddress = name;
    }

    //Method returns total stock(units) held at this restaurant.
    public void GetTotalStock(Connection connection) throws SQLException {
        Statement statement = null;
        String query = "SELECT stockItem, quantity " +
                "FROM Within " +
                "WHERE restaurantAddress ='" + restaurantAddress+"'";
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                String StockItem = rs.getString("StockItem");
                int Quantity = rs.getInt("Quantity");
                System.out.println(StockItem + "\t" + Quantity + "\n");
            }
        } catch (SQLException e) {
        	
            e.printStackTrace();
        } finally {
            if (statement != null) {statement.close();}
        }
    }


	//this method updates the stock for a restaurant when it has received an order.
    public void receiveOrder(Connection connection, int orderId) throws SQLException {
    	
    	//Checking order is being sent to the correct restaurant.
    	boolean correctRestaurant=true;
    	Statement statement = null;
    	String query = "SELECT restaurantAddress FROM Orders WHERE orderId ='"+orderId+"'";
    	try {
    		statement = connection.createStatement();
    		ResultSet rs = statement.executeQuery(query);
    		rs.next();
    		String restaurant = rs.getString("restaurantAddress");
    		if(!restaurant.equals(restaurantAddress)) {
    			System.out.println( "The order submitted is not for this restaurant. The correct "+
    								"delivery location is "+ restaurant+".");
    			correctRestaurant=false;
    		}else {
    			System.out.println("New order recieved at: "+this.restaurantAddress+". Order ID: "+ orderId);
    		}
    		
    	} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (statement != null) {statement.close();}
		}

    	//If order has passed check add order items to restaurant stock.
    	if(correctRestaurant==true) {
    		statement = null; 
    		query = "SELECT stockItem, quantity FROM Contains WHERE orderId ='" +orderId+"'";

    		try {
    			statement = connection.createStatement();      	
    			ResultSet rs = statement.executeQuery(query);
                	              
    			while (rs.next()) {
    				int quantityToAdd = rs.getInt("quantity");
    				String stockItem = rs.getString("stockItem");
    				System.out.println("New order contains "+ stockItem + ": " + quantityToAdd);
    				
    				Statement statement2 = null;
    				String query2 = "SELECT unitSize from Stock WHERE stockItem ='" +stockItem+"'";
    				
    				try{
    					statement2 = connection.createStatement();      	
    					ResultSet rs2 = statement2.executeQuery(query2);
    					rs2.next();
    	    			int conversion = rs2.getInt("unitSize");
    	    			
    	    			quantityToAdd=quantityToAdd*conversion;
    	    			
    				} catch (SQLException e) {
    					e.printStackTrace();
    				}finally {
						if (statement2 != null) {statement2.close();}
					}
    	    			
    				//then update Within table to add this new Quantity to our original full quantity of stock 
    				//Within must not be empty before doing this method
                	   
    				Statement innerStatement = null;
    				String innerQuery = "SELECT quantity FROM Within WHERE restaurantAddress ='" +restaurantAddress+"'" + 
    						"AND stockItem ='" +stockItem+"'";
    				try {
    					innerStatement = connection.createStatement();
    					ResultSet InnerRs = innerStatement.executeQuery(innerQuery);
    					InnerRs.next();
    					
    					int previousQuantity = InnerRs.getInt("quantity");
    					System.out.println("Previous stock quantity: " + previousQuantity + " for stock: " + stockItem);
                	        
    					Statement updateStatement = null;
    					int newQuantity = quantityToAdd + previousQuantity;
    					String updateQuery = "UPDATE Within SET quantity ='" +newQuantity+"'" + "WHERE stockItem ='" +stockItem+"'";
                	   
    					try {
    						updateStatement = connection.createStatement();
    						updateStatement.executeUpdate(updateQuery);
    						System.out.println("Updated stock of " + stockItem + ": " + newQuantity + " at restaurant: " + restaurantAddress);
    					} catch (SQLException e) {
    						e.printStackTrace();
    					} finally {
    						if (updateStatement != null) {updateStatement.close();}
    					}
    					
    				} catch (SQLException e) {
    					e.printStackTrace();
    				} finally {
    					if (innerStatement != null) {innerStatement.close();}
    				}
    			}
    		} catch (SQLException e) {
    			e.printStackTrace();
    		} finally {
    			if (statement != null) {statement.close();}
    		}
    	}
    }
    
    //Creates an order for any number of items. Numbers of each item required are passed as parameters.  Parameters are in alphabetical order.
    public void requestCustomOrder(Connection connection, int cheeseQ, int chickenfilletbreastsQ, int chickenpiecesQ, int chickenstripsQ, int colasyrupQ, int hashbrownsQ,
    	int mayonnaiseQ, int mycoproteinsouthernfriedburgerQ, int mycoproteinsouthernfriedstripsQ, int sesameseedbunsQ, int lettuceQ, int frenchfriesQ) throws SQLException {
    	
    	ArrayList<Integer> parameterList = new ArrayList<>();
    	parameterList.add(cheeseQ);parameterList.add(chickenfilletbreastsQ);parameterList.add(chickenpiecesQ);parameterList.add(chickenstripsQ);
    	parameterList.add(colasyrupQ);parameterList.add(hashbrownsQ);parameterList.add(mayonnaiseQ);parameterList.add(mycoproteinsouthernfriedburgerQ);
    	parameterList.add( mycoproteinsouthernfriedstripsQ);parameterList.add(sesameseedbunsQ);parameterList.add(lettuceQ);parameterList.add(frenchfriesQ);
    
    	
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
    		rs.next();
    		orderId = rs.getInt("orderId");
    	}catch (SQLException e ) {
    		e.printStackTrace();
    	} finally {
            if (statement != null) {statement.close();}
        } 
    	
    	// Add Order ID and restaurant address to orders table.
    	query = "INSERT INTO Orders (restaurantAddress, orderId) VALUES (?, ?)";
    	
    	try{			    				
    		PreparedStatement pstmt = connection.prepareStatement(query);
    		pstmt.setString(1, restaurantAddress);
    		pstmt.setInt(2, orderId); //same orderId as above
    		pstmt.executeUpdate();
    	}catch (SQLException e ) {
    		e.printStackTrace();
    	}
    	
    	//Getting stock items from Stock Table
    	statement = null;
    	query = "SELECT stockItem FROM Stock ORDER BY stockItem";
  
    	try {
    		statement = connection.createStatement();
    		ResultSet rs = statement.executeQuery(query);
    		int i =0;
    		while (rs.next()) {
    			String stockItem = rs.getString("stockItem");
    			
    			// Add specified quantities to Contains table.
    			String innerquery = "INSERT INTO Contains (orderId, quantity, stockItem) VALUES (?, ?, ?)";
    			try {
    				PreparedStatement pstmt = connection.prepareStatement(innerquery);
    				pstmt.setInt(1, orderId);
    				pstmt.setInt(2, parameterList.get(i));
    				pstmt.setString(3, stockItem);
    				pstmt.executeUpdate();
    				i=i+1;
    			}catch (SQLException e ) {
    				e.printStackTrace();
    			} 
    		}
    	} catch (SQLException e ) {
    		e.printStackTrace();
    	} finally {
    		if (statement != null) {statement.close();}
    	}  
    }
    
    
    
    public void requestStandardOrder(Connection connection) throws SQLException {
    	
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
    		rs.next();
    		orderId = rs.getInt("orderId");
    	}catch (SQLException e ) {
    		e.printStackTrace();
    	} finally {
            if (statement != null) {statement.close();}
        } 
    	
    	// Add Order ID and restaurant address to orders table.
    	query = "INSERT INTO Orders (restaurantAddress, orderId) VALUES (?, ?)";
    	
    	try{			    				
    		PreparedStatement pstmt = connection.prepareStatement(query);
    		pstmt.setString(1, restaurantAddress);
    		pstmt.setInt(2, orderId); //same orderId as above
    		pstmt.executeUpdate();
    	}catch (SQLException e ) {
    		e.printStackTrace();
    	}
	    			    		 
    	//Getting standard quantities from Stock Table
    	statement = null;
    	query = "SELECT stockItem, typicalUnitsOrdered FROM Stock"; 
    	//selecting all the stock for a standard order
    	try {
    		statement = connection.createStatement();
    		ResultSet rs = statement.executeQuery(query);
    		while (rs.next()) {
    			String stockItem = rs.getString("stockItem");
    			int typicalUnits = rs.getInt("typicalUnitsOrdered");
    			
    			// Add standard quantities to Contains table.
    			String innerquery = "INSERT INTO Contains (orderId, quantity, stockItem) VALUES (?, ?, ?)";
    			try {
    				PreparedStatement pstmt = connection.prepareStatement(innerquery);
    				pstmt.setInt(1, orderId);
    				pstmt.setInt(2, typicalUnits);
    				pstmt.setString(3, stockItem);
    				pstmt.executeUpdate();
    			}catch (SQLException e ) {
    				e.printStackTrace();
    			} 
    		}
    	} catch (SQLException e ) {
    		e.printStackTrace();
    	} finally {
    		if (statement != null) {statement.close();}
    	}  
    }
	    			    		   
    
    //Checks stock at restaurant is above the minimum stock level.
    public void minStockCheck(Connection connection) throws SQLException{
    	
    	Statement statement = null;
    	String query = "SELECT stockItem, quantity, minQuantity " +
    				   "FROM Within " +            
                       "WHERE restaurantAddress='"+restaurantAddress+"'";
                           
    	try {
    		statement = connection.createStatement();
    		ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                String stockItem = rs.getString("StockItem");
                int quantity = rs.getInt("quantity");
                int minQuantity = rs.getInt("minQuantity");

                if(quantity < minQuantity){
                	int deficit = minQuantity - quantity;
                	System.out.println("Current stock of "+ stockItem +" is below minimum stock levels by "+deficit+".");
                }    
            }
            } catch (SQLException e ) {
                e.printStackTrace();
            } finally {
                if (statement != null) {statement.close();}
            }                 
        }
        
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
}    