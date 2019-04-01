import java.sql.*;



//UNTESTED testing these methods 02/04/2019

public class Accounts {
	
	//Method to add account to database.
	public void addAccount(Connection connection, String name, String username, String password) throws SQLException {
		
		Statement statement = null;
		String query = "INSERT INTO Accounts (name, username, password) SELECT '"+name+"', '"+username+"', SHA2('"+password+"', 256)";
    	
    	try{
    		statement = connection.createStatement();
            statement.executeUpdate(query);
            System.out.print("Account created for " +name);
            
    	} catch (SQLException e ) {
    		e.printStackTrace();
    	} finally {
            if (statement != null) {statement.close();}
        }
	} 
	
	//Method to set account permissions.
	public void setPermissions(Connection connection, String name, boolean restaurant, boolean warehouse, boolean driver) throws SQLException {
		int rest =0; int ware = 0; int driv =0;
		if(restaurant == true) {rest=1;}
		if(warehouse == true) {ware=1;}
		if(driver == true) {driv=1;}
		
		Statement statement = null;
    	String query = "UPDATE ACCOUNTS SET restaurant ="+rest+", warehouse ="+ware+", driver ="+driv+" WHERE name ='"+name+"'";
    	
    	try {
            statement = connection.createStatement();
            statement.executeUpdate(query);
            System.out.println("Updated permissions for: "+name);
            
        } catch (SQLException e ) {
            e.printStackTrace();
        } finally {
            if (statement != null) {statement.close();}
        }
	}
    
	//Method to remove account from database.
	public void removeAccount(Connection connection, String name) throws SQLException {
		
		Statement statement = null;
		String query = "DELETE FROM Accounts WHERE name ='"+name+"'";
		
		try {
            statement = connection.createStatement();
            statement.executeQuery(query);
            System.out.println("Account for employee "+ name + " removed.");
            
        } catch (SQLException e ) {
            e.printStackTrace();
        } finally {
            if (statement != null) {statement.close();}
        }
	}
	
	//Method to see staff info
	public void getStaffInfo(Connection connection) throws SQLException {
		
		Statement statement = null;
		String query = "SELECT id, name, username, restaurant, warehouse, driver FROM Accounts";
		
		try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
           
            while(rs.next()){
            	
            	int id = rs.getInt("id");
            	String name = rs.getString("name");
            	String username = rs.getString("username");
            	
            	int rest = rs.getInt("restaurant");
            	String sRest;
            	if(rest == 1) {sRest="Yes";}
            	else {sRest="No";}
            	
            	int ware = rs.getInt("warehouse");
            	String sWare;
            	if(ware == 1) {sWare="Yes";}
            	else {sWare="No";}
            	
            	int driv = rs.getInt("driver");
            	String sDriv;
            	if(driv == 1) {sDriv="Yes";}
            	else {sDriv="No";}
            	
            	System.out.print("ID: "+ id +" Name: "+ name +" Username: "+ username +" \n");
            	System.out.print("Restaurant access: "+sRest+" Warehouse access: "+sWare+" Driver access "+sDriv);
            }
        } catch (SQLException e ) {
            e.printStackTrace();
        } finally {
            if (statement != null) {statement.close();}
        }	
	}
	
	
	//Method to check access rights.
	public void checkAccess(Connection connection, String name) throws SQLException {
		
		Statement statement = null;
		String query = "SELECT restaurant, warehouse, driver FROM Accounts WHERE name ='"+name+"'";
		
		try {
			statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            
            rs.next();
            int rest = rs.getInt("restaurant");
            int ware = rs.getInt("warehouse");
            int driv = rs.getInt("driver");
            
            String access="";
            if(rest ==1 && ware == 1 && driv == 1) {access="Manager";}
            if(rest ==1 && ware == 0 && driv == 0) {access="Restaurant";}
            if(rest ==0 && ware == 1 && driv == 0) {access="Warehouse";}
            if(rest ==0 && ware == 0 && driv == 1) {access="Driver";}
            if(rest ==1 && ware == 1 && driv == 0) {access="Restaurant and warehouse";}
            if(rest ==1 && ware == 0 && driv == 1) {access="Restaurant and driver";}
            if(rest ==0 && ware == 1 && driv == 1) {access="Warehouse and driver";}
            if(rest ==0 && ware == 0 && driv == 0) {access="No access";}
            
            System.out.print("The user "+name+" has permissions for: "+access);
                        
		} catch (SQLException e ) {
            e.printStackTrace();
        } finally {
            if (statement != null) {statement.close();}
        }	
		
	}	
}
