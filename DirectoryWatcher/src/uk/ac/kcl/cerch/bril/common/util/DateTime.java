package uk.ac.kcl.cerch.bril.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTime{
/*convert 2008-06-25 13:53:04 BST format of jhove output to comparable date time objects
 * yyyy/MM/dd HH:mm:ss*/
	

	public DateTime(){}
	
	public static long diffInSeconds;
	public static long diffInMinutes;
	public static long diffInHours;
	
	/**
	 * @param dateLong e.g.,
	 * @return
	 */
	public static String getDateTime(long dateLong){
		String date="";

		date = new java.text.
		SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date (dateLong*1000));
		return date;
	}
	
	public static long getLongDateTime(String dateString, String pattern){
		long longDate=0;		
		DateFormat formatter = DateFormat.getDateTimeInstance();
		if (pattern!=null){
			((SimpleDateFormat) formatter).applyPattern(pattern);
		}else
	   ((SimpleDateFormat) formatter).applyPattern("dd MMM yyyy HH:mm:ss");

		try {	
			  Date date = new Date();
			   
			  date =formatter.parse(dateString);
			  longDate=date.getTime();
			  longDate=longDate/1000;
		      
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return longDate;
	}
	
	
	/**
	 * @param dateString e.g., 17 Jun 2008 10:12:08
	 * @return dd/MM/YYYY HH/mm/ss format of date
	 */
	public static String getDateTime(String dateString, String pattern){
		String date="";		
		DateFormat formatter = DateFormat.getDateTimeInstance();
		if (pattern!=null){
			((SimpleDateFormat) formatter).applyPattern(pattern);
		}else
	   ((SimpleDateFormat) formatter).applyPattern("dd MMM yyyy HH:mm:ss");

		try {	
			 Date d =formatter.parse(dateString);			
			date = (new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")).format(d);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}
	
	public static String getCurrentSysDateTime() {
	        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	        Date date = new Date();
	        return dateFormat.format(date);
	    }
	public static long  getdiffInSeconds(String dateNew,String dateOld){	
		Date d1 = DateTime.getDate(dateNew);
		Date d2 = DateTime.getDate(dateOld);
		DateTime.DateDiff(d1,d2);		
		return diffInSeconds;
	}
	
	public static long getdiffInMinutes(String dateNew, String dateOld){
		Date d1 = DateTime.getDate(dateNew);
		Date d2 = DateTime.getDate(dateOld);
		DateTime.DateDiff(d1,d2);		
		return diffInMinutes;
	}
	
	private static void  DateDiff(Date dateNew, Date dateOld){
		long diff = dateNew.getTime() - dateOld.getTime();
		setdiffInSeconds(diff);
		setdiffInMinutes(diff);	
	}

	private static void setdiffInSeconds(long diff){
		diffInSeconds = diff / 1000;
	}
	
	private static void setdiffInMinutes(long diff){
		diffInMinutes = diff / (60*1000);
	}
	
	private static Date getDate(String d1String){
		Date myDate = null;
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		
        try {
        	myDate = df.parse(d1String);     
        
        } catch (ParseException e) {                            
            System.out.println(e.getMessage());
        }               
	return myDate;
	}

	
	public static void main(String[]arg){
		//this gives incorrect result
		System.out.println(DateTime.getdiffInSeconds("17/06/2008 10:30:14","17/06/2008 09:30:10"));
	
		// convert the string data into long
		long time1 = Long.valueOf("1213691429").longValue(); //chainsaw 5
		long time2 = Long.valueOf("1213691410").longValue(); //chainsaw 4
		
		
		//convert into 17/06/2008 09:30:29 format
		System.out.println("Long date converted (chainsaw 5): "+ DateTime.getDateTime(time1));
		System.out.println("Long date converted (chainsaw 4): "+ DateTime.getDateTime(time2));
	
		// get the difference using data format returned from getDataTime(long date): 
		System.out.println(DateTime.getdiffInSeconds(DateTime.getDateTime(time1),DateTime.getDateTime(time2)));
		System.out.println("String date diff (chainsaw 5 and chain 4 as a string  17 Jun 2008  09:30:10): "+ 
				DateTime.getdiffInSeconds(DateTime.getDateTime(time1),DateTime.getDateTime("17 Jun 2008  09:30:10",null)));		
		
		String stDate = DateTime.getDateTime("2008-06-17T09:29:03",
		"yyyy-MM-dd'T'HH:mm:ss");
		long longDate = DateTime.getLongDateTime(stDate,"dd/MM/yyyy HH:mm:ss");
		System.out.println("long: " + longDate);
		System.out.println(longDate / 1000);
		System.out.println(DateTime.getDateTime(longDate / 1000));
		
	}

	public static void diffInSeconds(String string, String string2) {
		// TODO Auto-generated method stub
		
	}
		
}
