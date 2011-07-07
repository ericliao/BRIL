package uk.ac.kcl.cerch.bril.service.queue;


import java.net.URL;

import javax.jms.JMSException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

//import uk.ac.kcl.cerch.bril.service.queue.ADException;

public interface ADQueueWriter {

	public void disconnect() throws JMSException;
	public void sendByteArrayMessage(byte[] fileByteArray, String ID, String checksum, String domain,String projectName, String entryType,
			String dateTime) throws JMSException,XMLStreamException,FactoryConfigurationError;
	public void sendBlobMessage(URL fileURL, String ID, String checksum, String domain, String projectName, String entryType, String dateTime,String experimentId)throws JMSException;
	public void sendNewExperimentMessage(String experimentXMLString) throws JMSException, XMLStreamException, FactoryConfigurationError;
}
