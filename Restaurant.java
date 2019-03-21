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


    public void requestStandardOrder(Connection connection) throws SQLException {
        Statement statement = null;
        String query = "SELECT O.orderId, C.typicalUnitsOrdered" + "FROM Orders O, Contains C, Within W"
                + "WHERE O.restaurantAddress=" + restaurantAddress + "AND O.restaurantAddress = W.RestaurantAddress"
                + "AND W.quantity = C.quantity" + "AND C.typicalUnitsOrdered = W.typicalUnitsOrdered";

        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);

            while (rs.next()) {

                String OrderId = rs.getString("orderId");
                String TypicalUnitsOrdered = rs.getString("typicalUnitsOrdered");
                System.out.println(OrderId + "\t" + TypicalUnitsOrdered + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }


    public void requestCustomOrder(Connection connection) throws SQLException {
        Statement statement = null;
        String query = "SELECT C.stockItem, O.orderId, W.quantity" + "FROM Contains C, Orders O, Within W"
                + "WHERE O.restaurantAddress=" + restaurantAddress + "AND O.restaurantAddress = W.restaurantAddress"
                + "AND C.stockItem = W.stockItem" + "AND O.orderId = C.orderId" + "AND C.quantity = W.quantity";

        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);

            while (rs.next()) {

                String OrderId = rs.getString("orderId");
                String StockItem = rs.getString("stockItem");
                int Quantity = rs.getInt("quantity");
                System.out.println(OrderId + "\t" + StockItem + "\n" + Quantity + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }



    public void stockCreatesMeals(Connection connection) throws SQLException {
        Statement statement = null;
        String query = "SELECT T.mealId, W.stockItem, WW.amount" + "FROM ToMake T, Works_With WW, Within W"
                + "WHERE WW.restaurantAddress=" + restaurantAddress + "AND WW.restaurantAddress = W.restaurantAddress"
                + "AND W.stockItem = T.name" + "AND WW.amount = W.quantity";
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);

            while (rs.next()) {

                String MealId = rs.getString("mealId");
                String StockItem = rs.getString("stockItem");
                int Amount = rs.getInt("amount");
                System.out.println(MealId + "\t" + StockItem + "\n" + Amount + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                statement.close();
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