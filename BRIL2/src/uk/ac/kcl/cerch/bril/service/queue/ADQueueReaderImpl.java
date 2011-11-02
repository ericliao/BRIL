package uk.ac.kcl.cerch.bril.service.queue;


/*import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;
import java.util.Map;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.xml.stream.XMLStreamException;
import javax.jms.MessageListener;
import uk.ac.kcl.cerch.bril.Consumer;
import uk.ac.kcl.cerch.bril.Listener;
import org.apache.activemq.BlobMessage;
import uk.ac.kcl.cerch.bril.service.queue.MessageMetadata;
import uk.ac.kcl.cerch.bril.metadata.queue.MessageMetadata;*/
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;

import uk.ac.kcl.cerch.bril.common.types.DataStreamType;
import uk.ac.kcl.cerch.bril.common.types.DatastreamMimeType;
import uk.ac.kcl.cerch.bril.service.queue.ADException;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.JDOMException;

import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.FitsOutput;
import edu.harvard.hul.ois.fits.exceptions.FitsConfigurationException;
import edu.harvard.hul.ois.fits.exceptions.FitsException;


public class ADQueueReaderImpl implements ADQueueReader, Runnable {
	private Properties properties;
	//private static transient ConnectionFactory factory;
	//private transient Connection connection;
	//private transient Session session;
	private ActiveMQConnectionFactory factory;
	private ActiveMQConnection connection;
	private ActiveMQSession session;
	//private QueueBrowser queueBrowser;
	
	private MessageConsumer messageConsumer;
	private Destination autoDepositQueue;
	public javax.jms.Queue queue;
	public ADMessageListener listener; 
	//private MessageMetadata messageMetadata;
	private boolean flag = false;
	private Thread thread;
	private Logger logger;
//	private int numberOfMessages;
	private static String FITS_HOME = "/home/eliao/fits/";

	public ADQueueReaderImpl() throws ADException{
		try {
			properties = new Properties();
			properties.load(this.getClass().getClassLoader().getResourceAsStream(
					"bril.properties"));
			logger = Logger.getLogger(this.getClass());

			String brokerURL = properties.getProperty("messaging.url") + ":"
					+ properties.getProperty("messaging.port");
			System.out.println(brokerURL);
			factory = new ActiveMQConnectionFactory(brokerURL);

			// using the connection factory create JMS connection
			connection = (ActiveMQConnection)factory.createConnection();

			// start the connection to enable messages to start flowing
			connection.start();

			// JMS session: auto acknowledge
			session = (ActiveMQSession)connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			// producer = session.createProducer(null);
			// must be a message queue

			// create JMS queue using the session
			autoDepositQueue = session.createQueue(properties
					.getProperty("queue.submit.name"));

			// javax.jms.Queue queue = session.createQueue("MyQueue");
			queue = (javax.jms.Queue) autoDepositQueue;

			// create a queue browser using the selector
			//queueBrowser = session.createBrowser(queue);
			listener = new ADMessageListener();
			// browse the messages
		/*	Enumeration e = queueBrowser.getEnumeration();

			// count number of messages
			while (e.hasMoreElements()) {
				Message message = (Message) e.nextElement();
				numberOfMessages++;
			}
			System.out.println(queue + " has " + numberOfMessages + " messages");
*/
			
		} catch (IOException e) {
			throw new ADException(e);
		} catch (JMSException e) {
			throw new ADException(e);
		}
	}

	public void stopService() throws ADException {
		try {
			System.out.println("ADQueueReader service stopped ----- ");
		//	if (connection != null) {
				//session.commit();
				messageConsumer.close();
				//connection.stop();
				//connection.close();
				flag= false;
		//	}
		} catch (JMSException e) {
			logger.error("Stop Service: "+e);
			throw new ADException(e);
		}
	}
	
