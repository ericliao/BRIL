package uk.ac.kcl.cerch.bril.relationship.common;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import uk.ac.kcl.cerch.bril.ccp4.TaskObject;
import uk.ac.kcl.cerch.bril.ccp4.TaskObjectVector;
import uk.ac.kcl.cerch.bril.characteriser.TaskObjectElement;
import uk.ac.kcl.cerch.bril.common.fedora.BrilObjectRepositoryException;
import uk.ac.kcl.cerch.bril.common.fedora.FedoraAdminstrationImpl;
import uk.ac.kcl.cerch.bril.common.fedora.FedoraNamespaceContext.FedoraNamespace;
//import uk.ac.kcl.cerch.bril.common.fedora.FedoraNamespaceContext.FedoraNamespace;
import uk.ac.kcl.cerch.bril.common.types.DataStreamType;
import uk.ac.kcl.cerch.bril.common.types.DatastreamObject;
import uk.ac.kcl.cerch.bril.common.types.DatastreamObjectContainer;
import uk.ac.kcl.cerch.bril.common.util.DateTime;
import uk.ac.kcl.cerch.bril.fileformat.CrystallographyObjectType;
import uk.ac.kcl.cerch.bril.relationship.ObjectRelationship;
import uk.ac.kcl.cerch.bril.relationship.Relationship;
import uk.ac.kcl.cerch.soapi.objectstore.ArchivalObject;
import uk.ac.kcl.cerch.soapi.objectstore.DublinCore;
//import uk.ac.kcl.cerch.soapi.objectstore.FileFormat;
import uk.ac.kcl.cerch.soapi.objectstore.FileCharacterisation;
import uk.ac.kcl.cerch.soapi.objectstore.ObjectArtifact;
import uk.ac.kcl.cerch.soapi.objectstore.ObjectStore;
import uk.ac.kcl.cerch.soapi.objectstore.ObjectStoreException;
import uk.ac.kcl.cerch.soapi.objectstore.OriginalContent;
import uk.ac.kcl.cerch.soapi.objectstore.database.ArchivalObjectDao;
import uk.ac.kcl.cerch.soapi.sip.SIP;
//import fedora.server.types.gen.ObjectFields;

public class GeneratorUtils {

	 private FedoraAdminstrationImpl fedoraAdmin;
	 private String archivalObjectDate;
	 private String archivalObjectFileName;
	 private String archivalObjectTitle;
	 private String archivalObjectFileCharacteriserMetadata;
	 private TaskObjectVector taskObjectVector; 
	
