package uk.ac.kcl.cerch.bril.test;

import java.io.File;

import uk.ac.kcl.cerch.bril.ccp4.TaskObject;
import uk.ac.kcl.cerch.bril.ccp4.processor.log.DEFTaskProcessor;

public class DEFTaskProcMainTest {
public static void main(String args[]){
	File file = new File("C:\\brilstore\\00EXPT123\\phaser\\CCP4_DATABASE\\2_phaser_MR.def");
	//File file = new File("/home/shrijar/brilstore/expa416b0b2-de69-471c-bd63-fcb64dc15a28/3_chainsaw.def");
	
	DEFTaskProcessor def = new DEFTaskProcessor(file);
	System.out.println("read task...");
	TaskObject to = def.readTaskDEFFile();
	System.out.println("**********RESULT of processing '"+ file + "'--- a Task DEF file**********");
	
	System.out.println("Version: " + to.getSoftwareVersion());
	System.out.println("TASKNAME: " + to.getTaskName());
	System.out.println("Date: " + to.getRunDateTime());
	System.out.println("User: " + to.getUserName());
	System.out.println("JOB_ID: " + to.getJobID());
	System.out.println("project: " + to.getProjectName());
	System.out.println("Ouput vector: "+to.getOutputFileNames());
	System.out.println("Input vector: "+to.getInputFileNames());
}
}
