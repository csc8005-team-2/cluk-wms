package GP;


public class Person {

	protected String firstName;
	protected String lastName;
	protected int ID;
	
	public Person(String firstName, String lastName, int ID){
		
		this.firstName = firstName;
		this.lastName = lastName;
		this.ID = ID;
	}
	
	public String getFirstName(){
		return firstName;
	}
	
	public String getLastName(){
		return lastName;
	}
	
	public int getID(){
		return ID;
	}
	
	public void setFirstName(String firstName){
		this.firstName = firstName;
	}
	
	public void setLastName(String lastName){
		this.lastName = lastName;
	}
	
	public void setID(int ID){
		this.ID = ID;
	}
	
	public void getInstance() {
		
	}
	
	public static Person getInstance(String firstName, String lastName, int ID) {
		
		Person person = new Person(firstName, lastName, ID);
		
		return person;
	}
}

