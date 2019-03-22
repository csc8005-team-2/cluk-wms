import java.sql.*;
import java.util.ArrayList;

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

    public void UpdateStock(Connection connection, String stockItem, int quantity) throws SQLException {
        Statement statement = null;
        String query = "SELECT stockItem, quantity " +
                "FROM Within " +
                "WHERE stockItem='" + stockItem + "' AND restaurantAddress ='" + restaurantAddress+"'";
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            
            rs.next();
            int Quantity = rs.getInt("quantity");
            System.out.println("Previous stock of " + stockItem + "\t" + Quantity + "\n");
            int newQuantity = Quantity + quantity;

            Statement statement2 = null;
            String query2 = "UPDATE Within " +
                    "SET quantity ='" + newQuantity +
                    "' WHERE stockItem='" + stockItem + "' AND restaurantAddress ='" + restaurantAddress+"'";
            try {
                statement2 = connection.createStatement();
                statement.executeUpdate(query2);
                System.out.println("Updated stock of " + stockItem + "\t" + newQuantity + "\n");
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (statement2 != null) {
                    statement2.close();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }


        public void requestCustomOrder(Connection connection, int quantity, int orderId, String stockItem, String orderDateTime, String orderStatus) throws SQLException {
    	
    	boolean orderCreated = false;
    	while (orderCreated == false){
    		
    		Statement statement3 = null;
	    	String query3 = "INSERT INTO Contains (quantity, stockItem) VALUES (?,?)";

	    	try{
				statement3 = connection.createStatement();
				ResultSet rs1 = statement3.executeQuery(query3);
			
		            rs1.next();
		            rs1.getInt(quantity);
		            rs1.getString(stockItem); 

					if(orderCreated = true){ 
						System.out.println("Order has already been created.");
					}
					} catch (SQLException e ) {
						e.printStackTrace();
					} finally {
						if (statement3 != null) {statement3.close();
						}
					}
    	}

    		while (orderCreated == true) { 
    			
    			Statement statement4 = null;
    	    	String query4 = "INSERT INTO StockOrders (orderDateTime, orderStatus) VALUES (?,?)";

    	    	try{
    				statement4 = connection.createStatement();
    				ResultSet rs2 = statement4.executeQuery(query4);
    			
    			
    		        rs2.next();
    		    //    rs.getInt(orderId); 
    		        rs2.getString(orderDateTime); 
    				rs2.getString(orderStatus);
    				
    				if(orderStatus == "complete"){ 
    					System.out.println("Order has already been completed.");
    				}
    				} catch (SQLException e ) {
    					e.printStackTrace();
    				} finally {
    					if (statement4 != null) {statement4.close();
    					}
    				}
        	}
        }
    
    	//QUESTIONS: 
        //how do we make it so that the standard order's quantity = typical units ordered (in Stock) ? 
        //also code has lots of repetition, is there a way to minimise?
    
	//this method needs fixed *
    	public void requestStandardOrder(Connection connection, int quantity, int orderId, String stockItem, String orderDateTime, String orderStatus) throws SQLException {

        	boolean orderCreated = false;
        	while (orderCreated == false){
        		
        		Statement statement5 = null;
    	    	String query5 = "INSERT INTO Contains (quantity, stockItem) VALUES (?,?)";

    	    	try{
    				statement5 = connection.createStatement();
    				ResultSet rs3 = statement5.executeQuery(query5);
    			
    		            rs3.next();
    		            rs3.getInt(quantity); 
    		            rs3.getString(stockItem); 

    					if(orderCreated = true){ 
    						System.out.println("Order has already been created.");
    					}
    					} catch (SQLException e ) {
    						e.printStackTrace();
    					} finally {
    						if (statement5 != null) {statement5.close();
    						}
    					}
        	}
        	
    		while (orderCreated == true) { 
    			
    			Statement statement6 = null;
    	    	String query6 = "INSERT INTO StockOrders (orderDateTime, orderStatus) VALUES (?,?)";

    	    	try{
    				statement6 = connection.createStatement();
    				ResultSet rs4 = statement6.executeQuery(query6);
    			
    			
    		        rs4.next();
    		    //    rs.getInt(orderId); 
    		        rs4.getString(orderDateTime); 
    				rs4.getString(orderStatus);
    				
    				if(orderStatus == "complete"){ 
    					System.out.println("Order has already been completed.");
    				}
    				} catch (SQLException e ) {
    					e.printStackTrace();
    				} finally {
    					if (statement6 != null) {statement6.close();
    					}
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
            
            // for (int i =0; i<belowStock.size();i++){
            //	this.createCustomOrder(connection, belowStock.get(i), 100);
            //}
            
            
            
        }
        } catch (SQLException e ) {
            e.printStackTrace();
        } finally {
            if (statement != null) {statement.close();}
        }                 

    }
}
