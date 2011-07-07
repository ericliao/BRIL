package uk.ac.kcl.cerch.bril.test;

import java.io.File;
import java.util.Map;
import java.util.Vector;

import uk.ac.kcl.cerch.bril.ccp4.TaskObjectVector;
import uk.ac.kcl.cerch.bril.ccp4.processor.log.DEFDatabaseProcessor;
import uk.ac.kcl.cerch.bril.characteriser.TaskObjectElement;
import junit.framework.TestCase;

public class DEFDatabaseProcessorTest extends TestCase{
	
	public void testProcessor(){
		//File file = new File("C:\\brilstore\\00EXPT123\\database.def");
		File file = new File("C:\\brilstore\\00EXPT123\\phaser\\CCP4_DATABASE\\database.def");
		DEFDatabaseProcessor def = new DEFDatabaseProcessor(file);
		TaskObjectVector vec = def.processFileData();
		/*
		 * if (def.setIsTaskNamePresent(taskname,jobid)==true){
		 * //def.getInputFNames(); //def.getOutputFNames();
		 * if(def.getOutputFileNames().equals("\"\"")){System.out.println(
		 * "There is not output file created for this TASKNAME- possibly a test task"
		 * ); } System.out.println("Vector inputs: "+def.getInputFileNames());
		 * System.out.println("Vector outputs: "+def.getOutputFileNames());
		 * System.out.println("date : "+def.getRunDateTime()); }
		 */
		Map<Integer,String> jobids = vec.getJobIDVectorObject();
		
	/*	for (Map.Entry<Integer,String> entry: jobids.entrySet()){
			String jobId = entry.getValue();
			String task = vec.getTaskVectorObject().get(jobId);
			//System.out.println(jobId+": "+task);
			
			Vector<String> inputs =vec.getInputVectorObject().get(jobId);
			 for(int i=0; i<inputs.size();i++){
					//add element <input_filename> to  <input_filenames> 
				 //System.out.println("input: "+inputs.get(i));
				 }
			// System.out.println("---------------------------");
		}*/
		
		
		System.out.println("jobID vector: "
				+def.getJobIDVector());
		System.out.println("task vector: "
				+ vec.getTaskVectorObject());
		System.out.println("id vector: "
				+ vec.getJobIDVectorObject());
		System.out.println("date vector: "
				+ vec.getDateVectorObject());
		System.out.println("title vector: "
				+ vec.getTitleVectorObject());
		System.out.println("status vector: "
				+ vec.getStatusVectorObject());
		System.out.println("input  vector: "
				+ vec.getInputVectorObject());
		System.out.println("output vector: "
				+ vec.getOutputVectorObject());
	
	}
	
}
