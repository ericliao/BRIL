package uk.ac.kcl.cerch.bril.ccp4;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

/**
 * @author Shri
 *
 */
public class TaskObjectVector {
	Map <Integer,String> idVector= new HashMap<Integer,String>();
	Map <String,String> taskVector = new HashMap<String,String>();	
	Map <String,Long> dateVector= new HashMap<String,Long>();
	Map <String,String> statusVector= new HashMap<String,String>();
	Map <String,String> titleVector= new HashMap<String,String>();
	Map <String,String> logfileVector= new HashMap<String,String>();
	Map <String, Vector<String>> inputsVector = new HashMap<String,Vector<String>>();
	Map <String, Vector<String>> outputsVector = new HashMap<String, Vector<String>>();
	
	
	public TaskObjectVector(){}
	
	/**
	 * @param key
	 * @param jobID
	 * */

	public void setJobIDVectorObject(int key, String jobID){
		idVector.put(key, jobID);
	}

	/**
	 * @param jobID associated job id - an integer value 1, 2, 3 etc
	 * @param taskname name of the program or task ran
	 * */
	public void setTaskVectorObject(String jobID, String taskname){
		taskVector.put(jobID, taskname);
	}

    /**
     * @param jobID associated job id - an integer value e.g., 1, 2, 3 etc
     * @param date Date the task was ran in long format e.g; 1213691410
     * */
	
    public void setDateVectorObject(String jobID, long date){
		dateVector.put(jobID, date);
	}
    
    /**
     * @param jobID
     * @param status status of the task that was ran ;e.g FINISHED
     * */
    public void setStatusVectorObject(String jobID, String status){
		statusVector.put(jobID, status);
	}
    
    /**
     * @param jobID
     * @param title Title that describes what is being done at this task- e.g, 
     * 		  Entered by the user in the CCP4I interface when running the task - 
     * 		  In case if it is not entered a text "[No title given]" would be present.
     * */
    public void setTitleVectorObject(String jobID, String title){
		titleVector.put(jobID, title);
	}
    
    /**
     * @param jobID
     * @param logfile Logfile name associated to the task run with this jobid
     * */
    public void setLogfileVectorObject(String jobID, String logfile){
		logfileVector.put(jobID, logfile);
	} 

    /**
     * @param jobID job id of the task that is associated the input files names.
     * @param inputFileNames List of input filenames for the task with the given job id.
     * */
    public void setInputVectorObject(String jobID, Vector<String> inputFileNames){
    	inputsVector.put(jobID, inputFileNames);
	}
      
    /**
     * @param jobID Job id of the task that is associated the output files names.
     * @param outputFileNames List of output filenames for the task with the given job id.
     * */
    public void setOutputVectorObject(String jobID, Vector<String> outputFileNames){
		outputsVector.put(jobID, outputFileNames);
	}
    

    /**
     * @return Map object with key and job id
     */
    public Map <Integer,String> getJobIDVectorObject(){
    	return idVector;
    }
    
    /**
     * @return Map holding job id and its corresponding task name
     */
    public Map <String,String> getTaskVectorObject(){
    	return taskVector;
    }
    
    /**
     * @return Map holding job id and its corresponding task run date
     */
    public Map <String,Long>getDateVectorObject(){
    	return dateVector;
    }
    
    /**
     * @return Map holding job id and its corresponding task run status
     */
    public Map <String,String> getStatusVectorObject(){
    	return statusVector;
    }
    
    /**
     * @return Map holding job id and its corresponding task title
     */
    public Map <String,String> getTitleVectorObject(){
    	return titleVector;
    }
    
    /**
     * @return Map holding job id and its corresponding task run's logfile name
     */
    public Map <String,String> getLogfileVectorObject(){
    	return logfileVector;
    }
       
    
    /**
     * @return Map holding job id and its corresponding task input file names
     */
    public Map<String, Vector<String>> getInputVectorObject(){
    	return inputsVector;
    }
    
    /**
     * @return Map holding job id and its corresponding task output file names
     */
    public Map<String, Vector<String>> getOutputVectorObject(){
    	return outputsVector;
    }
    

/*    *//**
     * @param jobID
     * @param taskname
     * @param date
     * @param title
     * @param status
     * @param inputFileNames
     * @param outputFileNames
     *//*
    public void setTaskObject(String jobID, String taskname,String date, String title, String status, String inputFileNames,String outputFileNames ){
    	setTaskVectorObject(jobID,taskname);
    	setDateVectorObject(jobID,date);
    //	setInputVectorObject(jobID,inputFileNames);
    //	setOutputVectorObject(jobID,outputFileNames);   	
    }*/

}
