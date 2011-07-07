package uk.ac.kcl.cerch.bril.service.queue;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.StartElement;




public class MessageMetadata implements MsgMetadata {
	private String msgMetadata;
	private Properties propertiesXML;
	private String entryType;
	private String id;
	private String dateTime;
	private String checksum;
	private String experimentType;
	private String projectName;
	private String mimeType;
	private String ExperimentId;
	private String directoryPath;

	public MessageMetadata(String msgMetadata) {
		setMetadata(msgMetadata);
		try {
			propertiesXML = new Properties();
			propertiesXML.load(getClass().getClassLoader().getResourceAsStream(
					"messagexml.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String getMetadata() {
		return msgMetadata;
	}

	private void setMetadata(String metadata) {
		this.msgMetadata = metadata;
		try{
		readMessageXMLParameter(msgMetadata);
		
		}catch(XMLStreamException e){
			e.printStackTrace();
		}
	}
	
	private void setIdPath(String id){
		this.id =id;
	}
	
	public String getIdPath(){
		return id;
	}
	
	private void setEntryType(String entry){
		this.entryType =entry;
	}
	
	public String getEntryType(){
		return entryType;
	}

	
	private void setDateTime(String dateTime){
		this.dateTime =dateTime;
	}
	
	/* 
	 * The date time format would be: "dd/MM/yyyy HH:mm:ss" 
	 * as set by the DirectoryMonitor when the file object is sent to the queue
	 * @see uk.ac.kcl.cerch.bril.metadata.queue.MsgMetadata#getDateTime()
	 */
	public String getDateTime(){
		return dateTime;
	}
	
	private void setChecksum(String checksum){
		this.checksum =checksum;
	}
	
	public String getChecksum(){
		return checksum;
	}
	
	private void setExperimentType(String experimentType){
		this.experimentType =experimentType;
	}
	
	public String getExperimentType(){
		return experimentType;
	}
	
	private void setProjectName(String projectName){
		this.projectName =projectName;
	}
	
	
	public String getProjectName(){
		return projectName;
	}
	
	public void setMimeType(String mimeType){
		this.mimeType =mimeType;
	}
	
	
	public String getMimeType(){
		return mimeType;
	}
	
	public void setExperimentId(String ExperimentId){
		this.ExperimentId =ExperimentId;
	}
	
	
	public String getExperimentId(){
		return ExperimentId;
	}
	
	public void setDirectoryPath(String directoryPath){
		this.directoryPath =directoryPath;
	}
	
	
	public String setdirectoryPath(){
		return directoryPath;
	}


	private void readMessageXMLParameter(String msgParamXML)
			throws XMLStreamException {
		StringReader strReader = new StringReader(msgParamXML);
		XMLInputFactory staxFactory = XMLInputFactory.newInstance();
		staxFactory.setProperty(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);
		XMLStreamReader xmlReader = staxFactory
				.createXMLStreamReader(strReader);
		int eventType =xmlReader.getEventType();
		/*while (xmlReader.hasNext()){
			eventType=xmlReader.next();
			//System.out.println("event type: "+eventType);
		//	if(eventType==XMLStreamConstants.START_ELEMENT && xmlReader.getLocalName().equals("message_parameter")){
			//	System.out.println("localname: "+xmlReader.getLocalName());			
		//	}
			
	
			if( xmlReader.getLocalName().equals("id")){
			  	String idtxt = xmlReader.getElementText();
				setIdPath(idtxt);
				System.out.println("id: "+idtxt);
			}
			if (xmlReader.getLocalName().equals("entryType")) {
				String entryType = xmlReader.getElementText();
				setEntryType(entryType);
				System.out.println("entryType: "+entryType);
			}
			if (xmlReader.getLocalName().equals("dateTime")) {
				String dateTime = xmlReader.getElementText();
				setDateTime(dateTime);
				System.out.println("dateTime: "+dateTime);
			}
			if (xmlReader.getLocalName().equals("checksum")) {
				String checksum = xmlReader.getElementText();
				setChecksum(checksum);
				System.out.println("checksum: "+checksum);
			}
			if (xmlReader.getLocalName().equals("domain")) {
				String domain = xmlReader.getElementText();
				setDomain(domain);
				System.out.println("domain: "+domain);
			}
			
			
		}
*/
		while (xmlReader.hasNext()) {
			if (xmlReader.isStartElement()) {
				String sName = xmlReader.getLocalName();
				
				//System.out.println("localname: "+sName);
				if (sName.equals("experimentId")) {
					String experimentId = xmlReader.getElementText();
					setExperimentId(experimentId);
					//System.out.println("domain: "+domain);
				}
				if (sName.equals("id")) {
					String id = xmlReader.getElementText();
					setIdPath(id);
					//System.out.println("id: "+id);
				}
				if (sName.equals("entryType")) {
					String entryType = xmlReader.getElementText();
					setEntryType(entryType);
					///System.out.println("entry type: "+entryType);
				}
				if (sName.equals("dateTime")) {
					String dateTime = xmlReader.getElementText();
					setDateTime(dateTime);
					//System.out.println("datetime:" +dateTime);
				}
				if (sName.equals("checksum")) {
					String checksum = xmlReader.getElementText();
					setChecksum(checksum);
				//	System.out.println("checksum: "+checksum);
				}
				if (sName.equals("experimentType")) {
					String experimentType = xmlReader.getElementText();
					setExperimentType(experimentType);
					//System.out.println("domain: "+domain);
				}
				if (sName.equals("projectName")) {
					String projectName = xmlReader.getElementText();
					setProjectName(projectName);
					//System.out.println("domain: "+domain);
				}
				if (sName.equals("mimeType")) {
					String mimeType = xmlReader.getElementText();
					setMimeType(mimeType);
					//System.out.println("domain: "+domain);
				}
			
				if (sName.equals("directoryPath")) {
					String directoryPath = xmlReader.getElementText();
					setDirectoryPath(directoryPath);
					//System.out.println("domain: "+domain);
				}
			}
			xmlReader.next();
		}
		
		xmlReader.close();
	}
	
	public static void main(String arg[]){
		String msgParamXML= 
				"<?xml version= \"1.0\" ?><message_parameter><experimentId>C:\\Experiment\\baa5d5\\5d5.mtz</experimentId>" +
				"<checksum>606dd4359242113467c835a09709fa76</checksum><entryType>" +
				"MODIFY_ENTER</entryType><dateTime>29/07/2010 15:04:22</dateTime>" +
				"<experimentType>Crystallography</experimentType>" +
				"<projectName>baa5d5</projectName>"+
				"</message_parameter>";
		//<?xml version="1.0" ?>
		MessageMetadata msgMetadata= new MessageMetadata(msgParamXML);
		System.out.println("ID: "+ msgMetadata.getIdPath());
		System.out.println("EXP ID: "+ msgMetadata.getExperimentId());
		System.out.println("Entry: "+msgMetadata.getEntryType());
		System.out.println("Datetime: "+ msgMetadata.getDateTime());
		System.out.println("Classification: "+msgMetadata.getExperimentType());
		
		System.out.println("project: "+msgMetadata.getProjectName());
	}
}
