import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.sql.*;

public class WorkingHours {

	private Connection connection;
	private LocalDate date;
	private Date startTime;
	private Date endTime;
	//private int workingHoursID;
	private final int id;


	public WorkingHours(Connection connection, LocalDate date, Date startTime, Date endTime, int id) {
		this.connection = connection;
		this.date = LocalDate.now();
		this.startTime = new Date(startTime.getTime());
		this.endTime = new Date(endTime.getTime());
		this.id = id;
	}

	public Date getStartTime() {
		return startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public LocalDate getLocalDate() { return date; }


	//method to print out the current day and date in the format shown
	public void printCurrentDate(int id) throws SQLException{

		LocalDate date = getLocalDate();
		System.out.println(DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy").format(date));

		Statement statement1 = null;
		String query1 = "SELECT date " +
				"FROM WorkingHours " +
				"WHERE id ='" + id+"'";

		try {
			statement1 = this.connection.createStatement();
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

	/*public void updateDate(int id) throws SQLException{

		Statement statement2 = null;
		String query2 = "SELECT date" +
				"FROM WorkingHours " +
				"WHERE id ='" + id +"'";

		try {
			statement2 = connection.createStatement();
			ResultSet rs = statement2.executeQuery(query2);

			rs.next();
			//String Date = rs.getString("date");
			//System.out.println("Previous workday for Driver " + ID + "\t" + "is" + Date + "\n");
			//LocalDate date = getLocalDate();
			//String Date = rs.getString("date");
			//System.out.println(DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy").format(Date));

			Statement statement3 = null;
			String query3 = "UPDATE WorkingHours " +
					"SET date ='" +  +
					"'WHERE id='" + id + "'";

			try {
				statement3 = this.connection.createStatement();
				statement3.executeUpdate(query3);
				System.out.println("Updated  for Driver " + id + "\t" + "is" +  + "\n");

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (statement3 != null) {
					statement3.close();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (statement2 != null) {
				statement2.close();
			}
		}


	}*/

	//method to print a Driver's shift start time
	public void printStartTime(int id) throws SQLException{

			Statement statement3 = null;
			String query3 = "SELECT startTime " +
					"FROM WorkingHours " +
					"WHERE id ='" + id + "'";

			try {
				statement3 = this.connection.createStatement();
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

	//method to update a driver's shift end time
	public void updateStartTime(int hour1, int min1, int sec1, int id) throws SQLException {

		Calendar c1 = Calendar.getInstance();

		c1.set(Calendar.HOUR, hour1);
		c1.set(Calendar.MINUTE, min1);
		c1.set(Calendar.SECOND, sec1);
		String startTime = c1.getTime().toString();

		try {

			Date dateFormat = new SimpleDateFormat("HH:mm:ss").parse(startTime);
			//System.out.println(dateFormat.format(startTime));
			java.sql.Date sqlStartTime = new java.sql.Date(dateFormat.getTime());
			PreparedStatement p = this.connection.prepareStatement("UPDATE WorkingHours " +
					"SET startTime ='" + sqlStartTime +
					"'WHERE id ='" + id + "'");
			p.setDate(1, sqlStartTime);
			System.out.println("Driver " + id + "'s shift start time is updated to " + sqlStartTime + "\n");

		} catch (ParseException e) {
			e.printStackTrace();
		}
	}


	//method to print a driver's shift end time
	public void printEndTime(int id) throws SQLException{

		Statement statement3 = null;
		String query3 = "SELECT endTime " +
				"FROM WorkingHours " +
				"WHERE id ='" + id + "'";

		try {
			statement3 = this.connection.createStatement();
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


	//method to update a driver's shift end time
	public void updateEndTime(int hour2, int min2, int sec2, int id) throws SQLException{

		Calendar c2 = Calendar.getInstance();

		c2.set(Calendar.HOUR, hour2);
		c2.set(Calendar.MINUTE, min2);
		c2.set(Calendar.SECOND, sec2);
		String endTime = c2.getTime().toString();

		try {

			Date dateFormat = new SimpleDateFormat("HH:mm:ss").parse(endTime);
			//System.out.println(dateFormat.format(endTime));
			java.sql.Date sqlEndTime = new java.sql.Date(dateFormat.getTime());
			PreparedStatement p = this.connection.prepareStatement("UPDATE WorkingHours " +
					"SET endTime ='" + sqlEndTime +
					"'WHERE id ='" + id + "'");
			p.setDate(1, sqlEndTime);
			System.out.println("Driver " + id + "'s shift end time is updated to " + sqlEndTime + "\n");

		} catch (ParseException e) {
			e.printStackTrace();
		}

	}
}

