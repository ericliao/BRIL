package uk.ac.kcl.cerch.soapi.utils;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;


public class CalendarUtilities {

    public static final SimpleDateFormat supportedDateFormats[] = 
        new SimpleDateFormat[] { new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"), 
        new SimpleDateFormat("yyyy-MM-dd"), 
        new SimpleDateFormat("yyyyMMdd"), 
        new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'z'"), 
        new SimpleDateFormat("yyyyMMdd'T'hhmm"), 
        new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'"), 
        new SimpleDateFormat("dd-MMM-yyyy"),
        new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy") 
    };
    
    /**
     * Gets a <code>Calendar</code> and returns the date it contains as <code>String</code>
     *  
     * @param calendar
     * @return
     */
    public static String getDateAsString(Calendar calendar)
    {
        String UTC = "";

        // Can change the Timezone if required
        calendar.setTimeZone(TimeZone.getTimeZone("Europe/London"));
         
        if(calendar.getTimeZone().getDSTSavings() != 0)
            UTC = "Z";
            
        String dateToParse = Integer.toString(calendar.get(Calendar.YEAR));

        String month = Integer.toString(calendar.get(Calendar.MONTH)+1);
        if(month.length() == 1)
            month = "0" + month;

        String day = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
        if(day.length() == 1)
            day = "0" + day;
        
        String hours = Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));
        if( hours.length() == 1)
            hours = "0" + hours;

        String minutes = Integer.toString(calendar.get(Calendar.MINUTE));
        if(minutes.length()==1)
            minutes = "0" + minutes;

        String seconds = Integer.toString(calendar.get(Calendar.SECOND));
        if(seconds.length()==1)
            seconds = "0" + seconds;
        
        String time = 'T' + hours + minutes + seconds + UTC;
        if( hours.equals("00") && minutes.equals("00") && seconds.equals("00"))
        {
            time = "";
        }
        
        dateToParse += month + day + time;

        return dateToParse;
    }
    
    /**
     * Gets a <code>String</code> containing the date and returns it as a <code>Calendar</code>
     * 
     * @param dateString The date as <code>String</code>
     * @return The date as a <code>Calendar</code>
     * @throws ParseException
     */    
    public static Calendar dateString2Calendar(String dateString) throws ParseException {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setLenient(false);

        Date date = new Date();
       
        boolean dateValid= false;

      
        for (int i=0; i< supportedDateFormats.length; i++)
        {
           try
           {
                supportedDateFormats[i].setLenient(false);
                date = supportedDateFormats[i].parse(dateString, new ParsePosition(0));

                calendar.setTime(date);

                dateValid = true;
                break;
            }
            catch(Exception eee)
            { 
                // Do nothing, we don't want to break the operation due to exception!
            }
        }    
        if(!dateValid)
            calendar = null;
        return calendar;
    }
}
