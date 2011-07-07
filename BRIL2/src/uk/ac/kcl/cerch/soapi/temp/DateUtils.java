package uk.ac.kcl.cerch.soapi.temp;

/*
 * YYYY-MM-DDThh:mm:ss

YYYY-MM-DD

YYYYMMDD

YYYY-MM-DDThh:mm:ssz

YYYYMMDDThhmm

 */
import java.util.*;
import java.util.Date;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;

public class DateUtils {

    public static final SimpleDateFormat supportedDateFormats[] = 
        new SimpleDateFormat[] { new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"), 
        new SimpleDateFormat("yyyy-MM-dd"), 
        new SimpleDateFormat("yyyyMMdd"), 
        new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'z'"), 
        new SimpleDateFormat("yyyyMMdd'T'hhmm"), 
        new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'"), 
        new SimpleDateFormat("dd-EEE-yyyy") };
    
    public static void main(String args[])
    {
        String dateString = "2008-03-12";
        //String dateString = "20080304";

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
                System.out.println("error1");
            }
        }
        if(dateValid)
        {
            System.out.println(calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + 
                    "/" + calendar.get(Calendar.DATE) + "/" + calendar.get(Calendar.HOUR_OF_DAY));
        }
        else
        {
            System.out.println("error");
        }
    }
} 
