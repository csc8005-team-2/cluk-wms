import java.sql.*;

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
                       "FROM Inside" +            
                       "WHERE warehouseAddress ="+Address+")";
                       
        try {
        statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(query);
        while (rs.next()) {
            String StockItem = rs.getString("StockItem");
            int Quantity = rs.getInt("Quantity");
            System.out.println(StockItem + "\t" + Quantity + "\n");
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
                       "FROM Inside" +            
                       "WHERE stockItem="+stockItem+"AND warehouseAddress ="+Address+")";
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
        
            int Quantity = rs.getInt("quantity");
            System.out.println("Previos stock of "+stockItem + "\t" + Quantity + "\n");
            int newQuantity = Quantity+quantity;
            
            Statement statement2 = null;
            String query2 = "UPDATE Inside " +
                            "SET quantity ="+newQuantity+            
                            "WHERE stockItem="+stockItem+"AND warehouseAddress ="+Address+")";
            try {
                statement2 = connection.createStatement();
                ResultSet rs2 = statement.executeQuery(query2);
                System.out.println("Updated stock of "+stockItem + "\t" + newQuantity + "\n");
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
    
    
}
