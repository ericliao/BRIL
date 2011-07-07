package uk.ac.kcl.cerch.bril.service.queue;

import java.io.BufferedReader;
import java.io.File;
/*import java.io.FileInputStream;
import java.io.FileNotFoundException;*/
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.RemoteException;
//import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
//import java.util.Map.Entry;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
//import javax.jms.QueueBrowser;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/*import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;*/

import org.apache.activemq.BlobMessage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import uk.ac.kcl.cerch.bril.service.queue.ADException;
import uk.ac.kcl.cerch.bril.common.fedora.BrilObjectRepositoryException;
import uk.ac.kcl.cerch.bril.common.fedora.FedoraAdminstrationImpl;
import uk.ac.kcl.cerch.bril.common.fedora.FedoraHandler;
//import uk.ac.kcl.cerch.bril.common.fedora.FedoraUtils;
import uk.ac.kcl.cerch.bril.common.metadata.DublinCore;
//import uk.ac.kcl.cerch.bril.common.metadata.DublinCoreElement;
import uk.ac.kcl.cerch.bril.common.types.DataStreamType;
import uk.ac.kcl.cerch.bril.common.types.DatastreamMimeType;
import uk.ac.kcl.cerch.bril.common.types.DatastreamObjectContainer;
import uk.ac.kcl.cerch.bril.common.util.FileUtil;
//import uk.ac.kcl.cerch.bril.fileformat.FileSuffixFileFormat;
import uk.ac.kcl.cerch.bril.service.queue.MessageMetadata;
import uk.ac.kcl.cerch.bril.service.uuid.IDGenerator;
/*import uk.ac.kcl.cerch.bril.sip.BrilSIP;
import uk.ac.kcl.cerch.bril.sip.processor.BrilSIPProcessor;
import uk.ac.kcl.cerch.soapi.sip.processor.SIPProcessorException;
 */

public class ADMessageListener implements MessageListener {

	private InputStream inputStream;
	//private FileOutputStream out;
	private MessageMetadata msgMetadata;
	//private Vector<MessageMetadata> messageVector;
	String firstMsgID = null;
	String firstMsgEntry = null;
	String firstMsgChecksum = null;
	//private int numOfMessages=0;
	private int count=0;
	//private QueueBrowser queueBrowser;
	private Map<String, MessageMetadata> sameMessageMap; 
	private Map<String, String> msgIdentifier; 
	//private Map<String, InputStream> sameInputstream;
	//private boolean flag;
	private String msgMetadataXML;
	private String filePath;
	private String brilTmpStore;
	private Properties properties;

