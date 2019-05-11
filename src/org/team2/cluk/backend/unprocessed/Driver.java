package org.team2.cluk.backend.unprocessed;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
//import java.time.LocalDate;
//import java.util.Date;
import java.sql.*;

/*
* Driver class handles driver contact information, including shift information
*/

public class Driver {

	private Connection connection;
	private String firstName;
	private String lastName;
	private final int id;
	private String phoneNumber;
	private int capacity;
	private int workDuration; //mins.
	private final int breakTime = 45; //mins


	public Driver(Connection connection, String firstName, String lastName, int id, String phoneNumber,  int capacity, int workDuration){
		this.connection = connection;
		this.firstName = firstName;
		this.lastName = lastName;
		this.id = id;
		this.phoneNumber = phoneNumber;
		this.capacity = capacity;
		this.workDuration = workDuration;

	}

       /*
	* Method which adds driver information to the driver table of the database
	* Users are required to input their name, capacity and work duration
	* If successful, the system will show "Driver information " + id + "has been added to the database"
	* @param firstName, lastName, id, phoneNumber driver contact details
	* @param capacity amount of space they have available for stock (in units)
	* @param workDuration is the length of their delivery shift
	* @return updated driver information to the database
	*/
	public void addDriverInfo(String firstName, String lastName, int id, String phoneNumber,  int capacity, int workDuration) throws SQLException {

		Statement statement1 = null;
		String query1 = "INSERT INTO Driver (firstName, lastName, id, phoneNumber, capacity, workDuration) " +
				"SELECT '"+ firstName + "', '" + lastName + "', '" + id + "', '" + phoneNumber + "', '" + capacity + "', '" + workDuration + "')";

		try {
			statement1 = this.connection.createStatement();
			statement1.executeQuery(query1);
			System.out.println("Driver information " + id + "has been added to the database" + ".\n");

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (statement1 != null) {
				statement1.close();
			}
		}
	}

       /*
	* Method which removes driver information to the driver table of the database
	* Users are required to input id
	* If successful, the system will show "Driver information id " + id + "has been removed from the database"
	* @param id of the driver (in units)
	* @return updated driver information to the database
	*/
	public void removeDriverInfo(int id) throws SQLException{

		Statement statement2 = null;
		String query2 = "DELETE FROM Driver WHERE id = '" + id + "'";

		try {
			statement2 = this.connection.createStatement();
			statement2.executeUpdate(query2);
			System.out.println("Driver information id " + id + "has been removed from the database" + ".\n");

		} catch (SQLException e ) {
			e.printStackTrace();
		} finally {
			if (statement2 != null) {
				statement2.close();
			}
		}
	}

