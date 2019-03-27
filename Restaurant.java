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


	//this method updates the stock for each restaurant when it has received an order - basically it updates the Within table
      public void receiveOrder(Connection connection, int orderId) throws SQLException {

    	  Statement statement1 = null;
    	  
    	  String query1 = "SELECT orderId, restaurantAddress FROM Orders WHERE restaurantAddress = '"+restaurantAddress+"'" + 
    	  "AND orderId = '" +orderId+"'";
    	  
    	  try {
    		  statement1 = connection.createStatement();
    		  ResultSet rs = statement1.executeQuery(query1);
                  while (rs.next()) {
                  int OrderId = rs.getInt("OrderId");
                  String RestaurantAddress = rs.getString("RestaurantAddress");
                  System.out.println(OrderId + "\t" + RestaurantAddress + "\n");
              
  
                  	Statement statement2 = null; 
 
                	String query2 = "SELECT stockItem, quantity FROM Contains WHERE orderId ='" +orderId+"'";

                	   try {
                	      	statement2 = connection.createStatement();
                	      	
                	        ResultSet rs2 = statement2.executeQuery(query2);
                	              
                	        while (rs2.next()) {
                	        int quantityToAdd = rs2.getInt("quantity");
                	        String StockItem = rs2.getString("stockItem");
                	        System.out.println("New order of "+ StockItem + ": " + quantityToAdd);

                	   
    	               //then update Within table to add this new Quantity to our original full quantity of stock 
                	   //Within must not be empty before doing this method aka have an address, stock item and quantity of decimal entered into it beforehand
                	   
                	   Statement statement3 = null;
                	   String query3 = "SELECT stockItem, quantity FROM Within WHERE restaurantAddress ='" +restaurantAddress+"'"; //amount of stock before this next order is added in
                	
                	   try {
                	      	statement3 = connection.createStatement();
                	      	
                	        ResultSet rs3 = statement3.executeQuery(query3);
                	              
                	        rs3.next();
                	        
                	        int previousQuantity = rs3.getInt("quantity");
                	        StockItem = rs3.getString("stockItem");
                	        System.out.println("Previous stock quantity: " + previousQuantity + " for stock: " + StockItem);
                	   
                	   Statement statement4 = null;
                	   int newQuantity = quantityToAdd + previousQuantity;
                	   
                	   String query4 = "UPDATE Within SET quantity ='" +newQuantity+"'" + "WHERE stockItem ='" +StockItem+"'";
                	   
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
    	  }

    
    public void requestCustomOrder(Connection connection, int lettuceQ, int cheeseQ, int chickenpiecesQ, int sesameseedbunsQ, int chickenfilletbreastsQ, 
    		int chickenstripsQ, int mycoproteinsouthernfriedstripsQ, int mycoproteinsouthernfriedburgerQ, int mayonnaiseQ, int hashbrownsQ, 
    		int frenchfriesQ, int colasyrupQ) throws SQLException {
    	

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
    			    
    			   			
    			   String query02 = "SELECT orderId FROM StockOrders WHERE orderDateTime ='" +currentTime+"'"; 

    			    	  try { 
    			    	   statement02 = connection.createStatement();
    			    	   ResultSet rs02 = statement02.executeQuery(query02);
    			    	        while (rs02.next()) {
    			    	           int OrderId = rs02.getInt("orderId");
   

    			    	Statement statement03 = null;
    		    			    	    
    		    		 String query03 = "INSERT INTO Orders (restaurantAddress, orderId) VALUES (?, ?)";
    		    			    	    
    		    		try{
    		    			 statement03 = connection.createStatement();
    		    			    				
    			    		 PreparedStatement pstmt03 = connection.prepareStatement(query03);
    			    		 pstmt03.setString(1, restaurantAddress);
    			    	     pstmt03.setInt(2, OrderId); //same orderId as above
    			    		 pstmt03.executeUpdate();
    			    		
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
    			 	    	
    			   for (String i: queries) {
    			    	if (i == "query1") { 		 

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
		    			} else if (i == "query2") {
		    			    try {
		     	    		    statement2 = connection.createStatement();
		    	    					
		     	    			PreparedStatement pstmt2 = connection.prepareStatement(query2);
		     	    			pstmt2.setInt(1, OrderId); //orderId same as above
		     	    			pstmt2.setString(2, "Cheese slices");
		     	    			pstmt2.setInt(3,cheeseQ);
		     	    			pstmt2.executeUpdate();
		        			    } catch (SQLException e ) {
		    						e.printStackTrace();
		    					} finally {
		    						if (statement2 != null) {statement2.close();
		    						}
		    					}
		    		    			} else if (i == "query3") {
		    		    			    try {
		    		     	    		    statement3 = connection.createStatement();
		    		    	    					
		    		     	    			PreparedStatement pstmt3 = connection.prepareStatement(query3);
		    		     	    			pstmt3.setInt(1, OrderId); //orderId same as above
		    		     	    			pstmt3.setString(2, "Chicken Pieces");
		    		     	    			pstmt3.setInt(3,chickenpiecesQ);
		    		     	    			pstmt3.executeUpdate();
		    		        			    } catch (SQLException e ) {
		    		    						e.printStackTrace();
		    		    					} finally {
		    		    						if (statement3 != null) {statement3.close();
		    		    						}
		    		    					}
		    		    		    			} else if (i == "query4") {
		    		    		    			    try {
		    		    		     	    		    statement1 = connection.createStatement();
		    		    		    	    					
		    		    		     	    			PreparedStatement pstmt4 = connection.prepareStatement(query4);
		    		    		     	    			pstmt4.setInt(1, OrderId); //orderId same as above
		    		    		     	    			pstmt4.setString(2, "Sesame Seed Buns");
		    		    		     	    			pstmt4.setInt(3, sesameseedbunsQ);
		    		    		     	    			pstmt4.executeUpdate();
		    		    		        			    } catch (SQLException e ) {
		    		    		    						e.printStackTrace();
		    		    		    					} finally {
		    		    		    						if (statement4 != null) {statement4.close();
		    		    		    						}
		    		    		    					}
		    		    		    		    			} else if (i == "query5") {
		    		    		    		    			    try {
		    		    		    		     	    		    statement5 = connection.createStatement();
		    		    		    		    	    					
		    		    		    		     	    			PreparedStatement pstmt5 = connection.prepareStatement(query5);
		    		    		    		     	    			pstmt5.setInt(1, OrderId); //orderId same as above
		    		    		    		     	    			pstmt5.setString(2, "Chicken strips");
		    		    		    		     	    			pstmt5.setInt(3, chickenstripsQ);
		    		    		    		     	    			pstmt5.executeUpdate();
		    		    		    		        			    } catch (SQLException e ) {
		    		    		    		    						e.printStackTrace();
		    		    		    		    					} finally {
		    		    		    		    						if (statement5 != null) {statement5.close();
		    		    		    		    						}
		    		    		    		    					}
		    		    		    		    		    			} else if (i == "query6") {
		    		    		    		    		    			    try {
		    		    		    		    		     	    		    statement6 = connection.createStatement();
		    		    		    		    		    	    				
		    		    		    		    		     	    			PreparedStatement pstmt6 = connection.prepareStatement(query6);
		    		    		    		    		     	    			pstmt6.setInt(1, OrderId); //orderId same as above
		    		    		    		    		     	    			pstmt6.setString(2, "Mycoprotein based meat substitute Southern fried Strips");
		    		    		    		    		     	    			pstmt6.setInt(3,mycoproteinsouthernfriedstripsQ);
		    		    		    		    		     	    			pstmt6.executeUpdate();
		    		    		    		    		        			    } catch (SQLException e ) {
		    		    		    		    		    						e.printStackTrace();
		    		    		    		    		    					} finally {
		    		    		    		    		    						if (statement6 != null) {statement6.close();
		    		    		    		    		    						}
		    		    		    		    		    					}
		    		    		    		    		    		    			} else if (i == "query7") {
		    		    		    		    		    		    			    try {
		    		    		    		    		    		     	    		    statement7 = connection.createStatement();
		    		    		    		    		    		    	    					
		    		    		    		    		    		     	    			PreparedStatement pstmt7 = connection.prepareStatement(query7);
		    		    		    		    		    		     	    			pstmt7.setInt(1, OrderId); //orderId same as above
		    		    		    		    		    		     	    			pstmt7.setString(2, "Mycoprotein based meat substitute Southern fried burger");
		    		    		    		    		    		     	    			pstmt7.setInt(3, mycoproteinsouthernfriedburgerQ);
		    		    		    		    		    		     	    			pstmt7.executeUpdate();
		    		    		    		    		    		        			    } catch (SQLException e ) {
		    		    		    		    		    		    						e.printStackTrace();
		    		    		    		    		    		    					} finally {
		    		    		    		    		    		    						if (statement7 != null) {statement7.close();
		    		    		    		    		    		    						}
		    		    		    		    		    		    					}
		    		    		    		    		    		    		    			} else if (i == "query8") {
		    		    		    		    		    		    		    			    try {
		    		    		    		    		    		    		     	    		    statement8 = connection.createStatement();
		    		    		    		    		    		    		    	    					
		    		    		    		    		    		    		     	    			PreparedStatement pstmt8 = connection.prepareStatement(query8);
		    		    		    		    		    		    		     	    			pstmt8.setInt(1, OrderId); //orderId same as above
		    		    		    		    		    		    		     	    			pstmt8.setString(2, "Mayonnaise");
		    		    		    		    		    		    		     	    			pstmt8.setInt(3, mayonnaiseQ);
		    		    		    		    		    		    		     	    			pstmt8.executeUpdate();
		    		    		    		    		    		    		        			    } catch (SQLException e ) {
		    		    		    		    		    		    		    						e.printStackTrace();
		    		    		    		    		    		    		    					} finally {
		    		    		    		    		    		    		    						if (statement8 != null) {statement8.close();
		    		    		    		    		    		    		    						}
		    		    		    		    		    		    		    					}
		    		    		    		    		    		    		    		    			} else if (i == "query9") {
		    		    		    		    		    		    		    		    			    try {
		    		    		    		    		    		    		    		     	    		    statement9 = connection.createStatement();
		    		    		    		    		    		    		    		    	    					
		    		    		    		    		    		    		    		     	    			PreparedStatement pstmt9 = connection.prepareStatement(query9);
		    		    		    		    		    		    		    		     	    			pstmt9.setInt(1, OrderId); //orderId same as above
		    		    		    		    		    		    		    		     	    			pstmt9.setString(2, "Hash Browns");
		    		    		    		    		    		    		    		     	    			pstmt9.setInt(3, hashbrownsQ);
		    		    		    		    		    		    		    		     	    			pstmt9.executeUpdate();
		    		    		    		    		    		    		    		        			    } catch (SQLException e ) {
		    		    		    		    		    		    		    		    						e.printStackTrace();
		    		    		    		    		    		    		    		    					} finally {
		    		    		    		    		    		    		    		    						if (statement9 != null) {statement9.close();
		    		    		    		    		    		    		    		    						}
		    		    		    		    		    		    		    		    					}
		    		    		    		    		    		    		    		    		    			} else if (i == "query10") {
		    		    		    		    		    		    		    		    		    			    try {
		    		    		    		    		    		    		    		    		     	    		    statement10 = connection.createStatement();
		    		    		    		    		    		    		    		    		    	    					
		    		    		    		    		    		    		    		    		     	    			PreparedStatement pstmt10 = connection.prepareStatement(query10);
		    		    		    		    		    		    		    		    		     	    			pstmt10.setInt(1, OrderId); //orderId same as above
		    		    		    		    		    		    		    		    		     	    			pstmt10.setString(2, "Uncooked French Fries");
		    		    		    		    		    		    		    		    		     	    			pstmt10.setInt(3, frenchfriesQ );
		    		    		    		    		    		    		    		    		     	    			pstmt10.executeUpdate();
		    		    		    		    		    		    		    		    		        			    } catch (SQLException e ) {
		    		    		    		    		    		    		    		    		    						e.printStackTrace();
		    		    		    		    		    		    		    		    		    					} finally {
		    		    		    		    		    		    		    		    		    						if (statement10 != null) {statement10.close();
		    		    		    		    		    		    		    		    		    						}
		    		    		    		    		    		    		    		    		    					}
		    		    		    		    		    		    		    		    		    		    			} else if (i == "query11") {
		    		    		    		    		    		    		    		    		    		    			    try {
		    		    		    		    		    		    		    		    		    		     	    		    statement11 = connection.createStatement();
		    		    		    		    		    		    		    		    		    		    	    					
		    		    		    		    		    		    		    		    		    		     	    			PreparedStatement pstmt11 = connection.prepareStatement(query11);
		    		    		    		    		    		    		    		    		    		     	    			pstmt11.setInt(1, OrderId); //orderId same as above
		    		    		    		    		    		    		    		    		    		     	    			pstmt11.setString(2, "Cola syrup");
		    		    		    		    		    		    		    		    		    		     	    			pstmt11.setInt(3, colasyrupQ );
		    		    		    		    		    		    		    		    		    		     	    			pstmt11.executeUpdate();
		    		    		    		    		    		    		    		    		    		        			    } catch (SQLException e ) {
		    		    		    		    		    		    		    		    		    		    						e.printStackTrace();
		    		    		    		    		    		    		    		    		    		    					} finally {
		    		    		    		    		    		    		    		    		    		    						if (statement11 != null) {statement11.close();
		    		    		    		    		    		    		    		    		    		    						}
		    		    		    		    		    		    		    		    		    		    					}
		    		    		    		    		    		    		    		    		    					    } else {
		    		    		    		    		    		    		    		    		    		    				System.out.println("no stock item available in order to add to quantity");
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

    
    	public void requestStandardOrder(Connection connection) throws SQLException {
    		
    		
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
    			    
		   			
	    			   String query02 = "SELECT orderId FROM StockOrders WHERE orderDateTime ='" +currentTime+"'"; 

	    			    	  try { 
	    			    	   statement02 = connection.createStatement();
	    			    	   ResultSet rs02 = statement02.executeQuery(query02);
	    			    	        while (rs02.next()) {
	    			    	           int OrderId = rs02.getInt("orderId");
	   

	    			    	Statement statement03 = null;
	    		    			    	    
	    		    		 String query03 = "INSERT INTO Orders (restaurantAddress, orderId) VALUES (?, ?)";
	    		    			    	    
	    		    		try{
	    		    			 statement03 = connection.createStatement();
	    		    			    				
	    			    		 PreparedStatement pstmt03 = connection.prepareStatement(query03);
	    			    		 pstmt03.setString(1, restaurantAddress);
	    			    	     pstmt03.setInt(2, OrderId); //same orderId as above
	    			    		 pstmt03.executeUpdate();
	    			    		 
	    			    		 //Getting standard quantities from Stock Table
	    			    	    Statement statement04 = null;
	    			    	    String query04 = "SELECT stockItem, typicalUnitsOrdered FROM Stock"; //selecting all the stock for a standard order
	    			    	                       
	    			    	    try {
	    			    	     statement04 = connection.createStatement();
	    			    	     ResultSet rs04 = statement04.executeQuery(query04);
	    			    	     while (rs04.next()) {
	    			    	        String StockItem = rs04.getString("StockItem");
	    			    	        int typicalUnits = rs04.getInt("typicalUnitsOrdered");
	    			    	        
	    			    	        Statement statement = null;
	    			    	        String innerquery = "INSERT INTO Contains (orderId, quantity, stockItem) VALUES (?, ?, ?)";
	    			    	        
	    			    	    	try {
	    			    	    		statement = connection.createStatement();
	    			    				
	    			    			    PreparedStatement pstmt = connection.prepareStatement(innerquery);
	    			    			    pstmt.setInt(1, OrderId);
	    			    			    pstmt.setInt(2,  typicalUnits);
	    			    			    pstmt.setString(3,  StockItem);
	    			    			    pstmt.executeUpdate();
	    			    	                      
	    			    	        }catch (SQLException e ) {
	    			    	            e.printStackTrace();
	    			    	         } 
	    			    	     }
	    			    	     } catch (SQLException e ) {
	    			    	         e.printStackTrace();
	    			    	     } finally {
	    			    	        if (statement04 != null) {statement04.close();}
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
    	
    	
    	public void createMeal(Connection connection, String meal) throws SQLException {
			
    		Statement statement = null;
        	String query = "SELECT stockItem, quantity FROM MadeWith WHERE mealId ='"+meal+"'";
        	
        	try {
                statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(query);
                while(rs.next()){
                	String stockItem = rs.getString("stockItem");
                	float quantity = rs.getFloat("quantity");
                	
                	
                	Statement innerstatement = null;
                    String innerquery = "UPDATE within set quantity =quantity-"+quantity+" WHERE stockItem='"+stockItem+"'";
                    try {
                    	innerstatement = connection.createStatement();
                    	innerstatement.executeUpdate(innerquery);
    				} catch (SQLException e ) {
    					e.printStackTrace();
    				} finally {
    					if (innerstatement != null) {innerstatement.close();}
    				}                     
                }
        	} catch (SQLException e ) {
        		e.printStackTrace();
        	} finally {
        		if (statement != null) {statement.close();}
        	} 
    	}
}    