	protected void closeConnection(){
		try{
			connection.close();
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public void startService() throws ADException {

		try {
			// create JMS consumer using the session and destination
			messageConsumer = session.createConsumer(queue);	   
			//Message inMessage = messageConsumer.receive();
			//Message inMessage = messageConsumer.receiveNoWait();
			//Message inMessage = messageConsumer.receive(timeout);
			
		//	if (inMessage!= null) {
			 
				messageConsumer.setMessageListener(listener);	
				//listener.setNumberOfMessages(numberOfMessages);
				//listener.setQueueBrowser(queueBrowser);
				//listener.onMessage(inMessage);
				 
				flag = true;
				
				System.out.println("ADQueueReader service started ----- ");
				
				thread = new Thread(this);
				thread.start();
				//messageMetadata= listener.getMessageMetadata();
				//System.out.println("Message reveived by message Consumer ----- ");
			
			//} else {
				//System.out.println("Consumer: received empty message");
			//}
		} catch (JMSException e) {
			String error = "Start Service- Consumer: JMSException Occured: "
				+ e.getMessage();
				System.out.println("Start Service- Consumer: JMSException Occured: "
					+ e.getMessage());
			logger.error(error);
			throw new ADException(e);
			
		}
	}
	
	public void run(){
		try{
			while(flag){
				Thread.sleep(1000);
			}
			
		}catch (InterruptedException exception){
			
			System.out.println("thread interrupted: "+ exception);
			String error = "thread interrupted: "+ exception;
			logger.error(error);
		}
	}
/*	public void browseQueue() throws JMSException {

		// create a queue browser using the selector
		QueueBrowser queueBrowser = session.createBrowser(queue);

		// browse the messages
		Enumeration e = queueBrowser.getEnumeration();

		// count number of messages
		while (e.hasMoreElements()) {
			Message message = (Message) e.nextElement();
			numberOfMessages++;
		}
		System.out.println(queue + " has " + numberOfMessages + " messages");

	}*/

	private static byte[] outputPREMISXml(FitsOutput fitsOutput) throws XMLStreamException, IOException, TransformerConfigurationException {
		
		Document doc = fitsOutput.getFitsXml();
	    
	    //initialize transformer for PREMIS xslt	        
	    TransformerFactory tFactory = TransformerFactory.newInstance();		
	    
	    // Get a XSLT transformer
	    StreamSource xsltSource = new StreamSource(new FileInputStream(FITS_HOME + "xml/fits_to_premis_object.xsl"));
		Transformer transformer;		
		transformer = tFactory.newTransformer(xsltSource);
		    
        if(doc != null && transformer != null) {
        	
        	// Make the input sources for the XML and XSLT documents
    		org.jdom.output.DOMOutputter outputter = new org.jdom.output.DOMOutputter();
    		org.w3c.dom.Document domDocument;
			try {
				domDocument = outputter.output(doc);
			
	    		javax.xml.transform.Source xmlSource = new javax.xml.transform.dom.DOMSource(domDocument);	    		
	    	
	    		ByteArrayOutputStream xsltOutStream = new ByteArrayOutputStream();
	    		StreamResult xmlResult = new StreamResult(xsltOutStream);	    		    	
	    	
	    		// Do the transform
				transformer.transform(xmlSource, xmlResult);
				
	    		return xsltOutStream.toString().getBytes("UTF-8");
    		
    		} catch (JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();				
    		} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}                                  
        }
        else {
                System.err.println("Error: output cannot be converted to PREMIS format");
        }
		return null;
	}
	

	public static void main(String[] args) throws ADException, Exception {
		ADQueueReaderImpl consumer = new ADQueueReaderImpl();

		/*
		Fits fits;
		try {
			fits = new Fits();
			File file = new File("/home/eliao/5d5.mtz");
			FitsOutput fitsOut;			
			System.out.println("Testing FITS toolkit...");
			fitsOut = fits.examine(file);			
			byte[] PREMIS = outputPREMISXml(fitsOut);
			System.out.println(PREMIS.toString());								
		} catch (FitsConfigurationException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (FitsException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
				
		while (true) {
			System.out.println("---------------Message Queue Started-----------");
			consumer.startService();
			Thread.sleep(20000);
			consumer.stopService();
			System.out.println("---------------Message Queue Stopped-----------");

		}		
	}

}
