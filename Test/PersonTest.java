package Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import GP.Person;

class PersonTest {

	public static Person person1;
	public static Person person2;
	public static Person person3;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		person1 = Person.getInstance("James", "Paul", 0001);
		person2 = Person.getInstance("Rogen", "Scholes", 0002);
		person3 = Person.getInstance("David", "Britten", 0003);

	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@Test
	void testGetFirstName() {
		assertEquals("James", person1.getFirstName());
		assertEquals("Rogen", person2.getFirstName());
		assertEquals("David", person3.getFirstName());

	}

	@Test
	void testGetLastName() {
		assertEquals("Paul", person1.getLastName());
		assertEquals("Scholes", person2.getLastName());
		assertEquals("Britten", person3.getLastName());

	}

	@Test
	void testGetID() {
		assertEquals(0001, person1.getID());
		assertEquals(0002, person2.getID());
		assertEquals(0003, person3.getID());

	}
	
	@Test
	public void testGetInstance() {
		assertNotNull(person1);
		assertNotNull(person2);
		assertNotNull(person3);

	}




}
