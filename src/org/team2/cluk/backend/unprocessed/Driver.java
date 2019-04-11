package org.team2.cluk.backend.unprocessed;

import org.team2.cluk.backend.tools.DbConnection;
import org.team2.cluk.backend.tools.JsonTools;
import org.team2.cluk.backend.tools.ServerLog;

import javax.json.*;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.sql.*;

import java.time.LocalDate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.sql.*;

@Path("/driver")

public class Driver {

	private Connection connection;
	private String firstName;
	private String lastName;
	private final int id;
	private String phoneNumber;
	//private int availableCapacity;
	//private int assignedOrderCapacity;
	private int workDuration; //mins.
	private final int breakTime = 45; //mins
	private String region;
	//private boolean availability;
	private final int maxWorkDuration = 600;//10 hours = 600mins, use this to limit assigning order to driver etc
	//private final int maxAvailableCapacity = 500; //not sure of the number but this should be the maximum capacity a driver can have!


	public Driver(Connection connection, String firstName, String lastName, int id, String phoneNumber, int workDuration, String region){
		this.connection = connection;
		this.firstName = firstName;
		this.lastName = lastName;
		this.id = id;
		this.phoneNumber = phoneNumber;
		//this.availableCapacity = availableCapacity;
		//this.assignedOrderCapacity = assignedOrderCapacity;
		this.workDuration = workDuration;
		this.region = region;
		//this.availability = availability;
	}


