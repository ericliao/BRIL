package uk.ac.kcl.cerch.bril.ccp4.processor.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;

import uk.ac.kcl.cerch.bril.common.util.DateTime;
import uk.ac.kcl.cerch.bril.ccp4.TaskObjectVector;

/**
 * The class reads the CCP4I database.def file to get the 
 * metadata of all tasks run for a particular project. 
 * 'database.def' This is log file of CCP4I
 * that holds details/metadata of all the task runs. 
 * 
 * @author Shrija
 *
 */
public class DEFDatabaseProcessor {
	private File DEF_File;
	// private Properties properties;
	boolean lineHasToken = false;
	private String inputFiles;
	private String outputFiles;
	private Vector<String> inputFileNames = new Vector<String>();
	private Vector<String> outputFileNames = new Vector<String>();
	private String date;
	private Vector<String> jobIDS;
	private String jobID;
	private String logFile;
	private String taskName;
	private TaskObjectVector taskListObject;
	private Map<String, String> dataMap = new HashMap<String, String>();

	public DEFDatabaseProcessor(File DEF_File) {
		this.DEF_File = DEF_File;
		taskListObject = new TaskObjectVector();
		jobIDS=new Vector<String>();
		/*
		 * try { properties = new Properties();
		 * properties.load(getClass().getClassLoader().getResourceAsStream(
		 * "CCP4I.properties")); }catch(IOException ioe){
		 * System.out.println("IOException properties file not found: " + ioe);
		 * }
		 */
		readFile();
		
	}


