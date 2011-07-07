package uk.ac.kcl.cerch.bril.service.queue;

//import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
//import java.io.FileNotFoundException;
import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
import java.io.StringWriter;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
import java.net.URL;
//import java.util.Map;
import java.util.Properties;

//import javax.jms.Connection;
//import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
//import javax.jms.BytesMessage;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.BlobMessage;
/*import org.apache.activemq.blob.BlobTransferPolicy;
import org.apache.activemq.blob.BlobUploader;
*/import org.apache.activemq.command.ActiveMQBlobMessage;
import org.apache.activemq.command.ActiveMQMapMessage;
//import org.mortbay.util.IO;


//import uk.ac.kcl.cerch.bril.service.queue.ADException;
import uk.ac.kcl.cerch.bril.common.util.FileUtil;
import uk.ac.kcl.cerch.bril.service.upload.FileServerClient;
import uk.ac.kcl.cerch.soapi.checksum.ChecksumProcessorException;
import uk.ac.kcl.cerch.soapi.checksum.MD5ChecksumProcessor;

public class ADQueueWriterImpl implements ADQueueWriter {
	//private static transient ConnectionFactory factory;
	//private transient Connection connection;
	
	private ActiveMQConnectionFactory factory;
	private ActiveMQConnection connection;
	
	private ActiveMQSession session;
	private MessageProducer messageProducer;
	//private OutputStream out;
	private Properties properties;
	private Properties propertiesXML;

	Destination autoDepositQueue;
	String checksum="something";

	public ADQueueWriterImpl(){
		try {
			properties = new Properties();
			properties.load(getClass().getClassLoader().getResourceAsStream(
					"bril.properties"));

			propertiesXML = new Properties();
			propertiesXML.load(getClass().getClassLoader().getResourceAsStream(
					"messagexml.properties"));
			
 //messaging.url.server for bril-dev server , messaging.url for localhost
			String brokerURL = properties.getProperty("messaging.url.server") + ":"
					+ properties.getProperty("messaging.port");
System.out.println(brokerURL);
			//String blobUploadURL="?jms.blobTransferPolicy.uploadUrl=http//:localhost:8161/fileserver";
			factory = new ActiveMQConnectionFactory(brokerURL);

			// using the connection factory create JMS connection
			connection = (ActiveMQConnection)factory.createConnection();
			
			//connection.setBlobTransferPolicy(blobTransferPolicy);
			
			// start the connection to enable messages to start flowing
			connection.start();

			// JMS session: auto acknowledge
			session = (ActiveMQSession)connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			// producer = session.createProducer(null);
			// must be a message queue

			// create JMS queue using the session
			// autoDepositQueue = session.createQueue("AutoDepositQueue");
			autoDepositQueue = session.createQueue(properties
					.getProperty("queue.submit.name"));

			// create JMS consumer using the session and destination
			// (autoDepositQueue)
			messageProducer = session.createProducer(autoDepositQueue);
			//OutputStream out = connection.createOutputStream(autoDepositQueue);
			

		} catch (IOException e) {
			System.out.println("IOException : properties file not found  "+ e);
		} catch(JMSException e){
			System.out.println("JMSException :   "+ e);
		}
	}

	public void connect() throws JMSException {
			
	}

	public void disconnect() throws JMSException {
		
			if (connection != null) {
				
				messageProducer.close();
				connection.stop();
				connection.close();
				System.out.println("disconnect");
			}
		
	}

	/**
	 * 
	 * @param dataFile
	 *            the byte data
	 * @param entryType
	 *            created, modified or deleted file
	 * @param ID
	 *            filename
	 * @param session
	 * @return Message object
	 * @throws JMSException
	 */
	protected Message createByteArrayMessage(byte[] fileByteArray, String ID,String checksum,String domain,String projectName,
			String entryType, String dateTime, Session session)
			throws JMSException,XMLStreamException {

		/**
		 * Can replace this with XML string for parameters: entryType, ID and
		 * dateTime
		 */
		MapMessage message;
		
			message = session.createMapMessage();
			String msgParam = createXMLParameter(ID, checksum, entryType,projectName, dateTime, domain,"");
			message.setBytes("dataFile", fileByteArray);
			message.setString("msgParamXML", msgParam);
			//message.setString("checksum", msgParam);
			

		// message.setString("entryType", entryType);
		// message.setString("entryID", ID);
		return message;
	}
	
