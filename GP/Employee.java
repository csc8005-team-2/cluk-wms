package GP;
import java.util.Calendar;

public class Employee extends Person{

	protected final int breakTime = 45;//mins
	protected Calendar workDay;
	
	public Employee(String firstName, String lastName, int ID){
		super(firstName, lastName, ID);
	
	}

}

