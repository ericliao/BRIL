package uk.ac.kcl.cerch.bril.ccp4.processor.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.StringTokenizer;


/**
 * 
 * This class reads a log file generated by CCP4 when a command line task or program is ran.
 * It extracts/sets the userName, taskName, softwareName and version
 * This is tested to read log file generated by CCP4 6.0.
 * 
 * Note: CCP4 creates log file (*.log) with same name as 
 * the script file (*.com) created to run the program. 
 * This program may be used alongside {@code ScriptProcessor} to set username, softwareName 
 * and version info in {@code TaskObject}.
 *
 * @author Shrija
 *
 */
public class LOGFileProcessor {
	
	private File logFile;
	private String aLine;
	private String softwareName;
	private String softwareVersion;
	private String taskName;
	private String userName;
	private String dateTime;

	public LOGFileProcessor(File logFileLocation) {
		this.logFile = logFileLocation;	
		readLogFileModifiedDate(logFileLocation);
		readLOGFile();
		
	}
	
	private void readLOGFile() {
		try {
			//File file = new File(DEF_FileLocation);
			Scanner scanner = new Scanner(logFile);
			scanner.useDelimiter(System.getProperty("line.separator"));

			while (scanner.hasNext()) {
				parseLineHeader(scanner.next());
			}

			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void parseLineHeader(String line) {
		//System.out.println(line);
		
		Scanner lineScanner = new Scanner(line);
		//lineScanner.useDelimiter("\\s*User:\\s*");
		lineScanner.useDelimiter("\n");
		while (lineScanner.hasNext()) {
			aLine = lineScanner.next();
					
			//System.out.println("-----------");
			
			boolean hastokenCCP4=hasToken(aLine, "CCP4");
			boolean hastokenVersion=hasToken(aLine,"version");
			boolean hastokenUser=hasToken(aLine,"User:");
			boolean hastokenSys=hasToken(aLine,"System:");
			
			//System.out.println("char token: "+hastokenHtml);
			
			/*
			 * Captures and reads this line in the log file
			 * to get and set softwareName,version and taskName 
			 * ### CCP4 6.0: FREERFLAG          version 6.0       : 06/09/05##
			 */

			if (hastokenCCP4 == true && hastokenVersion==true){
				softwareName ="CCP4";
			//	System.out.println(aLine);
				int endIndex = aLine.indexOf(':');
				if(endIndex!= -1){
					softwareVersion =	aLine.substring(endIndex-4, endIndex);
			//	System.out.println(softwareVersion);
				}
	
		    String searchString ="version";
			int lastIndex =	getLastIndexOf(aLine,searchString);
			int lengthOfSearchString = searchString.length();
			int startIndexOfSearchString = lastIndex - lengthOfSearchString;
			//System.out.println(lastIndex);
			//System.out.println(startIndexOfSearchString);
			taskName = aLine.substring(endIndex+1, startIndexOfSearchString).toLowerCase();
			//System.out.println(taskName);
				//String version = aLine.substring(startIndex, firstIndexofSpace);
			}
			/**
			 * Captures this line in the log file to get and set the userName 
			 *  User: stella  Run date: 16/ 6/2008 Run time: 13:54:22 
			 */
			if(hastokenUser==true && hastokenSys==false){
				
				System.out.println(aLine);
				int endIndex = aLine.indexOf(':');
			
				if(endIndex!= -1){
					userName=	aLine.substring(endIndex+1, endIndex+9);
			   // 	System.out.println(userName);
				
				}
				
				
			}
		
			
			
		}	
		
	}
	private boolean hasToken(String line, String token){
		boolean lineHasToken=false;
		 StringTokenizer st = new StringTokenizer(line);
		     while (st.hasMoreTokens()) {
		    	 //System.out.println();
		    	 String tok = st.nextToken();
		    	if(tok.equals(token)){
		    		lineHasToken=true;
		    	//	System.out.println("title line:" +line); 
		    	  }
		     }
		return lineHasToken;
	}
	
	private int getLastIndexOf(String line,String searchString){
		int indexOfString =-2;
		searchString= searchString.toLowerCase();
		for(int i=0;i<line.length();i++){
			char c =line.charAt(i);
			if(searchString.indexOf(c)!=-1){
				//System.out.println("Index of "+searchString+": " + i); 
				indexOfString=i;
			}
		}
		return indexOfString;
	}
	
	/**
	 * Place the last modified date in the format  dd MMM yyyy  HH:mm:ss"
	 *  e.g., 17 Jun 2008  09:30:10. This is the same format as the task DEF file form CCP4I 
	 * @param file
	 */
	private void readLogFileModifiedDate(File file){
		
		//17 Jun 2008  09:30:10
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy  HH:mm:ss");
		
		this.dateTime =sdf.format(file.lastModified());
	}
	
	public String getSoftwareName(){
		return softwareName;
	}
	
	public String getSoftwareVersion(){
		return softwareVersion;
	}
	
	
	/**
	 * @return Name of the task whose run's log was created.
	 */
	public String getTaskName(){
		return taskName;
	}
	
	/**
	 * @return User name as assigned and logged by CCP4. This is the 
	 *         username in the machine used to run the software.
	 */
	public String getUserName(){
		return userName;
	}
	
	/**
	 * @return Last modified date of the log file in the format "dd MMM yyyy  HH:mm:ss"
	 * 
	 * This is the same format as the task DEF file form CCP4I
	 */
	public String getDateTime(){
		return dateTime;
	}

}
