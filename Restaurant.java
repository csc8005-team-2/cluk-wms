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
    	
    	//TO DO: must fix orderId - change it from increment just to manually writing it! This is because we can then have one order of many ingredients, 
        //instead of many orders of each separate ingredient 
    	//then add Orders table in order to connect restaurant address
 		
    		Statement statement1 = null;
    		Statement statement2 = null;
    		Statement statement3 = null;
    		Statement statement4 = null;
    		Statement statement5 = null;
    		Statement statement6 = null;
    		Statement statement7 = null;
    		Statement statement8 = null;
    		Statement statement9 = null;
    		Statement statement10 = null;
    		Statement statement11 = null;
    		
	    	String query1 = "INSERT INTO Contains (orderId, stockItem, quantity) VALUES (?, ?, ?)";
	    	String query2 = "INSERT INTO Contains (orderId, stockItem, quantity) VALUES (?, ?, ?)";
	    	String query3 = "INSERT INTO Contains (orderId, stockItem, quantity) VALUES (?, ?, ?)";
	    	String query4 = "INSERT INTO Contains (orderId, stockItem, quantity) VALUES (?, ?, ?)";
	    	String query5 = "INSERT INTO Contains (orderId, stockItem, quantity) VALUES (?, ?, ?)";
	    	String query6 = "INSERT INTO Contains (orderId, stockItem, quantity) VALUES (?, ?, ?)";
	    	String query7 = "INSERT INTO Contains (orderId, stockItem, quantity) VALUES (?, ?, ?)";
	    	String query8 = "INSERT INTO Contains (orderId, stockItem, quantity) VALUES (?, ?, ?)";
	    	String query9 = "INSERT INTO Contains (orderId, stockItem, quantity) VALUES (?, ?, ?)";
	    	String query10 = "INSERT INTO Contains (orderId, stockItem, quantity) VALUES (?, ?, ?)";
	    	String query11 = "INSERT INTO Contains (orderId, stockItem, quantity) VALUES (?, ?, ?)";

	    	
	    	String[] queries = {"query1","query2","query3","query4","query5","query6","query7","query8","query9","query10","query11"};
	    	
	    	int orderId = 1;

//	    			for (String i: queries) {
//	    				if (i == "query1") {
	    		    	try{
	    		    	
    			    	    Statement statement02 = null;
    			    	    
    			    	    String query02 = "INSERT INTO Orders (restaurantAddress, orderId) VALUES (?, ?)";
    			    	    

    			    	    
    			    	    try{
    			    			statement02 = connection.createStatement();
    			    				
	    				         PreparedStatement pstmt02 = connection.prepareStatement(query02);
	    				         pstmt02.setString(1, restaurantAddress);
	    				         pstmt02.setInt(2, orderId);
	    				         pstmt02.executeUpdate();
	    				         
    			    	    } catch (SQLException e ) {
		    					e.printStackTrace();
		    				} finally {
		    					if (statement02 != null) {statement02.close();
		    					}
		    				}
	    		    		
	    		    		statement1 = connection.createStatement();
	    					
	    				    PreparedStatement pstmt1 = connection.prepareStatement(query1);
	    				    pstmt1.setInt(1, orderId); //orderId is manually entered
	    				    pstmt1.setString(2, "Shredded iceberg lettuce");
	    				    pstmt1.setInt(3,lettuceQ);
	    				    pstmt1.executeUpdate();
	    					
	    					} catch (SQLException e ) {
	    						e.printStackTrace();
	    					} finally {
	    						if (statement1 != null) {statement1.close();
	    						}
	    							
	    			    		Statement statement01 = null;
	    			    			
	    			    		java.util.Date orderDate = new java.util.Date();

	    			    		java.text.SimpleDateFormat sdf = 
	    			    			  new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	    			    		String currentTime = sdf.format(orderDate);
	    			    			
	    			    	    String query01 = "INSERT INTO StockOrders (orderId, orderDateTime, orderStatus) VALUES (?, ?, ?)";
	    			    	    	
	    			    	    try{
	    			    			statement01 = connection.createStatement();
	    			    				
		    				         PreparedStatement pstmt01 = connection.prepareStatement(query01);
		    				         pstmt01.setInt(1, orderId);
		    				         pstmt01.setString(2, currentTime);
		    				         pstmt01.setString(3,"Out for delivery"); //manual set to out for delivery
		    				         pstmt01.executeUpdate();
	    			    				
	    			    				} catch (SQLException e ) {
	    			    					e.printStackTrace();
	    			    				} finally {
	    			    					if (statement01 != null) {statement01.close();
	    			    					}
	    			    				}
	    					}
  }
	    			    	    

	    			    	    //perhaps make status go from pending to out for delivery using update query MySQL method
//}

