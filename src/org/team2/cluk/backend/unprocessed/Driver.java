package org.team2.cluk.backend.unprocessed;

import org.team2.cluk.backend.tools.DbConnection;
import org.team2.cluk.backend.tools.JsonTools;
import org.team2.cluk.backend.tools.ServerLog;
import org.team2.cluk.backend.webresources.Authorisation;

import javax.json.*;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.sql.*;

import java.time.LocalDate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Path("/driver")

public class Driver {

	//private String firstName;
	//private String lastName;
	//private final int id;
	//private String phoneNumber;
	//private int availableCapacity;
	//private int assignedOrderCapacity;
	private int workDuration; //mins.
	private final int breakTime = 45; //mins
	//private String region;
	//private boolean availability;
	private final int maxWorkDuration = 600;//10 hours = 600mins, use this to limit assigning order to driver etc
	//private final int maxAvailableCapacity = 500; //not sure of the number but this should be the maximum capacity a driver can have!


	public Driver(/*String firstName, String lastName, int id, String phoneNumber, */int workDuration){
		//this.firstName = firstName;
		//this.lastName = lastName;
		//this.id = id;
		//this.phoneNumber = phoneNumber;
		//this.availableCapacity = availableCapacity;
		//this.assignedOrderCapacity = assignedOrderCapacity;
		this.workDuration = workDuration;
		//this.region = region;
		//this.availability = availability;
	}


	@GET
	@Path("/add-driver-info")
	@Produces("application/json")
	//method to add a driver's information to the driver table
	public Response addDriverInfo(@HeaderParam("Authorisation") String idToken, @HeaderParam("firstName") String firstName, @HeaderParam("lastName") String lastName, @HeaderParam("driverId") Integer driverId, @HeaderParam("phoneNumber") String phoneNumber, @HeaderParam("workDuration") int workDuration, String requestBody){

		if (!Authorisation.checkAccess(idToken, "warehouse")) {
			return Response.status(Response.Status.UNAUTHORIZED).entity("Cannot get access").build();
		}

		ServerLog.writeLog("Adding driver info to driver table ");
		// fetch db connection
		Connection connection = DbConnection.getConnection();
		// parse request body
		JsonArray infoToAdd = JsonTools.parseArray(requestBody);

		boolean infoAddition = false;

		for (JsonValue entry : infoToAdd) {
			// check if array entry is a JSON
			if (!(entry instanceof JsonObject)) {
				ServerLog.writeLog("Driver info entry misidentified. Skipping entry!");
				continue;
			}

			JsonObject entryObj = (JsonObject) entry;

			// check if JSON correctly specified
			if (!(entryObj.containsKey("firstName") && entryObj.containsKey("lastName") && entryObj.containsKey("driverId") && entryObj.containsKey("phoneNumber") && entryObj.containsKey("workDuration") /*&& entryObj.containsKey("region")*/)) {
				ServerLog.writeLog("Driver entry misidentified. Skipping entry!");
				continue;
			}

			firstName = entryObj.getString("firstName");
			lastName = entryObj.getString("lastName");
			driverId = entryObj.getInt("driverId");
			phoneNumber = entryObj.getString("phoneNumber");
			workDuration = entryObj.getInt("workDuration");
			//region = entryObj.getString("region");


			Statement statement = null;
			String query = "INSERT INTO Driver (firstName, lastName, driverId, phoneNumber, availableCapacity, assignedOrderCapacity, workDuration, availability) " +
					"SELECT '" + firstName + "', '" + lastName + "', '" + driverId + "', '" + phoneNumber + "', '" + workDuration + "', '";
			try {
				statement = DbConnection.getConnection().createStatement();
				statement.executeQuery(query);
				ServerLog.writeLog("Driver information " + driverId + "has been added to the database");
				infoAddition = true;

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (statement != null) {
					try {
						statement.close();
					} catch (SQLException e) {
						ServerLog.writeLog("SQL exception occurred when closing SQL statement");
					}
				}
			}
		}
		return Response.status(Response.Status.OK).entity(response.toString()).build();
	}


