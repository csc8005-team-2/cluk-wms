import java.sql.*;
import java.util.ArrayList;

 class Warehouse
{
    private String Address;
    
    public Warehouse(String address)
    {   
        Address = address;
    }

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
    
    public void SendOrder(Connection connection, int orderId) throws SQLException
    {
    	
    	boolean orderFulfilled = false;
    	
    	while (orderFulfilled == false){
    		
    		Statement statement9 = null;
	    	String query9 = "SELECT orderStatus "+
	    					"FROM StockOrders "+
	    					"WHERE orderId ='"+orderId+"'";
	    	
	    	try{
				statement9 = connection.createStatement();
				ResultSet rs = statement9.executeQuery(query9);
			
					rs.next();
					String orderStatus = rs.getString("orderStatus");
					if(!orderStatus.equalsIgnoreCase("Pending")){
						System.out.println("Order has already been fulfilled.");
						orderFulfilled = true;
					}
					} catch (SQLException e ) {
						e.printStackTrace();
					} finally {
						if (statement9 != null) {statement9.close();}
    		
    		
    		
    	
				boolean stockAvaliable = true;
				while (stockAvaliable == true && orderFulfilled == false){
				Statement statement0 = null;
				String query0 = "SELECT c.quantity, I.quantity "+
    					"FROM Contains c, Inside I "+
    					"WHERE c.orderId ='"+orderId+"' OR I.warehouseAddress ='"+Address+"'";
    	
    	
    	
				try{
					statement0 = connection.createStatement();
					ResultSet rs = statement0.executeQuery(query0);
		
					while(rs.next()){
						int CQuantity = rs.getInt("c.quantity");
						int IQuantity= rs.getInt("I.quantity");
						if(CQuantity > IQuantity){
							System.out.println("Order cannot be fulfilled. Warehouse stock too low.");
							stockAvaliable = false;
				}
					}

		} catch (SQLException e ) {
			e.printStackTrace();
		} finally {
			if (statement0 != null) {statement0.close();}

    	

    			Statement statement = null;
    			String query = "SELECT stockItem, quantity " +
    							"FROM Contains " +            
    							"WHERE orderId='"+orderId+"'";
    			try {
    				statement = connection.createStatement();
    				ResultSet rs = statement.executeQuery(query);
            
    				while(rs.next()){
    					int Quantity = rs.getInt("quantity");
    					String StockItem = rs.getString("StockItem");
    					Statement statement2 = null;
    					String query2 = "UPDATE Inside " +
                            				"SET quantity = quantity-"+Quantity+           
                            				" WHERE stockItem='"+StockItem+"' AND warehouseAddress ='"+Address+"'";
            	
    					try {
    						statement2 = connection.createStatement();
    						statement2.executeUpdate(query2);
    					} catch (SQLException e ) {
    						e.printStackTrace();
    					} finally {
    						if (statement2 != null) {statement2.close();}
    					}                     
    				}
  
    			} catch (SQLException e ) {
    				e.printStackTrace();
    			} finally {
    				if (statement != null) {statement.close();}
    			}   
        
    			Statement statement3 = null;
    			String query3 =  "UPDATE StockOrders "+
    							"SET orderStatus = 'Out for delivery' "+
    								"WHERE orderId='"+orderId+"'";
    			try {
    				statement3 = connection.createStatement();
    				statement3.executeUpdate(query3);
    			} catch (SQLException e ) {
    				e.printStackTrace();
    			} finally {
    				if (statement3 != null) {statement3.close();}
    			}                     

    			}
 
    			}
			}
 
    	}
    }
    
    public void minStockCheck(Connection connection) throws SQLException
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
            
            for (int i =0; i<belowStock.size();i++){
            	this.UpdateStock(connection, belowStock.get(i), 100);
            }
            
            
        }
        } catch (SQLException e ) {
            e.printStackTrace();
        } finally {
            if (statement != null) {statement.close();}
        }                 
    }
}