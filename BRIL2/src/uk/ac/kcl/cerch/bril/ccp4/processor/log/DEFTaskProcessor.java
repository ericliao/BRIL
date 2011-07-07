package uk.ac.kcl.cerch.bril.ccp4.processor.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.StringTokenizer;
import uk.ac.kcl.cerch.bril.ccp4.TaskObject;

/**
 * This class reads the CCP4 task .DEF file that is the log file generated 
 * when a task is ran in CCP4 interface. 
 * It has a method that processes the file and returns 
 * {@code TaskObject} that holds metadata about the task    
 * 
 * TODO: add processing for .seq files  
 *   
 * @author Shrija
 * @author Eric
 *
 */
public class DEFTaskProcessor{
	private File DEF_File;
	private TaskObject taskObject = new TaskObject();
	private String aLine = "";
	private Map<String, String> dataMap = new HashMap<String, String>();
	private Map<String, String> inputTypeNamesMap = new HashMap<String, String>();
	private Map<String, String> outputTypeNamesMap = new HashMap<String, String>();

	static int count = 0;
	boolean lineHasToken= false;
	boolean isCCP4_DEF= false;
	private  boolean inputFilesNotFound = false;
	private  boolean outputFilesNotFound  = false;
	private  boolean isInput = false;
	
	public DEFTaskProcessor(File DEF_File) {
		this.DEF_File = DEF_File;
		readDEFFileHeader();		
	}

