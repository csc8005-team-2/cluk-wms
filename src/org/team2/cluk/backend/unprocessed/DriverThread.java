package org.team2.cluk.backend.unprocessed;

import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class DriverThread extends Thread{

    public void run(){

        for (int i=0; i<10; i++) {

            try {
                String currentTimeString = new SimpleDateFormat("HH:mm:ss").format(new Date());
                String timeToCompareString = "06:00:00";
                boolean x = currentTimeString.equals(timeToCompareString);

                if (x){
                    Driver.assignOrderToDriver();
                }else{
                    try{
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                        Date currentTimeDate = sdf.parse(currentTimeString);
                        Date timeToCompareDate = sdf.parse(timeToCompareString);
                        boolean y = currentTimeDate.before(timeToCompareDate);
                        boolean z = currentTimeDate.after(timeToCompareDate);
                        if(y){
                            long sleepTimeHours = Math.abs(currentTimeDate.getTime() - timeToCompareDate.getTime());
                            long sleepTimeMilliSeconds = TimeUnit.HOURS.convert(sleepTimeHours, TimeUnit.MILLISECONDS);
                            sleep(sleepTimeMilliSeconds);
                        }
                        if(z){
                            long sleepTimeHours = Math.abs(TimeUnit.HOURS.toMillis(24) - currentTimeDate.getTime() - timeToCompareDate.getTime() + ());
                            long sleepTimeMilliSeconds = TimeUnit.HOURS.convert(sleepTimeHours, TimeUnit.MILLISECONDS);
                            sleep(sleepTimeMilliSeconds);
                        }
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }

            } catch (ParseException e) {
                e.printStackTrace();

            }
        }
    }
}
