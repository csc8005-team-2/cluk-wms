import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class Restaurant {


    private String restaurantAddress;

    public Restaurant() {
        restaurantAddress = "";
       
    }

    public Restaurant(String name) {
        this.restaurantAddress = name;
    }

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
            if (statement != null) {
                statement.close();
            }
        }
    }

 
	//this method updates the stock for each restaurant when it has received an order 
  //  public void receiveOrder(Connection connection, String stockItem, int quantity, String restaurantAddress) throws SQLException {
    	
      public void receiveOrder(Connection connection, int orderId) throws SQLException {
    	
    	//check order id for the current restaurant by checking order table - if not right restaurant dont do it 
    	//see whats in the contains table for that order id
    	//add that stock to the within table for the restaurant
    	  
    	  boolean restaurantRecievesOrder = false;
    	  
    	  if (restaurantRecievesOrder == true ) {
    	  Statement statement1 = null;
    	  
    	  String query1 = "SELECT orderId, restaurantAddress FROM Orders WHERE restaurantAddress = '"+restaurantAddress+"'";
    	  
    	  try {
    		  statement1 = connection.createStatement();
    		  ResultSet rs = statement1.executeQuery(query1);
              while (rs.next()) {
                  int OrderId = rs.getInt("OrderId");
                  String RestaurantAddress = rs.getString("RestaurantAddress");
                  System.out.println(OrderId + "\t" + RestaurantAddress + "\n");
              
  
                  	Statement statement2 = null; 
 
                	String query2 = "SELECT stockItem, quantity FROM Contains WHERE orderId ='" +orderId+"'"; //this is the same restaurant order


                	
                	   try {
                	      	statement2 = connection.createStatement();
                	      	
                	        ResultSet rs2 = statement2.executeQuery(query2);
                	              
                	        rs2.next();
                	        int quantityToAdd = rs2.getInt("quantity");
                	        String StockItem = rs2.getString("stock");
                	        System.out.println("New order of "+ StockItem + ": " + quantityToAdd);

                	   
    	  //then update with Within to add this new Quantity to our original full quantity of stock 
                	   
                	   Statement statement3 = null;
                	   String query3 = "SELECT stockItem, quantity FROM Within WHERE restaurantAddress ='" +restaurantAddress+"'"; //amount of stock before this next order is added in
                	
                	   try {
                	      	statement3 = connection.createStatement();
                	      	
                	        ResultSet rs3 = statement3.executeQuery(query3);
                	              
                	        rs3.next();
                	        
                	        int previousQuantity = rs3.getInt("quantity");
                	        StockItem = rs3.getString("stock");
                	        System.out.println("Previous stock quantity: " + previousQuantity + " for stock: " + StockItem);

                	   
                	   
                	   Statement statement4 = null;
                	   int newQuantity = quantityToAdd + previousQuantity;
                	   
                	   String query4 = "UPDATE Within SET quantity ='" +newQuantity+"' WHERE stockItem = " + StockItem;
                	   
                     try {
                     statement4 = connection.createStatement();
                     statement4.executeUpdate(query4);
                     System.out.println("Updated stock of " + StockItem + ": " + newQuantity + " at restaurant: " + restaurantAddress);
                	   
                	   
                       } catch (SQLException e) {
                          	
                           e.printStackTrace();
                       } finally {
                           if (statement4 != null) {
                               statement4.close();
                           }
                       }
                       } catch (SQLException e) {
                          	
                           e.printStackTrace();
                       } finally {
                           if (statement3 != null) {
                               statement3.close();
                           }
                       }
              } catch (SQLException e) {
                 	
                  e.printStackTrace();
              } finally {
                  if (statement2 != null) {
                      statement2.close();
                  }
              }
              }
          } catch (SQLException e) {
             	
              e.printStackTrace();
          } finally {
              if (statement1 != null) {
                  statement1.close();
              }
          }
          } else {
        	  System.out.println("Restaurant has not recieved an order");
          }
	      
	      
	      
       
    //START OF ORDER METHODS:
	
	
//    public void requestCustomOrder(Connection connection, int lettuceQ, int cheeseQ, int chickenpiecesQ, int sesameseedbunsQ, int chickenfilletbreastsQ, 
//    		int chickenstripsQ, int mycoproteinsouthernfriedstripsQ, int mycoproteinsouthernfriedburgerQ, int mayonnaiseQ, int hashbrownsQ, 
//    		int frenchfriesQ, int colasyrupQ) throws SQLException {
    	
  public void requestCustomOrder(Connection connection, int lettuceQ) throws SQLException {

    Statement statement01 = null;
			    			
    		java.util.Date orderDate = new java.util.Date();

    		java.text.SimpleDateFormat sdf = 
    			   new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    		String currentTime = sdf.format(orderDate);

    			    			
    		 String query01 = "INSERT INTO StockOrders (orderDateTime, orderStatus) VALUES (?, ?)";
    			    	    	
    		try{
    			    statement01 = connection.createStatement();
    			    
    			    PreparedStatement pstmt01 = connection.prepareStatement(query01);
	    		    //auto-increment orderId
	    			pstmt01.setString(1, currentTime);
	    			pstmt01.setString(2,"Out for delivery"); //manual set to out for delivery
	    			pstmt01.executeUpdate();
	

    			    Statement statement02 = null;
    			    
    			   			
    			   String query02 = "SELECT orderId FROM StockOrders WHERE orderDateTime =" + currentTime; 
			//error- not correct syntax. 
			//stops working here

    			    	  try { 
    			    	   statement02 = connection.createStatement();
    			    	   ResultSet rs02 = statement02.executeQuery(query02);
    			    	        while (rs02.next()) {
    			    	           int OrderId = rs02.getInt("orderId");
    			    	           currentTime = rs02.getString("orderDateTime");
    			    	           System.out.println(OrderId + "\t" + currentTime + "\n");
   

    			    	Statement statement03 = null;
    		    			    	    
    		    		 String query03 = "INSERT INTO Orders (restaurantAddress, orderId) VALUES (?, ?)";
    		    			    	    
    		    		try{
    		    			 statement03 = connection.createStatement();
    		    			    				
    			    		 PreparedStatement pstmt03 = connection.prepareStatement(query03);
    			    		 pstmt03.setString(1, restaurantAddress);
    			    	     pstmt03.setInt(2, OrderId); //same orderId as above
    			    		 pstmt03.executeUpdate();

    			    		 
    			    		 
    			    Statement statement1 = null;
    			    String query1 = "INSERT INTO Contains (orderId, stockItem, quantity) VALUES (?, ?, ?)";
    			    
    			    try {
 	    		    statement1 = connection.createStatement();
	    					
 	    			PreparedStatement pstmt1 = connection.prepareStatement(query1);
 	    			pstmt1.setInt(1, OrderId); //orderId same as above
 	    			pstmt1.setString(2, "Shredded iceberg lettuce");
 	    			pstmt1.setInt(3,lettuceQ);
 	    			pstmt1.executeUpdate();
 					} catch (SQLException e ) {
						e.printStackTrace();
					} finally {
						if (statement1 != null) {statement1.close();
						}
					}
		    			} catch (SQLException e ) {
				    		e.printStackTrace();
				    	} finally {
				    		if (statement03 != null) {statement03.close();
				    		}
				    	}
    			    	        }
   		   			    	   } catch (SQLException e) {
				    	        	
					    	       e.printStackTrace();
					    	   } finally {
					    	        if (statement02 != null) {
					    	           statement02.close();
					    	       }
					    	   }
    		} catch (SQLException e ) {
    			e.printStackTrace();
    		} finally {
    			if (statement01 != null) {statement01.close();
    			}
    		}

  }
