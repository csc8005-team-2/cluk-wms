package GP;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import GP.WorkingHours;

//import java.util.Date;
//import java.util.Locale;
//import java.text.DateFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;

public class Driver extends Employee{

	private final int breakTime = 45; //mins
	private String phoneNumber;
	private int capacity;
	private double workDuration;
	private int breakCount;

	public Driver(String firstName, String lastName, int ID, WorkingHours workHours, String phoneNumber, int capacity, double workDuration){
		super(firstName, lastName, ID);
		this.phoneNumber = phoneNumber;
		this.capacity = capacity;
		this.workDuration = workDuration;

	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public double getWorkDuration(WorkingHours w) {
		
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		double startTime = Double.parseDouble(formatter.format(w.getStartTime()));
		double endTime = Double.parseDouble(formatter.format(w.getEndTime()));
		workDuration = endTime-startTime;
		return workDuration;
	}

	public boolean goOnBreak(){
		boolean x = false;
		workDuration = workDuration + breakTime;
		x = true;
		return x;
	}

	public boolean goOnCompulsoryBreak() {
		boolean y = false;
		if (workDuration == 270) {
			if (goOnBreak() == false) {
				goOnBreak();
				breakCount++;
				y = true;
			}else {
				return y;
			}
			
		}
		return y;
	}

	public int breakCount() {
		
		if (goOnBreak() == true) {
			breakCount++;
		}
		return breakCount;
	}

	public boolean manageWorkHours() {
		boolean x = false;
		if (workDuration > 10){
			this.workDuration = 10;
			x = true;
			System.out.println("Maximum workDuration is 10 hours per shift");
		}
		return x;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	
	public Driver getInstance(Person person, WorkingHours workHours, String phoneNumber, int capacity, int workDuration) {
		
		person = new Person(person.getFirstName(), person.getLastName(), person.getID());
		phoneNumber = "07778533425";
		capacity = 60;
		workDuration = 9;
		LocalDate date = LocalDate.now();
		Date startTime = new Date (120000);
		Date endTime = new Date (150000);
		//Calendar workDay = Calendar.day;
		WorkingHours wh = new WorkingHours(date, startTime, endTime);
		Driver d1 = new Driver(firstName, lastName, ID, wh, phoneNumber, capacity, workDuration);
		return d1;
		
		
	}


}

