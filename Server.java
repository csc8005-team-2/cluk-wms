import java.sql.*;
public class Server
{
    private Connection connection;
    
    public Server()
    {
      connection = null;
    }

    public void Connect()
    {
        try{
            String userName = "csc8005_team02";
            String password = "HogsGet(Text";
            String url = "jdbc:mysql://homepages.cs.ncl.ac.uk/csc8005_team02";
            Class.forName ("com.mysql.cj.jdbc.Driver").newInstance ();
            connection = DriverManager.getConnection (url, userName, password);
            System.out.println ("Database connection established"); 
        }
         catch (Exception e)
        {
            System.err.println ("Cannot connect to database server");
            System.err.println (e.getMessage ());
        }  
    }

    public void Disconnect()
    {
        try
        {
            connection.close ();
            System.out.println ("Database connection terminated");
        }
        catch (Exception e)
        {
            System.err.println ("Cannot Disconnect from database server");
            System.err.println (e.getMessage ());
        }
    }
    
    public Connection GetConnection()
    {
        return connection;
    }
}