       /*
	* Method to print a driver's first name using the driver's id. 
	* If successful, the system will show "Driver " + id + "'s first name is " + firstName + "
	* @param id of the driver
	* @return the first name of the driver
	*/
	public void printFirstName(int id) throws SQLException{

		Statement statement3 = null;
		String query3 = "SELECT firstName " +
				"FROM Driver " +
				"WHERE id ='" + id + "'";

		try {
			statement3 = this.connection.createStatement();
			ResultSet rs = statement3.executeQuery(query3);
			while (rs.next()) {
				String firstName = rs.getString("firstName");
				System.out.println("Driver " + id + "'s first name is " + firstName + ".\n");

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (statement3 != null) {
				statement3.close();
			}
		}
	}

       /* 
	* Method to update a driver's first name using the id and first name
	* If successful, the system will show "Driver " + id + "'s first name has been updated to " + newFirstName
	* @param id of the driver
	* @param firstName of the driver
	* @return the updated first name of the driver to the database
	*/
	public void updateFirstName(int id, String firstName) throws SQLException{

		Statement statement4 = null;
		String query4 = "SELECT firstName" +
				"FROM Driver " +
				"WHERE id='" + id +"'";

		try {
			statement4 = this.connection.createStatement();
			ResultSet rs = statement4.executeQuery(query4);

			rs.next();
			String newFirstName = rs.getString("firstName");
			System.out.println("Driver " + id + "'s first name is " + firstName + "\n");

			Statement statement5 = null;
			String query5 = "UPDATE Driver " +
					"SET firstName ='" + newFirstName +
					"'WHERE id='" + id + "'";

			try {
				statement5 = this.connection.createStatement();
				statement5.executeUpdate(query5);
				System.out.println("Driver " + id + "'s first name has been updated to " + newFirstName + "\n");

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (statement5 != null) {
					statement5.close();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (statement4 != null) {
				statement4.close();
			}
		}
	}

       /*
	* Method to print a driver's last name using id as an input parameter
	* If successful, the system will show "Driver " + id + "'s last name is " + lastName
	* @param id of the driver 
	* @return the last name of the driver
	*/
	public void printLastName(int id) throws SQLException{

		Statement statement6 = null;
		String query6 = "SELECT lastName " +
				"FROM Driver " +
				"WHERE id ='" + id + "'";

		try {
			statement6 = this.connection.createStatement();
			ResultSet rs = statement6.executeQuery(query6);

			while (rs.next()) {
				String lastName = rs.getString("lastName");
				System.out.println("Driver " + id + "'s last name is " + lastName + ".\n");

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (statement6 != null) {
				statement6.close();
			}
		}
	}

       /* 
	* Method to update a driver's last name using the id and last name
	* If successful, the system will show "Driver " + id + "'s last name has been updated to " + newLastName 
	* @param id of the driver
	* @param lastName of the driver
	* @return the updated last name of the driver to the database
	*/
	public void updateLastName(int id, String lastName) throws SQLException{

		Statement statement7 = null;
		String query7 = "SELECT lastName" +
				"FROM Driver " +
				"WHERE id='" + id +"'";

		try {
			statement7 = this.connection.createStatement();
			ResultSet rs = statement7.executeQuery(query7);

			rs.next();
			String newLastName = rs.getString("lastName");
			System.out.println("Driver " + id + "'s last name is " + lastName + "\n");

			Statement statement8 = null;
			String query8 = "UPDATE Driver " +
					"SET lastName ='" + newLastName +
					"'WHERE id='" + id + "'";

			try {
				statement8 = this.connection.createStatement();
				statement8.executeUpdate(query8);
				System.out.println("Driver " + id + "'s last name has been updated to " + newLastName + "\n");

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (statement8 != null) {
					statement8.close();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (statement7 != null) {
				statement7.close();
			}
		}
	}

	/*
	* Method to print the phone number of the driver 
	* If successful, the system will show "Driver " + id + "'s phone number is " + PhoneNumber
	* @param id of the driver
	* @return the phone number of the driver
	*/
	public void printPhoneNumber(int id) throws SQLException{

		Statement statement9 = null;
		String query9 = "SELECT phoneNumber " +
				"FROM Driver " +
				"WHERE id ='" + id + "'";

		try {
			statement9 = this.connection.createStatement();
			ResultSet rs = statement9.executeQuery(query9);
			while (rs.next()) {
				String PhoneNumber = rs.getString("phoneNumber");
				System.out.println("Driver " + id + "'s phone number is " + PhoneNumber + "\n");

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (statement9 != null) {
				statement9.close();
			}
		}
	}

       /*
	* Method to update the driver's phone number using the id and phone number
	* Users need to input their id and phonenumber
	* If successful, the system will show "Driver " + id + "'s phone number is " + PhoneNumber
	* @param id of the driver
	* @param phoneNumber of the driver
	* @return the updated phone number to the database
	*/
	public void UpdatePhoneNumber(int id, String phoneNumber) throws SQLException{

		Statement statement10 = null;
		String query10 = "SELECT phoneNumber" +
				"FROM Driver " +
				"WHERE id='" + id +"'";

		try {
			statement10 = this.connection.createStatement();
			ResultSet rs = statement10.executeQuery(query10);

			rs.next();
			String PhoneNumber = rs.getString("phoneNumber");
			System.out.println("Driver " + id + "'s phone number is " + PhoneNumber + "\n");
			String newPhoneNumber = PhoneNumber + phoneNumber;

			Statement statement11 = null;
			String query11 = "UPDATE Driver " +
					"SET phoneNumber ='" + newPhoneNumber +
					"'WHERE id='" + id+"'";

			try {
				statement11 = this.connection.createStatement();
				statement11.executeUpdate(query11);
				System.out.println("Updated phone number for Driver " + id + "\t" + "is" + newPhoneNumber + "\n");

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (statement11 != null) {
					statement11.close();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (statement10 != null) {
				statement10.close();
			}
		}
	}

       /*
	* Method to print a driver's current work duration
	* Uses date format HH:mm:ss
	* If successful, system shows "Driver " + id + "'s work duration so far is " + WorkDuration
	* @param id of the driver 
	* @param WorkingHours the amount of time a driver is delivering orders from a warehouse to a restaurant
	* @return the driver's id and work duration so far
	*/
	public void printWorkDuration(int id, WorkingHours w) throws SQLException{

		DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		int startTime = Integer.parseInt(formatter.format(w.getStartTime()));
		int endTime = Integer.parseInt(formatter.format(w.getEndTime()));
		workDuration = endTime-startTime;

		Statement statement12 = null;

		String query12 = "SELECT date" +
				"FROM WorkingHours" +
				"WHERE id = id";
		try {
			statement12 = this.connection.createStatement();
			ResultSet rs = statement12.executeQuery(query12);

			rs.next();
			int WorkDuration = rs.getInt(workDuration);
			System.out.println("Driver " + id + "'s work duration so far is " + WorkDuration + "\n");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (statement12 != null) {
				statement12.close();
			}
		}
	}

       /*
	* Method which makes drivers go on a break by applying a break time to their work duration
	* Users need to input the driver's id
	* This method is required as drivers must take a 45-minute break after driving for 4.5 hours
	* Drivers have a maximum of 10 hours per shift
	* If successful, the system will show "Updated work Duration for Driver " + id + "\t" + "is" + newWorkDuration + " after going on break"
	* @param id of the driver
	* @param WorkingHours the amount of time a driver is delivering orders from a warehouse to a restaurant
	*/
	public void goOnBreak(int id, WorkingHours w) throws SQLException{

		DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		int startTime = Integer.parseInt(formatter.format(w.getStartTime()));
		int endTime = Integer.parseInt(formatter.format(w.getEndTime()));
		workDuration = endTime-startTime;

		boolean goOnBreak = false;

		while (goOnBreak == false) {

			if(workDuration == 270) {

				Statement statement13 = null;
				String query13 = "SELECT workDuration" +
						"FROM Driver " +
						"WHERE id='" + id +"'";

				try {
					statement13 = this.connection.createStatement();
					ResultSet rs = statement13.executeQuery(query13);

					rs.next();
					int WorkDuration = rs.getInt(workDuration);
					System.out.println("Previous work duration of driver " + id + "\t" + "is" + WorkDuration + "\n");
					int newWorkDuration = WorkDuration + breakTime;

					Statement statement14 = null;
					String query14 = "UPDATE Driver " +
							"SET workDuration ='" + newWorkDuration +
							"'WHERE id='" + id+"'";

					try {
						statement14 = this.connection.createStatement();
						statement14.executeUpdate(query14);
						System.out.println("Updated work Duration for Driver " + id + "\t" + "is" + newWorkDuration + " after going on break" + "\n");
						goOnBreak = true;

					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						if (statement14 != null) {
							statement14.close();
						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					if (statement13 != null) {
						statement13.close();
					}
				}
			}
		}
	}
	
       /*
	* Method to print the capacity of the drivers
	* The capacity is the quantity of stock they can deliver during one trip
	* If successful, the system will show "Driver " + id + "'s car capacity is " + Capacity
	*/
	
	public void printCapacity() throws SQLException{

		Statement statement15 = null;
		String query15 = "SELECT capacity " +
				"FROM Driver " +
				"WHERE id ='" + id +"'";

		try {
			statement15 = this.connection.createStatement();
			ResultSet rs = statement15.executeQuery(query15);
			while (rs.next()) {
				int Capacity = rs.getInt("capacity");
				System.out.println("Driver " + id + "'s car capacity is " + Capacity + "\n");

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (statement15 != null) {
				statement15.close();
			}
		}
	}

       /*
	* This method updates the driver's capacity
	* User is required to input the driver's id
	* If successful, the system will show "Updated capacity of Driver "+ id + " is " + newCapacity
	* @param id of the driver
	* @param capacity is the initial capacity of the driver
	*/
	public void UpdateCapacity(int id, int capacity) throws SQLException{

		Statement statement16 = null;
		String query16 = "SELECT capacity " +
				"FROM Driver " +
				"WHERE id ='" + id+"'";
		try {
			statement16 = this.connection.createStatement();
			ResultSet rs = statement16.executeQuery(query16);

			rs.next();

			int Capacity = rs.getInt("capacity");
			System.out.println("Previous capacity of Driver " + id + " is " + capacity);
			int newCapacity = Capacity+capacity;

			Statement statement17 = null;
			String query17 = "UPDATE Driver " +
					"SET capacity ='" + newCapacity+
					"' WHERE id='" + id +"' AND capacity ='"+ capacity+ "'";
			try {
				statement17 = this.connection.createStatement();
				statement17.executeUpdate(query17);
				System.out.println("Updated capacity of Driver "+ id + " is " + newCapacity);
			} catch (SQLException e ) {
				e.printStackTrace();
			} finally {
				if (statement17 != null) {
					statement17.close();
				}
			}
		} catch (SQLException e ) {
			e.printStackTrace();
		} finally {
			if (statement16 != null) {
				statement16.close();
			}
		}
	}
}