	public ADMessageListener() throws ADException {	
		sameMessageMap = new HashMap<String, MessageMetadata>();
		msgIdentifier= new HashMap<String, String>();
		try {		
			/*	propertiesXML = new Properties();
			propertiesXML.load(getClass().getClassLoader().getResourceAsStream(
					"messagexml.properties"));*/

			properties = new Properties();
			properties.load(getClass().getClassLoader().getResourceAsStream(
			"bril.properties"));


			/*	experimentTypesProperties = new Properties();
			experimentTypesProperties.load(getClass().getClassLoader()
					.getResourceAsStream("domain.properties"));*/

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void onMessage(Message inMessage) {

		try {
			System.out.println("ADMessageListener: Read Message...... ");
			if (inMessage instanceof MapMessage) {
				MapMessage map = (MapMessage) inMessage;
				System.out.println("Message of Map type");
				processMapMessage(map);

			} else if (inMessage instanceof BlobMessage) {

				BlobMessage blobMessage = (BlobMessage) inMessage;
				System.out.println("Message of Blob type");
				processBlobMessage(blobMessage);
				//processBlobMessage1(blobMessage);
			} else {

				System.out.println("Message of wrong type: "
						+ inMessage.getClass().getName());
			}
			//	numOfMessages++;
			System.out.println("ADMessageListener: Completed Read Message...... ");

		} catch (Exception e) {
			System.out.println("Consumer: JMSException Occured: "
					+ e.getMessage());
			e.printStackTrace();
		}
	}



	/**
	 * @param mapMsg
	 * @throws ADException
	 */
	private void processMapMessage(MapMessage mapMessage) throws ADException {

		try {
			if (mapMessage.itemExists("msgParamXML")) {
				String msgParamXML = mapMessage.getString("msgParamXML");
				ingestNewExperimentObjectToFedora(msgParamXML);	
			}

		} catch (JMSException e) {
			System.out.println("JMSException : Error reading a message " + e);
		}

	}

	private void ingestNewExperimentObjectToFedora(String msgParamXML){
		MessageMetadata msgMetadata = new MessageMetadata(msgParamXML);	
		System.out.println(msgParamXML);
		if (msgMetadata.getExperimentId()!=null){
			/*System.out.println(msgMetadata.getExperimentId());
			System.out.println(msgMetadata.getDateTime());
			System.out.println(msgMetadata.getExperimentType());
			System.out.println(msgMetadata.getProjectName());
			System.out.println(msgMetadata.getDateTime());*/
			DublinCore digitalObjectDC = new DublinCore("bril:"+msgMetadata.getExperimentId());
			digitalObjectDC.setTitle(msgMetadata.getProjectName());
			digitalObjectDC.setDescription("I am a new experiment: "+msgMetadata.getExperimentType());
			digitalObjectDC.setDate(msgMetadata.getDateTime(), "dd/MM/yyyy HH:mm:ss");
			System.out.println("--------  END: Create Dublin code metadata created --------------");

			DatastreamObjectContainer dsc = new DatastreamObjectContainer(msgMetadata.getExperimentId());
			dsc.addMetaData(digitalObjectDC);
			dsc.addDatastreamObject(DataStreamType.OriginalData,
					DatastreamMimeType.TEXT_XML.getMimeType(), "ExperimentObject",
					"bril", null);

			try {
				FedoraAdminstrationImpl fedoraAdmin = new FedoraAdminstrationImpl();
				if(fedoraAdmin.hasObject(digitalObjectDC.getIdentifier())==true){
					System.out.println("PID "+ digitalObjectDC.getIdentifier() +" already exists in the repository");
				}else{
					fedoraAdmin.storeObject(dsc);
				}
			} catch (BrilObjectRepositoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}



	/**
	 * Process the blob message 
	 * @param blobMessage
	 */
	private void processBlobMessage(BlobMessage blobMessage) {
		boolean oldMsgActive=false;
		boolean compressedFile=false;
		//Read the object property containing the metadata in xml string and checksum
		try {
			if(blobMessage.getObjectProperty("msgParamXML")!=null)
			{
				msgMetadataXML = blobMessage.getObjectProperty("msgParamXML").toString();
				System.out.println(msgMetadataXML);

				msgMetadata= new MessageMetadata(msgMetadataXML);

				inputStream = blobMessage.getInputStream();


				//URL location =blobMessage.getURL();

				//Check if the file is of correct type
				int pos =msgMetadata.getIdPath().lastIndexOf('.');

				if(msgMetadata.getIdPath().substring(pos+1).equals("bz2")){
					compressedFile=true;
					System.out.println("compressed diffraction file");
				}
				System.out.println("Compressed diffraction file? "+compressedFile);
				if(compressedFile==false){

					/*
					 * If the first message is not the last and queue reader is still actively reading other messages then
					 * the map will not be empty and would contain the first message.
					 */

					if (sameMessageMap.isEmpty() ==false) {
						int size = sameMessageMap.size();
						String key = Integer.toString(size);
						MessageMetadata firstMessageMetadata = sameMessageMap.get(key);
						//add this new message in the map
						if (msgMetadata.getIdPath().equals(firstMessageMetadata.getIdPath())) {
							String tmpPath=putFileInDirectory(msgMetadata.getIdPath(),msgMetadata.getExperimentId());
							System.out.println("checksum of the first msg: " +firstMessageMetadata.getChecksum());

							//reaches only till here for 2nd, 3rd so on diff image message....
							//Cannot match checksum here- since the file is being created or unzipped when each message
							//is generated- the checksum would be different in each message
							//if (msgMetadata.getChecksum().equals(firstMessageMetadata.getChecksum())) {
							//instead check with the experiment id...
							if (msgMetadata.getExperimentId().equals(firstMessageMetadata.getExperimentId())) {
								size = size + 1;
								System.out
								.println("The message is for the same file uploaded to the file server!!");
								System.out
								.println("Message "+size+" for the file with path: "
										+ msgMetadata.getIdPath()
										+ ", "
										+ msgMetadata.getChecksum()
										+ ", "
										+ msgMetadata.getEntryType()
										+ ", "
										+ msgMetadata.getDateTime());

								String newKey = Integer.toString(size);

								sameMessageMap.put(newKey, msgMetadata);


								count++;					
								System.out
								.println("Count: "+ count);

								if(msgMetadata.getEntryType().equals("ENTRY_MODIFY")){
									String path = msgMetadata.getIdPath().replaceAll("\\\\", "/");

									System.out.println("------------ START "+size+ ": Search for path in the respository ---------- ");	
									//search for the path in the repository and purge it if present
									String objectIdentifier = searchInRepositoryAndPurge(msgMetadata.getExperimentId(),path);
									System.out.println("ID in repo:-  "+objectIdentifier);
									//pass this id to message processor and ingest it
									//System.out.println("Older message processed: "+ oldMsgActive);
									if(objectIdentifier!=null){
										// pass this identifier to the message processor
										System.out.println("EXISTING identifier: "+ objectIdentifier);
										//String tmpPath=putFileInDirectory(msgMetadata.getIdPath(),msgMetadata.getExperimentId());

										runMessageProcessor(objectIdentifier,tmpPath);
									}
									else{
										if(objectIdentifier==null){
											System.out.println("----The path not present in the repository yet: Still in SIPProcessing----");

											System.out.println("SIZE of MAP:- "+ sameMessageMap.size());
											//look for the path of the previous message
											String oldPath = msgIdentifier.get("1");
											System.out.println("OLD PATH:- "+ oldPath);
											//compare paths of the old and new messages
											if(oldPath.equals(msgMetadata.getIdPath())){
												System.out.println("---- processing Next message for the same object (in same original path)----");

												boolean found = false; 
												int count =1;
												for (int ct=0;ct<count ;ct++){
													if(found==false){
														//wait for a second
														waitForSomeTime();								  	   
														// then search for object with this path and exp id 
														// to check if previous message is ingested by now
														// and purge it and return the identifier
														objectIdentifier = searchInRepositoryAndPurge(msgMetadata.getExperimentId(),path);
														//if not add one to count
														if(objectIdentifier==null){
															count++; 
														}else
															//if yes then	use the identifier to add this new updated object and message.   
															if(objectIdentifier!=null){
																found=true;
																System.out.println("EXISTING identifier: "+ objectIdentifier);	
																runMessageProcessor(objectIdentifier,tmpPath);										   

															}	
													}
												}

											}

										}
									}


									System.out.println("------------END "+size+ ": Search for path in the respository --------- ");
									/***
									 * Call the MessageProcessor
									 */
									/*System.out.println("-----------------------------------------");
							System.out.println("Call the First MessageProcessor ----- ");				

							runMessageProcessor();

							System.out.println("MessageProcessor Run completed ----- ");
							System.out.println("-----------------------------------------");*/
								}


							}
						} else {

							/*
							 * 
							 * This else statement occurs when a new message is identified by the 'pathID'
							 * 
							 * A Map created would hold the previous messages with the same pathID 
							 * 
							 * 	A Case where there are number of messages already present 
							 * in the queue that belongs to more then one data file. 
							 * (Queue reader stared at some point after the experiment was 
							 * performed creating new files thereby messages were sent to the queue)
							 * */

							System.out.println("New message is identified in the queue: ---------------");

							//					filterMessagesAndProcess();
							// Then empty the Map containing the previous messages with same pathID
							emptyMessageMap();
							//Set count =0 as this message has a new pathID indicating a new file 
							count = 0;





						}

					}

					/*
					 * Occurs for the first message in the queue or message with new pathID
					 * 
					 * */
					if (count == 0) {

						firstMsgID = msgMetadata.getIdPath();
						firstMsgChecksum = msgMetadata.getChecksum();
						firstMsgEntry = msgMetadata.getEntryType();
						//	if(inputStream!=null){
						//	sameInputstream.put("1", inputStream);
						//	}
						//put the first message or new pathID message in the Map<String, MessageMetadata>
						sameMessageMap.put("1", msgMetadata);
						System.out
						.println("Message "+ 1 +" for the file with path "
								+ firstMsgID
								+ ", "
								+ firstMsgChecksum
								+ ", "
								+ firstMsgEntry
								+ ","
								+ msgMetadata.getDateTime());

						String tmpPath=putFileInDirectory(msgMetadata.getIdPath(),msgMetadata.getExperimentId());
						msgIdentifier.put("1",firstMsgID);
						count++;
						//System.out.println("-----------------------------------------");

						/*if(firstMsgEntry.equals("ENTRY_MODIFY")){
				filePath =saveInTmpDirectory();
				System.out.println("First entry filepath: "+filePath);
				}*/

						/*If the first message has the entry type modify then
						 * this may be the file created-modified but the 'ENTRY_CREATE' was not captured
						 * 
						 * When this is the last message in the queue that needs to be processed-- in a case where no more messages are left
						 *  or coming-in in the queue. -And the queue reader stops and starts again. 
						 * */
						if(firstMsgEntry.equals("ENTRY_CREATE")){

							/***
							 * Call the MessageProcessor
							 */
							System.out.println("-----------------------------------------");
							System.out.println("Call the First  ENTRY_CREATE Message ----- ");				
							String objectIdentifier= "bril:" + IDGenerator.generateUUID();
							runMessageProcessor(objectIdentifier,tmpPath);

							System.out.println("MessageProcessor Run completed ----- ");
							System.out.println("-----------------------------------------");
						}else{
							if(firstMsgEntry.equals("ENTRY_MODIFY")){
								//put file in tmp dir
								//String tmpPath=putFileInDirectory(msgMetadata.getIdPath(),msgMetadata.getExperimentId());
								String path = firstMsgID.replaceAll("\\\\", "/");

								System.out.println("------------ START: Search for path in the respository ---------- ");	
								//search for the path in the repository and purge it if present
								String objectIdentifier = searchInRepositoryAndPurge(msgMetadata.getExperimentId(),path);				
								System.out.println("------------ END: Search for path in the respository ---------- ");	
								/***
								 * Call the MessageProcessor
								 */
								System.out.println("-----------------------------------------");
								System.out.println("Call the First ENTRY_MODIFY Message----- ");									
								if(objectIdentifier!=null){
									System.out.println("EXISTING identifier (first msg): "+ objectIdentifier);
									boolean res = runMessageProcessor(objectIdentifier, tmpPath);
									oldMsgActive=res;
								}else{
									objectIdentifier= "bril:" + IDGenerator.generateUUID();
									System.out.println("NEW identifier (first msg): "+ objectIdentifier);
									boolean res = runMessageProcessor(objectIdentifier,tmpPath);
									oldMsgActive=res;
								}
								System.out.println("MessageProcessor Run completed ----- ");
								System.out.println("-----------------------------------------");
							}
						}

					}

				} //END compressed file is false
				//if there is no more message coming in then this is the only version of message
				//which needs to be processed.

			}//END blob message get property is not null

			//}
		} catch (JMSException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}

	private void processBlobMessage1(BlobMessage blobMessage){
		try{
			String tmpPath=null;
			if(blobMessage.getObjectProperty("msgParamXML")!=null)
			{
				msgMetadataXML = blobMessage.getObjectProperty("msgParamXML").toString();
				System.out.println(msgMetadataXML);

				msgMetadata= new MessageMetadata(msgMetadataXML);

				try {
					inputStream = blobMessage.getInputStream();
					tmpPath = putFileInDirectory(msgMetadata.getIdPath(),msgMetadata.getExperimentId());
					String path = msgMetadata.getIdPath().replaceAll("\\\\", "/");
					System.out.println("------------ START: Search for path in the respository ---------- ");

					String objectIdentifier = searchInRepositoryAndPurge(msgMetadata.getExperimentId(),path);				
					System.out.println("------------ END: Search for path in the respository ---------- ");	

					if(objectIdentifier!=null){
						System.out.println("EXISTING identifier (first msg): "+ objectIdentifier);
						boolean res = runMessageProcessor(objectIdentifier, tmpPath);

					}else{
						objectIdentifier= "bril:" + IDGenerator.generateUUID();
						System.out.println("NEW identifier (first msg): "+ objectIdentifier);
						boolean res = runMessageProcessor(objectIdentifier,tmpPath);

					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (JMSException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 

	}
	public String searchInRepositoryAndPurge(String experimentId, String path){
		String id=null;
		Vector<String> res= getObjectIdentifiers(experimentId,path);
		System.out.println(res);
		if(res.size()!=0){
			//purge the object here before ingesting this new ones.
			for (int i=0;i<res.size();i++){
				id= res.elementAt(i).substring(res.elementAt(i).indexOf('/')+1);
				System.out.println(id);
				//purge it
				purgeIfPresentInRepository(id);

			}
		}
		return id;
	}
	//This can be creatingSIP
	private boolean runMessageProcessor(String objectIdentifier,String tmpPath){
		boolean result=false;
		MessageProcessor p = new MessageProcessor();
		if(inputStream!=null && msgMetadata!=null){

			result= p.processMessage(tmpPath,  msgMetadataXML,objectIdentifier);

		}
		return result;
	}
	/*Put the file in the directory with the name as experimentId.
	 * 
	 */
	private String putFileInDirectory(String path, String expId) {

		String digitalObjectFileName = FileUtil.getFileName(path);
		System.out.println("---------- Write file to a tmp directory filename----- "
				+ digitalObjectFileName);
		filePath = writeStreamInExperimentDirectory(digitalObjectFileName, expId);

		System.out.println("tmppath: ----- " + filePath);

		return filePath;

	}
	/* Write file to tmp directory called "brilstore/expeitmentId" 
	 * */
	private String writeStreamInExperimentDirectory(String fileName, String experimentId) {
		String OS = System.getProperty("os.name").toLowerCase();

		String fileTmpPath = "";
		brilTmpStore= properties.getProperty("tmp.file.store");
		try {
			if(experimentId.contains(":")){
				experimentId=experimentId.replace(":", "-");
			}
			String expDirectory=null;
			if(OS.indexOf("windows")>-1){
				System.out.println("is windows machine..");
				expDirectory = brilTmpStore + "\\" + experimentId;
				fileTmpPath= expDirectory+ "\\" +fileName;
			}else{
				System.out.println("is Linux machine..");
				expDirectory = brilTmpStore + "/" + experimentId;
				fileTmpPath= expDirectory+ "/" +fileName;

			}

			boolean existsDirectory = (new File(expDirectory)).exists();

			if (existsDirectory == false) {
				FileUtil.createDirectory(expDirectory);
			}
			OutputStream outputStream = new FileOutputStream(fileTmpPath);

			// write the file on to activeMQ
			byte[] buffer = new byte[1024];
			while (true) {

				int bytesRead = inputStream.read(buffer);
				if (bytesRead == -1) {
					break;
				}
				// System.out.println("write start");
				outputStream.write(buffer, 0, bytesRead);
			}
			// close the stream so the receiving side knows the stream is
			// finished
			outputStream.close();


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileTmpPath;
	}
	private void runMessageProcessor(){
		/*System.out
		.println("Message processed: "
				+ firstMsgID
				+ ", "
				+ firstMsgChecksum
				+ ", "
				+ firstMsgEntry
				+ ","
				+ msgMetadata.getDateTime());*/
		MessageProcessor p = new MessageProcessor();
		if(inputStream!=null && msgMetadata!=null){

			p.processMessage("",  msgMetadataXML,"");

		}
	}


	private String saveInTmpDirectory(){
		String path="";
		MessageProcessor p = new MessageProcessor();
		if(inputStream!=null && msgMetadata!=null){

			p.processMessage("",  msgMetadataXML,"");
			path= p.getTmpFilePath();

		}
		return path;
	}



	private void filterMessagesAndProcess(){
		int finalSize = sameMessageMap.size();
		String lastKey = Integer.toString(finalSize);
		/*	for(Map.Entry<String,MessageMetadata> entry : sameMessageMap.entrySet()){
			//System.out.println(entry.getKey());

			String key1 = entry.getKey();
			MessageMetadata mmd =entry.getValue();
			System.out.println(key1 +" has message with entry type: "+ mmd.getEntryType());
			//if(mmd.getEntryType().equals(""))


		}*/
		if (sameMessageMap.containsKey(lastKey)){
			MessageMetadata messageToProcess =sameMessageMap.get(lastKey);
			//call the message processor here;
			if (messageToProcess.getEntryType().equals("ENTRY_MODIFY")){


				/**
				 * Call the MessageProcessor
				 **/
				System.out.println("-----------------------------------------");
				System.out.println("Call the MessageProcessor ----- ");
				System.out.println(messageToProcess.getIdPath()+", "+messageToProcess.getChecksum()+", "+
						messageToProcess.getEntryType()+", "+
						messageToProcess.getDateTime());	

				/*if(inputStream!=null && msgMetadata!=null){

			    p.processMessage(inputStream, msgMetadata);

				}*/
				System.out.println("MessageProcessor Run completed ----- ");
				System.out.println("-----------------------------------------");
			}
		}	
	}

	private void emptyMessageMap(){
		System.out.println("Empty the Map --------");
		Vector<String> keys= new Vector<String>();
		for(Map.Entry<String,MessageMetadata> entry : sameMessageMap.entrySet()){
			//System.out.println(entry.getKey());
			String key1 =entry.getKey();
			keys.add(key1);							
		}
		//	System.out.println(keys.size());
		for (int i= 1 ;i<keys.size()+1;i++ ){
			sameMessageMap.remove(Integer.toString(i));
		}

		//System.out.println("Map size: "+sameMessageMap.size());
		System.out.println("Map empty: "+sameMessageMap.isEmpty());
	}
	private Vector<String> getObjectIdentifiers(String expId, String title){
		Vector <String> results= new Vector<String>();
		String xmlSource=getIdentifierWithTheTitle(expId,title);
		System.out.println(xmlSource);
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();			
			Document sourceDoc= builder.parse(new InputSource(new StringReader(xmlSource)));

			NodeList list = sourceDoc.getElementsByTagName("*");
			for (int i = 0; i < list.getLength(); i++) {
				Element element = (Element) list.item(i);
				//System.out.println(element);
				if(element.getTagName().equals("result")){
					NodeList childList = element.getChildNodes();
					//	System.out.println(childList.getLength());
					String pid=null;
					Vector<String> date=new Vector<String>();
					String[] date1=null;
					for(int j=0;j<childList.getLength();j++){


						if(childList.item(j).getNodeName().equals("object")){
							Element obj = (Element) childList.item(j);
							// System.out.println(childList.item(j).getNodeName());
							// System.out.println("pid: "+obj.getAttribute("uri").trim());
							pid=obj.getAttribute("uri").trim();
						}

					}

					results.add(pid);

				}		

			}
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}

	/**
	 * Search the repository using the ITQL query (REST interface), that has the given DC title and isPartof
	 * the experiment ID
	 *  
	 * @param expId
	 * @param title
	 * @return
	 */
	private String getIdentifierWithTheTitle(String expId, String title){
		System.out.println(title);
		String title1 = replaceSlashes(title);
		String result=null;
		FedoraHandler fedoraHandler;
		String risearchURL=null;
		try {
			fedoraHandler = new FedoraHandler();

			risearchURL= fedoraHandler.getFedoraURL()+"/risearch";

		} catch (BrilObjectRepositoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		System.out.println(title1);

		String requestParameters = "query=select+%24object+from+%3C%23ri%3Ewhere+%24object+%3Cdc%3Atitle%3E+%27" +
		title1 +
		"%27and+%24object+%3Cfedora-rels-ext%3AisPartOf%3E+%3Cinfo%3Afedora%2Fbril%3A" +
		expId +
		"%3E&format=Sparql&type=tuples&lang=itql";

		System.out.println(requestParameters);

		try
		{
			// Send data

			if (requestParameters != null && requestParameters.length () > 0)
			{
				risearchURL += "?" + requestParameters;
			}
			System.out.println(risearchURL);
			URL url = new URL(risearchURL);
			URLConnection conn = url.openConnection ();

			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = rd.readLine()) != null)
			{
				sb.append(line);
			}
			rd.close();
			result =sb.toString();
			//System.out.println(sb.toString());
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return result;

	}
	private String replaceSlashes(String path){

		char BACKSLASH_CHAR = '\\';

		int charCount =0;
		char lookFor='/';

		for (int i = 0; i < path.length(); i++) {  
			final char c = path.charAt(i);  
			if (c == lookFor) {  
				charCount++;  
			}  
		} 
		String nPath =path;
		if(charCount!=0){ 

			for (int i=0;i<charCount;i++){
				int pos =nPath.indexOf("/");

				//replace with one back slash
				StringBuffer buf = new StringBuffer(nPath);
				buf.setCharAt( pos, BACKSLASH_CHAR );
				String path1 = buf.toString( );
				nPath = new StringBuffer(path1).insert(pos+1, BACKSLASH_CHAR).toString();
				System.out.println("Way1 at add slash: "+ path1);
				System.out.println("Way1 at add slash: "+ nPath);

			}

		}

		/*
        String extraslashString="\\\\";

		String resultPath="";

		  if(path.indexOf("/")!=-1){
			  resultPath= path.substring(0,path.indexOf("/"));
			  resultPath =resultPath+extraslashString;
			 System.out.println(resultPath);
			 String path2= path.substring(path.indexOf("/")+1);
			 System.out.println(path2);
			 if(path2.indexOf("/")!=-1){
			 String p2=path2.substring(0,path2.indexOf("/"));
			 resultPath =resultPath+p2+extraslashString;
			 String path3 =path2.substring(path2.indexOf("/") +1);
			 if(path3.indexOf("/")!=-1){
				 String p3=path3.substring(0,path3.indexOf("/"));
				 resultPath =resultPath+p3+extraslashString; 
				 String path4 =path3.substring(path3.indexOf("/") +1);
				 System.out.println("path4: "+path4);
				 if(path4.indexOf("/")!=-1){
					 String p4=path4.substring(0,path4.indexOf("/"));
					 resultPath =resultPath+p4+extraslashString; 
				 }
				 if(path4.indexOf(".")!=-1){
					 resultPath =resultPath+path4;
				 }

				} }
			 }*/
		return nPath;
	}
	private void purgeIfPresentInRepository(String identifier){

		FedoraHandler handler;
		boolean result =false;

		try {
			handler = new FedoraHandler();
			try {
				result = handler.hasObject(identifier);

				System.out.println("purge:" +result);
				if (result==true){
					handler.purgeObject(identifier, "purge to update", false);	
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (BrilObjectRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

	}
	private void waitForSomeTime(){
		System.out.println("----..Starting----");

		Thread thisThread = Thread.currentThread();
		try{
			thisThread.sleep(10000);
		}catch(Throwable t){
			throw new OutOfMemoryError("An error has occured");
		}
		System.out.println("----....Waiting----");
		System.out.println("----..Ending----");
	}


}