//	    				} else if (i == "query2")  {
//	    					try {
//	    				
//	    					statement2 = connection.createStatement();
//	    					
//	    				    PreparedStatement pstmt2 = connection.prepareStatement(query2);
//	    				    pstmt2.setInt(1,1);
//	    				    pstmt2.setString(2, "Cheese slices");
//	    				    pstmt2.setInt(3,cheeseQ);
//	    				    pstmt2.executeUpdate();
//	    					
//	    					} catch (SQLException e ) {
//	    						e.printStackTrace();
//	    					} finally {
//	    						if (statement2 != null) {statement2.close();
//	    						}
//	    							
//	    			    		Statement statement02 = null;
//	    			    			
//	    			    		java.util.Date orderDate = new java.util.Date();
//
//	    			    		java.text.SimpleDateFormat sdf = 
//	    			    			  new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//	    			    		String currentTime = sdf.format(orderDate);
//	    			    			
//	    			    	    String query02 = "INSERT INTO StockOrders (orderDateTime, orderStatus) VALUES (?, ?)";
//	    			    	    	
//	    			    	    try{
//	    			    			statement02 = connection.createStatement();
//	    			    				
//		    				         PreparedStatement pstmt02 = connection.prepareStatement(query02);
//		    				         pstmt02.setString(1, currentTime);
//		    				         pstmt02.setString(2,"Out for delivery");
//		    				         pstmt02.executeUpdate();
//	    			    				
//	    			    				} catch (SQLException e ) {
//	    			    					e.printStackTrace();
//	    			    				} finally {
//	    			    					if (statement02 != null) {statement02.close();
//	    			    					}
//	    			    				}
//	    					}
//	    					} else if (i=="query3")  {
//		    					try {
//		    	    				
//			    					statement3 = connection.createStatement();
//			    					
//			    				    PreparedStatement pstmt3 = connection.prepareStatement(query3);
//			    				    pstmt3.setInt(1,1);
//			    				    pstmt3.setString(2, "Chicken Pieces");
//			    				    pstmt3.setInt(3,chickenpiecesQ);
//			    				    pstmt3.executeUpdate();
//			    					
//			    					} catch (SQLException e ) {
//			    						e.printStackTrace();
//			    					} finally {
//			    						if (statement3 != null) {statement3.close();
//			    						}
//			    							
//			    			    		Statement statement03 = null;
//			    			    			
//			    			    		java.util.Date orderDate = new java.util.Date();
//
//			    			    		java.text.SimpleDateFormat sdf = 
//			    			    			  new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//			    			    		String currentTime = sdf.format(orderDate);
//			    			    			
//			    			    	    String query03 = "INSERT INTO StockOrders (orderDateTime, orderStatus) VALUES (?, ?)";
//			    			    	    	
//			    			    	    try{
//			    			    			statement03 = connection.createStatement();
//			    			    				
//				    				         PreparedStatement pstmt03 = connection.prepareStatement(query03);
//				    				         pstmt03.setString(1, currentTime);
//				    				         pstmt03.setString(2,"Out for delivery");
//				    				         pstmt03.executeUpdate();
//			    			    				
//			    			    				} catch (SQLException e ) {
//			    			    					e.printStackTrace();
//			    			    				} finally {
//			    			    					if (statement03 != null) {statement03.close();
//			    			    					}
//			    			    				}
//			    					}
//			    		    	} else if (i=="query4")  {
//			    		    		
//			    					try {
//			    	    				
//				    					statement4 = connection.createStatement();
//				    					
//				    				    PreparedStatement pstmt4 = connection.prepareStatement(query4);
//				    				    pstmt4.setInt(1,1);
//				    				    pstmt4.setString(2, "Sesame Seed Buns");
//				    				    pstmt4.setInt(3,sesameseedbunsQ);
//				    				    pstmt4.executeUpdate();
//				    					
//				    					} catch (SQLException e ) {
//				    						e.printStackTrace();
//				    					} finally {
//				    						if (statement4 != null) {statement4.close();
//				    						}
//				    							
//				    			    		Statement statement04 = null;
//				    			    			
//				    			    		java.util.Date orderDate = new java.util.Date();
//
//				    			    		java.text.SimpleDateFormat sdf = 
//				    			    			  new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//				    			    		String currentTime = sdf.format(orderDate);
//				    			    			
//				    			    	    String query04 = "INSERT INTO StockOrders (orderDateTime, orderStatus) VALUES (?, ?)";
//				    			    	    	
//				    			    	    try{
//				    			    			statement04 = connection.createStatement();
//				    			    				
//					    				         PreparedStatement pstmt02 = connection.prepareStatement(query04);
//					    				         pstmt02.setString(1, currentTime);
//					    				         pstmt02.setString(2,"Out for delivery");
//					    				         pstmt02.executeUpdate();
//				    			    				
//				    			    				} catch (SQLException e ) {
//				    			    					e.printStackTrace();
//				    			    				} finally {
//				    			    					if (statement04 != null) {statement04.close();
//				    			    					}
//				    			    				}
//				    					}
//
//				    		    	} else if (i=="query5")  {
//
//				    					try {
//				    	    				
//					    					statement5 = connection.createStatement();
//					    					
//					    				    PreparedStatement pstmt5 = connection.prepareStatement(query5);
//					    				    pstmt5.setInt(1,1);
//					    				    pstmt5.setString(2, "Chicken strips");
//					    				    pstmt5.setInt(3,chickenstripsQ);
//					    				    pstmt5.executeUpdate();
//					    					
//					    					} catch (SQLException e ) {
//					    						e.printStackTrace();
//					    					} finally {
//					    						if (statement5 != null) {statement5.close();
//					    						}
//					    							
//					    			    		Statement statement05 = null;
//					    			    			
//					    			    		java.util.Date orderDate = new java.util.Date();
//
//					    			    		java.text.SimpleDateFormat sdf = 
//					    			    			  new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//					    			    		String currentTime = sdf.format(orderDate);
//					    			    			
//					    			    	    String query05 = "INSERT INTO StockOrders (orderDateTime, orderStatus) VALUES (?, ?)";
//					    			    	    	
//					    			    	    try{
//					    			    			statement05 = connection.createStatement();
//					    			    				
//						    				         PreparedStatement pstmt05 = connection.prepareStatement(query05);
//						    				         pstmt05.setString(1, currentTime);
//						    				         pstmt05.setString(2,"Out for delivery");
//						    				         pstmt05.executeUpdate();
//					    			    				
//					    			    				} catch (SQLException e ) {
//					    			    					e.printStackTrace();
//					    			    				} finally {
//					    			    					if (statement05 != null) {statement05.close();
//					    			    					}
//					    			    				}
//					    					}
//				    		    	
//					    		    	} else if (i=="query6")  {
//
//					    					try {
//					    	    				
//						    					statement6 = connection.createStatement();
//						    					
//						    				    PreparedStatement pstmt6 = connection.prepareStatement(query6);
//						    				    pstmt6.setInt(1,1);
//						    				    pstmt6.setString(2, "Mycoprotein based meat substitute Southern fried Strips");
//						    				    pstmt6.setInt(3,mycoproteinsouthernfriedstripsQ);
//						    				    pstmt6.executeUpdate();
//						    					
//						    					} catch (SQLException e ) {
//						    						e.printStackTrace();
//						    					} finally {
//						    						if (statement6 != null) {statement6.close();
//						    						}
//						    							
//						    			    		Statement statement06 = null;
//						    			    			
//						    			    		java.util.Date orderDate = new java.util.Date();
//
//						    			    		java.text.SimpleDateFormat sdf = 
//						    			    			  new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//						    			    		String currentTime = sdf.format(orderDate);
//						    			    			
//						    			    	    String query06 = "INSERT INTO StockOrders (orderDateTime, orderStatus) VALUES (?, ?)";
//						    			    	    	
//						    			    	    try{
//						    			    			statement06 = connection.createStatement();
//						    			    				
//							    				         PreparedStatement pstmt02 = connection.prepareStatement(query06);
//							    				         pstmt02.setString(1, currentTime);
//							    				         pstmt02.setString(2,"Out for delivery");
//							    				         pstmt02.executeUpdate();
//						    			    				
//						    			    				} catch (SQLException e ) {
//						    			    					e.printStackTrace();
//						    			    				} finally {
//						    			    					if (statement06 != null) {statement06.close();
//						    			    					}
//						    			    				}
//						    					}
//						    		    	} else if (i=="query7")  {
//						    					try {
//						    	    				
//							    					statement7 = connection.createStatement();
//							    					
//							    				    PreparedStatement pstmt7 = connection.prepareStatement(query7);
//							    				    pstmt7.setInt(1,1);
//							    				    pstmt7.setString(2, "Mycoprotein based meat substitute Southern fried burger");
//							    				    pstmt7.setInt(3,mycoproteinsouthernfriedburgerQ);
//							    				    pstmt7.executeUpdate();
//							    					
//							    					} catch (SQLException e ) {
//							    						e.printStackTrace();
//							    					} finally {
//							    						if (statement7 != null) {statement7.close();
//							    						}
//							    							
//							    			    		Statement statement07 = null;
//							    			    			
//							    			    		java.util.Date orderDate = new java.util.Date();
//
//							    			    		java.text.SimpleDateFormat sdf = 
//							    			    			  new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//							    			    		String currentTime = sdf.format(orderDate);
//							    			    			
//							    			    	    String query07 = "INSERT INTO StockOrders (orderDateTime, orderStatus) VALUES (?, ?)";
//							    			    	    	
//							    			    	    try{
//							    			    			statement07 = connection.createStatement();
//							    			    				
//								    				         PreparedStatement pstmt07 = connection.prepareStatement(query07);
//								    				         pstmt07.setString(1, currentTime);
//								    				         pstmt07.setString(2,"Out for delivery");
//								    				         pstmt07.executeUpdate();
//							    			    				
//							    			    				} catch (SQLException e ) {
//							    			    					e.printStackTrace();
//							    			    				} finally {
//							    			    					if (statement07 != null) {statement07.close();
//							    			    					}
//							    			    				}
//						    		    	}
//						    		    	} else if (i=="query8")  {
//
//						    					try {
//						    	    				
//							    					statement8 = connection.createStatement();
//							    					
//							    				    PreparedStatement pstmt8 = connection.prepareStatement(query8);
//							    				    pstmt8.setInt(1,1);
//							    				    pstmt8.setString(2, "Mayonnaise");
//							    				    pstmt8.setInt(3,mayonnaiseQ);
//							    				    pstmt8.executeUpdate();
//							    					
//							    					} catch (SQLException e ) {
//							    						e.printStackTrace();
//							    					} finally {
//							    						if (statement8 != null) {statement8.close();
//							    						}
//							    							
//							    			    		Statement statement08 = null;
//							    			    			
//							    			    		java.util.Date orderDate = new java.util.Date();
//
//							    			    		java.text.SimpleDateFormat sdf = 
//							    			    			  new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//							    			    		String currentTime = sdf.format(orderDate);
//							    			    			
//							    			    	    String query08 = "INSERT INTO StockOrders (orderDateTime, orderStatus) VALUES (?, ?)";
//							    			    	    	
//							    			    	    try{
//							    			    			statement08 = connection.createStatement();
//							    			    				
//								    				         PreparedStatement pstmt08 = connection.prepareStatement(query08);
//								    				         pstmt08.setString(1, currentTime);
//								    				         pstmt08.setString(2,"Out for delivery");
//								    				         pstmt08.executeUpdate();
//							    			    				
//							    			    				} catch (SQLException e ) {
//							    			    					e.printStackTrace();
//							    			    				} finally {
//							    			    					if (statement08 != null) {statement08.close();
//							    			    					}
//							    			    				}
//							    					}
//								    		    	} else if (i=="query9")  {
//
//								    					try {
//								    	    				
//									    					statement9 = connection.createStatement();
//									    					
//									    				    PreparedStatement pstmt9 = connection.prepareStatement(query9);
//									    				    pstmt9.setInt(1,1);
//									    				    pstmt9.setString(2, "Hash Browns");
//									    				    pstmt9.setInt(3,hashbrownsQ);
//									    				    pstmt9.executeUpdate();
//									    					
//									    					} catch (SQLException e ) {
//									    						e.printStackTrace();
//									    					} finally {
//									    						if (statement9 != null) {statement9.close();
//									    						}
//									    							
//									    			    		Statement statement09 = null;
//									    			    			
//									    			    		java.util.Date orderDate = new java.util.Date();
//
//									    			    		java.text.SimpleDateFormat sdf = 
//									    			    			  new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//									    			    		String currentTime = sdf.format(orderDate);
//									    			    			
//									    			    	    String query09 = "INSERT INTO StockOrders (orderDateTime, orderStatus) VALUES (?, ?)";
//									    			    	    	
//									    			    	    try{
//									    			    			statement09 = connection.createStatement();
//									    			    				
//										    				         PreparedStatement pstmt02 = connection.prepareStatement(query09);
//										    				         pstmt02.setString(1, currentTime);
//										    				         pstmt02.setString(2,"Out for delivery");
//										    				         pstmt02.executeUpdate();
//									    			    				
//									    			    				} catch (SQLException e ) {
//									    			    					e.printStackTrace();
//									    			    				} finally {
//									    			    					if (statement09 != null) {statement09.close();
//									    			    					}
//									    			    				}
//									    					}
//									    		    	} else if (i=="query10")  {
//
//									    					try {
//									    	    				
//										    					statement10 = connection.createStatement();
//										    					
//										    				    PreparedStatement pstmt10 = connection.prepareStatement(query10);
//										    				    pstmt10.setInt(1,1);
//										    				    pstmt10.setString(2, "Uncooked French Fries");
//										    				    pstmt10.setInt(3,frenchfriesQ);
//										    				    pstmt10.executeUpdate();
//										    					
//										    					} catch (SQLException e ) {
//										    						e.printStackTrace();
//										    					} finally {
//										    						if (statement10 != null) {statement10.close();
//										    						}
//										    							
//										    			    		Statement statement010 = null;
//										    			    			
//										    			    		java.util.Date orderDate = new java.util.Date();
//
//										    			    		java.text.SimpleDateFormat sdf = 
//										    			    			  new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//										    			    		String currentTime = sdf.format(orderDate);
//										    			    			
//										    			    	    String query010 = "INSERT INTO StockOrders (orderDateTime, orderStatus) VALUES (?, ?)";
//										    			    	    	
//										    			    	    try{
//										    			    			statement010 = connection.createStatement();
//										    			    				
//											    				         PreparedStatement pstmt010 = connection.prepareStatement(query010);
//											    				         pstmt010.setString(1, currentTime);
//											    				         pstmt010.setString(2,"Out for delivery");
//											    				         pstmt010.executeUpdate();
//										    			    				
//										    			    				} catch (SQLException e ) {
//										    			    					e.printStackTrace();
//										    			    				} finally {
//										    			    					if (statement010 != null) {statement010.close();
//										    			    					}
//										    			    				}
//										    					}
//										    		    	} else if (i=="query11")  {
//										    					try {
//										    	    				
//											    					statement2 = connection.createStatement();
//											    					
//											    				    PreparedStatement pstmt11 = connection.prepareStatement(query11);
//											    				    pstmt11.setInt(1,1);
//											    				    pstmt11.setString(2, "Cola syrup");
//											    				    pstmt11.setInt(3,colasyrupQ);
//											    				    pstmt11.executeUpdate();
//											    					
//											    					} catch (SQLException e ) {
//											    						e.printStackTrace();
//											    					} finally {
//											    						if (statement11 != null) {statement11.close();
//											    						}
//											    							
//											    			    		Statement statement011 = null;
//											    			    			
//											    			    		java.util.Date orderDate = new java.util.Date();
//
//											    			    		java.text.SimpleDateFormat sdf = 
//											    			    			  new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//											    			    		String currentTime = sdf.format(orderDate);
//											    			    			
//											    			    	    String query011 = "INSERT INTO StockOrders (orderDateTime, orderStatus) VALUES (?, ?)";
//											    			    	    	
//											    			    	    try{
//											    			    			statement011 = connection.createStatement();
//											    			    				
//												    				         PreparedStatement pstmt011 = connection.prepareStatement(query011);
//												    				         pstmt011.setString(1, currentTime);
//												    				         pstmt011.setString(2,"Out for delivery");
//												    				         pstmt011.executeUpdate();
//											    			    				
//											    			    				} catch (SQLException e ) {
//											    			    					e.printStackTrace();
//											    			    				} finally {
//											    			    					if (statement011 != null) {statement011.close();
//											    			    					}
//											    			    				}
//										    		    	}
 
  
//                                                                    } else  {
//											    		    		System.out.println("Ingredients have not been requested for an order");
//	    		    	}
//}
	    		    	
    



        
   
    
    	public void requestStandardOrder(Connection connection) throws SQLException {

    		
    		int cheeseQ = 10;
    		int lettuceQ = 10; 
    		int chickenpiecesQ = 40;
    		int sesameseedbunsQ = 44;
    		int chickenfilletbreastsQ = 40; 
    		int chickenstripsQ = 40;
    		int mycoproteinsouthernfriedstripsQ = 5;
    		int mycoproteinsouthernfriedburgerQ = 5; 
    		int mayonnaiseQ = 2; 
    		int hashbrownsQ = 16;
    		int frenchfriesQ = 60; 
    		int colasyrupQ = 3;
    		
    		Statement statement1 = null;
    		Statement statement2 = null;
    		Statement statement3 = null;
    		Statement statement4 = null;
    		Statement statement5 = null;
    		Statement statement6 = null;
    		Statement statement7 = null;
    		Statement statement8 = null;
    		Statement statement9 = null;
    		Statement statement10 = null;
    		Statement statement11 = null;
    		
	    	String query1 = "INSERT INTO Contains (stockItem, quantity) VALUES (?, ?)";
	    	String query2 = "INSERT INTO Contains (stockItem, quantity) VALUES (?, ?)";
	    	String query3 = "INSERT INTO Contains (stockItem, quantity) VALUES (?, ?)";
	    	String query4 = "INSERT INTO Contains (stockItem, quantity) VALUES (?, ?)";
	    	String query5 = "INSERT INTO Contains (stockItem, quantity) VALUES (?, ?)";
	    	String query6 = "INSERT INTO Contains (stockItem, quantity) VALUES (?, ?)";
	    	String query7 = "INSERT INTO Contains (stockItem, quantity) VALUES (?, ?)";
	    	String query8 = "INSERT INTO Contains (stockItem, quantity) VALUES (?, ?)";
	    	String query9 = "INSERT INTO Contains (stockItem, quantity) VALUES (?, ?)";
	    	String query10 = "INSERT INTO Contains (stockItem, quantity) VALUES (?, ?)";
	    	String query11 = "INSERT INTO Contains (stockItem, quantity) VALUES (?, ?)";
	    	
	    	String[] queries = {"query1","query2","query3","query4","query5","query6","query7","query8","query9","query10","query11"};

			for (String i: queries) {
				if (i == "query1") {
					try{
				
						statement1 = connection.createStatement();
				
			            PreparedStatement pstmt1 = connection.prepareStatement(query1);
			            pstmt1.setString(1, "Shredded iceberg lettuce");
			            pstmt1.setInt(2,lettuceQ);
			            pstmt1.executeUpdate();
				

					} catch (SQLException e ) {
						e.printStackTrace();
						} finally {
							if (statement1 != null) {statement1.close();
							}	
					}
						
		    		Statement statement01 = null;
		    			
		    		java.util.Date orderDate = new java.util.Date();

		    		java.text.SimpleDateFormat sdf = 
		    			  new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		    		String currentTime = sdf.format(orderDate);
		    			
		    	    String query01 = "INSERT INTO StockOrders (orderDateTime, orderStatus) VALUES (?, ?)";
		    	    	
		    	    try{
		    			statement01 = connection.createStatement();
		    				
				         PreparedStatement pstmt01 = connection.prepareStatement(query01);
				         pstmt01.setString(1, currentTime);
				         pstmt01.setString(2,"Out for delivery");
				         pstmt01.executeUpdate();
		    				
		    				} catch (SQLException e ) {
		    					e.printStackTrace();
		    				} finally {
		    					if (statement01 != null) {statement01.close();
		    					}
		    				}
		    				} else if (i=="query2") {
		    					try {
		    	    				
			    					statement2 = connection.createStatement();
			    					
			    				    PreparedStatement pstmt2 = connection.prepareStatement(query2);
			    				    pstmt2.setString(1, "Cheese slices");
			    				    pstmt2.setInt(2,cheeseQ);
			    				    pstmt2.executeUpdate();
			    					
			    					} catch (SQLException e ) {
			    						e.printStackTrace();
			    					} finally {
			    						if (statement2 != null) {statement2.close();
			    						}
			    							
			    			    		Statement statement02 = null;
			    			    			
			    			    		java.util.Date orderDate = new java.util.Date();

			    			    		java.text.SimpleDateFormat sdf = 
			    			    			  new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			    			    		String currentTime = sdf.format(orderDate);
			    			    			
			    			    	    String query02 = "INSERT INTO StockOrders (orderDateTime, orderStatus) VALUES (?, ?)";
			    			    	    	
			    			    	    try{
			    			    			statement02 = connection.createStatement();
			    			    				
				    				         PreparedStatement pstmt02 = connection.prepareStatement(query02);
				    				         pstmt02.setString(1, currentTime);
				    				         pstmt02.setString(2,"Out for delivery");
				    				         pstmt02.executeUpdate();
			    			    				
			    			    				} catch (SQLException e ) {
			    			    					e.printStackTrace();
			    			    				} finally {
			    			    					if (statement02 != null) {statement02.close();
			    			    					}
			    			    				}
			    					}
			    					} else if (i=="query3") {
				    					try {
				    	    				
					    					statement3 = connection.createStatement();
					    					
					    				    PreparedStatement pstmt3 = connection.prepareStatement(query3);
					    				    pstmt3.setString(1, "Chicken Pieces");
					    				    pstmt3.setInt(2,chickenpiecesQ);
					    				    pstmt3.executeUpdate();
					    					
					    					} catch (SQLException e ) {
					    						e.printStackTrace();
					    					} finally {
					    						if (statement3 != null) {statement3.close();
					    						}
					    							
					    			    		Statement statement03 = null;
					    			    			
					    			    		java.util.Date orderDate = new java.util.Date();

					    			    		java.text.SimpleDateFormat sdf = 
					    			    			  new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

					    			    		String currentTime = sdf.format(orderDate);
					    			    			
					    			    	    String query03 = "INSERT INTO StockOrders (orderDateTime, orderStatus) VALUES (?, ?)";
					    			    	    	
					    			    	    try{
					    			    			statement03 = connection.createStatement();
					    			    				
						    				         PreparedStatement pstmt03 = connection.prepareStatement(query03);
						    				         pstmt03.setString(1, currentTime);
						    				         pstmt03.setString(2,"Out for delivery");
						    				         pstmt03.executeUpdate();
					    			    				
					    			    				} catch (SQLException e ) {
					    			    					e.printStackTrace();
					    			    				} finally {
					    			    					if (statement03 != null) {statement03.close();
					    			    					}
					    			    				}
					    					}
				    					
			    					} else if (i=="query4") {
				    					try {
				    	    				
					    					statement4 = connection.createStatement();
					    					
					    				    PreparedStatement pstmt4 = connection.prepareStatement(query4);
					    				    pstmt4.setString(1, "Sesame Seed Buns");
					    				    pstmt4.setInt(2,sesameseedbunsQ);
					    				    pstmt4.executeUpdate();
					    					
					    					} catch (SQLException e ) {
					    						e.printStackTrace();
					    					} finally {
					    						if (statement4 != null) {statement4.close();
					    						}
					    							
					    			    		Statement statement04 = null;
					    			    			
					    			    		java.util.Date orderDate = new java.util.Date();

					    			    		java.text.SimpleDateFormat sdf = 
					    			    			  new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

					    			    		String currentTime = sdf.format(orderDate);
					    			    			
					    			    	    String query04 = "INSERT INTO StockOrders (orderDateTime, orderStatus) VALUES (?, ?)";
					    			    	    	
					    			    	    try{
					    			    			statement04 = connection.createStatement();
					    			    				
						    				         PreparedStatement pstmt02 = connection.prepareStatement(query04);
						    				         pstmt02.setString(1, currentTime);
						    				         pstmt02.setString(2,"Out for delivery");
						    				         pstmt02.executeUpdate();
					    			    				
					    			    				} catch (SQLException e ) {
					    			    					e.printStackTrace();
					    			    				} finally {
					    			    					if (statement04 != null) {statement04.close();
					    			    					}
					    			    				}
					    					}
					    					} else if (i == "query5") {

						    					try {
						    	    				
							    					statement5 = connection.createStatement();
							    					
							    				    PreparedStatement pstmt5 = connection.prepareStatement(query5);
							    				    pstmt5.setString(1, "Chicken strips");
							    				    pstmt5.setInt(2,chickenstripsQ);
							    				    pstmt5.executeUpdate();
							    					
							    					} catch (SQLException e ) {
							    						e.printStackTrace();
							    					} finally {
							    						if (statement5 != null) {statement5.close();
							    						}
							    							
							    			    		Statement statement05 = null;
							    			    			
							    			    		java.util.Date orderDate = new java.util.Date();

							    			    		java.text.SimpleDateFormat sdf = 
							    			    			  new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

							    			    		String currentTime = sdf.format(orderDate);
							    			    			
							    			    	    String query05 = "INSERT INTO StockOrders (orderDateTime, orderStatus) VALUES (?, ?)";
							    			    	    	
							    			    	    try{
							    			    			statement05 = connection.createStatement();
							    			    				
								    				         PreparedStatement pstmt05 = connection.prepareStatement(query05);
								    				         pstmt05.setString(1, currentTime);
								    				         pstmt05.setString(2,"Out for delivery");
								    				         pstmt05.executeUpdate();
							    			    				
							    			    				} catch (SQLException e ) {
							    			    					e.printStackTrace();
							    			    				} finally {
							    			    					if (statement05 != null) {statement05.close();
							    			    					}
							    			    				}
							    					}
							    					} else if ( i == "query6") {

								    					try {
								    	    				
									    					statement6 = connection.createStatement();
									    					
									    				    PreparedStatement pstmt6 = connection.prepareStatement(query6);
									    				    pstmt6.setString(1, "Mycoprotein based meat substitute Southern fried Strips");
									    				    pstmt6.setInt(2,mycoproteinsouthernfriedstripsQ);
									    				    pstmt6.executeUpdate();
									    					
									    					} catch (SQLException e ) {
									    						e.printStackTrace();
									    					} finally {
									    						if (statement6 != null) {statement6.close();
									    						}
									    							
									    			    		Statement statement06 = null;
									    			    			
									    			    		java.util.Date orderDate = new java.util.Date();

									    			    		java.text.SimpleDateFormat sdf = 
									    			    			  new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

									    			    		String currentTime = sdf.format(orderDate);
									    			    			
									    			    	    String query06 = "INSERT INTO StockOrders (orderDateTime, orderStatus) VALUES (?, ?)";
									    			    	    	
									    			    	    try{
									    			    			statement06 = connection.createStatement();
									    			    				
										    				         PreparedStatement pstmt02 = connection.prepareStatement(query06);
										    				         pstmt02.setString(1, currentTime);
										    				         pstmt02.setString(2,"Out for delivery");
										    				         pstmt02.executeUpdate();
									    			    				
									    			    				} catch (SQLException e ) {
									    			    					e.printStackTrace();
									    			    				} finally {
									    			    					if (statement06 != null) {statement06.close();
									    			    					}
									    			    				}
									    					}
									    					} else if (i == "query7") {
										    					try {
										    	    				
											    					statement7 = connection.createStatement();
											    					
											    				    PreparedStatement pstmt7 = connection.prepareStatement(query7);
											    				    pstmt7.setString(1, "Mycoprotein based meat substitute Southern fried burger");
											    				    pstmt7.setInt(2,mycoproteinsouthernfriedburgerQ);
											    				    pstmt7.executeUpdate();
											    					
											    					} catch (SQLException e ) {
											    						e.printStackTrace();
											    					} finally {
											    						if (statement7 != null) {statement7.close();
											    						}
											    							
											    			    		Statement statement07 = null;
											    			    			
											    			    		java.util.Date orderDate = new java.util.Date();

											    			    		java.text.SimpleDateFormat sdf = 
											    			    			  new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

											    			    		String currentTime = sdf.format(orderDate);
											    			    			
											    			    	    String query07 = "INSERT INTO StockOrders (orderDateTime, orderStatus) VALUES (?, ?)";
											    			    	    	
											    			    	    try{
											    			    			statement07 = connection.createStatement();
											    			    				
												    				         PreparedStatement pstmt07 = connection.prepareStatement(query07);
												    				         pstmt07.setString(1, currentTime);
												    				         pstmt07.setString(2,"Out for delivery");
												    				         pstmt07.executeUpdate();
											    			    				
											    			    				} catch (SQLException e ) {
											    			    					e.printStackTrace();
											    			    				} finally {
											    			    					if (statement07 != null) {statement07.close();
											    			    					}
											    			    				}
										    		    	}
									    					} else if (i == "query8") {
										    					try {
										    	    				
											    					statement8 = connection.createStatement();
											    					
											    				    PreparedStatement pstmt8 = connection.prepareStatement(query8);
											    				    pstmt8.setString(1, "Mayonnaise");
											    				    pstmt8.setInt(2,mayonnaiseQ);
											    				    pstmt8.executeUpdate();
											    					
											    					} catch (SQLException e ) {
											    						e.printStackTrace();
											    					} finally {
											    						if (statement8 != null) {statement8.close();
											    						}
											    							
											    			    		Statement statement08 = null;
											    			    			
											    			    		java.util.Date orderDate = new java.util.Date();

											    			    		java.text.SimpleDateFormat sdf = 
											    			    			  new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

											    			    		String currentTime = sdf.format(orderDate);
											    			    			
											    			    	    String query08 = "INSERT INTO StockOrders (orderDateTime, orderStatus) VALUES (?, ?)";
											    			    	    	
											    			    	    try{
											    			    			statement08 = connection.createStatement();
											    			    				
												    				         PreparedStatement pstmt08 = connection.prepareStatement(query08);
												    				         pstmt08.setString(1, currentTime);
												    				         pstmt08.setString(2,"Out for delivery");
												    				         pstmt08.executeUpdate();
											    			    				
											    			    				} catch (SQLException e ) {
											    			    					e.printStackTrace();
											    			    				} finally {
											    			    					if (statement08 != null) {statement08.close();
											    			    					}
											    			    				}
											    					}
										    					
									    					} else if (i == "query9") {
									    						try {
										    	    				
											    					statement9 = connection.createStatement();
											    					
											    				    PreparedStatement pstmt9 = connection.prepareStatement(query9);
											    				    pstmt9.setString(1, "Hash Browns");
											    				    pstmt9.setInt(2,hashbrownsQ);
											    				    pstmt9.executeUpdate();
											    					
											    					} catch (SQLException e ) {
											    						e.printStackTrace();
											    					} finally {
											    						if (statement9 != null) {statement9.close();
											    						}
											    							
											    			    		Statement statement09 = null;
											    			    			
											    			    		java.util.Date orderDate = new java.util.Date();

											    			    		java.text.SimpleDateFormat sdf = 
											    			    			  new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

											    			    		String currentTime = sdf.format(orderDate);
											    			    			
											    			    	    String query09 = "INSERT INTO StockOrders (orderDateTime, orderStatus) VALUES (?, ?)";
											    			    	    	
											    			    	    try{
											    			    			statement09 = connection.createStatement();
											    			    				
												    				         PreparedStatement pstmt02 = connection.prepareStatement(query09);
												    				         pstmt02.setString(1, currentTime);
												    				         pstmt02.setString(2,"Out for delivery");
												    				         pstmt02.executeUpdate();
											    			    				
											    			    				} catch (SQLException e ) {
											    			    					e.printStackTrace();
											    			    				} finally {
											    			    					if (statement09 != null) {statement09.close();
											    			    					}
											    			    				}
											    					}
											    		    	} else if (i=="query10")  {

											    					try {
											    	    				
												    					statement10 = connection.createStatement();
												    					
												    				    PreparedStatement pstmt10 = connection.prepareStatement(query10);
												    				    pstmt10.setString(1, "Uncooked French Fries");
												    				    pstmt10.setInt(2,frenchfriesQ);
												    				    pstmt10.executeUpdate();
												    					
												    					} catch (SQLException e ) {
												    						e.printStackTrace();
												    					} finally {
												    						if (statement10 != null) {statement10.close();
												    						}
												    							
												    			    		Statement statement010 = null;
												    			    			
												    			    		java.util.Date orderDate = new java.util.Date();

												    			    		java.text.SimpleDateFormat sdf = 
												    			    			  new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

												    			    		String currentTime = sdf.format(orderDate);
												    			    			
												    			    	    String query010 = "INSERT INTO StockOrders (orderDateTime, orderStatus) VALUES (?, ?)";
												    			    	    	
												    			    	    try{
												    			    			statement010 = connection.createStatement();
												    			    				
													    				         PreparedStatement pstmt010 = connection.prepareStatement(query010);
													    				         pstmt010.setString(1, currentTime);
													    				         pstmt010.setString(2,"Out for delivery");
													    				         pstmt010.executeUpdate();
												    			    				
												    			    				} catch (SQLException e ) {
												    			    					e.printStackTrace();
												    			    				} finally {
												    			    					if (statement010 != null) {statement010.close();
												    			    					}
												    			    				}
												    					}
												    		    	} else if (i=="query11")  {
												    					try {
												    	    				
													    					statement2 = connection.createStatement();
													    					
													    				    PreparedStatement pstmt11 = connection.prepareStatement(query11);
													    				    pstmt11.setString(1, "Cola syrup");
													    				    pstmt11.setInt(2,colasyrupQ);
													    				    pstmt11.executeUpdate();
													    					
													    					} catch (SQLException e ) {
													    						e.printStackTrace();
													    					} finally {
													    						if (statement11 != null) {statement11.close();
													    						}
													    							
													    			    		Statement statement011 = null;
													    			    			
													    			    		java.util.Date orderDate = new java.util.Date();

													    			    		java.text.SimpleDateFormat sdf = 
													    			    			  new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

													    			    		String currentTime = sdf.format(orderDate);
													    			    			
													    			    	    String query011 = "INSERT INTO StockOrders (orderDateTime, orderStatus) VALUES (?, ?)";
													    			    	    	
													    			    	    try{
													    			    			statement011 = connection.createStatement();
													    			    				
														    				         PreparedStatement pstmt011 = connection.prepareStatement(query011);
														    				         pstmt011.setString(1, currentTime);
														    				         pstmt011.setString(2,"Out for delivery");
														    				         pstmt011.executeUpdate();
													    			    				
													    			    				} catch (SQLException e ) {
													    			    					e.printStackTrace();
													    			    				} finally {
													    			    					if (statement011 != null) {statement011.close();
													    			    					}
													    			    				}
												    		    	}
												    					} else  {
													    		    		System.out.println("Ingredients have not been requested for an order");
												    					}
			}
    	}


    	
    	
    	public void minStockCheck(Connection connection) throws SQLException
        {
            Statement statement = null;
            String query = "SELECT stockItem, quantity " +
                           "FROM Within " +            
                           "WHERE restaurantAddress='"+restaurantAddress+"'";
                           
            try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                String StockItem = rs.getString("StockItem");
                int Quantity = rs.getInt("quantity");
                
                ArrayList<String> belowStock = new ArrayList<String>();
                int min=0;
                if(StockItem.equals("Cheese Slices")){min = 100;}
                else if(StockItem.equals("Chicken Breast Fillets")){min = 100;}
                else if(StockItem.equals("Chicken Pieces")){min = 100;}
                else if(StockItem.equals("Chicken strips")){min = 100;}
                else if(StockItem.equals("Cola syrup")){min = 100;}
                else if(StockItem.equals("Hash Browns")){min = 100;}
                else if(StockItem.equals("Mayonnaise")){min = 100;}
                else if(StockItem.equals("Mycoprotein based meat substitute Southern fried burger")){min = 100;}
                else if(StockItem.equals("Mycoprotein based meat substitute Southern fried Strips")){min = 100;}
                else if(StockItem.equals("Sesame Seed Buns")){min = 100;}
                else if(StockItem.equals("Shredded iceberg lettuce")){min = 100;}
                else if(StockItem.equals("Uncooked French Fries")){min = 100;}
                 
                if(Quantity < min){
                	int deficit = min - Quantity;
                	System.out.println("Current stock of "+ StockItem +" is below minimum stock levels by "+deficit+".");
                	belowStock.add(StockItem);
                }
                 
                
//                 for (int i =0; i<belowStock.size();i++){
//                	this.createCustomOrder(connection, belowStock.get(i), 100);
//                }
                
            }
                } catch (SQLException e ) {
                e.printStackTrace();
            } finally {
                if (statement != null) {statement.close();}                 
            }
        }
   
                
        //create meals from stock using name of ingredient and meal id (meal name) from ToMake
    	//add to meals table mealid, datetime, price
    	//then use works_with to connect to restaurant
    	//then solve with within to do min stock check etc