	/**
	 * Reads and scans the header data from the task def file and sets all the
	 * available info like ccp4i version, task name, user and jobid.
	 */
	private void readDEFFileHeader() {
		try {			
			Scanner scanner = new Scanner(DEF_File);
			scanner.useDelimiter(" *#CCP4I *");
			while (scanner.hasNext()) {
				parseLineHeader(scanner.next());
			}

			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isCCP4I_DefFile(){
		try {
			String aLine;
			//File file = new File(DEF_FileLocation);
			Scanner scanner = new Scanner(DEF_File);
			scanner.useDelimiter(System.getProperty("line.separator"));

			while (scanner.hasNext()) {
				aLine = scanner.next();
				
				if (hasToken(aLine, "#CCP4I") == true) {
					isCCP4_DEF=true;
				}
			}
		scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return isCCP4_DEF;
	
 }

	/**
	 * Reads and scans the Task DEF file to get metadata 
	 * about the task such as IO, runtime, task name etc
	 * 
	 * @return A TaskObject that holds the metadata about the task
	 */
	public TaskObject readTaskDEFFile() {
		try {
			Scanner scanner = new Scanner(DEF_File);
			scanner.useDelimiter(System.getProperty("line.separator"));

			while (scanner.hasNext()) {
				parseLineBody(scanner.next());
			}

			scanner.close();
							
			/*
			 * Put the value of key 'INPUT_FILES' from the dataMap
			 * that contains the space separated input type names
			 */
			setInputTypeNames();
			
			/*
			 * Put the value of key 'COMP_FILE,n' from the dataMap
			 * that contains the space separated input type names
			 */
			setSequenceInputTypeNames();
			
			isInput = false;
			/*
			 * Put the value of key 'OUTPUT_FILES' from dataMap
			 */
			setOutputTypeNames();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return taskObject;
	}

	private void parseLineHeader(String headerLine) {
		Scanner lineScanner = new Scanner(headerLine);
		lineScanner.useDelimiter(" *#CCP4I *");
		
		if (headerLine.contains("VERSION")) {
			taskObject.setSoftwareName("CCP4I");
			String versionLine = lineScanner.next();
			readLine(versionLine, "VERSION");
			System.out.println("version: " + versionLine);
		}
		
		if (headerLine.contains("DEF") && headerLine.contains("SCRIPT")) {
			String scriptLine = lineScanner.next();
			readLine(scriptLine, "SCRIPT");
			System.out.println("scriptLine: " + scriptLine);
		}
		
		if (headerLine.contains("DATE")) {
			String runDateLine = lineScanner.next();
			readLine(runDateLine, "DATE");
			System.out.println(runDateLine);
		}
		
		if (headerLine.contains("USER")) {
			String userLine = lineScanner.next();
			readLine(userLine, "USER");
			System.out.println(userLine);
		}
		
		if (headerLine.contains("JOB_ID")) {
			String jobLine = lineScanner.next();
			readLine(jobLine, "JOB_ID");
			System.out.println(jobLine);
		}
		
		if (headerLine.contains("PROJECT")) {
			String projectLine = lineScanner.next();
			readLine(projectLine, "PROJECT");
			System.out.println(projectLine);
		}
		lineScanner.close();
	}

	private void parseLineBody(String bodyLine) {
		Scanner lineScanner = new Scanner(bodyLine);
		lineScanner.useDelimiter("\n");
		while (lineScanner.hasNext()) {
			aLine = lineScanner.next();									 		
			 if (!aLine.contains("#CCP4I")) {								
				//now process each line and put the key and value pair in a Map
				//(key and value in each line is separated by spaces)
				int startIndex = 0;
				int firstIndexofSpace = aLine.indexOf("  ");

				try {
					String key = aLine.substring(startIndex, firstIndexofSpace);
					String value = aLine.substring(firstIndexofSpace + 1);				
					dataMap.put(key, value);
				} catch (Exception e) {
					System.out.println("ERROR to Ignore: Last line is empty (whitespaces) in the file when each line is processed:" + e);
				}
			 }
		}	
		
		lineScanner.close();
	}
 	
  /**
   * @param can be empty
   * gets INPUT_FILES Key value from the dataMap- contains space separated 
   * input types =HKLIN,1_1,XYZIN. 
   * Each input type will have input file name for this task DEF in the dataMap
   * Set all the input type names in a map 
   * @see Map inputTypeNamesMap
   * 
   */
	private void setInputTypeNames() {
		String inputFiles = dataMap.get("INPUT_FILES");		
		System.out.println(inputFiles);
		String key = "";
		int count = 0;
		String invComma = "\"\"";
		/*
		 * if the @param has value "" , then inputFilesNotFound = true we need to
		 * search in the database.def file for the input files for this taskname
		 * and job_id
		 */
		if (inputFiles != invComma) {
			Scanner s = new Scanner(inputFiles);
			while (s.hasNext()) {
				count++;
				key = Integer.toString(count);
				inputTypeNamesMap.put(key, s.next());
			}
		} else {
			inputFilesNotFound = true;
		}

		isInput = true;
		setInputOutputNames();
	}

	/**
	   * @param can be empty
	   * gets COMP_FILE,n Key value from the dataMap 
	   * then save to inputfilename of task object
	   * @see Map inputTypeNamesMap
	   * 
	   */
	private void setSequenceInputTypeNames() {
	
		int seq_count = 0;
		String sequenceFile = "COMP_FILE," + Integer.toString(seq_count);				
		String inputFile = dataMap.get(sequenceFile);		
		while (inputFile != null) {			
			if (inputFile.contains(".seq")) {						
				taskObject.setInputFileName(inputFile.trim());					
			} else {
				inputFilesNotFound = true;
			}	
			seq_count++;
			sequenceFile = "COMP_FILE," + Integer.toString(seq_count);				
			inputFile = dataMap.get(sequenceFile);
		}													
		isInput = true;			
	}	
	
	
  /**
   * @param can be empty
   * gets OUTPUT_FILES Key value from the dataMap- contains space separated 
   * input types (HKLOUT,XYZOUT). 
   * Each output type will have output file name for this task DEF in the dataMap
   * set all the output type names in map 
   * @see Map outputFilesMap
   */
	private void setOutputTypeNames() {
		String key = "";
		String outputFiles = dataMap.get("OUTPUT_FILES");
		int count = 0;
		String invComma = "\"\"";
		if (outputFiles != invComma) {
			Scanner s = new Scanner(outputFiles);
			while (s.hasNext()) {
				count++;

				key = Integer.toString(count);
				outputTypeNamesMap.put(key, s.next());
			}
		} else {
			outputFilesNotFound = true;
		}

		System.out.println(outputTypeNamesMap.keySet());
		System.out.println(outputTypeNamesMap.values());

		setInputOutputNames();

	}
  
  /**
   * Reads the data from the inputTypeNamesMap or outputFilesNamesMap and searches for the parameter value 
   * in the Map dataMap
   */
	private void setInputOutputNames() {

		Map<String, String> fileNamesMap = new HashMap<String, String>();
		System.out.println(isInput);
		if (isInput == true) {
			fileNamesMap = inputTypeNamesMap;

		} else if (isInput == false) {
			fileNamesMap = outputTypeNamesMap;
		}

		System.out.println(fileNamesMap.values());

		for (Map.Entry<String, String> entry : fileNamesMap.entrySet()) {
			boolean hasCommaCharacter = false;
			boolean hasinvCommaCharacter = false;
			String paramName = entry.getValue();
			int len = paramName.length();		
			String trimmedParamName = "";
			String cleanedParamName = "";
			System.out.println("*****************************");
			System.out.println("String processed here: " + paramName + " and leng: " + len);

			if (paramName != null) {
				StringBuffer sb = new StringBuffer(paramName);

				for (int k = 0; k < sb.length(); k++) {
					char s = sb.charAt(k);
					if (s == ',') {
						hasCommaCharacter = true;
						// hasinvCommaCharacter=false;
						System.out.println("comma =true");
					}
					if (s == '"') {
						hasinvCommaCharacter = true;
						// hasCommaCharacter=false;
						System.out.println("inv comma =true");
					}

				}
				if (len > 1) {

					// if , is present in inputParamName
					if ((hasCommaCharacter == true) || (hasCommaCharacter == true && hasinvCommaCharacter == true)) {
						System.out.println("*****Comma = true*****");
						int commaIndex = paramName.lastIndexOf(',');
						// if
						// " is also present in inputParamName then first remove "
						// it then use this cleaned string to trim it-
						// i.e.,characters before ,
						// e.g HKLIN from HKLIN,1
						if (hasinvCommaCharacter == true) {
							int doubleInvertedCommaIndex = paramName.lastIndexOf('"');
							// System.out.println("inv comma index:"+doubleInvertedCommaIndex);
							// System.out.println("comman index:"+commaIndex);
							// last index; only one at the start of the string
							if (doubleInvertedCommaIndex == 0) {
								// TODO: Set this cleanedParamName to search for
								// the parameter values in dataMap
								cleanedParamName = removeInvertedComma(doubleInvertedCommaIndex, paramName);
								System.out.println("cleaned1 (to identify parameter in dataMap): " + cleanedParamName);
							} else if (doubleInvertedCommaIndex != 0) {
								// last index: at the end of the string
								// check it is present in the start of the
								// string as well
								int doubleInvertedCommaIndex1 = paramName.indexOf('"');
								// System.out.println(
								// "inv comma 1: "+doubleInvertedCommaIndex1);
								if (doubleInvertedCommaIndex1 == 0) {
									// two inverted comma present one at star
									// and one in the end of the string
									String tmpCleaned = removeInvertedComma(doubleInvertedCommaIndex, paramName);
									// TODO: Set this cleanedParamName to search
									// for the parameter values in dataMap
									cleanedParamName = removeInvertedComma(doubleInvertedCommaIndex1, tmpCleaned);

								} else {
									// TODO: Set this cleanedParamName to search
									// for the parameter values in dataMap
									cleanedParamName = removeInvertedComma(doubleInvertedCommaIndex, paramName);
									System.out.println("cleaned1 (to identify parameter in dataMap): " + cleanedParamName);
								}
							}
							int commaIndex1 = cleanedParamName.lastIndexOf(',');
							// System.out.println("comman index new:"+commaIndex1);
							trimmedParamName = cleanedParamName.substring(0, commaIndex1);
							System.out.println("trimmed1 (to identify input type): " + trimmedParamName);

						} else
						// eg XYZIN,1_1
						// take the string between 0 and comma index to check
						// the type of input hkl or xyz
						{
							// TODO: Set this inputParamName to search for the
							// parameter values in dataMap
							trimmedParamName = paramName.substring(0, commaIndex);
							System.out.println("trimmed2 (to identify input type): " + trimmedParamName);
						}
					}
					// if " is present in inputParamName
					// remove the "
					else if (hasinvCommaCharacter == true) {
						System.out.println("*****inv comma = true*****");
						int doubleInvertedCommaIndex = paramName.lastIndexOf('"');
						// TODO: Set this cleanedParamName to search for the
						// parameter values in dataMap
						cleanedParamName = removeInvertedComma(doubleInvertedCommaIndex, paramName);
						System.out.println("cleaned2 (to identify parameter in dataMap): " + cleanedParamName);
					}
					System.out.println("input: " + paramName);
					System.out.println("cleaned: " + cleanedParamName);
					System.out.println("trimmed: " + trimmedParamName);

					// check the parameter in dataMap and get values and set
					// them
					if (paramName.equals("HKLIN_MAIN") || cleanedParamName.equals("HKLIN_MAIN") || trimmedParamName.equals("HKLIN_MAIN")) {
						String fileNameValue = "";
						if (cleanedParamName != "") {
							fileNameValue = dataMap.get(cleanedParamName);
							taskObject.setInputFileName(fileNameValue.trim());
						} else {
							fileNameValue = dataMap.get(paramName);
							taskObject.setInputFileName(fileNameValue.trim());
						}
						System.out.println("TEST:HKLIN_MAIN:" + fileNameValue);
						// TODO: search for the value in the map and
						// set the hklin filename vector

					} else if (paramName.equals("HKLIN") || trimmedParamName.equals("HKLIN") || cleanedParamName.equals("HKLIN")) {
						dataMap.get(paramName);
						System.out.println("TEST:HKLIN");
						String fileNameValue = "";
						if (cleanedParamName != "") {
							fileNameValue = dataMap.get(cleanedParamName);
							taskObject.setInputFileName(fileNameValue.trim());
						} else {
							fileNameValue = dataMap.get(paramName);
							taskObject.setInputFileName(fileNameValue.trim());
						}
						System.out.println("TEST:HKLIN filename: "
								+ fileNameValue.trim());
						// TODO:set hklin vector

					} else if (paramName.equals("XYZIN") || trimmedParamName.equals("XYZIN") || cleanedParamName.equals("XYZIN")) {
						dataMap.get(paramName);

						String fileNameValue = "";
						if (cleanedParamName != "") {
							fileNameValue = dataMap.get(cleanedParamName);
							taskObject.setInputFileName(fileNameValue.trim());
						} else {
							fileNameValue = dataMap.get(paramName);
							taskObject.setInputFileName(fileNameValue.trim());
						}
						System.out.println("TEST:XYZIN filename: " + fileNameValue.trim());
						// TODO:set xyzin vector
					} else if (paramName.equals("ALIGNIN") || trimmedParamName.equals("ALIGNIN") || cleanedParamName.equals("ALIGNIN")) {
						String fileNameValue = "";
						if (cleanedParamName != "") {
							fileNameValue = dataMap.get(cleanedParamName);
							taskObject.setInputFileName(fileNameValue.trim());
						} else {
							fileNameValue = dataMap.get(paramName);
							taskObject.setInputFileName(fileNameValue.trim());
						}
						System.out.println("TEST:ALIGNIN filename: " + fileNameValue.trim());
						// TODO:set alignin vector
					} else if (paramName.equals("XYZOUT") || trimmedParamName.equals("XYZOUT") || cleanedParamName.equals("XYZOUT")) {
						String fileNameValue = "";
						System.out.println("test: " + paramName);
						if (cleanedParamName != "") {
							fileNameValue = dataMap.get(cleanedParamName);
							taskObject.setOutputFileName(fileNameValue.trim());
						} else {
							fileNameValue = dataMap.get(paramName);
							taskObject.setOutputFileName(fileNameValue.trim());
						}
						System.out.println("TEST:XYZOUT filename: " + fileNameValue.trim());
						// TODO:set alignin vector
					} else if (paramName.equals("HKLOUT") || trimmedParamName.equals("HKLOUT") || cleanedParamName.equals("HKLOUT")) {
						String fileNameValue = "";
						System.out.println("test: " + paramName);
						if (cleanedParamName != "") {
							fileNameValue = dataMap.get(cleanedParamName);
							taskObject.setOutputFileName(fileNameValue.trim());
						} else {
							fileNameValue = dataMap.get(paramName);
							taskObject.setOutputFileName(fileNameValue.trim());
						}
						System.out.println("TEST:HKLOUT filename: " + fileNameValue.trim());
						// TODO:set alignin vector
					}
				}
			}
		}
	}

	private void readLine(String line, String delimiter) {

		Scanner s = new Scanner(line);
		
		if (delimiter == "VERSION") {
			s.useDelimiter("\\s*" + delimiter + "\\s*");
			Scanner s1 = new Scanner(s.next())
					.useDelimiter("\\s*CCP4Interface\\s*");
			taskObject.setSoftwareVersion(s1.next());
		} else if (delimiter == "SCRIPT") {
			s.useDelimiter("\\s*" + delimiter + "\\s*");
			Scanner s1 = new Scanner(s.next()).useDelimiter("\\s*DEF\\s*");
			taskObject.setTaskName(s1.next());
		} else if (delimiter == "DATE") {
			s.useDelimiter("\\s*" + delimiter + "\\s*");
			taskObject.setRunDateTime(s.next());
		} else if (delimiter == "USER") {
			s.useDelimiter("\\s*" + delimiter + "\\s*");
			taskObject.setUserName(s.next());
		} else if (delimiter == "JOB_ID") {
			s.useDelimiter("\\s*" + delimiter + "\\s*");
			// if the delimiter is not present in this line of string then
			// result string will be the same as the original line
			String result = s.next();
			if (result.equals(line)) {
				// System.out.println("Result is same as the line????"+ result);
				// try if this line consists of 'PROJECT'
				checkAndSetProject(result);
			} else {
				taskObject.setJobID(result);
			}
		} else if (delimiter == "PROJECT") {
			s.useDelimiter("\\s*" + delimiter + "\\s*");
			if (taskObject.getProjectName() == null) {
				taskObject.setProjectName(s.next());
			}
		}
	}

	private void checkAndSetProject(String projectLine) {
		Scanner s = new Scanner(projectLine);
		s.useDelimiter("\\s*PROJECT\\s*");
		taskObject.setProjectName(s.next());
	}

	private boolean hasToken(String line, String token){
		 StringTokenizer st = new StringTokenizer(line);
		     while (st.hasMoreTokens()) {
		    	 //System.out.println();
		    	 String tok = st.nextToken();
		    	if(tok.equals(token)){
		    		lineHasToken=true;
		    		//System.out.println("title line:" +line); 
		    	  }
		     }
		return lineHasToken;
	}
	
    private static String removeInvertedComma(int doubleInvertedCommaIndex, String paramName){
    	String cleanedParamName="";
    	//if (Integer.toString(doubleInvertedCommaIndex)=="\"") {
			if(doubleInvertedCommaIndex==0){
				cleanedParamName = paramName.substring(1);	
			//	System.out.println("cleaned: "+cleanedParamName);
			}if (doubleInvertedCommaIndex==paramName.length()-1){
				cleanedParamName = paramName.substring(0,paramName.length()-1);
			//	System.out.println("cleaned: "+cleanedParamName);
			}if (doubleInvertedCommaIndex==0 && doubleInvertedCommaIndex==paramName.length()-1){
				cleanedParamName = paramName.substring(1,paramName.length()-1);
			//	System.out.println("cleaned: "+cleanedParamName);
			}
			
	   // } 
    	return cleanedParamName;
    }
	
}
