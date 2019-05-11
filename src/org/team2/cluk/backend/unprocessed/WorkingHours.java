package org.team2.cluk.backend.unprocessed;

import org.team2.cluk.backend.tools.DbConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.sql.*;

/*
* WorkingHours class for the drivers
* Shows the start time and end time of driver shifts
*/

public class WorkingHours {

	private Date date;
	private Date startTime;
	private Date endTime;
	private final String driverId;


	public WorkingHours(Connection connection, Date date, Date startTime, Date endTime, String driverId) {
		this.date = new Date();
		this.startTime = new Date(startTime.getTime());
		this.endTime = new Date(endTime.getTime());
		this.driverId = driverId;
	}

	/*
	* accessor methods to get start time, end time and date
	*/
	public Date getStartTime() {
		return startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public Date getDate() { return date; }

	public String getDriverId(){ return driverId; }


/*	*//*
	*method to print out the current day and date in the format shown
	*@param id of the driver
	*//*


	public void printCurrentDate(int id) throws SQLException{

		java.util.Date orderDate = new java.util.Date();
		java.text.SimpleDateFormat date = new java.text.SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		String currentDate = date.format(c.getTime());

		try {
			c.setTime(date.parse(currentDate));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//System.out.println(DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy").format(date));

		// fetch db connection
		Connection connection = DbConnection.getConnection();

		Statement statement1 = null;
		String query1 = "SELECT date " +
				"FROM WorkingHours " +
				"WHERE id ='" + id+"'";

		try {
			statement1 = DbConnection.getConnection().createStatement();
			ResultSet rs = statement1.executeQuery(query1);

			while (rs.next()) {
				String LocalDate = rs.getString("date");
				System.out.println("Workday for driver " + id + "is " + LocalDate + "\n");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (statement1 != null) {
				statement1.close();
			}
		}
	}

         *//* method to print a driver's shift start time
	 * @param id of the driver
	 *//*
	public void printStartTime(int id) throws SQLException{

		// fetch db connection
		Connection connection = DbConnection.getConnection();

			Statement statement3 = null;
			String query3 = "SELECT startTime " +
					"FROM WorkingHours " +
					"WHERE id ='" + id + "'";

			try {
				statement3 = DbConnection.getConnection().createStatement();
				ResultSet rs = statement3.executeQuery(query3);
				while (rs.next()) {
					String StartTime = rs.getString("startTime");
					System.out.println("Driver " + id + "'s start time is " + StartTime + ".\n");

				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (statement3 != null) {
					statement3.close();
				}
			}

	}

	*//*
	* method to update a driver's shift start time
	* @param hour1, min1, sec1 specific time for the shift start time 
	* @param id of the driver
	*//*
	public void updateStartTime(int hour1, int min1, int sec1, int id) throws SQLException {

		// fetch db connection
		Connection connection = DbConnection.getConnection();

		Calendar c1 = Calendar.getInstance();

		c1.set(Calendar.HOUR, hour1);
		c1.set(Calendar.MINUTE, min1);
		c1.set(Calendar.SECOND, sec1);
		String startTime = c1.getTime().toString();

		try {

			Date dateFormat = new SimpleDateFormat("HH:mm:ss").parse(startTime);
			//System.out.println(dateFormat.format(startTime));
			java.sql.Date sqlStartTime = new java.sql.Date(dateFormat.getTime());
			PreparedStatement p = connection.prepareStatement("UPDATE WorkingHours " +
					"SET startTime ='" + sqlStartTime +
					"'WHERE id ='" + id + "'");
			p.setDate(1, sqlStartTime);
			System.out.println("Driver " + id + "'s shift start time is updated to " + sqlStartTime + "\n");

		} catch (ParseException e) {
			e.printStackTrace();
		}
	}


	 *//*
         * method to print a driver's shift end time
	 * @param id of the driver
	 *//*
	public void printEndTime(int id) throws SQLException{

		// fetch db connection
		Connection connection = DbConnection.getConnection();

		Statement statement3 = null;
		String query3 = "SELECT endTime " +
				"FROM WorkingHours " +
				"WHERE id ='" + id + "'";

		try {
			statement3 = DbConnection.getConnection().createStatement();
			ResultSet rs = statement3.executeQuery(query3);
			while (rs.next()) {
				String EndTime = rs.getString("endTime");
				System.out.println("Driver " + id + "'s start time is " + EndTime + ".\n");

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (statement3 != null) {
				statement3.close();
			}
		}
	}


	*//*
	* method to update a driver's shift end time
	* @param hour1, min1, sec1 specific time for the shift end time 
	* @param id of the driver
	*//*
	public void updateEndTime(int hour2, int min2, int sec2, int id) throws SQLException{

		// fetch db connection
		Connection connection = DbConnection.getConnection();

		Calendar c2 = Calendar.getInstance();

		c2.set(Calendar.HOUR, hour2);
		c2.set(Calendar.MINUTE, min2);
		c2.set(Calendar.SECOND, sec2);
		String endTime = c2.getTime().toString();

		try {

			Date dateFormat = new SimpleDateFormat("HH:mm:ss").parse(endTime);
			//System.out.println(dateFormat.format(endTime));
			java.sql.Date sqlEndTime = new java.sql.Date(dateFormat.getTime());
			PreparedStatement p = connection.prepareStatement("UPDATE WorkingHours " +
					"SET endTime ='" + sqlEndTime +
					"'WHERE id ='" + id + "'");
			p.setDate(1, sqlEndTime);
			System.out.println("Driver " + id + "'s shift end time is updated to " + sqlEndTime + "\n");

		} catch (ParseException e) {
			e.printStackTrace();
		}

	}*/
}

