package uk.ac.kcl.cerch.bril.ccp4.processor.com;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ListIterator;
import java.util.Scanner;
import java.util.Vector;

//import uk.ac.kcl.cerch.bril.common.util.DateTime;
import uk.ac.kcl.cerch.bril.ccp4.TaskObject;

/**
 * @author shrijar
 * 
 * Process the command file (.com) of CCP4 software (mainly used for mtz file
 * processing) to get the name of the script, input and output file names.
 * 
 */
public class ScriptProcessor {

	private File scriptFile;

	
	TaskObject taskObject = new TaskObject();

	private Vector<String> tmpList = new Vector<String>();

	public ScriptProcessor(File scriptFileLocation) {
		readScriptFileModifiedDate(scriptFileLocation);
		this.scriptFile = scriptFileLocation;	
		taskObject.setJobID(scriptFileLocation.getName());
	}

	public TaskObject readScriptFile() {
		try {
			//File file = new File(scriptFile);
			int count = 0;
			Scanner scanner = new Scanner(scriptFile);
			while (scanner.hasNext()) {
				if (count == 0) {
				   // System.out.println("script: " +scanner.next());
					taskObject.setTaskName(scanner.next());
				}			
				tmpList.add(scanner.next());
				count++;
			}
			//iterate tmpList
			ListIterator<String> itr = tmpList.listIterator();
		
			while(itr.hasNext()){
			    String tmp=itr.next();
			    if(tmp.equals("xyzin") || tmp.equals("hklin") || tmp.equals("alignin")){
			    	//System.out.println(tmp);
			    	taskObject.setInputFileName(itr.next());
			    }
			    if (tmp.equals("xyzout") || tmp.equals("hklout") || tmp.equals("alignout")){
			    	//System.out.println(tmp);
			    	taskObject.setOutputFileName(itr.next());
			    }
			}
			scanner.close();
			
		//	String outputFileName = outputFileNames.elementAt(0);
		//	System.out.println(outputFileName);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return taskObject;
	}

	
	/**
	 * Place the last modified date in the format  dd MMM yyyy  HH:mm:ss"
	 *  e.g., 17 Jun 2008  09:30:10. This is the same format as the task DEF file form CCP4I 
	 * @param file
	 */
	private void readScriptFileModifiedDate(File file){
		
		//17 Jun 2008  09:30:10
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy  HH:mm:ss");
		
		taskObject.setRunDateTime(sdf.format(file.lastModified()));
	}

	public static void main(String[] arg) {
		
		File file = new File("C:\\brilstore\\00EXPT123\\free.com");
		ScriptProcessor p = new ScriptProcessor(file);
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy  HH:mm:ss");
		//SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		System.out.println("Original Last Modified Date : " 
				+ sdf.format(file.lastModified()));


		TaskObject to =p.readScriptFile();

		System.out.println("Task name: " + to.getTaskName());
		System.out.println("Job id: " + to.getJobID());
		System.out.println("Task date: " + to.getRunDateTime());
		System.out.println("Input filenames: " + to.getInputFileNames());
		System.out.println("Output filenames: " + to.getOutputFileNames());
	}

}
