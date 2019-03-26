import java.sql.;
import java.util.;

public class Restaurant {


    private String restaurantAddress;

    public Restaurant() {
        restaurantAddress = ;
       
    }

    public Restaurant(String name) {
        this.restaurantAddress = name;
    }

    
    Get address
    public String getRestaurantAddress() {
        return this.restaurantAddress;
    }


    public void requestStandardOrder(Connection connection) throws SQLException {
    	
    	java.util.Date orderDate = new java.util.Date();
    	java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(yyyy-MM-dd HHmmss);
    	String currentTime = sdf.format(orderDate);
    	
    	int orderId=0;
    	
    	
    	 Creating Order in StockOrders Table.
    	String query = INSERT INTO StockOrders (orderDateTime, orderStatus) VALUES (, );
    	
    	try{
    		PreparedStatement pstmt = connection.prepareStatement(query);
    		pstmt.setString(1, currentTime);
    		pstmt.setString(2,Out for delivery);
    		pstmt.executeUpdate();
		    				
    	} catch (SQLException e ) {
    		e.printStackTrace();
    	} 

    	 Getting back the orderId of the order we just created.
    	Statement statement = null;
    	query = SELECT orderId FROM StockOrders WHERE orderDateTime='+currentTime+';
    	
    	try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            rs.next();
            orderId = rs.getInt(orderId);
            } catch (SQLException e ) {
                e.printStackTrace();
            } finally {
                if (statement != null) {statement.close();}
            } 
    	
         Add Order ID and restaurant address to orders table.
    	query = INSERT into Orders (restaurantAddress, orderId) VALUES (,);
    	
    	try {
    		PreparedStatement ptsmt= connection.prepareStatement(query);
    		ptsmt.setString(1,this.getRestaurantAddress());
    		ptsmt.setInt(2, orderId);
    		ptsmt.execute();
		
    	}catch (SQLException e ) {
    		e.printStackTrace();
    	}
    	
    	 Getting Standard quantities from Stock Table.
    	statement = null;
    	query = SELECT stockItem, typicalUnitsOrdered FROM Stock;
                       
        try {
        statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(query);
        while (rs.next()) {
        	String StockItem = rs.getString(StockItem);
            int typicalUnits = rs.getInt(typicalUnitsOrdered);
            
             Add standard quantities to Contains table.
            String innerquery = INSERT into Contains (orderId,quantity,stockItem) VALUES (+orderId+,+typicalUnits+,+StockItem+);
            try {
            	PreparedStatement ptsmt= connection.prepareStatement(innerquery);
            	ptsmt.execute();
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
}