	protected Message createBlobMessage(URL fileURL,String ID, String checksum, String domain, String projectName, String entryType, String dateTime, String experimentId, ActiveMQSession session){
		BlobMessage message=null;
		FileInputStream in=null;
		
		
		String msgParam;
		try {
			msgParam = createXMLParameter(ID, checksum, domain, projectName, entryType, dateTime,experimentId);

			//in = new FileInputStream(fileLocation);
		    //	message.setName(name);
			//message = session.createBlobMessage(new File(fileLocation));
			message = session.createBlobMessage(fileURL);
			//message.setName(name);			
			message.setObjectProperty("msgParamXML", msgParam);	
			
			//message.setObjectProperty("checksum", checksum);	
		
			//message = session.createBlobMessage(in);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (XMLStreamException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (FactoryConfigurationError e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		return message;

	}
	
	/**
	 * @param fileURL
	 * 				the url of the file uploaded to fedora
	 * @param ID
	 * @param entryType
	 * @param dateTime
	 * @param session
	 * @return
	 * @throws JMSException
	 * @throws XMLStreamException
	 */
	protected Message createNewExperimentMessage(String experimentXMLString)
			throws JMSException,XMLStreamException {
		
		MapMessage message = session.createMapMessage();
		message.setString("msgParamXML", experimentXMLString);

		return message;
	}
	
	public void sendBlobMessage(URL fileURL, String ID, String checksum, String domain, String projectName, String entryType, String dateTime, String experimentId)throws JMSException{
		//boolean uploaded= uploadFileToServer(ID,fileURL);
		//System.out.println("uploaded: "+uploaded);
		
		Message message = createBlobMessage(fileURL,  ID, checksum , domain, projectName, entryType,  dateTime, experimentId, session);
		System.out.println("Sending: "
				+ ((ActiveMQBlobMessage) message).getName()
				+ " on queue: " + autoDepositQueue);
		messageProducer.send(message);
		
	}
	
	public void sendByteArrayMessage(byte[] fileByteArray, String ID, String checksum, String domain,String projectName, String entryType,
			String dateTime) throws JMSException,XMLStreamException,FactoryConfigurationError {

	
			Message message = createByteArrayMessage(fileByteArray, ID, checksum, domain, projectName, entryType, dateTime, session);
			

			System.out.println("Sending: "
					+ ((ActiveMQMapMessage) message).getContentMap()
					+ " on queue: " + autoDepositQueue);
			messageProducer.send(message);

	
	}
	
	public void sendNewExperimentMessage(String experimentXMLString) throws JMSException,XMLStreamException,FactoryConfigurationError {

			Message message = createNewExperimentMessage(experimentXMLString);

			System.out.println("Sending I'm new experiment: "
					+ ((ActiveMQMapMessage) message).getContentMap()
					+ " on queue: " + autoDepositQueue);
			messageProducer.send(message);

	}
	


	protected String createXMLParameter(String ID, String checksum,String domain,String projectName, String entryType,
			String dataTime, String experimentId) throws XMLStreamException,FactoryConfigurationError {
		    String messageParameter = "";
	
			StringWriter res = new StringWriter();
			XMLStreamWriter writer = XMLOutputFactory.newInstance()
					.createXMLStreamWriter(res);
			writer.writeStartDocument();

			writer.writeStartElement(propertiesXML.getProperty("root.element"));

			writer.writeStartElement(propertiesXML
					.getProperty("child.experiment.id"));
			writer.writeCharacters(String.valueOf(experimentId));
			writer.writeEndElement();
			
			writer.writeStartElement(propertiesXML
					.getProperty("child.id"));
			writer.writeCharacters(String.valueOf(ID));
			writer.writeEndElement();
			
			writer.writeStartElement(propertiesXML
					.getProperty("child.checksum"));
			writer.writeCharacters(String.valueOf(checksum));
			writer.writeEndElement();
			
			writer.writeStartElement(propertiesXML
					.getProperty("child.domain"));
			writer.writeCharacters(String.valueOf(domain));
			writer.writeEndElement();
			
			writer.writeStartElement(propertiesXML
					.getProperty("child.project"));
			writer.writeCharacters(String.valueOf(projectName));
			writer.writeEndElement();
			
			writer.writeStartElement(propertiesXML
					.getProperty("child.entry.type"));
			writer.writeCharacters(String.valueOf(entryType));
			writer.writeEndElement();

			writer.writeStartElement(propertiesXML
					.getProperty("child.date.time"));
			writer.writeCharacters(String.valueOf(dataTime));
			writer.writeEndElement();
			
			writer.writeEndElement();
			writer.writeEndDocument();

			messageParameter = res.toString();

		return messageParameter;
	}
    public boolean uploadFileToServer(String ID, URL fileURL){
    	File file = new File(ID);
    	byte[] fileContents;
    	boolean uploaded=false;
    	FileServerClient client = new FileServerClient(fileURL);
		try {
			fileContents = FileUtil.getBytesFromFile(file);
			uploaded = client.upload(fileContents);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return uploaded;
    }
    
    
	public static void main(String[] args)  {
	//	File file = new File("c:/Experiment/h2-2_MS_1_004.img.bz2");
		File file = new File("c:/Experiment/baa5d5/high.mtz");
		String serverLocation ="http://localhost:8161/fileserver/";
		String filename = "";
		String entryType = "ENTRY_CREATE";
		String ID = "c:/Experiment/5d5.mtz";
		
        URL url=null;
        
        //Generate checksum for the file here 
        
		
		byte[] fileContents;
		try {
			fileContents = FileUtil.getBytesFromFile(file);
			filename = "5d5.mtz";
			//FileUtil.getFileName("c:/Experiment/h2-2_MS_1_002.bz2");
			url = new URL(serverLocation+filename);
			MD5ChecksumProcessor c= new MD5ChecksumProcessor();
			String checksum = c.generateChecksum(file);
			String domain="crystallography";
			ADQueueWriterImpl qu = new ADQueueWriterImpl();
			
			//upload a file to a webserver
			FileServerClient client = new FileServerClient(url);
		//	boolean uploaded = client.upload(fileContents);
			
			//If upload is true send a blob message with this file url
			//if (uploaded=true){				
				//qu.sendBlobMessage(url,ID,domain,checksum,entryType,"12/10/2010 12:22:22");	
				try {
					qu.sendByteArrayMessage(fileContents, ID, checksum, domain, "baa5d5", entryType, "12/10/2010 12:22:22");
				} catch (XMLStreamException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
		//	}
			//qu.sendMessage(fileContents, ID, entryType, "");
			
			//qu.sendBlobMessage("c:/Experiment/hello123.xml", "123.xml");
					
				qu.disconnect();
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			//} catch (XMLStreamException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
			}catch (FactoryConfigurationError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		   } catch (ChecksumProcessorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

}
