package Test;
import java.time.LocalDate;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

//import GP.WorkingHours;

class WorkingHoursTest {
	
	public static LocalDate date1;
	public static Date startTime1;
	public static Date endTime1;


	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		date1 = LocalDate.now();
		startTime1 = new Date(070000);
		endTime1 = new Date(170000);
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@Test
	void testGetDate() {
		assertEquals(21/03/2019, date1);
	}

	@Test
	void testGetStartTime() {
		assertEquals(070000, startTime1.getTime());
	}

	@Test
	void testGetEndTime() {
		assertEquals(170000, endTime1.getTime());
	}

	
	@Test
	void testGetInstance() {
		assertNotNull(date1);
		assertNotNull(startTime1);
		assertNotNull(endTime1);

		
	}

}
