package GP;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
//import java.time.LocalDate;
//import java.util.Date;
import java.sql.*;

import GP.WorkingHours;

public class Driver extends Employee{

	private final int breakTime = 45; //mins
	//private String phoneNumber;
	//private int capacity;
	private int workDuration; //mins.
	private int breakCount;

	public Driver(String firstName, String lastName, int ID, WorkingHours workHours, int workDuration){
		super(firstName, lastName, ID);
		//this.phoneNumber = phoneNumber;
		//this.capacity = capacity;
		this.workDuration = workDuration;

	}

	public void getPhoneNumber(Connection connection) throws SQLException{
		Statement statement = null;
		String query = "SELECT phoneNumber " + 
				"FROM Driver " +
				"WHERE ID ='" + ID+"'";

		try {
			statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(query);
			while (rs.next()) {
				String PhoneNumber = rs.getString("phoneNumber");
				System.out.println("Driver " + ID + "'s phone number is " + PhoneNumber + "\n");

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (statement != null) {
				statement.close();
			}
		}
	}

	public void UpdatePhoneNumber(Connection connection, String phoneNumber) throws SQLException{
		Statement statement = null;
		String query = "SELECT phoneNumber" +
				"FROM Driver " +
				"WHERE phoneNumber=' + phoneNumber +' AND ID='" + ID +"'";

		try {
			statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(query);

			rs.next();
			String PhoneNumber = rs.getString("phoneNumber");
			System.out.println("Previous phone number of driver " + ID + "\t" + "is" + phoneNumber + "\n");
			String newPhoneNumber = PhoneNumber + phoneNumber;

			Statement statement2 = null;
			String query2 = "UPDATE Driver " +
					"SET phoneNumber ='" + newPhoneNumber + 
					"'WHERE phoneNumber='" + phoneNumber + "' AND ID='" + ID+"'";

			try {
				statement2 = connection.createStatement();
				statement.executeUpdate(query2);
				System.out.println("Updated phone number for Driver " + ID + "\t" + "is" + newPhoneNumber + "\n");

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

	public int getWorkDuration(WorkingHours w) {

		DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		int startTime = Integer.parseInt(formatter.format(w.getStartTime()));
		int endTime = Integer.parseInt(formatter.format(w.getEndTime()));
		workDuration = endTime-startTime;
		return workDuration;
	}

	public void goOnBreak(Connection connection, int ID, int workDuration) throws SQLException{

		boolean goOnBreak = false;
		while (goOnBreak == false) {

			if(workDuration == 270) {

				Statement statement3 = null;
				String query3 = "SELECT workDuration" +
						"FROM Driver " +
						"WHERE ID='" + ID +"'";

				try {
					statement3 = connection.createStatement();
					ResultSet rs = statement3.executeQuery(query3);

					rs.next();
					int WorkDuration = rs.getInt(workDuration);
					System.out.println("Previous work duration of driver " + ID + "\t" + "is" + WorkDuration + "\n");
					int newWorkDuration = WorkDuration + breakTime;

					Statement statement4 = null;
					String query4 = "UPDATE Driver " +
							"SET workDuration ='" + newWorkDuration + 
							"'WHERE workDuration='" + workDuration + "' AND ID='" + ID+"'";

					try {
						statement4 = connection.createStatement();
						statement4.executeUpdate(query4);
						System.out.println("Updated work Duration for Driver " + ID + "\t" + "is" + newWorkDuration + "\n");
						goOnBreak = true;
						breakCount++;


					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						if (statement4 != null) {
							statement4.close();
						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					if (statement3 != null) {
						statement3.close();
					}
				}
			}
		}

	}

	/*
	 * public boolean goOnCompulsoryBreak() { boolean y = false; if (workDuration ==
	 * 270) { if (goOnBreak() == false) { goOnBreak(); breakCount++; y = true; }else
	 * { return y; } } return y; }
	 */

	public int breakCount() {
		return breakCount;
	}

	//QUESTIONS
	// How do i terminate work duration once it reaches 600 mins, thats max work duration.



	/*
	 * public boolean manageWorkHours() { boolean x = false; if (workDuration >
	 * 600){ this.workDuration = 600; x = true;
	 * System.out.println("Maximum workDuration is 10 hours per shift"); } return x;
	 * }
	 */

	public void getCapacity(Connection connection) throws SQLException{

		Statement statement5 = null;
		String query5 = "SELECT capacity " + 
				"FROM Driver " +
				"WHERE ID ='" + ID+"'";

		try {
			statement5 = connection.createStatement();
			ResultSet rs = statement5.executeQuery(query5);
			while (rs.next()) {
				int Capacity = rs.getInt("capacity");
				System.out.println("Driver " + ID + "'s car capacity is " + Capacity + "\n");

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (statement5 != null) {
				statement5.close();
			}
		}
	}

	public void UpdateCapacity(Connection connection, int ID, int capacity) throws SQLException{

		Statement statement6 = null;
		String query6 = "SELECT capacity " +
				"FROM Driver " +            
				"WHERE ID ='" + ID+"'";
		try {
			statement6 = connection.createStatement();
			ResultSet rs = statement6.executeQuery(query6);

			rs.next();

			int Capacity = rs.getInt("capacity");
			System.out.println("Previos capacity of Driver " + ID + " is " + capacity);
			int newCapacity = Capacity+capacity;

			Statement statement7 = null;
			String query7 = "UPDATE Driver " +
					"SET capacity ='" + newCapacity+            
					"' WHERE ID='" + ID +"' AND capacity ='"+ capacity+ "'";
			try {
				statement7 = connection.createStatement();
				statement7.executeUpdate(query7);
				System.out.println("Updated capacity of "+ ID + " is " + newCapacity);
			} catch (SQLException e ) {
				e.printStackTrace();
			} finally {
				if (statement7 != null) {
					statement7.close();
				}
			}                     
		} catch (SQLException e ) {
			e.printStackTrace();
		} finally {
			if (statement6 != null) {
				statement6.close();
			}
		}  
	}

	/*
	 * public Driver getInstance(Person person, WorkingHours workHours, int
	 * capacity, int workDuration) {
	 * 
	 * person = new Person(person.getFirstName(), person.getLastName(),
	 * person.getID()); //phoneNumber = "07778533425"; capacity = 60; workDuration =
	 * 9; LocalDate date = LocalDate.now(); Date startTime = new Date (120000); Date
	 * endTime = new Date (150000); WorkingHours wh = new WorkingHours(date,
	 * startTime, endTime); Driver d1 = new Driver(firstName, lastName, ID, wh,
	 * capacity, workDuration); return d1;
	 * 
	 * }
	 */


}