	 public GeneratorUtils(){
			try {
				fedoraAdmin = new FedoraAdminstrationImpl();
			} catch (BrilObjectRepositoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	 }

	 public String getOriginalContentFilePath(String objectId)
		throws ObjectStoreException {
		 System.out.println("Searching for Archival Object:------- " + objectId);
			ApplicationContext applicationContext = new FileSystemXmlApplicationContext(
					"config/soapi.xml");
			// object that connects to the database or file system that holds all
			// the objects
			ArchivalObjectDao archivalObjectDao = (ArchivalObjectDao) applicationContext
					.getBean("archivalObjectDao");
			/*
			 * Get archival object with the given archival object id
			 */
			ArchivalObject archivalObject = archivalObjectDao
					.getArchivalObjectById(objectId);
			ObjectStore objectStore = (ObjectStore) applicationContext.getBean("objectStore");
			Set<ObjectArtifact> objectArtifacts = archivalObject.getObjectArtifactsByType("OriginalContent");
			
			ObjectArtifact objectArtifact = null;
			OriginalContent originalContent =null;
			Iterator<ObjectArtifact> iter = objectArtifacts.iterator();
			while (iter.hasNext()) {
				objectArtifact = (ObjectArtifact) iter.next();
				String objectArtifactId = objectArtifact.getId();
				try {
					originalContent = (OriginalContent) objectStore
							.getObjectArtifact(objectArtifactId);
				} catch (ObjectStoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
			
		 return originalContent.getFilePath();
	 }
	 
	
	/**
	 * Search for the archival object to set object date, title, filename
	 * @param objectId
	 * @throws ObjectStoreException
	 */
	 public void searchForArchivalObjects(String objectId)
			throws ObjectStoreException {
		System.out.println("Searching for Archival Object:------- " + objectId);
		ApplicationContext applicationContext = new FileSystemXmlApplicationContext(
				"config/soapi.xml");
		// object that connects to the database or file system that holds all
		// the objects
		ArchivalObjectDao archivalObjectDao = (ArchivalObjectDao) applicationContext
				.getBean("archivalObjectDao");
		/*
		 * Get archival object with the given archival object id
		 */
		ArchivalObject archivalObject = archivalObjectDao
				.getArchivalObjectById(objectId);
		ObjectStore objectStore = (ObjectStore) applicationContext
				.getBean("objectStore");

		// System.out.println("Archival Object id: "+archivalObject.getId());
		// System.out.println("Archival Object path: " +
		// archivalObject.getPath().toString().trim());
		// System.out.println("Archival Object filename: "+archivalObject.getFilename());
		setObjectFileName(archivalObject.getFilename());
		SIP sip = archivalObject.getSip();
		// System.out.println("SIP id: " + sip.getId());

		ObjectArtifact objectArtifact = null;
		// ObjectArtifact objectArtifact1 = null;
		/*
		 * Get the Object Artifacts for this Archival Object
		 */
		Set<ObjectArtifact> objectArtifactsSet = archivalObject
				.getObjectArtifacts();

		Iterator<ObjectArtifact> iter = objectArtifactsSet.iterator();
		System.out.println("Iterating ObjectArtifactsSet to set DC title and date:-------- " + objectArtifactsSet.size());

		while (iter.hasNext()) {
			objectArtifact = (ObjectArtifact) iter.next();
			String type = objectArtifact.getType();
			String objectArtifactId = objectArtifact.getId();
			DublinCore dublinCore = null;
			if (type.equals("DublinCore")) {
				try {
					dublinCore = (DublinCore) objectStore
							.getObjectArtifact(objectArtifactId);
					// System.out.println("DublinCore id: "+dublinCore.getId());
					// System.out.println("DublinCore title: "+dublinCore.getTitle());
					// System.out.println("DublinCore description: "+dublinCore.getDescription());
					// System.out.println("DublinCore format: "+dublinCore.getFormat());
					// System.out.println("DublinCore date: "+dublinCore.getDate());
					setObjectDate(dublinCore.getDate());
					setObjectTitle(dublinCore.getTitle());
					// System.out.println("FileFormat description: "+fileCharacterisation.getDescription());
				} catch (ObjectStoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			FileCharacterisation fileCharacterisation = null;
			if(type.equals("FileCharacterisation")){
				try {
					fileCharacterisation = (FileCharacterisation) objectStore
							.getObjectArtifact(objectArtifactId);
					setFileCharacterisationMetadata(fileCharacterisation.getMetadata());
				} catch (ObjectStoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}
	 
	private void setFileCharacterisationMetadata(String metadata){
		this.archivalObjectFileCharacteriserMetadata= metadata;
	}
	
	public String getFileCharacterisationMetadata(){
		return this.archivalObjectFileCharacteriserMetadata;
	}
	
	private  void setObjectDate(String date) {
		this.archivalObjectDate = date;
	}

	public String getObjectDate() {
		return archivalObjectDate;
	}
	
	private  void setObjectFileName(String filename) {
		this.archivalObjectFileName = filename;
	}

	public String getObjectFileName() {
		return archivalObjectFileName;
	}

	private void setObjectTitle(String title) {
		this.archivalObjectTitle = title;
	}

	public String getObjectTitle() {
		return archivalObjectTitle;
	}

	/*
	 * 
	 */
	
	/**
	 * Get the DC title for the given object pid that holds the original path for this object
	 * 
	 * @param selectedObjectPid object identifier
	 * @return path String
	 */
	public String getOriginalPathOfObjectInRepository(String selectedObjectPid) {

		/* Get the filename and path of this pid from the DC */
		String path = null;
		try {
			path = fedoraAdmin.getDCTitle(selectedObjectPid);
		} catch (BrilObjectRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return path;
	}
	
	public String getFormatOfTheObjectInRepository(String selectedObjectPid){
		String format = null;
		try {
			format = fedoraAdmin.getDCFormat(selectedObjectPid);
		} catch (BrilObjectRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return format;
	}
	
	public String searchForObjectDate(String experimentId, String pid) {
		String objectIdTimeStamp = null;
		
		try {
			fedoraAdmin = new FedoraAdminstrationImpl();
			if (experimentId.lastIndexOf('/') == -1) {
				experimentId = "info:fedora/" + experimentId;
			}

			String resultXML = fedoraAdmin.getObjectDate(experimentId, pid);
			System.out.println(resultXML);
			try {
				Document sourceDoc = stringToDom(resultXML.trim());

				NodeList list = sourceDoc.getElementsByTagName("*");
				for (int i = 0; i < list.getLength(); i++) {
					Element element = (Element) list.item(i);
					if (element.getTagName().equals("result")) {
						NodeList childList = element.getChildNodes();
						String date = null;
						for (int j = 0; j < childList.getLength(); j++) {						
							int count = 0;
							if (childList.item(j).getNodeName().equals("date")) {
								date = childList.item(j).getTextContent();
								count++;
							}
						}
						objectIdTimeStamp = date;
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
		} catch (BrilObjectRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return objectIdTimeStamp;
	}
			
	 /**
	 * Retrieves all the object identifier and its creation date with the given object type and has relationship isPartOf to the experimentId.
	 * This method uses method that uses ITQL query to get all the objects with this object type(in DC format) 
	 * and isPartOf the experimentId.
	 * @see getRepositoryDescSortedResult
	 * 
	 * @param experimentId
	 * @param objectType
	 * @return List of object ids and object creation date
	 */
	public Map<String, Vector<String>> searchForObjectIdsOfType(String experimentId, CrystallographyObjectType objectType) {
		 Map<String, Vector<String>> objectIdTimeStamp = new HashMap<String, Vector<String>>();
		 try {
				fedoraAdmin = new FedoraAdminstrationImpl();
				if (experimentId.lastIndexOf('/') == -1) {
					experimentId = "info:fedora/" + experimentId;
				}
				
				String resultXML = fedoraAdmin.getExperimentRelatedObjectIdsDate(experimentId, objectType.getType());
				System.out.println(resultXML);
				try {
					Document sourceDoc= stringToDom(resultXML.trim());
					
					NodeList list = sourceDoc.getElementsByTagName("*");
					for (int i = 0; i < list.getLength(); i++) {
						Element element = (Element) list.item(i);
						if(element.getTagName().equals("result")){
							NodeList childList = element.getChildNodes();
							String pid=null;
							Vector<String> date=new Vector<String>();							
							for(int j=0;j<childList.getLength();j++){
															 
							   if(childList.item(j).getNodeName().equals("object")){
								   Element obj = (Element) childList.item(j);
								   pid=obj.getAttribute("uri").trim();
							   }
							   int count =0;
							   if(childList.item(j).getNodeName().equals("date")){
								   date.add(childList.item(j).getTextContent());
								   count++;
							   }							
						
							}							
							objectIdTimeStamp.put(pid,date);
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
								
		 } catch (BrilObjectRepositoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			return objectIdTimeStamp;
		}
	
	
	 /**
	 * Retrieves all the object identifier and (DC) title with the given object type and has relationship isPartOf to the experimentId.
	 * This method uses method that uses ITQL query to get all the objects with this object type(in DC format) 
	 * and isPartOf the experimentId.
	 * 
	 * @see getInputFileObjectIds() method
	 * 
	 * @param experimentId
	 * @param objectType
	 * @return Map object with the list of object identifiers and its corresponding title in DC.
	 */
	
	public Map<String, Vector<String>> searchForObjectIdsTitle(String experimentId, CrystallographyObjectType objectType) {
		 Map<String, Vector<String>> objectIdTimeStamp = new HashMap<String, Vector<String>>();
		 try {
				fedoraAdmin = new FedoraAdminstrationImpl();
				if (experimentId.lastIndexOf('/') == -1) {
					experimentId = "info:fedora/" + experimentId;
				}
				/*
				 * Currently the method getExperimentRelatedObjectIds only returns object pids 
				 * must use 
				 * fedoraAdmin.getExperimentRelatedObjectIdsDate(experimentId, objectType.getType()) 
				 * that returns ids and date; ones its updated
				 */
				String resultXML = fedoraAdmin.getExperimentRelatedObjectIdsTitle(experimentId, objectType.getType());
				//System.out.println(resultXML);
				try {
					Document sourceDoc= stringToDom(resultXML.trim());
					
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
							//   System.out.println(childList.item(j).getNodeName());
							//   System.out.println("pid: "+obj.getAttribute("uri").trim());
							   pid=obj.getAttribute("uri").trim();
							   }
							   int count =0;
							   if(childList.item(j).getNodeName().equals("title")){
							//	   System.out.println(childList.item(j).getTextContent());
								   date.add(childList.item(j).getTextContent());
								 //  date1[count]=childList.item(j).getTextContent();
								   count++;
							   }							
						
							}
							
							objectIdTimeStamp.put(pid,date );
							//System.out.println(pid+"   "+date);
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
			
				
		 } catch (BrilObjectRepositoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			return objectIdTimeStamp;
		}
			
		
	 /**
	 * @param experimentId The experiment Id 
	 * @param queryString The string the object must contain in the title, format or description in DC
	 * @return Map object containing the objectids that belongs to the experiment and has the queryString
	 */
	public Vector<String> searchForObjectIds(String experimentId,
			String title, String queryString) {
		Vector<String> objectIds = new Vector<String>();

		try {
			fedoraAdmin = new FedoraAdminstrationImpl();
			/*
			 * Returns the object id that is of this experiment and has
			 */
			String resultXML = fedoraAdmin.getObjectIdsUsingDC(
					experimentId, title, queryString);
			try {
				Document sourceDoc = stringToDom(resultXML.trim());
				NodeList list = sourceDoc.getElementsByTagName("*");
				for (int i = 0; i < list.getLength(); i++) {
					Element element = (Element) list.item(i);
					// System.out.println(element);
					if (element.getTagName().equals("result")) {
						NodeList childList = element.getChildNodes();
						// System.out.println(childList.getLength());
						String pid = null;
						for (int j = 0; j < childList.getLength(); j++) {
							if (childList.item(j).getNodeName().equals("object")) {
								Element obj = (Element) childList.item(j);
								// System.out.println(childList.item(j).getNodeName());
								// System.out.println("pid: "+obj.getAttribute("uri").trim());
								pid = obj.getAttribute("uri").trim();
							}
						}
						if (pid != null) {
							objectIds.add(pid);
						}
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

		} catch (BrilObjectRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return objectIds;
	}
	
	 /**
	 * Checks if the given object identifier has the CCP4I DEF database log file.
	 * 
	 * @param objectIdentifier
	 * @return
	 */
	
	public boolean isDatabaseDEF(String objectIdentifier) {
		boolean result = false;
		String filename = null;
		try {
			String title = fedoraAdmin.getDCTitle(objectIdentifier);
			filename = title.substring(title.lastIndexOf("/") + 1);
			if (filename.equals("database.def") || filename.equals("tmp_database.def")) {
				result = true;
			}
		} catch (BrilObjectRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	 /**
	  * Get the jobId whose output filename matches with 
	  * the current object's filename.
	  *  
	 * @param DEFObjectId the identifier of the CCP4 DEF file in the repository that is being processed,
	 *  using this the metadata is retrieved that has the tasks with jobids, taskname, inputs and output.
	 *  The TaskObjectVector would hold this metadata.
	 * 
	 * @param taskObjectVector Metadata object with all the tasks in the database.def
	 * @param otherObjectFilename this value can be null, If not null this is used to check 
	 *        for the matching output filename in the DEF file to set the matching jobid.
	 * @return matching jobId whose output filename matches with the current object's filename.
	 */
	public String getJobIdInDatabaseDEF(String DEFObjectId, TaskObjectVector taskObjectVector, String otherObjectFilename) {
		byte[] databaseDEFFileMetadata = getDatastreamType(DEFObjectId,
				DataStreamType.ObjectMetadata);
		//TaskObject selectedTaskObject = new TaskObject();
		//set the TaskObjectVector with the XML data returned as byte[]
		setTaskObjectVector(databaseDEFFileMetadata, taskObjectVector);

		String originalObjectPath = getObjectTitle();
		String matchedJobID = null;
		
		String originalObjectFilename=null;
		if(originalObjectPath.contains("/")){
		originalObjectFilename = originalObjectPath
				.substring(originalObjectPath.lastIndexOf("/") + 1);
		}
		
		if(originalObjectPath.contains("\\")){
			originalObjectFilename = originalObjectPath
			.substring(originalObjectPath.lastIndexOf("\\") + 1);
		}

		Map<Integer, String> jobidMap = taskObjectVector.getJobIDVectorObject();
		Map<String, Vector<String>> allInputs = taskObjectVector
				.getInputVectorObject();
		Map<String, Vector<String>> allOutputs = taskObjectVector
				.getOutputVectorObject();
	//	Map<String, String> allTasks = taskObjectVector.getTaskVectorObject();
	//	Map<String, Long> allDates = taskObjectVector.getDateVectorObject();

		for (Map.Entry<Integer, String> entry : jobidMap.entrySet()) {
			String jobID = entry.getValue();
			Vector<String> inputs = allInputs.get(jobID);
			Vector<String> outputs = allOutputs.get(jobID);

			/* Check inputs and outputs for the given jobid */
			
			// TODO: need to check if outputs is null.
			if (outputs != null) {
				for (int i = 0; i < outputs.size(); i++) {
	
					String outputFileName = outputs.elementAt(i);
	
					//if (outputFileName.contains(".mtz")) {
						// TODO check if output contains directory structure
						// such as /somefolder/filename.mtz and how to handle
						// this
						// Assuming only filename is present in output variable
					if(otherObjectFilename==null){
						if (outputFileName.equals(originalObjectFilename)) {
							// This is the job id that was ran to produce the
							// current object
							matchedJobID = jobID;
						//	selectedTaskObject.setJobID(matchedJobID);
	
						}
					}else if (otherObjectFilename!=null){
						if (outputFileName.equals(otherObjectFilename)) {
							matchedJobID = jobID;
						}
					}
					//}
				}
			}
		}
		return matchedJobID;

	}
	
	/**
	 * Creates a TaskObject object using the object identifer of a DEF file object and 
	 * compares if the jobId of the TaskObject is same as the DEF object
	 * (Coordinate file or mtz Reflection file)
	 *
	 * @see CoordinateFileRelationshipGeneratorImpl and MTZReflectionFileRelationshipGeneratorImpl processCCP4IDefFile method
	 * @param DEFObjectId Object identifier of CCP4 DEF file
	 * @param taskObject TaskObject that would holds the metadata of a task with inputs and outputs filenames 
	 * @return boolean result
	 */
	
	public String getJobIdFromTaskObject(String DEFObjectId, TaskObject taskObject) {
				
		byte[] taskDEFFileMetadata = getDatastreamType(DEFObjectId, DataStreamType.ObjectMetadata);
				
		//put the metadata result to the taskObject
		setTaskObject(taskDEFFileMetadata, taskObject);		
		String jobID = taskObject.getJobID();	
		
		return jobID;

	}
	
	/**
	 * Creates a TaskObject object using the object identifer of a DEF file object and 
	 * compares if the output filename in the TaskObject is same as the current Bril object
	 * (Coordinate file or mtz Reflection file)
	 *
	 * @see CoordinateFileRelationshipGeneratorImpl and MTZReflectionFileRelationshipGeneratorImpl processCCP4IDefFile method
	 * @param DEFObjectId Object identifier of CCP4 DEF file
	 * @param taskObject TaskObject that would holds the metadata of a task with inputs and outputs filenames 
	 * @return boolean result
	 */
	
	public boolean hasOuputInTaskObject(String DEFObjectId, TaskObject taskObject) {
		boolean result = false;
		String originalObjectPath = getObjectTitle();
		String originalObjectFilename = originalObjectPath.substring(originalObjectPath.lastIndexOf("/") + 1);
		
		byte[] taskDEFFileMetadata = getDatastreamType(DEFObjectId,DataStreamType.ObjectMetadata);

		//put the metadata result to the taskObject
		setTaskObject(taskDEFFileMetadata, taskObject);
		
		Vector<String> ouputFilesNames= taskObject.getOutputFileNames();
		for (int i = 0; i < ouputFilesNames.size(); i++) {

			String outputFileName = ouputFilesNames.elementAt(i);

			// TODO check if output contains directory structure
			// such as /somefolder/filename.mtz and how to handle
			// this
			// Assuming only filename is present in output variable
			if (outputFileName.equals(originalObjectFilename)) {
				// This is the job id that was ran to produce the
				// current object
				result = true;
			}
		}
		
		return result;

	}
	
	/**
	 * Creates a TaskObject object using the object identifer of a DEF file object and 
	 * compares if the log file in the TaskObject is same as the current Bril object
	 * (Log file)
	 *
	 * @see CoordinateFileRelationshipGeneratorImpl and MTZReflectionFileRelationshipGeneratorImpl processCCP4IDefFile method
	 * @param DEFObjectId Object identifier of CCP4 DEF file
	 * @return boolean result
	 */
	
	public boolean hasLogInTaskObject(String DEFObjectId) {
		boolean result = false;
		String originalObjectPath = getObjectTitle();
		String originalObjectFilename = originalObjectPath.substring(originalObjectPath.lastIndexOf("/") + 1);
		System.out.println("originalObjectFilename: [" + originalObjectFilename + "]");
		
		byte[] taskDEFFileMetadata = getDatastreamType(DEFObjectId,DataStreamType.ObjectMetadata);			
		String logFile = getValueFromTaskXML(taskDEFFileMetadata, TaskObjectElement.LOG_FILENAME);
		
		System.out.println("logFile: [" + logFile + "]");
		
		if (logFile.equals(originalObjectFilename)) {
			result = true;
		}	
		
		return result;

	}
	
	 /**
	  * Puts the object XML metadata in byte[] in the TaskObjectVector object.
	  * This method is called when CCP4I database DEF file is being processed for object relationship generation 
	  * 
	  * @see {@code getJobIdInDatabaseDEF}
	  * @param data XML Metadata in byte array retrieved from CCP4I object (database.def) in the repository
	  * @param taskObjectVector TaskObjectVector that would hold the XML metadata byte[] data
	  */
	
	private void setTaskObjectVector(byte[] data,  TaskObjectVector taskObjectVector){
		InputStream inputstream = new ByteArrayInputStream(data);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder;

		try {
			builder = factory.newDocumentBuilder();

			Document sourceDoc = builder.parse(inputstream);
			// Get the list of all elements in the document
			NodeList list = sourceDoc.getElementsByTagName("*");
			for (int i = 0; i < list.getLength(); i++) {
				Element element = (Element) list.item(i);
				String localNodeName = element.getLocalName();
				//System.out.println(localNodeName);
				//System.out.println(element.getTextContent());
				if (localNodeName.equals("task")) {
					//Node tasknode = list.item(i);
					NodeList taskList = element.getChildNodes();
					String jobID =element.getAttribute(TaskObjectElement.JOB_ID.localName());
					//System.out.println("Job ID: "+ jobID);
					taskObjectVector.setJobIDVectorObject(i, jobID);
					for (int t = 0; t < taskList.getLength(); t++) {
						Element taskElement = (Element) taskList.item(t);
						String taskElementLocalName = taskElement.getLocalName();
						//System.out.println(taskElement.getLocalName());
					//	System.out.println("task element laoc name: "+ taskElementLocalName);
						//System.out.println(taskElementLocalName);
						if (taskElementLocalName.equals(TaskObjectElement.TASK_NAME.localName())) {						
							taskObjectVector.setTaskVectorObject(jobID,taskElement.getTextContent());
							//System.out.println(taskElement.getTextContent());
						}
						if (taskElementLocalName.equals(TaskObjectElement.DATE.localName())) {						
							taskObjectVector.setDateVectorObject(jobID,Long.parseLong(taskElement.getTextContent()));
						//	System.out.println(taskElement.getTextContent());
						}
						if (taskElementLocalName.equals(TaskObjectElement.TITLE.localName())) {						
							taskObjectVector.setTitleVectorObject(jobID,taskElement.getTextContent());
							//System.out.println(taskElement.getTextContent());
						}
						if (taskElementLocalName.equals(TaskObjectElement.STATUS.localName())) {						
							taskObjectVector.setStatusVectorObject(jobID,taskElement.getTextContent());
							//System.out.println(taskElement.getTextContent());
						}
						if (taskElementLocalName.equals(TaskObjectElement.LOG_FILENAME.localName())) {						
							taskObjectVector.setLogfileVectorObject(jobID,taskElement.getTextContent());
						//	System.out.println(taskElement.getTextContent());
						}
						if (taskElementLocalName.equals(TaskObjectElement.INPUT_FILENAME.localName()+"s")) {	
							//System.out.println("inputs:~****" +taskElement.getTextContent());
							Vector<String> inputFileNames= new Vector<String>();
							NodeList taskInputsList = taskElement.getChildNodes();
							for (int in = 0; in < taskInputsList.getLength(); in++) {
								Element inputElement = (Element) taskInputsList.item(in);
								String inputNodeName = inputElement.getLocalName();
								if (inputNodeName.equals(TaskObjectElement.INPUT_FILENAME.localName())) {
									//System.out.println("input:~****" +inputElement.getTextContent());
									inputFileNames.add(inputElement.getTextContent());
								}
							
							
							taskObjectVector.setInputVectorObject(jobID, inputFileNames);
						}
						}
						if (taskElementLocalName.equals(TaskObjectElement.OUTPUT_FILENAME.localName()+"s")) {						
						//	taskObjectVector.setLogfileVectorObject(jobID,taskElement.getTextContent());
							//System.out.println(taskElement.getTextContent());
							Vector<String> outputFileNames= new Vector<String>();
							NodeList taskInputsList = taskElement.getChildNodes();
							for (int in = 0; in < taskInputsList.getLength(); in++) {
								Element outputElement = (Element) taskInputsList.item(in);
								String outputNodeName = outputElement.getLocalName();
								if (outputNodeName.equals(TaskObjectElement.OUTPUT_FILENAME.localName())) {
								//	System.out.println("output:~****" +outputElement.getTextContent());
									outputFileNames.add(outputElement.getTextContent());
								}
							
							
							taskObjectVector.setOutputVectorObject(jobID, outputFileNames);
						}
						}
						
					}
					
				}
				// System.out.println(element.getLocalName()+": "+element.getTextContent());
			}

			// System.out.println("title direct---"
			// +sourceDoc.getElementsByTagName("title").item(0));
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	 
/*	private TaskObject getTaskDEFFile(String DEFObjectId) {
			byte[] taskDEFFileMetadata = getDatastreamType(
					DEFObjectId, DataStreamType.ObjectMetadata);
		   TaskObject taskObject = new TaskObject();
			setTaskObject(taskDEFFileMetadata, taskObject);

			String originalObjectPath = getObjectTitle();

			Vector<String> outputs = taskObject.getOutputFileNames();
			String originalObjectFilename = originalObjectPath
					.substring(originalObjectPath.lastIndexOf("/") + 1);
			 Check inputs and outputs for the given jobid 
			for (int i = 0; i < outputs.size(); i++) {

				String outputFileName = outputs.elementAt(i);

				if (outputFileName.contains(".mtz")) {
					// TODO check if output contains directory structure
					// such as /somefolder/filename.mtz and how to handle
					// this
					// Assuming only filename is present in output variable
					if (outputFileName.equals(originalObjectFilename)) {
						flag = 1;
					}
				}
			}
			return taskObject;

		}
		*/
	
	
	 /**
	  * Puts the object XML metadata in byte[] in the TaskObject object. 
	  * This method is called when CCP4I object that holds the task XML metadata is used to generate object relationship
	  * 
	 * @param data XML Metadata in byte array retrieved from CCP4I object (taskname.def) in the repository
	 * @param taskObject TaskObject object that would hold the XML metadata in byte array
	 */
	private void setTaskObject(byte[] data, TaskObject taskObject) {
		InputStream inputstream = new ByteArrayInputStream(data);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder;

		try {
			builder = factory.newDocumentBuilder();

			Document sourceDoc = builder.parse(inputstream);
			// Get the list of all elements in the document
			NodeList list = sourceDoc.getElementsByTagName("*");
			
			
			for (int i = 0; i < list.getLength(); i++) {
				Element element = (Element) list.item(i);
				String localNodeName = element.getLocalName();
				//System.out.println("localNodeName: " + localNodeName);
				//System.out.println("content: " + element.getTextContent());
				if (localNodeName.equals("task")) {
					String jobID = element.getAttribute(TaskObjectElement.JOB_ID.localName());
					System.out.println(jobID);
					taskObject.setJobID(jobID);
				}
				if (localNodeName.equals(TaskObjectElement.TASK_NAME.localName())) {
					taskObject.setTaskName(element.getTextContent());
				}
				if (localNodeName.equals(TaskObjectElement.DATE.localName())) {
				
					taskObject.setRunDateTime(element.getTextContent());
				}
				if (localNodeName.equals(TaskObjectElement.INPUT_FILENAME.localName()+"s")) {						
					Vector<String> inputFileNames= new Vector<String>();
					NodeList taskInputsList = element.getChildNodes();
					for (int in = 0; in < taskInputsList.getLength(); in++) {
						Element inputElement = (Element) taskInputsList.item(in);
						String inputNodeName = inputElement.getLocalName();
						if (inputNodeName.equals(TaskObjectElement.INPUT_FILENAME.localName())) {
							//System.out.println("input:~****" +inputElement.getTextContent());
							//inputFileNames.add(inputElement.getTextContent());
							taskObject.setInputFileName(inputElement.getTextContent());
						}
							
					//taskObject.setInputFileName(inputFileNames);
				}
				}
				if (localNodeName.equals(TaskObjectElement.OUTPUT_FILENAME.localName()+"s")) {						
					//	taskObjectVector.setLogfileVectorObject(jobID,taskElement.getTextContent());						
						Vector<String> outputFileNames= new Vector<String>();
						NodeList taskOutputsList = element.getChildNodes();
						for (int in = 0; in < taskOutputsList.getLength(); in++) {
							Element outputElement = (Element) taskOutputsList.item(in);
							String outputNodeName = outputElement.getLocalName();
							if (outputNodeName.equals(TaskObjectElement.OUTPUT_FILENAME.localName())) {
							//	System.out.println("output:~****" +outputElement.getTextContent());
							//	outputFileNames.add(outputElement.getTextContent());
								taskObject.setOutputFileName(outputElement.getTextContent());
							}
					
					//	taskObjectVector.setOutputVectorObject(jobID, outputFileNames);
					}
					}

				// System.out.println(element.getLocalName()+": "+element.getTextContent());
			}

			// System.out.println("title direct---"
			// +sourceDoc.getElementsByTagName("title").item(0));
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	 
	
	 
	 /**
	  * Selects either the input or output filenames from the XML metadata in byte[] 
	  * using the given taskObjectElement param.
	  * Used in method processCOMFile in class MTZReflectionFileRelationshipImpl.
	  * 
	  * @param data holds XML metadata of a TaskObject
	  * @param taskObjectElement TaskObjectElement enum element can be either 
	  *        TaskObjectElement.OUTPUT_FILENAME or TaskObjectElement.INPUT_FILENAME
	  * @return List of filenames (input or output) from the byte[] XML metadata
	  */
	public Vector<String> getFilenamesFromTaskXML(byte[] data,
				TaskObjectElement taskObjectElement) {
			InputStream inputstream = new ByteArrayInputStream(data);
			//FileUtil.writeByteArrayToFile("taskobjectReturned.xml", data);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder;
			Vector<String> fileNames = new Vector<String>();;

			try {
				builder = factory.newDocumentBuilder();

				Document sourceDoc = builder.parse(inputstream);
				// Get the list of all elements in the document
				NodeList list = sourceDoc.getElementsByTagName("*");
				for (int i = 0; i < list.getLength(); i++) {
					Element element = (Element) list.item(i);
					String localNodeName = element.getLocalName();
					//System.out.println("taskobject element: "+ taskObjectElement.localName());
					if (localNodeName.equals(taskObjectElement.localName())) {
						
					//	System.out.println("filenames ---" + element.getTextContent());
						fileNames.add(element.getTextContent());
					}

					// System.out.println(element.getLocalName()+": "+element.getTextContent());
				}

				// System.out.println("title direct---"
				// +sourceDoc.getElementsByTagName("title").item(0));
			} catch (SAXException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ParserConfigurationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return fileNames;
		}
 
	 
	 /**
	  * Get the value for the given TaskObjectElement in the byte array XML. 
	  * Used in {@code MTZReflectionFileRelationshipGeneratorImpl} and {@code CoordinateFileRelationshipGeneratorImpl}
	  *
	  * @param data The XML data in byte arry
	  * @param taskObjectElement element in the XML whose value is being searched for
	  * @return the value of the given element from the XML data
	  */
	public String getValueFromTaskXML(byte[] data,TaskObjectElement taskObjectElement) {
			InputStream inputstream = new ByteArrayInputStream(data);
			//FileUtil.writeByteArrayToFile("taskobjectReturned.xml", data);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder;
			String elementValue = "";

			try {
				builder = factory.newDocumentBuilder();

				Document sourceDoc = builder.parse(inputstream);
				// Get the list of all elements in the document
				NodeList list = sourceDoc.getElementsByTagName("*");
				for (int i = 0; i < list.getLength(); i++) {
					Element element = (Element) list.item(i);
					
					String localNodeName = element.getLocalName();
					if (localNodeName.equals("task")) {
						if(element.hasAttributes()==true){	

							if(TaskObjectElement.JOB_ID.localName().equals(taskObjectElement.localName())){
							String jobID =element.getAttributeNode(taskObjectElement.localName()).getLocalName();
							elementValue=element.getAttribute(jobID);
							//System.out.println("jobid:"+elementValue);
							}
						}
					
					}
					//System.out.println("local name: "+ localNodeName);
				
					if (localNodeName.equals(taskObjectElement.localName())) {
						
						//System.out.println("taskname ---" + element.getTextContent());
						elementValue= element.getTextContent();
					}

					// System.out.println(element.getLocalName()+": "+element.getTextContent());
				}

				// System.out.println("title direct---"
				// +sourceDoc.getElementsByTagName("title").item(0));
			} catch (SAXException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ParserConfigurationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return elementValue;
		}
	// TODO move to new class
	
	/**
	 * Gets the datastream of a given DataStreamType for an object from the repository.
	 *  
	 * Used in {@code MTZReflectionFileRelationshipGeneratorImpl} and {@code CoordinateFileRelationshipGeneratorImpl}
	 *
	 * @param objectId object identifier to retrieve the datastream of 
	 * @param streamtype 
	 * @return
	 */
	public  byte[] getDatastreamType(String objectId, DataStreamType streamtype) {
		DatastreamObjectContainer dsoc = null;
		try {

			dsoc = fedoraAdmin.retrieveDataFromObject(objectId, streamtype);

		} catch (BrilObjectRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DatastreamObject dso = dsoc.getDatastreamObject(streamtype);
		// xml data in byte array
		byte[] data = dso.getDataBytes();
		return data;
	}

	// TODO move to new class
	 public Vector<String> getFilenames(byte[] data,
			TaskObjectElement taskObjectElement) {
		InputStream inputstream = new ByteArrayInputStream(data);
		// FileUtil.writeByteArrayToFile("taskobjectReturned.xml", data);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder;
		Vector<String> fileNames = new Vector<String>();
		;

		try {
			builder = factory.newDocumentBuilder();

			Document sourceDoc = builder.parse(inputstream);
			// Get the list of all elements in the document
			NodeList list = sourceDoc.getElementsByTagName("*");
			for (int i = 0; i < list.getLength(); i++) {
				Element element = (Element) list.item(i);
				String localNodeName = element.getLocalName();
				// System.out.println("taskobject element: "+
				// taskObjectElement.localName());
				if (localNodeName.equals(taskObjectElement.localName())) {

					System.out.println("filenames ---"
							+ element.getTextContent());
					fileNames.add(element.getTextContent());
				}

				// System.out.println(element.getLocalName()+": "+element.getTextContent());
			}

			// System.out.println("title direct---"
			// +sourceDoc.getElementsByTagName("title").item(0));
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return fileNames;
	}
	 public TaskObjectVector getTaskObjectVectorFromXML(byte[] data){
		 TaskObjectVector taskObjectVector = new TaskObjectVector();
		 InputStream inputstream = new ByteArrayInputStream(data);
			// FileUtil.writeByteArrayToFile("taskobjectReturned.xml", data);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder;
			
		try {
				builder = factory.newDocumentBuilder();
			
			Document sourceDoc;
			
			sourceDoc = builder.parse(inputstream);
			
			// Get the list of all elements in the document
			NodeList list = sourceDoc.getElementsByTagName("*");
			for (int i = 0; i < list.getLength(); i++) {
				Element element = (Element) list.item(i);
				String localNodeName = element.getLocalName();
				 System.out.println("taskobject element: "+
						 localNodeName);
				if (localNodeName.equals(TaskObjectElement.TASK_NAME.localName())) {

					System.out.println("text ---"
							+ element.getTextContent());
					//fileNames.add(element.getTextContent());
				}

				// System.out.println(element.getLocalName()+": "+element.getTextContent());
			}
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return taskObjectVector;
		 
	 }
	 
		/**
		 * Based on the filename and the experiment id, this methods run a query to
		 * the fedora repository to get the object ids and title of all the objects
		 * in the repository. The search is also based on the object type of a
		 * particular filename based on its suffix.
		 * 
		 * @param inputNameList
		 *            List of input filenames
		 * @param experimentId
		 *            The experiment id
		 * @return
		 */
	public Vector<String> getInputFileObjectIds(Vector<String> inputNameList,
				String experimentId) {
			Vector<String> inputObjectIds = new Vector<String>();
			for (int o = 0; o < inputNameList.size(); o++) {
				String inputFilename = inputNameList.get(o);
				String suffix = inputFilename.substring(inputFilename
						.lastIndexOf(".") + 1);
				// System.out.println(suffix);

				if (suffix.equals("pdb")) {
					Map<String, Vector<String>> pdbObjectIdTitle = searchForObjectIdsTitle(experimentId,
									CrystallographyObjectType.CoordinateFile);

					// get the object's filename in title = filename
					for (Map.Entry<String, Vector<String>> ent : pdbObjectIdTitle.entrySet()) {

						String path = ent.getValue().get(0);
						String pdbFileName = path.substring(path.lastIndexOf('/') + 1);						
						if (inputFilename.equals(pdbFileName)) {
							inputObjectIds.add(ent.getKey());
						}
					}
				} else if (suffix.equals("seq")) {
					Map<String, Vector<String>> seqObjectIdTitle = searchForObjectIdsTitle(experimentId,
									CrystallographyObjectType.SEQFile);

					// get the object's filename in title = filename
					for (Map.Entry<String, Vector<String>> ent : seqObjectIdTitle.entrySet()) {

						String path = ent.getValue().get(0);
						String seqFileName = path.substring(path.lastIndexOf('/') + 1);
						if (inputFilename.equals(seqFileName)) {
							inputObjectIds.add(ent.getKey());
						}
					}	
				} else if (suffix.equals("aln")) {
					Map<String, Vector<String>> alnObjectIdTitle = searchForObjectIdsTitle(experimentId,
									CrystallographyObjectType.AlignmentFile);

					// get the object's filename in title = filename
					for (Map.Entry<String, Vector<String>> ent : alnObjectIdTitle.entrySet()) {
						
						String path = ent.getValue().get(0);
						String alnFileName = path.substring(path.lastIndexOf('/') + 1);
						if (inputFilename.equals(alnFileName)) {
							inputObjectIds.add(ent.getKey());
						}
					}
				} else if (suffix.equals("mtz")) {
					Map<String, Vector<String>> mtzObjectIdTitle = searchForObjectIdsTitle(experimentId,
									CrystallographyObjectType.MTZReflectionFile);

					System.out.println(mtzObjectIdTitle);
					// get the object's filename in title = filename
					for (Map.Entry<String, Vector<String>> ent : mtzObjectIdTitle.entrySet()) {

						String path = ent.getValue().get(0);
						String mtzFileName = null;
						if (path.lastIndexOf('\\') != -1){
							mtzFileName = path.substring(path.lastIndexOf('\\') + 1);
						}
						
						if (path.lastIndexOf('/') != -1) {
							mtzFileName = path.substring(path.lastIndexOf('/') + 1);
						}
						
						System.out.println(mtzFileName);
						if (inputFilename.equals(mtzFileName)) {
							inputObjectIds.add(ent.getKey());
						}
					}
				}
			}
			return inputObjectIds;
		}

	/**
	 * Time difference between the current object and the list of objects
	 * returned from the repository
	 * 
	 * @param objectIdTimeStamp
	 */

	// TODO move to new class
	 public Map<String, Long> objectCreationTimeDifference(
			Map<String, String[]> objectIdTimeStamp ) {
		String thisObjectDate = null;
		String thisObjectTitle = null;
		// try {
		// search in the object store for this objects DC data
		// searchForArchivalObjects(currentObjectId);
		// date from the metadata message --format is: 16/06/2008 13:54:36
		thisObjectDate = getObjectDate();
		//System.out.println("current object date: " + thisObjectDate);

		// title is the original path from the met metadata message:
		// C:'/Experiment'/baa5d5'/5d5.mtz
		thisObjectTitle = getObjectTitle();
		//System.out.println("current object title: " + thisObjectTitle);

		// } catch (ObjectStoreException e1) {
		// TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		System.out.println("Calculating miminum time difference between the current object whose date is '" + thisObjectDate+"' ");
		System.out.println("with given dates of the objects in the Repository:-----------");
		Map<String, Long> pidTimeDiff = new HashMap<String, Long>();

		String pid = null;
		for (Map.Entry<String, String[]> entry : objectIdTimeStamp.entrySet()) {
			pid = entry.getKey();
			String[] dates = entry.getValue();
			//System.out.println(pid);
			String date = null;
			String date1 = null;
			for (int u = 0; u < dates.length; u++) {
				// format 2008-06-16T13:48:36.000
				date = dates[u].substring(0, dates[u].lastIndexOf("."));
				//System.out.println(date);

				// check if the difference between the date is minimum
				if (date != null) {
					// more then one date for this pid is present
				}
				// assuming only one date for a pid
				date1 = DateTime.getDateTime(date, "yyyy-MM-dd'T'HH:mm:ss");
				long timeDiff = DateTime
						.getdiffInSeconds(thisObjectDate, date1);
				
				pidTimeDiff.put(pid, timeDiff);

			}

		}
		System.out.println("Time difference of all the objects in the Map are sorted assending order:-----------");
		Map<String, Long> sortedMap = sortByValue(pidTimeDiff);

		return sortedMap;
	}
	 
	public Map<String, Long> getRepositoryDescSortedResult(String experimentId, CrystallographyObjectType type) {
		Map<String, Long> repositoryResult = new HashMap<String, Long>();
		Map<String, Vector<String>> pdbObjectIdDate = searchForObjectIdsOfType(experimentId, type);

		for (Map.Entry<String, Vector<String>> ent : pdbObjectIdDate.entrySet()) {
			Vector<String> val = ent.getValue();
			// format 2008-06-17T09:29:03.000
			String date = val.get(0);
			repositoryResult.put(ent.getKey(), getLongDate(date));
			System.out.println(getStringDate(date));
		}

		Map<String, Long> sortedPDBObjectMap = sortMapByValueDescending(repositoryResult);
		return sortedPDBObjectMap;
	}
		
	private long getLongDate(String dateString) {
			// return format 2008-06-17T09:29:03
			if (dateString.contains(".")) {
				dateString = dateString.substring(0, dateString.lastIndexOf("."));
			}
		//	System.out.println("dc date: " + dateString);

			// return format 17/06/2008 09:22:46
			String stDate = DateTime.getDateTime(dateString,
			"yyyy-MM-dd'T'HH:mm:ss");
			long longDate = DateTime.getLongDateTime(stDate, "dd/MM/yyyy HH:mm:ss");
		
			return longDate;
		}
	
	private String getStringDate(String dateString) {
		// return format 2008-06-17T09:29:03
		if (dateString.contains(".")) {
			dateString = dateString.substring(0, dateString.lastIndexOf("."));
		}
		System.out.println("dc date: " + dateString);

		// return format 17/06/2008 09:22:46
		String stDate = DateTime.getDateTime(dateString, "yyyy-MM-dd'T'HH:mm:ss");
		return stDate;
	}

	/**
	 * Get the object Id whose time difference in the minimum value compared to
	 * the rest objects in the Map.
	 * 
	 * @param diffMap
	 *            the Map object containing the object ids and time difference
	 *            value in seconds between the current object and the
	 *            corresponding object
	 * 
	 * @return the object Id with the least time difference to the current
	 *         object (whose relationship is being generated)
	 */
	 public String getMinTimeDifferanceObject(Map<String, Long> diffMap) {
		//Map<String, Long> map = sortByValue(diffMap);
		String derivedFrom_pid = null;
		Long mintimediff = null;
		int count = 0;
		boolean negativeValue = false;

		for (Map.Entry<String, Long> entr : diffMap.entrySet()) {
			// set counter to 0 so it looks for time difference of the next
			// object
			if (negativeValue == true) {
				count = 0;
			}
			if (count == 0) {
				//System.out.println("COUNT: " + count);
				//System.out.println("NEG VALUE: " + negativeValue);
				mintimediff = entr.getValue();
				if (mintimediff > 0) {
					// time diff a non-negative value : negative value indicate
					// the objects in
					// repository was created after the current object (but
					// already ingested)

					derivedFrom_pid = entr.getKey();
					System.out.println("Minimum time difference is value is:------- " + Long.toString(mintimediff));
				} else {
					negativeValue = true;
				}
			}
			count++;
			// System.out.println("MINIMUM TIME DIFF: "
			// +Long.toString(mintimediff));
			// System.out.println(derivedFrom_pid);
		}

	//	System.out.println("===================");

		return derivedFrom_pid;

	}

	/**
	 * Sort the Map to have the lowest at the top of the map.
	 * Ascending order
	 * @param map
	 * @return
	 */

	 public Map sortByValue(Map<String, Long> map) {
		List list = new ArrayList(map.entrySet());
		Collections.sort(list, new Comparator<Object>() {
			public int compare(Object left, Object right) {
				return ((Comparable) ((Map.Entry) (left)).getValue())
						.compareTo(((Map.Entry) (right)).getValue());
			}

		});

		Map<String, Long> result = new LinkedHashMap<String, Long>();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry<String, Long> entry = (Map.Entry<String, Long>) it.next();
			result.put(entry.getKey(), entry.getValue());
		}

		return result;
	}
	 public LinkedHashMap<String, Long> sortMapByValueAscending( Map<String, Long>  map) { 
	        return sortMapByValue(map, SortingOrder.ASCENDING); 
	}
	 public LinkedHashMap<String, Long> sortMapByValueDescending( Map<String, Long>  map) { 
	        return sortMapByValue(map, SortingOrder.DESCENDING); 
	} 

	 public static <K, V> LinkedHashMap<String, Long> sortMapByValue(final Map<String, Long>  map, final SortingOrder sortingOrder) { 
	        Comparator<Map.Entry<String, Long> > comparator = new Comparator<Entry<String, Long> >() { 
	                public int compare(Entry<String, Long>  o1, Entry<String, Long>  o2) { 
	                        return comparableCompare(o1.getValue(), o2.getValue(), sortingOrder); 
	                } 
	        }; 
	 
	        return sortMap(map, comparator); 
	} 
	 /** 
	  * Sort a map by supplied comparator logic. 
	  *   
	  * @return new instance of {@link LinkedHashMap} contained sorted entries of supplied map. 
	  */ 
	 public static <K, V> LinkedHashMap<K, V> sortMap(final Map<K, V> map, final Comparator<Map.Entry<K, V>> comparator) { 
	         // Convert the map into a list of key,value pairs. 
	         List<Map.Entry<K, V>> mapEntries = new LinkedList<Map.Entry<K, V>>(map.entrySet()); 
	  
	         // Sort the converted list according to supplied comparator. 
	         Collections.sort(mapEntries, comparator); 
	  
	         // Build a new ordered map, containing the same entries as the old map.   
	         LinkedHashMap<K, V> result = new LinkedHashMap<K, V>(map.size() + (map.size() / 20)); 
	         for(Map.Entry<K, V> entry : mapEntries) { 
	                 // We iterate on the mapEntries list which is sorted by the comparator putting new entries into  
	                 // the targeted result which is a sorted map.  
	                 result.put(entry.getKey(), entry.getValue()); 
	         } 
	  
	         return result; 
	 } 
	  

	 @SuppressWarnings("unchecked") 
	 private static <T> int comparableCompare(T o1, T o2, SortingOrder sortingOrder) { 
	         int compare = ((Comparable<T>)o1).compareTo(o2); 
	  
	         switch (sortingOrder) { 
	         case ASCENDING: 
	                 return compare; 
	         case DESCENDING: 
	                 return (-1) * compare; 
	         } 
	  
	         return 0; 
	 } 
	 /** 
	  * Sorting order enum, specifying request result sort behavior. 
	  * 
	  * 
	  */ 
	 public static enum SortingOrder { 
	         /** 
	          * Resulting sort will be from smaller to biggest. 
	          */ 
	         ASCENDING, 
	         /** 
	          * Resulting sort will be from biggest to smallest. 
	          */ 
	         DESCENDING 
	 } 

	private Document stringToDom(String xmlSource) throws SAXException,
			ParserConfigurationException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(new InputSource(new StringReader(xmlSource)));
	}
	
	/**
	 * This get the input files in the phenix parameter DEF file and 
	 * checks the directory structure and check if the files mtz and pdb in the DEF file are 
	 * in the repository based on the original directory path.
	 * 
	 * @param phenixDEFFileObjectId  pid of the phenix .def file object
	 * @param experimentId
	 * @param phenixDEFMetadata The metadata
	 * @return the list of objectIds of the identified files in the repository.
	 */
	public Vector<String> inputObjectIdsInPhenixDefFile(String phenixDEFFileObjectId, byte[] phenixDEFMetadata, String experimentId) {
		
		
		// processing the phenix file we will get the
		// location of the phenix file and this mtz file must be the same
		Vector<String> inputObjectIds= new Vector<String>();
		System.out.println("phenixDEFFileObjectId: " +phenixDEFFileObjectId);
		String mtzpdbPath = getObjectTitle();
		
		System.out.println("inputPath: " +mtzpdbPath);
		String defPath = getOriginalPathOfObjectInRepository(phenixDEFFileObjectId);
		System.out.println("defPath: " +defPath);
		String defSlash="";
		if(defPath.contains("/")){
			defSlash="/";
		}
		if(defPath.contains("\\")){
			defSlash="\\";
		}
	
		defPath = defPath.substring(0, defPath.lastIndexOf(defSlash));
	
		System.out.println("def path: " +defPath);
		
		String mtzpdbSlash="";
		if(mtzpdbPath.contains("/")){
			mtzpdbSlash="/";
		}
		if(mtzpdbPath.contains("\\")){
			mtzpdbSlash="\\";
		}	
			mtzpdbPath = mtzpdbPath.substring(0, mtzpdbPath.lastIndexOf(mtzpdbSlash));
	
	
		// directory locations of the def file and mtz files are the same
		// eg., /home/stella/ba5d5/1
		if (mtzpdbPath.equals(defPath)) {
			// assume that this MTZ file wasgeneratedby using this def file
			// look for the input files in the phenix .def object's metadata
			// datastream

			/*byte[] phenixDEFMetadata = getDatastreamType(
					phenixDEFFileObjectId, DataStreamType.ObjectMetadata);
*/
			System.out
					.println("Get inputs filename in the PHENIX DEF file ............");
			Vector<String> inputNameList = getFilenamesFromTaskXML(phenixDEFMetadata,
							TaskObjectElement.INPUT_FILENAME);
			//String taskName = getValueFromTaskXML(phenixDEFMetadata, TaskObjectElement.TASK_NAME);
			
			Vector<String> inputPathList= new Vector<String>();

			/*
			 * Search for input filename in the repository
			 */
			for (int o = 0; o < inputNameList.size(); o++) {
				String inputFilename = inputNameList.get(o);
				String inputFilepath=null;
				System.out.println(inputFilename);
				String suffix = inputFilename.substring(inputFilename
						.lastIndexOf(".") + 1);
				//String inputPosibPath=null;
				//list of possiple inputFilename
				//inputFilename= ../filename.mtz
				//inputFilename= ../dir1/filename.mtz
				//inputFilename= /home/stella/baa5d5/1/fileName.mtz
				if(inputFilename.contains("../") || inputFilename.contains("..\\")){
					//if it contains the index of "/" and last index of "/" is the same
					System.out.println("contains ../");	
					int f =inputFilename.indexOf("/");
					int l = inputFilename.lastIndexOf("/");
					if(f==l){
						
						System.out.println("has only one / .....");	
				    //only one "/" is present
				    //directory of the file will be the one directory outside the def file directory
					// defPath = /home/stella/baa5d5/1
					 //IF //inputFilename = ../filename.mtz
					//then,
					// inputPosibPath = /home/stella/baa5d5
						String inputPosibPath = defPath.substring(0,defPath.lastIndexOf(defSlash));
						//System.out.println("inputPosibPath: "+ inputPosibPath);	
						//inputPosibPath= /home/stella/baa5d5/filename.mtz
						
						inputFilepath= inputPosibPath+defSlash+inputFilename.substring(l+1);
						//inputPathList.add(inputPosibPath+inputFilename.substring(l));
						System.out.println("inputPath1: "+ inputFilepath);	
						System.out.println("inputPath2: "+ inputPosibPath+inputFilename.substring(l));	
					}else{
						/*more then one two "/" is present meaning 
						 * inputFilename= ../dir1/filename.mtz
						 * take strign between the two "/" i.e dir1
						 * mid= /dir1
						 */
					//	String mid = inputFilename.substring(f,l);
						
							//get /home/stella/baa5d5 from   /home/stella/baa5d5/1
						String firstlevelDir= defPath.substring(0,defPath.lastIndexOf(defSlash));
						
					//	System.out.println("firstlevelDir: "+ firstlevelDir);	
						// /home/stella/baa5d5/dir1 
					//	String inputPosibPath = firstlevelDir+mid;
						// also handles something like inputFilename= ../dir1/dir2/filename.mtz
						//where mid = /dir1/dir2
						//inputPosibPath=  /home/stella/baa5d5/dir1/dir2
						 // inputFilename= ../dir1/filename.mtz
					
						String filename = inputFilename.substring(f);
						System.out.println(filename);
						if(filename.contains("/") && !"/".equals(defSlash)){
							filename = filename.replace("/", defSlash);
						}
					/*	int isSingleQuote = l-1;
						//System.out.println(inputFilename.charAt(isSingleQuote));
						char res =inputFilename.charAt(isSingleQuote);
						char singleQuote ='\'';
					  if(res!=singleQuote){
						if(Character.isUnicodeIdentifierStart(res)==true){
							//directory ../phaser/phaser-sol_ids.pdb
							String fPart= filename.substring(filename.indexOf('/')+1, filename.lastIndexOf('/'));
							//filename
							String lPart= filename.substring(filename.lastIndexOf('/')+1);
							//filename=fPart+"'"+lPart;
							filename=fPart+"'"+lPart;
						//	System.out.println(filename);
						}
						}*/
					  	
						inputFilepath =firstlevelDir+filename;
						//inputPathList.add(firstlevelDir+filename);
						System.out.println("inputPath: "+ inputFilepath);	
						}
					}else if(inputFilename.contains("./")) //Same directory as this def file
					{
						String filename = inputFilename.substring(inputFilename.lastIndexOf("/"));
						// defpath = /home/stella/baa5d5/1
						//filename =/filename.mtz
						inputPathList.add(defPath+filename);
					}else
						
						if(inputFilename.contains("\\") || inputFilename.contains("/") )
						
					{
						//check if this is the same directory as the defPath
						//inputFilename= /home/stella/baa5d5/1/fileName.mtz
						System.out.println("Contains directory struct in def file inputnames----");
						String firstlevelDir= inputFilename.substring(0,inputFilename.lastIndexOf("/"));
						if(inputFilename.contains("\\")){
						firstlevelDir=inputFilename.substring(0,inputFilename.lastIndexOf("\\"));
						
						}
						System.out.println(defPath);
						System.out.println(inputFilename);
						System.out.println(firstlevelDir +"\n");
						// defpath = /home/stella/baa5d5/1
						if(defPath.equals(firstlevelDir))
						{
							//inputPosibPath = defPath;
							inputPathList.add(inputFilename);
							inputFilepath=inputFilename;
						}else{
							System.out.println("Directory path of inputname '"+ inputFilename +"' (in DEF file) " +
									"\n is NOT same as path of the DEF file--'"+defPath+"'");
							System.out.println("Adding the input file (with def path) for the test case- must be remove for real scenario....");
							
							String newInputFilename=null;
							
							if(inputFilename.contains("/")){
								inputFilename = inputFilename.substring(inputFilename.lastIndexOf("/")+1);
								System.out.println(inputFilename + " 1 \n");
								
								
							}
							if(inputFilename.contains("\\")){
								inputFilename = inputFilename.substring(inputFilename.lastIndexOf("\\")+1);
								System.out.println(inputFilename + " 2 \n");
								newInputFilename =defPath+inputFilename;
							}
							if(defPath.contains("/")){
								newInputFilename =defPath+"/"+inputFilename;
								}
							if(defPath.contains("\\")){
								newInputFilename =defPath+"\\"+inputFilename;
							}
							System.out.println(newInputFilename + "\n");
							inputPathList.add(newInputFilename);
							inputFilepath=newInputFilename;
						}
						
					}
			
				System.out.println("FilePath:.... "+ inputFilepath );
				/**
				 * Get the object type from the repository base on the suffix.
				 */
				if (suffix.equals("pdb")) {
					 System.out.println("PDB file START.....");
					Map<String, Vector<String>> pdbObjectIdTitle = searchForObjectIdsTitle(experimentId,
									CrystallographyObjectType.CoordinateFile);

					// System.out.println("pdb files from repository: "+pdbObjectIdTitle);
					// get the object's filename in title = filename
					for (Map.Entry<String, Vector<String>> ent : pdbObjectIdTitle
							.entrySet()) {
						// String path =
						// generatorUtils.getOriginalPathOfObjectInRepository(ent.getKey());
						String retirevedPath = ent.getValue().get(0);
						String slashInPath="";
						if(retirevedPath.contains("/")){
							slashInPath="/";
						}
						if(retirevedPath.contains("\\")){
							slashInPath="\\";
						}
					//	 System.out.println(retirevedPath);
						String pdbFileName = retirevedPath.substring(retirevedPath.lastIndexOf(slashInPath) + 1);
						// System.out.println(path);
						String pdbFileDirectory = retirevedPath.substring(0, retirevedPath.lastIndexOf(slashInPath));
						
						if (retirevedPath.equals(inputFilepath) || retirevedPath==inputFilepath) {
							System.out.println("Matched path found and added.....");
							String id = ent.getKey();
							if(id.contains("/")){
								id = id.substring(id.lastIndexOf("/")+1);
							}
							inputObjectIds.add(id);
						}
						//else (retirevedPath.equals(anObject))
					}
					System.out.println("PDB file END.....");
				} 
				
				if (suffix.equals("mtz")) {
					 System.out.println("MTZ file START.....");
					Map<String, Vector<String>> mtzObjectIdTitle = searchForObjectIdsTitle(
									experimentId,
									CrystallographyObjectType.MTZReflectionFile);

					// System.out.println(mtzObjectIdTitle);
					// get the object's filename in title = filename
					for (Map.Entry<String, Vector<String>> ent : mtzObjectIdTitle
							.entrySet()) {
						// String path =
						// generatorUtils.getOriginalPathOfObjectInRepository(ent.getKey());
						String retirevedPath = ent.getValue().get(0);
						String slashInPath="";
						if(retirevedPath.contains("/")){
							slashInPath="/";
						}
						if(retirevedPath.contains("\\")){
							slashInPath="\\";
						}
					//	 System.out.println(retirevedPath);
						String mtzFileName = retirevedPath.substring(retirevedPath
								.lastIndexOf(slashInPath) + 1);
						// System.out.println(mtzFileName);
						String mtzFileDirectory = retirevedPath.substring(0, retirevedPath
								.lastIndexOf(slashInPath));
						// System.out.println("input filename in com file: "+
						// inputFilename);
						if (retirevedPath.equals(inputFilepath)|| retirevedPath==inputFilepath) {
							System.out.println("Matched path found and added.....");
							String id = ent.getKey();
							if(id.contains("/")){
								id = id.substring(id.lastIndexOf("/")+1);
							}
							inputObjectIds.add(id);
							// System.out.println("input object id added: "+
							// ent.getKey());
						}
					}
				}
				 System.out.println("MTZ file END.....");
			}//END: Input iterate
			
		}
		//System.out.println(inputObjectIds);
	
       return inputObjectIds;
	}
	
	public boolean hasRelationship(ObjectRelationship objectRelationship, String relation){
		boolean result=false;
		List<Relationship> relationshipList= objectRelationship.getRelationships();
		 Iterator<Relationship> relationshipIter = relationshipList.iterator();
		  while ( relationshipIter.hasNext() ){
			  Relationship relationship = (Relationship) relationshipIter.next();
			  if(relationship.getPredicate().equals(relation)){
				 // System.out.println();
				  result=true;
			  }
		      
		  }

		
		return result;
	}
	
	/**
	 * Get list of objects for the given subject and predicate 
	 * 
	 * @param subject pid
	 * @param predicate relationship string example wasDerivedFrom
	 * @return
	 */
	public Vector<String> getObjectsFrom(String subject,String predicate){
		//needs the brilrels url in ITQL query
		String brilrels= FedoraNamespace.BRILRELS.getURI();
		if(!predicate.contains("http://")){
			predicate=brilrels+predicate; 
		}
		return fedoraAdmin.getObjectsWithPredicate(subject, predicate);
	}
	
	public boolean hasPredicate(String subject,String predicate){
	
		//needs the brilrels url in ITQL query
		String brilrels= FedoraNamespace.BRILRELS.getURI();
		if(!predicate.contains("http://")){
			predicate=brilrels+predicate; 
		}
		return fedoraAdmin.hasPredicate(subject,predicate);

	}
	
	public boolean hasRelationship(String subject,String predicate,String object){
		//direct fedora tuples apis
		String brilrels= FedoraNamespace.BRILRELS.getURI();
		if(!predicate.contains("http://")){
			predicate=brilrels+predicate; 
		}
		if(!object.contains("info:fedora/")){
			object="info:fedora/"+object;
		}
		return fedoraAdmin.hasRelationship(subject, predicate, object);
		

	}

	// 
	
	// uses ITQL to return objects with the specified 'dc:title' that are related to the object by the predicate 
	public Vector<String> getRelatedObjects(String experimentId, String title, String predicate, String object){

		Vector<String> id = new Vector<String>();
		String resultXML = fedoraAdmin.getBRILRelatedObjectIds(experimentId, title, predicate, object);
		
		try {
			Document sourceDoc = stringToDom(resultXML.trim());			
			NodeList list = sourceDoc.getElementsByTagName("*");
			for (int i = 0; i < list.getLength(); i++) {
				Element element = (Element) list.item(i);
				if (element.getTagName().equals("result")) {
					NodeList childList = element.getChildNodes();					
					for (int j = 0; j < childList.getLength(); j++) {					   
					   int count = 0;
					   if(childList.item(j).getNodeName().equals("id")){
						   id.add(childList.item(j).getTextContent());
						   count++;
					   }											
					}					
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
		
		return id;
	}
	
	
	/**
	 * ITQL query to search for object ids/subject with wasDerivedFrom and object
	 * @param experimentId
	 * @param format
	 * @param predicate
	 * @param object
	 * @return
	 */
	public Vector<String> getSubjectsFrom(String experimentId, String format, String predicate, String object){
		

		//XML result
		Vector<String> id=new Vector<String>();
		String resultXML= fedoraAdmin.getBrilPredicateRelatedObjectIds(experimentId, format, predicate, object);
		//process the result to put the returned objectIs ibn the vector.
		System.out.println("ITQL Result");
		//System.out.println(resultXML);
		try {
			Document sourceDoc = stringToDom(resultXML.trim());

			NodeList list = sourceDoc.getElementsByTagName("*");
			for (int i = 0; i < list.getLength(); i++) {
				Element element = (Element) list.item(i);
				if (element.getTagName().equals("result")) {
					NodeList childList = element.getChildNodes();
					String pid = null;
					for (int j = 0; j < childList.getLength(); j++) {

						if (childList.item(j).getNodeName().equals("object")) {
							Element obj = (Element) childList.item(j);
							pid = obj.getAttribute("uri").trim();
						}
						int count = 0;
						if (childList.item(j).getNodeName().equals("id")) {
							id.add(childList.item(j).getTextContent());
							count++;
						}
					}
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
		return id;
		
	}

	public void setTaskObject(String taskDEFObjectId, TaskObject taskObject) {
		byte[] taskDEFFileMetadata = getDatastreamType(taskDEFObjectId, DataStreamType.ObjectMetadata);
		
		//put the metadata result to the taskObject
		setTaskObject(taskDEFFileMetadata, taskObject);						
	}

}