	@POST
	@Path("/remove-driver-info")
	//method to remove a driver's information from the table
	public Response removeDriverInfo(@HeaderParam("Authorisation") String idToken, @HeaderParam("driverId") Integer driverId, String requestBody){

		if (!Authorisation.checkAccess(idToken, "warehouse")){
			return Response.status(Response.Status.UNAUTHORIZED).entity("Cannot get permission").build();
		}

			ServerLog.writeLog("Removing driver info from driver table ");
			// fetch db connection
			Connection connection = DbConnection.getConnection();
			// parse request body
			JsonArray infoToRemove = JsonTools.parseArray(requestBody);

			boolean infoRemoval = false;
			for (JsonValue removal : infoToRemove) {
				// check if array removal is a JSON
				if (!(removal instanceof JsonObject)) {
					ServerLog.writeLog("Driver info removal misidentified. Skipping removal!");
					continue;
				}

				Statement statement = null;
				String query = "DELETE FROM Driver WHERE driverId = '" + driverId + "'";

				try {
					statement = DbConnection.getConnection().createStatement();
					statement.executeUpdate(query);
					ServerLog.writeLog("Driver information driverId " + driverId + "has been removed from the database");
					infoRemoval = true;

				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					if (statement != null) {
						try {
							statement.close();
						} catch (SQLException e) {
							ServerLog.writeLog("SQL exception occurred when closing SQL statement");
						}
					}
				}
			}

		JsonArray response = responseBuilder.build();
		// return Response OK if everything is alright
		return Response.status(Response.Status.OK).entity(response.toString()).build();
	}


	@GET
	@Path("/get-first-name")
	@Produces("application/json")

	//method to print a driver's first name using the driver's id
	public Response getFirstName(@HeaderParam("Authorisation") String idToken, @HeaderParam("driverId") Integer driverId){

		if (!Authorisation.checkAccess(idToken, "warehouse") || !Authorisation.checkAccess(idToken, "restaurant")){
			return Response.status(Response.Status.UNAUTHORIZED).entity("Cannot get permission").build();
		}

		ServerLog.writeLog("Requested the first name of driver" + driverId);

			if (driverId == null) {
				ServerLog.writeLog("Rejected request as driver id not specified");
				return Response.status(Response.Status.BAD_REQUEST).entity("ID_BLANK_OR_NOT_PROVIDED").build();
			}

			JsonArrayBuilder responseBuilder = Json.createArrayBuilder();
			// fetch current database connection
			Connection connection = DbConnection.getConnection();

			Statement statement = null;
			String query = "SELECT firstName " +
					"FROM Driver " +
					"WHERE driverId ='" + driverId + "'";

			try {
				statement = DbConnection.getConnection().createStatement();
				ResultSet rs = statement.executeQuery(query);
				while (rs.next()) {
					JsonObjectBuilder arrayEntryBuilder = Json.createObjectBuilder();

					String firstName = rs.getString("firstName");

					arrayEntryBuilder.add("firstName", firstName);

					JsonObject arrayEntry = arrayEntryBuilder.build();
					responseBuilder.add(arrayEntry);

					//System.out.println("Driver " + driverId + "'s first name is " + firstName + ".\n");

				}
			} catch (SQLException e) {
				ServerLog.writeLog("SQL exception occurred when executing query");
				e.printStackTrace();
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("SQL Exception occurred when executing query").build();

			} finally {
				if (statement != null) {
					try {
						statement.close();
					} catch (SQLException e) {
						ServerLog.writeLog("SQL exception occurred when closing SQL statement");
					}
				}

			}
			JsonArray response = responseBuilder.build();

			return Response.status(Response.Status.OK).entity(response.toString()).build();
	}

