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


    public void requestCustomOrder(Connection connection, int quantity, int orderId, String stockItem) throws SQLException {
    	
    	//assign it an order id, quantity etc (fill out Contains (Contains has: (quantity,orderId,stockItem)) )
    	boolean orderCreated = false;
    	while (orderCreated == false){
    		
    		Statement statement3 = null;
	    	String query3 = "INSERT INTO Contains (quantity, stockItem) VALUES (?,?)";

	    	try{
				statement3 = connection.createStatement();
				ResultSet rs = statement3.executeQuery(query3);
			
		            rs.next();
		            rs.getInt(quantity);
		            rs.getString(stockItem); 
				//	rs.getInt(orderId);
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
    }
    
    	
    	
    	//add stock order to StockOrders table - set status to pending etc. (orderId, orderdatetime, order status)
    	public void createStockOrder(Connection connection, int orderId, String orderDateTime, String orderStatus) throws SQLException {
    		boolean orderCreated = false;
    		while (orderCreated == true) { 
    			
    			Statement statement4 = null;
    	    	String query4 = "INSERT INTO StockOrders (orderDateTime, orderStatus) VALUES (?,?)";

    	    	try{
    				statement4 = connection.createStatement();
    				ResultSet rs = statement4.executeQuery(query4);
    			
    			
    		        rs.next();
    		    //    rs.getInt(orderId); 
    		        rs.getString(orderDateTime); 
    				rs.getString(orderStatus);
    				
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
