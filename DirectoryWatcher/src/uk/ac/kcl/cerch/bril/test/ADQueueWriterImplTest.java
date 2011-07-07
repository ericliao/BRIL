package uk.ac.kcl.cerch.bril.test;

import java.io.File;
import java.io.IOException;

import javax.jms.JMSException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import junit.framework.TestCase;
import uk.ac.kcl.cerch.bril.common.util.FileUtil;
import uk.ac.kcl.cerch.bril.service.queue.ADException;
import uk.ac.kcl.cerch.bril.service.queue.ADQueueWriterImpl;

public class ADQueueWriterImplTest  extends TestCase{
	public void testStartService() throws ADException, InterruptedException {
		 ADQueueWriterImpl producer = new  ADQueueWriterImpl();
			File file = new File("C:\\Experiment\\baa5d5\\high.mtz");
			
		 try {
			 byte[] fileContents;
				fileContents = FileUtil.getBytesFromFile(file);
				producer.sendNewExperimentMessage("Experiment XML string");
			//	producer.sendByteArrayMessage(fileContents, "c:/Experiment/baa5d5/high.mtz", "7yuhyuhyyuuukjdhdh6d6d6", "Crystallography", "baa5d5", "MODIFY_ENTRY", "12/10/2010 12:22:22");
			//	producer.sendBlobMessage(fileURL, ID, checksum, domain, projectName, entryType, dateTime, experimentId)	;	
		 
		 } catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FactoryConfigurationError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			
			} catch (XMLStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
	
	}
}