	@GET
	@Path("/add-driver-info")
	@Produces("application/json")
	//method to add a driver's information to the driver table
	public Response addDriverInfo(@HeaderParam("Authorization") String idToken, @HeaderParam("firstName") String firstName, @HeaderParam("lastName") String lastName, @HeaderParam("id") int id, @HeaderParam("phoneNumber") String phoneNumber, @HeaderParam("workDuration") int workDuration, @HeaderParam("region") String region) throws SQLException {

		Statement statement1 = null;
		String query1 = "INSERT INTO Driver (firstName, lastName, id, phoneNumber, availableCapacity, assignedOrderCapacity, workDuration, region, availability) " +
				"SELECT '"+ firstName + "', '" + lastName + "', '" + id + "', '" + phoneNumber + "', '" + availableCapacity + "', '" + assignedOrderCapacity + "', '" + workDuration + "', '" + region + "', '" + availability + "')";
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

	//method to remove a driver's information from the table
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


	@GET
	@Path("/get-first-name")
	@Produces("application/json")

	//method to print a driver's first name using the driver's id
	public Response getFirstName(@HeaderParam("Authorisation") String idToken, @HeaderParam("id") int id) throws SQLException{

		ServerLog.writeLog("Requested information of the driver's first name of " + id);

		if (id.isBlank()) {
			ServerLog.writeLog("Rejected request as driver id not specified");
			return Response.status(Response.Status.BAD_REQUEST).entity("ID_BLANK_OR_NOT_PROVIDED").build();
		}

		JsonArrayBuilder responseBuilder = Json.createArrayBuilder();
		// fetch current database connection
		Connection connection = DbConnection.getConnection();

		Statement statement3 = null;
		String query3 = "SELECT firstName " +
				"FROM Driver " +
				"WHERE id ='" + id + "'";

		try {
			statement3 = Dbconnection.getConnection().createStatement();
			ResultSet rs = statement3.executeQuery(query3);
			while (rs.next()) {	
				JsonObjectBuilder arrayEntryBuilder = Json.createObjectBuilder();

				String firstName = rs.getString("firstName");

				arrayEntryBuilder.add("firstName", firstName);

				JsonObject arrayEntry = arrayEntryBuilder.build();
				responseBuilder.add(arrayEntry);

				//System.out.println("Driver " + id + "'s first name is " + firstName + ".\n");

			}
		} catch (SQLException e) {
			ServerLog.writeLog("SQL exception occurred when executing query");
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("SQL Exception occurred when executing query").build();

		} finally {
			if (statement3 != null) {
				try{
					statement3.close();
				}catch (SQLException e) {
					ServerLog.writeLog("SQL exception occurred when closing SQL statement");
				}
			}

		}
		JsonArray response = responseBuilder.build();

		return Response.status(Response.Status.OK).entity(response.toString()).build();

	}

	// method to update a driver's first name using the id and new first name
	public void updateFirstName(@HeaderParam("Authorisation") String idToken, @HeaderParam("id") int id, @HeaderParam("firstName") String firstName) throws SQLException{

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

	@GET
	@Path("/get-last-name")
	@Produces("application/json")

	//method to print a driver's last name using the driver's id
	public Response getLastName(@HeaderParam("Authorisation") String idToken, @HeaderParam("id") int id) throws SQLException{

		ServerLog.writeLog("Requested information of the driver's last name of " + id);

		if (id.isBlank()) {
			ServerLog.writeLog("Rejected request as driver's id not specified");
			return Response.status(Response.Status.BAD_REQUEST).entity("ID_BLANK_OR_NOT_PROVIDED").build();
		}

		JsonArrayBuilder responseBuilder = Json.createArrayBuilder();
		// fetch current database connection
		Connection connection = DbConnection.getConnection();

		Statement statement6 = null;
		String query6 = "SELECT lastName " +
				"FROM Driver " +
				"WHERE id ='" + id + "'";

		try {
			statement6 = Dbconnection.getConnection().createStatement();
			ResultSet rs = statement6.executeQuery(query6);
			while (rs.next()) {	
				JsonObjectBuilder arrayEntryBuilder = Json.createObjectBuilder();

				String lastName = rs.getString("lastName");

				arrayEntryBuilder.add("lastName", lastName);

				JsonObject arrayEntry = arrayEntryBuilder.build();
				responseBuilder.add(arrayEntry);

				//System.out.println("Driver " + id + "'s last name is " + lastName + ".\n");

			}
		} catch (SQLException e) {
			ServerLog.writeLog("SQL exception occurred when executing query");
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("SQL Exception occurred when executing query").build();

		} finally {
			if (statement6 != null) {
				try{
					statement6.close();
				}catch (SQLException e) {
					ServerLog.writeLog("SQL exception occurred when closing SQL statement");
				}
			}

		}
		JsonArray response = responseBuilder.build();

		return Response.status(Response.Status.OK).entity(response.toString()).build();

	}

	//method to update driver's last name using the id and new last name
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


	@GET
	@Path("/get-phone-number")
	@Produces("application/json")

	//method to print a driver's phone number using id
	public Response getPhoneNumber(@HeaderParam("Authorization") String idToken, @HeaderParam("id") int id) throws SQLException{

		ServerLog.writeLog("Requested information on the driver's phone number of driver " + id);

		if (id.isBlank()) {
			ServerLog.writeLog("Rejected request as driver's id not specified");
			return Response.status(Response.Status.BAD_REQUEST).entity("ID_BLANK_OR_NOT_PROVIDED").build();
		}

		JsonArrayBuilder responseBuilder = Json.createArrayBuilder();
		// fetch current database connection
		Connection connection = DbConnection.getConnection();

		Statement statement9 = null;
		String query9 = "SELECT phoneNumber " +
				"FROM Driver " +
				"WHERE id ='" + id + "'";

		try {
			statement9 = Dbconnection.getConnection().createStatement();
			ResultSet rs = statement9.executeQuery(query9);
			while (rs.next()) {
				JsonObjectBuilder arrayEntryBuilder = Json.createObjectBuilder();

				String PhoneNumber = rs.getString("phoneNumber");

				arrayEntryBuilder.add("phoneNumber", phoneNumber);

				JsonObject arrayEntry = arrayEntryBuilder.build();
				responseBuilder.add(arrayEntry);

				//System.out.println("Driver " + id + "'s phone number is " + PhoneNumber + "\n");
			}
		} catch (SQLException e) {
			ServerLog.writeLog("SQL exception occurred when executing query");
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("SQL Exception occurred when executing query").build();
		} finally {
			if (statement9 != null) {
				try {
					statement9.close();
				} catch (SQLException e) {
					ServerLog.writeLog("SQL exception occurred when closing SQL statement");
				}
			}
		}
		JsonArray response = responseBuilder.build();

		return Response.status(Response.Status.OK).entity(response.toString()).build();
	}

	//method to update a driver's phone number using the id and new phone number
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

	@GET
	@Path("/get-work-duration")
	@Produces("application/json")

	//method to print a driver's current work duration
	public Response getWorkDuration(@HeaderParam("Authorization") String idToken, @HeaderParam("id") int id, @HeaderParam("w") WorkingHours w) throws SQLException{

		ServerLog.writeLog("Requested information on work duration of driver of " + id);

		if (id.isBlank()) {
			ServerLog.writeLog("Rejected request as driver's id not specified");
			return Response.status(Response.Status.BAD_REQUEST).entity("ID_BLANK_OR_NOT_PROVIDED").build();
		}

		JsonArrayBuilder responseBuilder = Json.createArrayBuilder();
		// fetch current database connection
		Connection connection = DbConnection.getConnection();

		DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		int startTime = Integer.parseInt(formatter.format(w.getStartTime()));
		int endTime = Integer.parseInt(formatter.format(w.getEndTime()));
		workDuration = endTime-startTime;

		Statement statement12 = null;

		String query12 = "SELECT date" +
				"FROM WorkingHours" +
				"WHERE id = id";
		try {
			statement12 = Dbconnection.getConnection().createStatement();
			ResultSet rs = statement12.executeQuery(query12);

			while(rs.next()) {
				JsonObjectBuilder arrayEntryBuilder = Json.createObjectBuilder();

				int WorkDuration = rs.getInt(workDuration);

				arrayEntryBuilder.add("workDuration", workDuration);

				//System.out.println(" Driver " + id + "'s work duration so far is " + WorkDuration + "\n");
			}
		} catch (SQLException e) {
			ServerLog.writeLog("SQL exception occurred when executing query");
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("SQL Exception occurred when executing query").build();
		} finally {
			if (statement12 != null) {
				try {
					statement12.close();
				} catch (SQLException e) {
					ServerLog.writeLog("SQL exception occurred when closing SQL statement");
				}
			}
		}
		JsonArray response = responseBuilder.build();

		return Response.status(Response.Status.OK).entity(response.toString()).build();
	}

	@POST
	@Path("go-on-break")
	//method to make drivers go on break, adding the break time to their work duration
	//need to make sure driver's work duration doesn't pass the max of 10hours
	public Response goOnBreak(@HeaderParam("Authorization") String idToken, @HeaderParam("id") int id, @HeaderParam("w") WorkingHours w) throws SQLException{

		// fetch current db connection
		Connection connection = DbConnection.getConnection();

		DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		int startTime = Integer.parseInt(formatter.format(w.getStartTime()));
		int endTime = Integer.parseInt(formatter.format(w.getEndTime()));
		workDuration = endTime-startTime;

		//check if driver has worked 4.5 hours before going on break
		boolean goOnBreak = false;
		while (goOnBreak == false) {
			//4.5hours = 270mins
			if(workDuration == 270) {

				Statement statement13 = null;
				String query13 = "SELECT workDuration" +
						"FROM Driver " +
						"WHERE id='" + id +"'";

				try {
					statement13 = DbConnection.getConnection().createStatement();
					ResultSet rs = statement13.executeQuery(query13);

					rs.next();
					int WorkDuration = rs.getInt(workDuration);

					//System.out.println("Previous work duration of driver " + id + "\t" + "is" + WorkDuration + "\n");

					int newWorkDuration = WorkDuration + breakTime;

					Statement statement14 = null;
					String query14 = "UPDATE Driver " +
							"SET workDuration ='" + newWorkDuration +
							"'WHERE id='" + id+"'";

					try {
						statement14 = DbConnection.getConnection();
						statement14.executeUpdate(query14);
						ServerLog.writeLog("Updated work Duration for Driver " + id + "\t" + "is" + newWorkDuration + " after going on break" + "\n");
						goOnBreak = true;

					} catch (SQLException e) {
						ServerLog.writeLog("Error when updating work duration of Driver: " + id);
						e.printStackTrace();
						response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("UPDATE_WORKDURATION_ERROR");
					} finally {
						if (statement14 != null) {
							try {
								statement14.close();
							} catch (SQLException e) {
								ServerLog.writeLog("SQL exception occurred when closing SQL statement");
							}
						}
					} finally {
						if (statement13 != null) {
							try {
								statement14.close();
							} catch (SQLException e) {
								ServerLog.writeLog("SQL exception occurred when closing SQL statement");
							}
						}
					}
				}

				if (goOnBreak){
					ServerLog.writeLog("Driver" + id + " went on break today. ");
					response = Response.status(Response.Status.OK).entity("BREAK_TAKEN");
				}

				return response.build();

			}
		}
	}

	@Path("/create-meal")
	@GET
	//method to assign order to driver.

	public Response assignOrderToDriver(@HeaderParam("Authorization") String idToken, String requestBody){

		Response.ResponseBuilder res = null;
		Connection connection = DbConnection.getConnection();

		JsonObject requestJson = JsonTools.parseObject(requestBody);

		if (!(requestJson.containsKey("orderId") || requestJson.containsKey("id")))
			return Response.status(Response.Status.BAD_REQUEST).entity("REQUEST_MISSPECIFIED").build();

		int orderId = requestJson.getInt("orderId");
		int id = requestJson.getInt("id");

		Statement statement = null;
		String query = "SELECT orderDeliveryDate" +
				"FROM StockOrders " +
				"WHERE orderId='" + orderId + "'";
		try {
			statement = DbConnection.getConnection().createStatement();
			ResultSet rs = statement.executeQuery(query);

			LocalDate date = getLocalDate();
			Date dateFormat = new DateTimeFormatter.ofPattern("yyyy-MM-dd").format(date);

			if (orderDeliveryDate == dateFormat){

			}
	}

	/*//method to print the available driver capacity after an order has been assigned
	public void printAvailableCapacity(int id) throws SQLException{

		Statement statement15 = null;
		String query15 = "SELECT availableCapacity " +
				"FROM Driver " +
				"WHERE id ='" + id +"'";

		try {
			statement15 = this.connection.createStatement();
			ResultSet rs = statement15.executeQuery(query15);
			while (rs.next()) {
				int availableCapacity = rs.getInt("availableCapacity");
				System.out.println("Driver " + id + "'s car availableCapacity is " + availableCapacity + "\n");

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (statement15 != null) {
				statement15.close();
			}
		}
	}

	//**Need to improve this method to update the available capacity after an order has been assigned to a driver
	public void UpdateAvailableCapacity(int id, int availableCapacity) throws SQLException{

		Statement statement16 = null;
		String query16 = "SELECT availableCapacity " +
				"FROM Driver " +
				"WHERE id ='" + id+ "'";
		try {
			statement16 = this.connection.createStatement();
			ResultSet rs = statement16.executeQuery(query16);

			rs.next();

			int AvailableCapacity = rs.getInt("availableCapacity");
			System.out.println("Previous availableCapacity of Driver " + id + " is " + availableCapacity);
			int newAvailableCapacity = availableCapacity+AvailableCapacity;

			Statement statement17 = null;
			String query17 = "UPDATE Driver " +
					"SET availableCapacity ='" + newAvailableCapacity+
					"' WHERE id='" + id + "'";
			try {
				statement17 = this.connection.createStatement();
				statement17.executeUpdate(query17);
				System.out.println("Updated availableCapacity of Driver "+ id + " is " + newAvailableavailableCapacity);
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

	//this method prints out the capacity of orders assigned to a driver, database should be updated
	public void printAssignedOrderCapacity(int id) throws SQLException{

		Statement statement18 = null;
		String query18 = "SELECT availableCapacity " +
				"FROM Driver " +
				"WHERE id ='" + id +"'";

		try {
			statement18 = this.connection.createStatement();
			ResultSet rs = statement18.executeQuery(query18);
			while (rs.next()) {
				int availableCapacity = rs.getInt("availableCapacity");
				System.out.println("Driver " + id + "'s car availableCapacity is " + availableCapacity + "\n");

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (statement18 != null) {
				statement18.close();
			}
		}
	}

	//**Need to improve this method to update the capacity of assigned orders to the driver
	public void UpdateAssignedOrderCapacity(int id, int assignedOrderCapacity) throws SQLException{

		Statement statement19 = null;
		String query19 = "SELECT availableCapacity " +
				"FROM Driver " +
				"WHERE id ='" + id+ "'";
		try {
			statement19 = this.connection.createStatement();
			ResultSet rs = statement19.executeQuery(query19);

			rs.next();

			int AvailableCapacity = rs.getInt("availableCapacity");
			System.out.println("Previous availableCapacity of Driver " + id + " is " + availableCapacity);
			int newAvailableCapacity = availableCapacity+AvailableCapacity;

			Statement statement20 = null;
			String query20 = "UPDATE Driver " +
					"SET availableCapacity ='" + newAvailableCapacity+
					"' WHERE id='" + id + "'";
			try {
				statement20 = this.connection.createStatement();
				statement20.executeUpdate(query20);
				System.out.println("Updated availableCapacity of Driver "+ id + " is " + newAvailableavailableCapacity);
			} catch (SQLException e ) {
				e.printStackTrace();
			} finally {
				if (statement20 != null) {
					statement20.close();
				}
			}
		} catch (SQLException e ) {
			e.printStackTrace();
		} finally {
			if (statement19 != null) {
				statement19.close();
			}
		}
	}*/


	@GET
	@Path("/get-region")
	@Produces("application/json")

	//method to print a driver's region using id either North or South
	public Response getRegion(@HeaderParam("Authorization") String idToken, @HeaderParam("id") int id) throws SQLException{

		ServerLog.writeLog("Requested information on region of driver " + id);

		if (id.isBlank()) {
			ServerLog.writeLog("Rejected request as driver's id not specified");
			return Response.status(Response.Status.BAD_REQUEST).entity("ID_BLANK_OR_NOT_PROVIDED").build();
		}

		JsonArrayBuilder responseBuilder = Json.createArrayBuilder();
		// fetch current database connection
		Connection connection = DbConnection.getConnection();

		Statement statement21 = null;
		String query21 = "SELECT region " +
				"FROM Driver " +
				"WHERE id ='" + id + "'";

		try {
			statement21 = Dbconnection.getConnection().createStatement();
			ResultSet rs = statement21.executeQuery(query21);
			while (rs.next()) {
				JsonObjectBuilder arrayEntryBuilder = Json.createObjectBuilder();

				String Region = rs.getString("region");

				arrayEntryBuilder.add("region", region);

				JsonObject arrayEntry = arrayEntryBuilder.build();
				responseBuilder.add(arrayEntry);

				//System.out.println("Driver " + id + "'s region " + region + "\n");
			}
		} catch (SQLException e) {
			ServerLog.writeLog("SQL exception occurred when executing query");
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("SQL Exception occurred when executing query").build();
		} finally {
			if (statement21 != null) {
				try {
					statement21.close();
				} catch (SQLException e) {
					ServerLog.writeLog("SQL exception occurred when closing SQL statement");
				}
			}
		}
		JsonArray response = responseBuilder.build();

		return Response.status(Response.Status.OK).entity(response.toString()).build();
	}

	//method to update driver's region using the id and region
	public void updateRegion(int id, String region) throws SQLException{

		Statement statement22 = null;
		String query22 = "SELECT region" +
				"FROM Driver " +
				"WHERE id='" + id +"'";

		try {
			statement22 = this.connection.createStatement();
			ResultSet rs = statement22.executeQuery(query22);

			rs.next();
			String newRegion = rs.getString("region");
			System.out.println("Driver " + id + "'s region is " + region + "\n");

			Statement statement23 = null;
			String query23 = "UPDATE Driver " +
					"SET region ='" + newRegion +
					"'WHERE id='" + id + "'";

			try {
				statement23 = this.connection.createStatement();
				statement23.executeUpdate(query23);
				System.out.println("Driver " + id + "'s region has been updated to " + newRegion + "\n");

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (statement23 != null) {
					statement23.close();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (statement22 != null) {
				statement22.close();
			}
		}
	}



	/*	//method to print a driver's availability using id
	public void printAvailability(int id) throws SQLException{

		Statement statement24 = null;
		String query24 = "SELECT availability " +
				"FROM Driver " +
				"WHERE id ='" + id + "'";

		try {
			statement24 = this.connection.createStatement();
			ResultSet rs = statement24.executeQuery(query24);
			while (rs.next()) {
				String Availability = rs.getString("availability");
				System.out.println("Driver " + id + "'s availability is " + availability + "\n");

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (statement24 != null) {
				statement24.close();
			}
		}
	}*/

	/*	//UPDATE??
	//**Need to update this method to update driver's availability using the driver's work duration and if the available capacity is max
	public void updateAvailability(int id, String availability) throws SQLException{

		Statement statement25 = null;
		String query25 = "SELECT availability" +
				"FROM Driver " +
				"WHERE id='" + id +"'";

		try {
			statement25 = this.connection.createStatement();
			ResultSet rs = statement25.executeQuery(query25);

			rs.next();
			String newAvailability = rs.getString("availability");
			System.out.println("Driver " + id + "'s availability is " + availability + "\n");

			Statement statement26 = null;
			String query26 = "UPDATE Driver " +
					"SET availability ='" + newRegion +
					"'WHERE id='" + id + "'";

			try {
				statement26 = this.connection.createStatement();
				statement26.executeUpdate(query26);
				System.out.println("Driver " + id + "'s availability has been updated to " + newAvailability + "\n");

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (statement26 != null) {
					statement26.close();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (statement25 != null) {
				statement25.close();
			}
		}
	}*/
}