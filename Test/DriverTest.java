package Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Date;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import GP.Driver;
import GP.Person;
import GP.WorkingHours;

class DriverTest {

	private static int ID;
	public static Driver d1;
	public WorkingHours w1;
	public static Person person;
	private static String firstName;
	public static String lastName;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		person = Driver.getInstance(firstName, lastName, ID);
		String phoneNumber = "07778533425";
		int capacity = 60;
		double workDuration = 270.0;
		LocalDate date = LocalDate.now();
		Date startTime = new Date (120000);
		Date endTime = new Date (163000);
		WorkingHours wh = new WorkingHours(date, startTime, endTime);
		d1 = new Driver(firstName, lastName, ID, wh, phoneNumber, capacity, workDuration);
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@Test
	void testGetPhoneNumber() {
		assertEquals("07778533425", d1.getPhoneNumber());
	}

	@Test
	void testGetWorkDuration() {
		assertEquals(270.0, d1.getWorkDuration(w1));
	}

	@Test
	void testGoOnBreak() {
		assertEquals(true, d1.goOnBreak());
	}

	@Test
	void testGoOnCompulsoryBreak() {
		assertEquals(true, d1.goOnCompulsoryBreak());
	}

	@Test
	void testBreakCount() {
		assertEquals(1, d1.breakCount());
	}

	@Test
	void testManageWorkHours() {
		assertEquals(false, d1.manageWorkHours());
	}

	@Test
	void testGetCapacity() {
		assertEquals(60, d1.getCapacity());
	}

	@Test
	void testGetInstance() {
		assertNotNull(d1);
	}

}
