package GP;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class WorkingHours {

	private LocalDate date;
	private Date startTime;
	private Date endTime;

	public WorkingHours(LocalDate date, Date startTime, Date endTime) {
		this.date = LocalDate.now();
		this.startTime = new Date(startTime.getTime());
		this.endTime = new Date(endTime.getTime());
	}

	public LocalDate getDate() {
		return date;
	}

	/*
	 * public void setDate(int day1, int month1, int year1) {
	 * 
	 * int day = day1; int month = month1; int year = year1;
	 * 
	 * Date date = new Date(); Calendar c = Calendar.getInstance();
	 * 
	 * c.set(Calendar.DATE, day); c.set(Calendar.MONTH, month); c.set(Calendar.YEAR,
	 * year); date = c.getTime();
	 * 
	 * Format dateFormat = new SimpleDateFormat("EEEE, dd/MM/yyyy");
	 * System.out.println(dateFormat.format(date));
	 * 
	 * }
	 */
	
	public void workingToday() {
		
		LocalDate date = LocalDate.now();
		System.out.println(DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy").format(date));

	}

	public Date getStartTime() {
		return startTime;
	}

	public Date setStartTime(int hour1, int min1, int sec1) {

		int hour = hour1;
		int min = min1;
		int sec = sec1;

		Date startTime = new Date();
		Calendar c = Calendar.getInstance();

		c.set(Calendar.HOUR, hour);
		c.set(Calendar.MINUTE, min);
		c.set(Calendar.SECOND, sec);
		startTime = c.getTime();
		
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		System.out.println(dateFormat.format(startTime));

		return startTime;

	}

	public Date getEndTime() {
		return endTime;
	}

	public Date setEndTime(int hour2, int min2, int sec2) {

		int hour = hour2;
		int min = min2;
		int sec = sec2;

		Date endTime = new Date();
		Calendar c = Calendar.getInstance();

		c.set(Calendar.HOUR, hour);
		c.set(Calendar.MINUTE, min);
		c.set(Calendar.SECOND, sec);
		endTime = c.getTime();
		
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		System.out.println(dateFormat.format(endTime));

		return endTime;

	}

		
		//Calendar c = Calendar.getInstance();

		/*
		 * switch(workDay) {
		 * 
		 * case 0: return Calendar.SUNDAY; case 1: return Calendar.MONDAY; case 2:
		 * return Calendar.TUESDAY; case 3: return Calendar.WEDNESDAY; case 4: return
		 * Calendar.THURSDAY; case 5: return Calendar.FRIDAY; case 6: return
		 * Calendar.SATURDAY; default: return 0;
		 */

	
	public WorkingHours getInstance(LocalDate date, Date startTime, Date endTime) {
		
		LocalDate date1 = date;
		Date startTime1 = startTime;
		Date endTime1 = endTime;
		WorkingHours wh = new WorkingHours(date1, startTime1, endTime1);
		
		return wh;
		
	}
}