	// method to update a driver's first name using the driverId and new first name
	@Path("/update-first-name")
	@POST
	@Consumes("application/json")
	public Response updateFirstName(@HeaderParam("Authorisation") String idToken, @HeaderParam("driverId") Integer driverId, @HeaderParam("firstName") String firstName, String firstNameObject){

		if (!Authorisation.checkAccess(idToken, "warehouse")){
			return Response.status(Response.Status.UNAUTHORIZED).entity("Cannot get permission").build();
		}

			ServerLog.writeLog("Requested an update of the first name of driver" + driverId);
			// fetch db connection
			Connection connection = DbConnection.getConnection();
			Statement statement = null;
			JsonObject firstNameObject = JsonTools.parseObject(firstNameObject);

			if (!(firstNameObject.containsKey("firstName")) {
				return Response.status(Response.Status.BAD_REQUEST).entity("REQUEST_MISSPECIFIED").build();
			}

			String newFirstName = firstNameObject.getString("firstName");
			String query = "UPDATE Driver " +
					"SET firstName ='" + newFirstName +
					"'WHERE driverId='" + driverId + "'";
			try {
				statement = DbConnection.getConnection().createStatement();
				statement.executeUpdate(query);
				ServerLog.writeLog("Driver " + driverId + "'s first name has been updated to: " + newFirstName);
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (statement != null) {
					statement.close();
				}
			}
		}


	@GET
	@Path("/get-last-name")
	@Produces("application/json")

	//method to print a driver's last name using the driver's id
	public Response getLastName(@HeaderParam("Authorisation") String idToken, @HeaderParam("driverId") Integer driverId){

		if (!Authorisation.checkAccess(idToken, "warehouse") || !Authorisation.checkAccess(idToken, "restaurant")){
			return Response.status(Response.Status.UNAUTHORIZED).entity("Cannot get permission").build();
		}

			ServerLog.writeLog("Requested the last name of driver" + driverId);

			if (driverId == null) {
				ServerLog.writeLog("Rejected request as driverId not specified");
				return Response.status(Response.Status.BAD_REQUEST).entity("ID_BLANK_OR_NOT_PROVIDED").build();
			}

			JsonArrayBuilder responseBuilder = Json.createArrayBuilder();
			// fetch current database connection
			Connection connection = DbConnection.getConnection();

			Statement statement = null;
			String query = "SELECT lastName " +
					"FROM Driver " +
					"WHERE driverId ='" + driverId + "'";

			try {
				statement = DbConnection.getConnection().createStatement();
				ResultSet rs = statement.executeQuery(query);
				while (rs.next()) {
					JsonObjectBuilder arrayEntryBuilder = Json.createObjectBuilder();

					String lastName = rs.getString("lastName");

					arrayEntryBuilder.add("lastName", lastName);

					JsonObject arrayEntry = arrayEntryBuilder.build();
					responseBuilder.add(arrayEntry);

					//System.out.println("Driver " + driverId + "'s last name is " + lastName + ".\n");

				}
			} catch (SQLException e) {
				//ServerLog.writeLog("SQL exception occurred when executing query");
				e.printStackTrace();
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("SQL Exception occurred when executing query").build();

			} finally {
				if (statement != null) {
					try {
						statement.close();
					} catch (SQLException e) {
						ServerLog.writeLog("SQL exception occurred when closing SQL statement");
					}
				}

			}
			JsonArray response = responseBuilder.build();

			return Response.status(Response.Status.OK).entity(response.toString()).build();
		}

	// method to update a driver's phone number
	@Path("/update-last-name")
	@POST
	@Consumes("application/json")
	public Response updateLastName(@HeaderParam("Authorisation") String idToken, @HeaderParam("driverId") Integer driverId, @HeaderParam("lastName") String lastName, String lastNameObject){

		if (!Authorisation.checkAccess(idToken, "warehouse")){
		return Response.status(Response.Status.UNAUTHORIZED).entity("Cannot get permission").build();
		}

			ServerLog.writeLog("Requested an update of the last name of driver" + driverId);
			// fetch db connection
			Connection connection = DbConnection.getConnection();
			Statement statement = null;
			JsonObject lastNameObject = JsonTools.parseObject(lastNameObject);

			if (!(lastNameObject.containsKey("lastName")) {
				return Response.status(Response.Status.BAD_REQUEST).entity("REQUEST_MISSPECIFIED").build();
			}

			String newlastName = lastNameObject.getString("lastName");
			String query = "UPDATE Driver " +
					"SET lastName ='" + newlastName +
					"'WHERE driverId='" + driverId + "'";
			try {
				statement = DbConnection.getConnection().createStatement();
				statement.executeUpdate(query);
				ServerLog.writeLog("Driver " + driverId + "'s first name has been updated to: " + newlastName);
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (statement != null) {
					statement.close();
				}
			}
		}


	@GET
	@Path("/get-phone-number")
	@Produces("application/json")

	//method to print a driver's phone number using driverId
	public Response getPhoneNumber(@HeaderParam("Authorisation") String idToken, @HeaderParam("driverId") Integer driverId){

		if (!Authorisation.checkAccess(idToken, "warehouse") || !Authorisation.checkAccess(idToken, "restaurant")){
		return Response.status(Response.Status.UNAUTHORIZED).entity("Cannot get permission").build();
		}

			ServerLog.writeLog("Requested the phone number of driver " + driverId);

			if (driverId == null ) {
				ServerLog.writeLog("Rejected request as driverId not specified");
				return Response.status(Response.Status.BAD_REQUEST).entity("ID_BLANK_OR_NOT_PROVIDED").build();
			}

			JsonArrayBuilder responseBuilder = Json.createArrayBuilder();
			// fetch current database connection
			Connection connection = DbConnection.getConnection();

			Statement statement = null;
			String query = "SELECT phoneNumber " +
					"FROM Driver " +
					"WHERE driverId ='" + driverId + "'";

			try {
				statement = DbConnection.getConnection().createStatement();
				ResultSet rs = statement.executeQuery(query);
				while (rs.next()) {
					JsonObjectBuilder arrayEntryBuilder = Json.createObjectBuilder();

					String phoneNumber = rs.getString("phoneNumber");

					arrayEntryBuilder.add("phoneNumber", phoneNumber);

					JsonObject arrayEntry = arrayEntryBuilder.build();
					responseBuilder.add(arrayEntry);

					//System.out.println("Driver " + driverId + "'s phone number is " + PhoneNumber + "\n");
				}
			} catch (SQLException e) {
				ServerLog.writeLog("SQL exception occurred when executing query");
				e.printStackTrace();
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("SQL Exception occurred when executing query").build();
			} finally {
				if (statement != null) {
					try {
						statement.close();
					} catch (SQLException e) {
						ServerLog.writeLog("SQL exception occurred when closing SQL statement");
					}
				}
			}
			JsonArray response = responseBuilder.build();

			return Response.status(Response.Status.OK).entity(response.toString()).build();
		}

	// method to update a driver's phone number
	@Path("/update-phone-number")
	@POST
	@Consumes("application/json")
	public Response updatePhoneNumber(@HeaderParam("Authorisation") String idToken, @HeaderParam("driverId") Integer driverId, @HeaderParam("phoneNumber") String phoneNumber, String phoneNumberObject) throws SQLException{

		if (!Authorisation.checkAccess(idToken, "warehouse")){
		return Response.status(Response.Status.UNAUTHORIZED).entity("Cannot get permission").build();
		}
			ServerLog.writeLog("Requested an update of the phone number of driver" + driverId);

			// fetch db connection
			Connection connection = DbConnection.getConnection();
			Statement statement = null;
			JsonObject phoneNumberObject = JsonTools.parseObject(phoneNumberObject);

			if (!(phoneNumberObject.containsKey("phoneNumber")) {
				return Response.status(Response.Status.BAD_REQUEST).entity("REQUEST_MISSPECIFIED").build();
			}

			String newPhoneNumber = phoneNumberObject.getString("phoneNumber");
			String query = "UPDATE Driver " +
					"SET phoneNumber ='" + newPhoneNumber +
					"'WHERE driverId='" + driverId + "'";
			try {
				statement = DbConnection.getConnection().createStatement();
				statement.executeUpdate(query);
				ServerLog.writeLog("Driver " + driverId + "'s phone number has been updated to: " + newPhoneNumber);
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (statement != null) {
					statement.close();
				}
			}
	}

	@GET
	@Path("/get-work-duration")
	@Produces("application/json")

	//method to print a driver's current work duration
	public Response getWorkDuration(@HeaderParam("Authorisation") String idToken, @HeaderParam("driverId") Integer driverId, @HeaderParam("w") WorkingHours w){

		if (!Authorisation.checkAccess(idToken, "warehouse") || !Authorisation.checkAccess(idToken, "restaurant")){
		return Response.status(Response.Status.UNAUTHORIZED).entity("Cannot get permission").build();
		}
			ServerLog.writeLog("Requested information on work duration of driver of " + driverId);

			if (driverId == null) {
				ServerLog.writeLog("Rejected request as driverId not specified");
				return Response.status(Response.Status.BAD_REQUEST).entity("ID_BLANK_OR_NOT_PROVIDED").build();
			}

			JsonArrayBuilder responseBuilder = Json.createArrayBuilder();
			// fetch current database connection
			Connection connection = DbConnection.getConnection();

			DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
			int startTime = Integer.parseInt(formatter.format(w.getStartTime()));
			int endTime = Integer.parseInt(formatter.format(w.getEndTime()));
			workDuration = endTime - startTime;

			Statement statement = null;

			String query = "SELECT date" +
					"FROM WorkingHours" +
					"WHERE driverId = driverId";
			try {
				statement = DbConnection.getConnection().createStatement();
				ResultSet rs = statement.executeQuery(query);

				while (rs.next()) {
					JsonObjectBuilder arrayEntryBuilder = Json.createObjectBuilder();

					int WorkDuration = rs.getInt(workDuration);

					arrayEntryBuilder.add("workDuration", workDuration);

					//System.out.println(" Driver " + driverId + "'s work duration so far is " + WorkDuration + "\n");
				}
			} catch (SQLException e) {
				ServerLog.writeLog("SQL exception occurred when executing query");
				e.printStackTrace();
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("SQL Exception occurred when executing query").build();
			} finally {
				if (statement != null) {
					try {
						statement.close();
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
	//basically updating the workDuration field
	//this method would only update the workDuration field while goOnBreak is false and workDuration hasn't exceeded
	//the max which means that the driver can only take one break per day which happens after the they have worked
	//270 mins = 4.5 hours and obviosuly the driver cannot go on break unless their workDuration reaches 270 mins.

	public Response goOnBreak(@HeaderParam("Authorisation") String idToken, @HeaderParam("driverId") Integer driverId, @HeaderParam("w") WorkingHours w) throws SQLException{

		if (!Authorisation.checkAccess(idToken, "warehouse")){
		return Response.status(Response.Status.UNAUTHORIZED).entity("Cannot get permission").build();
		}
			// fetch current db connection
			Connection connection = DbConnection.getConnection();

			DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
			int startTime = Integer.parseInt(formatter.format(w.getStartTime()));
			int endTime = Integer.parseInt(formatter.format(w.getEndTime()));
			workDuration = endTime - startTime;

			//check if driver has worked 4.5 hours before going on break
			boolean goOnBreak = false;
			while (!goOnBreak && workDuration < maxWorkDuration) {
				//4.5hours = 270mins
				if (workDuration == 270) {

					Statement statement = null;
					String query = "SELECT workDuration" +
							"FROM Driver " +
							"WHERE driverId='" + driverId + "'";

					try {
						statement = DbConnection.getConnection().createStatement();
						ResultSet rs = statement.executeQuery(query);

						rs.next();
						int WorkDuration = rs.getInt(workDuration);

						//System.out.println("Previous work duration of driver " + driverId + "\t" + "is" + WorkDuration + "\n");

						int newWorkDuration = WorkDuration + breakTime;

						Statement statement2 = null;
						String query2 = "UPDATE Driver " +
								"SET workDuration ='" + newWorkDuration +
								"'WHERE driverId='" + driverId + "'";

						try {
							statement2 = (Statement) DbConnection.getConnection();
							statement2.executeUpdate(query2);
							ServerLog.writeLog("Updated work Duration for Driver " + driverId + "is" + newWorkDuration + " after going on break");
							goOnBreak = true;

						} catch (SQLException e) {
							ServerLog.writeLog("Error when updating work duration of Driver: " + driverId);
							e.printStackTrace();
							response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("UPDATE_WORKDURATION_ERROR");
						} finally {
							if (statement != null) {
								try {
									statement.close();
								} catch (SQLException e) {
									ServerLog.writeLog("SQL exception occurred when closing SQL statement");
								}
							}
						} finally{
							if (statement != null) {
								try {
									statement.close();
								} catch (SQLException e) {
									ServerLog.writeLog("SQL exception occurred when closing SQL statement");
								}
							}
						}
					}
				}

				if (goOnBreak) {
					ServerLog.writeLog("Driver" + driverId + " went on break today. ");
					response = Response.status(Response.Status.OK).entity("BREAK_TAKEN");
				}

				return response.build();
			}
		}

	@Path("/assign-order-to-driver")
	@POST
	//method to assign order to driver by first checking for orders that are to be delivered today and the orderstatus
	// is approved, then use the orderId to get the corresponding restaurantAddress in the orders table, thereafter
	// getting the restaurantAddress from Restaurant table that matches the one in orders table where the region is north
	//and south individually

	public Response assignOrderToDriver(@HeaderParam("Authorisation") String idToken, String requestBody) {

		if (!Authorisation.checkAccess(idToken, "warehouse")){
		return Response.status(Response.Status.UNAUTHORIZED).entity("Cannot get permission").build();
		}

			Response.ResponseBuilder res = null;
			Connection connection = DbConnection.getConnection();

			JsonObject requestJson = JsonTools.parseObject(requestBody);

			if (!(requestJson.containsKey("orderId") || requestJson.containsKey("driverId"))) {
				return Response.status(Response.Status.BAD_REQUEST).entity("REQUEST_MISSPECIFIED").build();
			}

			int orderId = requestJson.getInt("orderId");
			Integer driverId = requestJson.getInt("driverId");

			//get today's date
			LocalDate date = getLocalDate();
			Date dateFormat = new DateTimeFormatter.ofPattern("yyyy-MM-dd").format(date);

			//get orderId from stockOrders table where the orderDeliveryDate is today's date and orderSatatus is approved for delivery.
			Statement statement = null;
			String query = "SELECT orderId" +
					"FROM StockOrders" +
					"WHERE orderDeliveryDate='" + dateFormat + " AND orderStatus= approved'";
			try {
				statement = DbConnection.getConnection().createStatement();
				ResultSet rs = statement.executeQuery(query);

				while (rs.next()) {
					int orderIdStockOrders = rs.getInt("orderId");

					//get the restaurantAddress where the orderId from orders table is equal to the orderId from stockOrders table.
					Statement statement1 = null;
					String query1 = "SELECT restaurantAddress " +
							"FROM Orders " +
							"WHERE orderid ='" + orderIdStockOrders + "'";

					try {
						statement1 = DbConnection.getConnection().createStatement();
						ResultSet rs1 = statement1.executeQuery(query1);

						while (rs.next()) {
							String restaurantAddressOrdersTable = rs1.getString("restaurantAddress");

							//get the restaurant address in orders table which is equal to that in Restaurant table and where the region is north
							Statement statement2 = null;
							String query2 = "SELECT restaurantAddress" +
									"FROM Restaurant " +
									"WHERE restaurantAddress = '" + restaurantAddressOrdersTable /*+ " AND region= north*/
							'";
							try {
								statement2 = DbConnection.getConnection().createSatement();
								ResultSet rs2 = statement2.executeQuery(query2);

								while (rs2.next()) {
									String restaurantAddressRestaurantTable = rs2.getString("restaurantAddress");

									//get the restaurant address in orders table which is equal to that in Restaurant table and where the region is south
									Statement statement3 = null;
									String query3 = "SELECT restaurantAddress" +
											"FROM Restaurant " +
											"WHERE restaurantAddress = '" + restaurantAddressOrdersTable + "'";
									try {
										statement3 = DbConnection.getConnection().createSatement();
										ResultSet rs3 = statement3.executeQuery(query3);

										while (rs3.next()) {
											restaurantAddressRestaurantTable = rs3.getString("restaurantAddress");
										}
									} catch (SQLException e) {
										e.printStackTrace();
									}

								}
							} catch (SQLException e) {
								e.printStackTrace();
							}

						}
					} catch (SQLException e) {
						e.printStackTrace();
					}

				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}






		/*//hashmap? to assign orderId to driverId??
		HashMap<Integer, Integer> assignedOrder = new HashMap<>();

		Statement statement4 = null;
		String query4 = "SELECT id" +
				"FROM Driver" +
				"WHERE region = 'north' OR region = 'south'";
		try {
			statement4 = connection.createStatement();
			ResultSet rs4 = statement4.executeQuery(query4);
			while (rs4.next()) {
				int driverid = rs4.getInt("id");
				assignedOrder.put(driverid, orderId);
				ServerLog.writeLog("Order " + orderId + " has been assigned to driver: " + driverid);
			}
		} catch (SQLException e) {
			ServerLog.writeLog("SQL exception occurred when assigning order to driver");
			e.printStackTrace();
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("ASSIGNED_ORDER_QUERY_ERROR");
		} finally {
			if (statement != null) {
				try {
					statement.close();
				}
				catch (SQLException e) {
					ServerLog.writeLog("SQL exception occurred when closing SQL statement");
				}
			}
		}*/

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


	/*@GET
	@Path("/get-region")
	@Produces("application/json")

	//method to print a driver's region using id either North or South
	public Response getRegion(@HeaderParam("Authorization") String idToken, @HeaderParam("id") int id) throws SQLException{

		ServerLog.writeLog("Requested information on region of driver " + id);

		if (id.equals(null)) {
			ServerLog.writeLog("Rejected request as driver's id not specified");
			return Response.status(Response.Status.BAD_REQUEST).entity("ID_BLANK_OR_NOT_PROVIDED").build();
		}

		JsonArrayBuilder responseBuilder = Json.createArrayBuilder();
		// fetch current database connection
		Connection connection = DbConnection.getConnection();

		Statement statement = null;
		String query = "SELECT region " +
				"FROM Driver " +
				"WHERE id ='" + id + "'";

		try {
			statement = Dbconnection.getConnection().createStatement();
			ResultSet rs = statement.executeQuery(query);
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

	// method to update a driver's region
	@Path("/update-region")
	@POST
	@Consumes("application/json")
	public Response updateLastName(@HeaderParam("Authorisation") String idToken, @HeaderParam("id") int id, @HeaderParam("region") String region, String regionObject) throws SQLException{

		// fetch db connection
		Connection connection = DbConnection.getConnection();
		Statement statement = null;
		JsonObject regionObject = JsonTools.parseObject(regionObject);

		if (!(regionObject.containsKey("region")) {
			return Response.status(Response.Status.BAD_REQUEST).entity("REQUEST_MISSPECIFIED").build();
		}

		String newRegion = lastNameObject.getString("region");
		String query = "UPDATE Driver " +
				"SET region ='" + newRegion +
				"'WHERE id='" + id + "'";
		try {
			statement = this.connection.createStatement();
			statement.executeUpdate(query);
			ServerLog.writeLog("Driver " + id + "'s region has been updated to: " + newRegion);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (statement != null) {
				statement.close();
			}
		}
	}*/


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
