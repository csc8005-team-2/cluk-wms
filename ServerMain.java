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
