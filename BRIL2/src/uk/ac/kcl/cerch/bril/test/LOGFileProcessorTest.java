package uk.ac.kcl.cerch.bril.test;

import java.io.File;

import uk.ac.kcl.cerch.bril.ccp4.processor.log.LOGFileProcessor;
import junit.framework.TestCase;

public class LOGFileProcessorTest extends TestCase{
	public void testLOGFileProcessor(){
		File file = new File("/BRIL/data/free.log");
		LOGFileProcessor p = new LOGFileProcessor(file);
		System.out.println(p.getSoftwareName().trim());
		System.out.println(p.getSoftwareVersion().trim());
		System.out.println(p.getTaskName().trim());
		System.out.println(p.getUserName().trim());
		System.out.println(p.getDateTime().trim());
		
	}
}
