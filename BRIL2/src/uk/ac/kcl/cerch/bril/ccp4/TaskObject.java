package uk.ac.kcl.cerch.bril.ccp4;

import java.util.Vector;

//import uk.ac.kcl.cerch.bril.common.util.DateTime;

/**
 * @author Shri
 *
 */
public class TaskObject {

	private String taskName;
	private String jobID;
	private String runDateTime;
	private String userName;
	private String projectName;
	private String softwareVersion;
	private String softwareName;
	private Vector<String> inputFileNames = new Vector<String>();
	private Vector<String> outputFileNames = new Vector<String>();

	public TaskObject() {

	}

	/**
	 * @param taskName Name of the task retrieved by processing either a script file 
	 *                 (.com) or from a task DEF file.
	 */
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	/**
	 * @param jobID The job id (an integer number) assigned by CCP4I in task DEF ( if used DEFTaskProcessor). 
	 *              Or this can be script filename (if used ScriptProcessor) 
	 */
	public void setJobID(String jobID) {
		this.jobID = jobID;
	}

	/**
	 * @param runDate date of the task run in format DD MMM YYYY  HH:mm:ss e.g., 17 Jun 2008  09:30:10
	 */
	public void setRunDateTime(String runDate) {
		// DateTime.getDateTime(runDate);
		this.runDateTime = runDate;
	}

	/**
	 * @param userName user name assigned by CCP4I in the task DEF file (based on the machine user name)
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @param projectName Name of the project in the CCP4I generated Task DEF file. 
	 *  This is entered by the user in the CCP4I interface where first a project (a directory) is created to which the task belongs to.
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/**
	 * @param SoftwareVersion The version of the software used e.g., CCP4 or CCP4I
	 */
	public void setSoftwareVersion(String softwareVersion) {
		this.softwareVersion = softwareVersion;
	}
	
	public void setSoftwareName(String softwareName){
		this.softwareName =softwareName;
	}

	/**
	 * @param name Input file name that is added to the vector
	 */
	public void setInputFileName(String name) {
		this.inputFileNames.add(name);
	}

	/**
	 * @param name Output file name that is added to the vector
	 */
	public void setOutputFileName(String name) {
		this.outputFileNames.add(name);
	}

	public String getTaskName() {
		return taskName;
	}

	public String getJobID() {
		return jobID;
	}

	public String getRunDateTime() {
		return runDateTime;
	}

	public String getUserName() {
		return userName;
	}

	public String getProjectName() {
		return projectName;
	}
	
	public String getSoftwareName() {
		return softwareName;
	}

	public String getSoftwareVersion() {
		return softwareVersion;
	}

	public Vector<String> getInputFileNames() {
		return inputFileNames;
	}

	public Vector<String> getOutputFileNames() {
		return outputFileNames;
	}

}
