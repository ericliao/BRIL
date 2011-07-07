package uk.ac.kcl.cerch.bril.test;

import java.io.File;
import java.text.SimpleDateFormat;

import uk.ac.kcl.cerch.bril.ccp4.TaskObject;
import uk.ac.kcl.cerch.bril.ccp4.processor.com.ScriptProcessor;
import junit.framework.TestCase;

public class ScriptProcessorTest extends TestCase{
	public void testScriptProcessor(){
		File file = new File("/BRIL/data/free.com");
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