//    	public void createStockMeals(Connection connection) {
//    	String customerOrder = "";
//		
//		if (customerOrder == "3 Boneless Southern Fried Chicken Strips") {
//			try{
//				Statement statement1 = null;
//				String query1 = "INSERT INTO ToMake (name, mealId) VALUES (?, ?)";
//				statement1 = connection.createStatement();
//
//			    PreparedStatement pstmt1 = connection.prepareStatement(query1);
//			    pstmt1.setString(1, "3 Boneless Southern Fried Chicken Strips");
//			    pstmt1.setString (2, "Chicken strips");
//
//			    pstmt1.executeUpdate();
//			    
//			} catch (SQLException e ) {
//				e.printStackTrace();
//			} finally {
//				if (statement1 != null) {statement1.close();
//				}
//			}  
//			
//			Statement statement2 = null;
//			
//    		java.util.Date orderDate = new java.util.Date();
//
//    		java.text.SimpleDateFormat sdf = 
//    			  new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//    		String currentTime = sdf.format(orderDate);
//    			
//    	    String query2 = "INSERT INTO Meals (mealId, dateTime, price) VALUES (?, ?)";
//    	    	
//    	    try{
//    			statement2 = connection.createStatement();
//    				
//		         PreparedStatement pstmt2 = connection.prepareStatement(query2);
//		         pstmt2.setString(1, "3 Boneless Southern Fried Chicken Strips");
//		         pstmt2.setString(2,currentTime);
//		         pstmt2.setDouble(3, 2.5);
//		         pstmt2.executeUpdate();
//    				
//    				} catch (SQLException e ) {
//    					e.printStackTrace();
//    				} finally {
//    					if (statement2 != null) {statement2.close();
//    					}
//    				}
//    	    
//    	    Statement statement3 = null;
//    	    //change quantity within some table? Ingredients table 'amount'?
//    	    
//    	    
//		}
//    	}

        		
        


    	
    	//write methods for stockCreateMeals:
    	//-different method for each meal 
    	//-write which meal has been ordered, then take off ingredients quantities which it uses to make it
    	//-would need method for if the ingredients are close to running out, need to restock 
    	//3 Pieces of Southern Fried Chicken £2.50 Random mix of Chicken thigh, drumstick and wing pieces. 


    	// 3 Boneless Southern Fried Chicken Strips £2.50 Strips of Boneless Southern Fried Chicken 

    	// CLUK Burger £3.50 Sesame seed bun, Boneless Southern Fried chicken breast fillet, mayonnaise and lettuce (5 grams). 

    	// CLUK Super Burger £4.50 Sesame seed bun, Boneless Southern Fried chicken breast fillet, mayonnaise (10ml), cheese, hash brown and lettuce (5 grams). 

    	// 3 Vegetarian Southern Fried Strips £2.50 Mycoprotein based meat substitute Southern fried Strips. 

    	// CLUK Vegetarian Burger £3.50 Sesame seed bun, Mycoprotein based meat substitute Southern fried burger, mayonnaise and lettuce. 

    	// CLUK Vegetarian Super Burger £4.50 Sesame seed bun, Mycoprotein based meat substitute Southern fried burger, mayonnaise (10ml), cheese, hash brown and lettuce (5 grams). 

    	// Fries £0.99 120g serving of cooked fries. 

    	// Cola £0.99 750ml serving. Contains approximately 50ml of Cola syrup.
    }
