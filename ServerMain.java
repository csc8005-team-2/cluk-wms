import java.sql.*;
public class ServerMain
{

   public static void main (String[] args)
   {
        Connection connection = null;

        try
        {
            String userName = "csc8005_team02";
            String password = "HogsGet(Text";
            String url = "jdbc:mysql://homepages.cs.ncl.ac.uk/csc8005_team02";
            Class.forName ("com.mysql.cj.jdbc.Driver").newInstance ();
            connection = DriverManager.getConnection (url, userName, password);
            System.out.println ("Database connection established");

         /* Here we put all our back end functionality.
          */
           
           
           
           //restaurant methods 
           //restaurant requestCustomOrder method 
                String query = "INSERT INTO Contains (quantity, stockItem)"
                  + "values (?, ?)";
                
                PreparedStatement preparedStmt = connection.prepareStatement(query);
                preparedStmt.setInt (1, 10);
                preparedStmt.setString (2, "Shredded iceberg lettuce");
         
                // execute
                preparedStmt.execute();
               // connection.close();

           
                //restaurant createStockOrder method 
                String query2 = "INSERT INTO StockOrders (orderDateTime, orderStatus) VALUES (?,?)";
                PreparedStatement preparedStmt2 = connection.prepareStatement(query2, Statement.RETURN_GENERATED_KEYS);

                	ResultSet rs = preparedStmt2.getGeneratedKeys();
                	if (rs.next()) {	
                	int orderId = rs.getInt(1);
                	System.out.println(orderId);
                	}

                    preparedStmt2.setString (1, "2019-02-14 10:50:34");
                    preparedStmt2.setString (2, "Pending");

                // execute
                preparedStmt2.execute();
               // connection.close();

        }
        catch (Exception e)
        {
            System.err.println ("Cannot connect to database server");
            System.err.println (e.getMessage ());
        }

        finally
        {
           if (connection != null)
           {
               try
               {
                   connection.close ();
                   System.out.println ("Database connection terminated");
               }
               catch (Exception e) { /* ignore close errors */ }
           }
        }
   }
}