	private void readFile() {
		try {
			// File file = new File(DEF_FileLocation);
			Scanner scanner = new Scanner(DEF_File);
			scanner.useDelimiter(System.getProperty("line.separator"));

			while (scanner.hasNext()) {
				parseLine(scanner.next());
			}

			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	int count = 0;
	private String aLine;

	private void parseLine(String line) {

		Scanner lineScanner = new Scanner(line);

		lineScanner.useDelimiter("\n");
		while (lineScanner.hasNext()) {
			aLine = lineScanner.next();

			if (hasToken(aLine, "NJOBS") == true) {
				//System.out.println(aLine);
				// now process each line and put the key and value pair in a Map
				int startIndex = 0;
				int firstIndexofSpace = aLine.indexOf("  ");

				try {
					String key = aLine.substring(startIndex, firstIndexofSpace);
					String value = aLine.substring(firstIndexofSpace + 1);
				//	System.out.println("key= " + key);
				//	System.out.println("value= " + value.trim());
					dataMap.put(key, value);
				} catch (Exception e) {
					System.out
							.println("ERROR to Ignore: Last line is empty "
									+ "(whitespaces) in the file when each line is processed:"
									+ e);
				}
			}
			//System.out.println("*******************");

			count++;
		}

		// isInput=false;
		// setOutputFiles("");
		lineScanner.close();
	}

	/**
	 * @return List filtered input file names
	 * 
	 */
/*	public Vector<String> getInputFileNames() {
		return inputFileNames;
	}
*/
	/**
	 * @return List filtered output file names
	 * 
	 */
	/**
	 * @return
	 */
/*	public Vector<String> getOutputFileNames() {
		return outputFileNames;
	}*/

/*	private void setDate(String date) {
		this.date = date;
	}

	public String getRunDateTime() {
		return date;
	}
*/
	private void addJobIDToVector(String jobID) {
		this.jobIDS.add(jobID);
	}

	public Vector<String> getJobIDVector() {
		return jobIDS;
	}


	 /** 
	 * Process the database.def file and set all the metadata values for
	 * all the tasks based on job ids to object TaskObjectVector
	 * 
	 * @return TaskObjectVector that contains the 
	 * metadata for all the task listed in the def file
	 * 
	 * */
	public TaskObjectVector processFileData() {
		boolean idToken = false;
		boolean taskToken = false;
		boolean dateToken=false;
		boolean statusToken=false;
		boolean titleToken=false;
		boolean inputToken=false;
		boolean outputToken=false;
		boolean logfileToken=false;
		String idtmp = "";
		String jobid = "";
		String invComma = "\"\"";
		System.out.println("START map iter PUT data in TaskObjectVector-----------------");
		for (Map.Entry<String, String> entry : dataMap.entrySet()) {
			// here key = values such as:- LOGFILE,5 TASKNAME,3 DATE,4 etc
			String key = entry.getKey();
			String value;			
			
			if(key.contains(",")) {
				taskToken = hasParamBeforeComma(key, "TASKNAME");
				if(taskToken==true) {
					idtmp = getIDAfterComma(key);
									
					if (key.equals("TASKNAME," + idtmp)) {
						value = entry.getValue();
						String trimValue = value.trim();
						
						int c = getJobIDVector().size();
						addJobIDToVector(idtmp);
						c=c+1;
						taskListObject.setJobIDVectorObject(c, idtmp);
						taskListObject.setTaskVectorObject(idtmp, trimValue);
					}
				}	
								
				inputToken= hasParamBeforeComma(key, "INPUT_FILES");							
				if(inputToken==true) {
					idtmp = getIDAfterComma(key);
					if (key.equals("INPUT_FILES," + idtmp)) {
						inputFiles = entry.getValue().trim();						
						Vector<String> inputVector= putFileNamesInVector(inputFiles);
						taskListObject.setInputVectorObject(idtmp, inputVector);
					}		
				}
				
				outputToken= hasParamBeforeComma(key, "OUTPUT_FILES");				
				if(outputToken==true) {
					idtmp = getIDAfterComma(key);					
					if (key.equals("OUTPUT_FILES," + idtmp)) {
						outputFiles = entry.getValue().trim();
						System.out.println("OUTPUT_FILES," + idtmp + " has value: "
								+ outputFiles);
						
						Vector<String> outputVector= putFileNamesInVector(outputFiles);
						taskListObject.setOutputVectorObject(idtmp, outputVector);	
					}		
				}
				
				titleToken = hasParamBeforeComma(key, "TITLE");
				if(titleToken==true) {
					idtmp = getIDAfterComma(key);
					if (key.equals("TITLE," + idtmp)) {
						String title = entry.getValue().trim();
						System.out.println("TITLE,"+idtmp+" has value: "+title);						
						taskListObject.setTitleVectorObject(idtmp, title);
					}
				}
				
				statusToken = hasParamBeforeComma(key, "STATUS");
				if(statusToken==true) {
					idtmp = getIDAfterComma(key);				
					if (key.equals("STATUS," + idtmp)) {
						String status = entry.getValue().trim();											
						taskListObject.setStatusVectorObject(idtmp, status);
					}
				}
									
				dateToken= hasParamBeforeComma(key, "DATE");
				if(dateToken==true) {
					idtmp = getIDAfterComma(key);
					//example key = TASKNAME,6					
					if (key.equals("DATE," + idtmp)) {
						String ddate = entry.getValue().trim();
						long tm = Long.valueOf(ddate).longValue();					
						taskListObject.setDateVectorObject(idtmp,tm);
					}
				}
				
				logfileToken= hasParamBeforeComma(key, "LOGFILE");
				if(logfileToken==true) {
					idtmp = getIDAfterComma(key);				
					if (key.equals("LOGFILE," + idtmp)) {
						String logfile = entry.getValue().trim();
						taskListObject.setLogfileVectorObject(idtmp, logfile);
					}				
				}	
			}
		}

		System.out.println("END iterate datamap and setting values in TaskVector object-----------------" + count);		
		return taskListObject;

	}


	/**
	 * Check if this task name is present in the database.def 
	 * and set the input and output file names for the given 
	 * taskname and jobid.	
	 * @param taskName
	 * @param jobID
	 * @return boolean value
	 */
	public boolean setIsTaskNamePresent(String taskName, String jobID) {
		boolean thisTaskNameIsPresent = false;

		for (Map.Entry<String, String> entry : dataMap.entrySet()) {
			String key = entry.getKey();
			String value;
			if (key.equals("TASKNAME," + jobID)) {
				value = entry.getValue();
				String trimValue = value.trim();
				//System.out.println("TASKNAME," + jobID + " has value: "
				//		+ trimValue);
				if (trimValue.equals(taskName)) {
					thisTaskNameIsPresent = true;
					this.taskName = trimValue;
				}
			}
		}

		if (thisTaskNameIsPresent == true) {
			for (Map.Entry<String, String> entry : dataMap.entrySet()) {
				String key = entry.getKey();

				String invComma = "\"\"";
				if (key.equals("INPUT_FILES," + jobID)) {
					inputFiles = entry.getValue().trim();
					//System.out.println("INPUT_FILES," + jobID + " has value: "
						//	+ inputFiles);
					if (inputFiles != invComma) {
						Scanner s = new Scanner(inputFiles);
						while (s.hasNext()) {
							// filter this string and put in input vector
							inputFileNames.add(filterFileName(s.next()));
						}

					} else {
						// inputFilesNotFound=true;
					}
				}
				if (key.equals("OUTPUT_FILES," + jobID)) {
					outputFiles = entry.getValue().trim();
					//System.out.println("OUTPUT_FILES," + jobID + " has value: "
					//		+ outputFiles);
					if (outputFiles != invComma) {
						Scanner s = new Scanner(outputFiles);
						while (s.hasNext()) {
							// filter this string and put in input vector
							outputFileNames.add(filterFileName(s.next()));
						}

					} else {
						System.out
								.println("There was no OUTPUT FILES for this TASKNAME");
					}
				}
				if (key.equals("DATE," + jobID)) {
					String ddate = entry.getValue().trim();
					// System.out.println("DATE,"+jobID+" has value: "+date);
					long tm = Long.valueOf(ddate).longValue();
					//System.out.println("long tm: " + tm);
					date = DateTime.getDateTime(tm);

				}
				if (key.equals("LOGFILE," + jobID)) {
					logFile = entry.getValue().trim();
					//System.out.println("LOGFILE," + jobID + " has value: "
							//+ logFile);
				}
			}
		}
		return thisTaskNameIsPresent;
	}

	/**
	 * Filter the filename and put it in vector
	 * 
	 * @param fileName
	 */
	private String filterFileName(String fileName) {
		boolean hasinvCommaCharacter = false;
		String filteredFileName = "";

		StringBuffer sb = new StringBuffer(fileName);
		for (int k = 0; k < sb.length(); k++) {
			char s = sb.charAt(k);
			if (s == '"') {
				hasinvCommaCharacter = true;
			}
		}

		if (hasinvCommaCharacter == true) {
			// remove this character
			int length = fileName.length();
			// System.out.println(length+" is length of "+ fileName);
			int doubleInvertedCommaIndex = fileName.lastIndexOf('"');

			if (doubleInvertedCommaIndex == 0) {
				filteredFileName = fileName.substring(1);
			} else if (doubleInvertedCommaIndex == length - 1) {
				filteredFileName = fileName.substring(0,
						doubleInvertedCommaIndex);
				char comma = '"';
				char isPresent = fileName.charAt(0);
				if (comma == isPresent) {
					filteredFileName = filteredFileName.substring(1);
				}
			} else {
				filteredFileName = fileName;
			}
		}else{
			filteredFileName= fileName;	
		}
		return filteredFileName;
	}

	private boolean hasToken(String line, String token) {
		StringTokenizer st = new StringTokenizer(line);
		while (st.hasMoreTokens()) {
			// System.out.println();
			String tok = st.nextToken();
			if (tok.equals(token)) {
				lineHasToken = true;
				System.out.println("title line tok:" + tok);
			}
		}
		return lineHasToken;
	}

	/**
	 * @param line line to check from
	 * @param param parameter that is searched
	 * @return boolean value
	 */
	private boolean hasParamBeforeComma(String line, String param) {
		boolean hasParam = false;
		int commaIndex = line.lastIndexOf(',');
		if (line.length() > 1) {
			String tmpParam = line.substring(0, commaIndex);
			if (tmpParam.equals(param)) {
				hasParam = true;
			}
		}
		return hasParam;
	}

	private String getIDAfterComma(String line) {
		String id = "";
		int commaIndex = line.lastIndexOf(',');
		if (line.length() > 1) {
			id = line.substring(commaIndex + 1);
		}
		return id;
	}

	private boolean hasCommaInString(String line) {
		boolean hasComma = false;
		StringBuffer sb = new StringBuffer(line);
		for (int k = 0; k < sb.length(); k++) {
			char s = sb.charAt(k);
			if (s == ',') {
				hasComma = true;
			}
		}
		return hasComma;
	}
	
	private Vector<String> putFileNamesInVector(String fileNamesValue){
		Vector<String> filesNames = new Vector<String>();
		String invComma = "\"\"";
		String inveretedComma ="\"";
		//System.out.println("invertedcomma: "+inveretedComma);	
		if (fileNamesValue != invComma) {
			//System.out.println(fileNamesValue);
			Scanner s = new Scanner(fileNamesValue);
			while (s.hasNext()) {
				// filter this string and put in input vector
				String name = s.next();
				//System.out.println("name: "+ name);
			
				String filtered =  filterFileName(name);
				//System.out.println("filtered name: "+ filtered);
				filesNames.add(filtered);
			
			}
			}
		return filesNames;
	}
}
