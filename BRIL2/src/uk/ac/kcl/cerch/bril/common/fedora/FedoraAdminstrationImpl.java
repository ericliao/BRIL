package uk.ac.kcl.cerch.bril.common.fedora;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
//import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.RemoteException;
//import java.text.ParseException;
import java.text.SimpleDateFormat;
//import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
//import org.jrdf.graph.Node;
//import org.mortbay.log.Log;
import org.trippi.TrippiException;
import org.trippi.TupleIterator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
//import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.fcrepo.server.types.gen.ComparisonOperator;
import org.fcrepo.server.types.gen.Condition;
import org.fcrepo.server.types.gen.Datastream;
import org.fcrepo.server.types.gen.FieldSearchQuery;
import org.fcrepo.server.types.gen.FieldSearchResult;
import org.fcrepo.server.types.gen.ListSession;
import org.fcrepo.server.types.gen.ObjectFields;
import org.fcrepo.server.types.gen.RelationshipTuple;


import uk.ac.kcl.cerch.bril.common.fedora.FedoraNamespaceContext.FedoraNamespace;
import uk.ac.kcl.cerch.bril.common.metadata.DublinCore;
//import uk.ac.kcl.cerch.bril.common.metadata.DublinCoreElement;
import uk.ac.kcl.cerch.bril.common.types.*;
import uk.ac.kcl.cerch.bril.common.util.FileUtil;
import uk.ac.kcl.cerch.bril.fileformat.CrystallographyObjectType;

/**
 * This calls provides methods to ingest fedora and retrieve objects.
 */
/**
 * @author Shri
 *
 */
public class FedoraAdminstrationImpl implements FedoraAdministration {

	private static Logger logger = Logger.getLogger(FedoraAdminstrationImpl.class);

	private FedoraHandler fedoraHandler;
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

	/**
	 * Initializes the FedoraAdminstrationImpl; tries to connect to the
	 * underlying objectrepository. If this fails in some way, an
	 * BrilObjectRepositoryException is thrown.
	 */

	public FedoraAdminstrationImpl() throws BrilObjectRepositoryException{
		this.fedoraHandler= new FedoraHandler();
	}

	public boolean hasObject( String identifier ) throws BrilObjectRepositoryException
	{
		try
		{
			return this.fedoraHandler.hasObject( identifier );
		}
		catch ( RemoteException e )
		{
			String error = String.format( "RemoteException Error Connecting to fedora: %s", e.getMessage() );
			//log.error( error, e );
			throw new BrilObjectRepositoryException( error, e );
		}
	}

	/**
	 * Stores data in DatastreamObjectContainer {@code dsObject   } 
	 * in the  fedora repository. The minimum datastreams stored in 
	 * the container {@code dsContainer} are the DC and REL-EXT. 
	 * 
	 * @param  dsContainer the data to store 
	 * @param logmessage message to log the operation with 
	 * @return the objectIdentifier of the stored object 
	 * @throws Exception if the dsContainer could not be transformed into foxml or the foxml could not be stored      
	 */
	public String storeObject(DatastreamObjectContainer dsContainer) throws BrilObjectRepositoryException{
		FedoraUtils fileutil = new FedoraUtils();
		String objectIdAsString = dsContainer.getIdentifier();
		String returnedObjectId=null;
		byte[] foxmlByte=null;
		String locationURI =null;
		if (dsContainer.hasDatastream(DataStreamType.OriginalData)){
			DatastreamObject dso = dsContainer.getDatastreamObject(DataStreamType.OriginalData);
			byte[] filePathByte = dso.getDataBytes();
			if(filePathByte!=null){
				String filePath= new String(filePathByte);
				//the path is not a URL then put in the internal fedora repository
				if(filePath.indexOf("http://")<0){	
					//upload the file to fedora reposirory
					System.out.println("remote filepath: "+filePath);
					locationURI = uploadDatastream(filePath);
				}else{
					System.out.println("local filepath: "+filePath);
					locationURI = filePath;
				}
			}
		}
		try {
			if (locationURI!=null){
				fileutil.setContentLocation(locationURI);
			}
			foxmlByte =fileutil.DataStreamObjectToFoxml(dsContainer);
			//String foxmlString = new String(foxmlByte);
			//System.out.println("FOXML: -------------");
			//System.out.println(foxmlString);

		} catch (BrilTransformException e) {
			String error = String.format( "Failed to create foxml document (from DatastreamContainer) with pid '%s': %s",objectIdAsString, e.getMessage() );
			logger.error(error);
			throw new BrilObjectRepositoryException(error,e);
		} 

		try{

			returnedObjectId = fedoraHandler.ingest(foxmlByte, "info:fedora/fedora-system:FOXML-1.1",  "Ingest of " /*+ url*/ + ".");


		} catch (ConfigurationException e) {
			String error = String.format( "Failed to ingest object with pid '%s' into the repository: %s",objectIdAsString, e.getMessage() );
			logger.error(error);
			throw new BrilObjectRepositoryException(error,e);

		} catch (ServiceException e) {
			String error = String.format( "Failed to ingest object with pid '%s' into the repository: %s",objectIdAsString, e.getMessage() );
			logger.error(error);
			throw new BrilObjectRepositoryException(error,e);
		} catch (IOException e) {
			String error = String.format( "Failed to ingest object with pid '%s' into the repository: %s",objectIdAsString, e.getMessage() );
			logger.error(error);
			throw new BrilObjectRepositoryException(error,e);
		} 
		if(returnedObjectId.equals("")){
			logger.info(String.format("With an empty pid, ingest returned object pid: '%s'", returnedObjectId));
		}else if(!returnedObjectId.equals(objectIdAsString)){
			logger.info(String.format("Expected pid '%s' from repository, got object id: '%s'",objectIdAsString, returnedObjectId));
		}
		return returnedObjectId;

	}

	private String uploadDatastream(String filePath) throws BrilObjectRepositoryException{
		String location;
		try{
			File file = new File(filePath);
			location=this.fedoraHandler.uploadFile(file);
		}
		catch(IOException ex){
			String error ="Failed to upload file to fedora server";
			logger.error(error);
			throw new BrilObjectRepositoryException(error, ex);
		}
		return location;
	}


	/**
	 * Retrieves an object encoded as a {@link DatastreamObjectContainer} from the fedora object repository.    
	 *    
	 * @param objectId the fedora pid identifying the object in the repository 
	 * @return the object encoded as a {@link DatastreamObjectContainer }    
	 *  
	 * @param objectId
	 */
	public DatastreamObjectContainer retrieveObject(String objectId) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Retrieves an object encoded as a {@link DatastreamObjectContainer} from      
	 * the fedora object repository.    
	 *    
	 * @param objectId the fedora pid identifying the object in the repository 
	 * @return  the object encoded as a {@link DatastreamObjectContainer }    
	 * @throws RemoteException 
	 */

	public DatastreamObjectContainer retrieveDataFromObject(String objectId,DataStreamType streamtype) throws BrilObjectRepositoryException {
		DatastreamObjectContainer dsoc = new DatastreamObjectContainer();

		byte[] data = null;
		//DublinCore dcMetadataElements = new DublinCore();
		DublinCore dcMetadataElements = new DublinCore(objectId);
		try {
			if(streamtype == DataStreamType.DublinCore){				
				data = this.fedoraHandler.getDatastreamDissemination(objectId,"DC", null);

				dsoc.addDatastreamObject(streamtype, DatastreamMimeType.TEXT_XML.getMimeType(), "DC", "", data);

				/*InputStream inputstream = new ByteArrayInputStream(data);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder;
			try{
			builder = factory.newDocumentBuilder();

				Document sourceDoc = builder.parse(inputstream);
				// Get the list of all elements in the document
				NodeList list = sourceDoc.getElementsByTagName("*");
				for (int i = 0; i < list.getLength(); i++) {
					Element element = (Element) list.item(i);
					String localNodeName = element.getLocalName();
					//System.out.println("node: "+localNodeName);
					//System.out.println("value: "+element.getTextContent());
					if (localNodeName.equals(DublinCoreElement.ELEMENT_TITLE.localName())) {
						dcMetadataElements.setTitle(element.getTextContent());
						//System.out.println(element.getTextContent());
					}

					if(localNodeName.equals(DublinCoreElement.ELEMENT_DESCRIPTION.localName())){
						dcMetadataElements.setDescription(element.getTextContent());
						System.out.println(element.getTextContent());
					}
					if(localNodeName.equals(DublinCoreElement.ELEMENT_FORMAT.localName())){
						dcMetadataElements.setFormat(element.getTextContent());
						System.out.println(element.getTextContent());
					}
					if(localNodeName.equals(DublinCoreElement.ELEMENT_DATE.localName())){
						String date = element.getTextContent();
						Date d=null;
						try {
							d = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")).parse(date);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						String s2 = (new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(d));
						dcMetadataElements.setDate(s2,"dd/MM/yyyy HH:mm:ss");
						System.out.println(s2);
					}

				}
				} catch (RemoteException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 

				dsoc.addMetaData(dcMetadataElements);

				 */
			}


			if(streamtype == DataStreamType.RelsExt){
				data = this.fedoraHandler.getDatastreamDissemination(objectId,"RELS-EXT", null);

				dsoc.addDatastreamObject(streamtype, DatastreamMimeType.APPLICATION_RDF.getMimeType(), "RELS-EXT", "", data);
			}
			if(streamtype == DataStreamType.ObjectMetadata){
				data = this.fedoraHandler.getDatastreamDissemination(objectId,"BRILMETA", null);
				dsoc.addDatastreamObject(streamtype, DatastreamMimeType.TEXT_XML.getMimeType(), "BRILMETA", "", data);
			}
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TODO Auto-generated method stub
		return dsoc;
	}

	public String getDCTitle(String objectId) throws BrilObjectRepositoryException {
		String titleValue=null; 
		byte[] data = null;
		try {
			data = this.fedoraHandler.getDatastreamDissemination(objectId,"DC", null);
		} catch (RemoteException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		InputStream inputstream = new ByteArrayInputStream(data);
		FileUtil.writeByteArrayToFile("dcReturned.xml", data);

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();

			Document sourceDoc = builder.parse(inputstream);
			//Get the list of all elements in the document
			NodeList list = sourceDoc.getElementsByTagName("*");
			for (int i=0; i<list.getLength();i++){
				Element element =(Element)list.item(i);
				String localNodeName =element.getLocalName();
				if(localNodeName.equals("title")){
					titleValue=	element.getTextContent();
				}
				//System.out.println(element.getLocalName()+": "+element.getTextContent());
			}

			//System.out.println("title direct---" +sourceDoc.getElementsByTagName("title").item(0));
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
		return titleValue;
		// TODO Auto-generated method stub

	}

	public String getDCFormat(String objectId) throws BrilObjectRepositoryException {
		String titleValue=null; 
		byte[] data = null;
		try {
			data = this.fedoraHandler.getDatastreamDissemination(objectId,"DC", null);
		} catch (RemoteException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		InputStream inputstream = new ByteArrayInputStream(data);
		FileUtil.writeByteArrayToFile("dcReturned.xml", data);

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();

			Document sourceDoc = builder.parse(inputstream);
			//Get the list of all elements in the document
			NodeList list = sourceDoc.getElementsByTagName("*");
			for (int i=0; i<list.getLength();i++){
				Element element =(Element)list.item(i);
				String localNodeName =element.getLocalName();
				if(localNodeName.equals("format")){
					titleValue=	element.getTextContent();
				}
				//System.out.println(element.getLocalName()+": "+element.getTextContent());
			}

			//System.out.println("title direct---" +sourceDoc.getElementsByTagName("title").item(0));
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
		return titleValue;
		// TODO Auto-generated method stub

	}



	/**
	 * Method for finding pids of objects based on object properties 
	 * @param property, the property to match 
	 * @param operator, the operator to apply, "has", "eq", "lt", "le","gt" and "ge" are valid //eq cannot work
	 * @param value, the value the property adheres to 
	 * @return an array of pids of the matching objects
	 */
	public ObjectFields[] findObjectPids(String propertyFieldName, String comparisionOperator, String value, int maximumResult) {

		ComparisonOperator comp = ComparisonOperator.fromString(comparisionOperator);

		Condition[] cond= { new Condition(propertyFieldName, comp, value) };

		FieldSearchQuery fsq = new FieldSearchQuery(cond, null);
		String[] resultFields = {"pid"};
		ObjectFields[] objectFields=null;
		NonNegativeInteger maxResults = new NonNegativeInteger(Integer.toString(maximumResult));


		try {
			FieldSearchResult fsr = fedoraHandler.findObjects(resultFields,maxResults, fsq);		
			objectFields = fsr.getResultList();

		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return objectFields;
	}

	/**
	 * Method for finding ObjectFields of objects based on object properties
	 * @param resultFields, the fields to return:-Key fields: pid, label, state -Dublin core fields: title, creator, subject, description, publisher, contributor, date, format, identifier, source, language, relation, coverage, rights
	 * @param property, the property to match
	 * @param value, the value the property adheres to
	 * @return ObjectField array of the matching objects
	 * @param resultFields
	 * @param property
	 * @param value
	 */

	public ObjectFields[] findObjectFields(String[] resultFields, String propertyFieldName, String comparisionOperator, String value, int maximumResult) {
		ComparisonOperator comp = ComparisonOperator.fromString(comparisionOperator);

		Condition[] cond = { new Condition(propertyFieldName, comp, value) };


		FieldSearchQuery fsq = new FieldSearchQuery(cond, null);

		ObjectFields[] objectFields=null;
		NonNegativeInteger maxResults = new NonNegativeInteger(Integer.toString(maximumResult));
		LinkedList<ObjectFields[]> objectFieldsList = new LinkedList<ObjectFields[]>();	
		int numberOfResults =0;
		FieldSearchResult fsr=null;
		try {
			fsr = fedoraHandler.findObjects(resultFields,maxResults, fsq);		

			numberOfResults+=fsr.getResultList().length;
			//push the objectFields result in the linkedlist
			objectFieldsList.push(fsr.getResultList());

			ListSession listSession =  fsr.getListSession();

			while(listSession!=null){
				String token = listSession.getToken();
				fsr= this.fedoraHandler.resumeFindObjects(token);
				if(fsr!=null){
					numberOfResults+=fsr.getResultList().length;
					objectFieldsList.push(fsr.getResultList());
					listSession=fsr.getListSession();
				}else{
					listSession=null;
				}
			}

		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(fsr==null){
			return new ObjectFields[]{};
		}
		objectFields= new ObjectFields[numberOfResults];
		//copy objectfields from linkedlisd to one single array
		int destinationPosition=0;
		for(ObjectFields[] of: objectFieldsList){
			System.arraycopy(of, 0, objectFields, destinationPosition, of.length);
			destinationPosition+=of.length;
		}
		return objectFields;
	}

	public ObjectFields[] findObjectFields(String[] resultFields, Map<String,String> propertyFieldName, String comparisionOperator, int maximumResult) {
		ComparisonOperator comp = ComparisonOperator.fromString(comparisionOperator);

		int size = propertyFieldName.size();
		Condition[] cond = new Condition[size];
		int i = 0;
		for(Map.Entry<String, String> entry: propertyFieldName.entrySet()){
			cond[i] = new Condition(entry.getKey(), comp, entry.getValue());
			i++;
		}

		FieldSearchQuery fsq = new FieldSearchQuery(cond, null);
		ObjectFields[] objectFields=null;
		NonNegativeInteger maxResults = new NonNegativeInteger(Integer.toString(maximumResult));

		try {
			FieldSearchResult fsr = fedoraHandler.findObjects(resultFields,maxResults, fsq);		
			objectFields = fsr.getResultList();

		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return objectFields;
	}

	/**
	 * @param objectId Identifier of the fedora object of which to get the datastream
	 * @param dataStreamID The type of data strema such as DC REL-EXT
	 * 
	 */

	public Datastream getDatastream( String objectId, String dataStreamID ) throws BrilObjectRepositoryException
	{
		Datastream ds = null;

		try
		{
			ds = this.fedoraHandler.getDatastream( objectId, dataStreamID );
		}
		catch( RemoteException re )
		{
			String error = String.format( "Failed to retrieve datastream with name '%s' from object with objectIdentifier '%s': %s", dataStreamID, objectId, re.getMessage() );
			//  log.error( error );
			throw new BrilObjectRepositoryException( error, re );
		}

		if ( ds == null )
		{
			String error = String.format( "Failed to retrieve datastream with name '%s' from object with objectIdentifier '%s': Got nothing back from the object repository", dataStreamID, objectId );
			// log.error( error );
			throw new BrilObjectRepositoryException( error );
		}

		return ds;
	}
	public Datastream[] getDatastreamXML( String objectId, String dataStreamID ) throws BrilObjectRepositoryException
	{
		Datastream[] ds = null;

		try
		{
			ds = this.fedoraHandler.getDatastreamXML(objectId);
		}
		catch( RemoteException re )
		{
			String error = String.format( "Failed to retrieve datastream with name '%s' from object with objectIdentifier '%s': %s", dataStreamID, objectId, re.getMessage() );
			//  log.error( error );
			throw new BrilObjectRepositoryException( error, re );
		}

		if ( ds == null )
		{
			String error = String.format( "Failed to retrieve datastream with name '%s' from object with objectIdentifier '%s': Got nothing back from the object repository", dataStreamID, objectId );
			// log.error( error );
			throw new BrilObjectRepositoryException( error );
		}

		return ds;
	}
	/**
	 * Adds relationship about an object
	 * 
	 *@param subjectIdentifier Identifier of the subject (repository object) where the relationship is added
	 *@param relation The relationship of the subject
	 *@param objectIdentifier The identifier of the object that is related to this subject
	 *
	 */

	public void addObjectRelation(String subjectIdentifier, String relation,  String objectIdentifier, boolean literal) throws BrilObjectRepositoryException
	{

		logger.info(String.format("trying to add %s - %s -> %s",subjectIdentifier, relation, objectIdentifier));
		try {
			this.fedoraHandler.addRelationship(subjectIdentifier, relation, objectIdentifier, literal, null);
		} catch (ConfigurationException e) {
			String error="Failed to add Relation to fedora Object";
			logger.error(error,e);
			throw new BrilObjectRepositoryException(error, e);
		} catch (MalformedURLException e) {		
			String error="Failed to add Relation to fedora Object";
			logger.error(error,e);
			throw new BrilObjectRepositoryException(error, e);
		} catch (ServiceException e) {
			String error="Failed to add Relation to fedora Object";
			logger.error(error,e);
			throw new BrilObjectRepositoryException(error, e);
		} catch (IOException e) {
			String error="Failed to add Relation to fedora Object";
			logger.error(error,e);
			throw new BrilObjectRepositoryException(error, e);

		}

	}

	/**
	 * @param subject the identifier of the subject where the relation tuple is searched
	 * @param predicate The relation search for the subject
	 * @param object The object to which this subject may have this relationship.
	 * 
	 * @return boolean
	 */
	public boolean hasRelationship(String subject,String predicate,String object){

		boolean isRelation= false;
		try
		{
			RelationshipTuple[] tuples = this.fedoraHandler.getRelationships(subject, null);
			System.out.println(tuples.length);
			for(int i=0; i<tuples.length;i++){
				RelationshipTuple tuple = tuples[i];
				//System.out.println(tuple.getSubject());

				//String gotPredicate=tuple.getPredicate();
				//System.out.println("repo got: "+gotPredicate);
				//System.out.println("to check with: "+predicate);

				//System.out.println("repo got datatype : "+tuple.getDatatype());
				//System.out.println(tuple.getObject());
				//System.out.println(tuple.getPredicate());

				if(tuple.getPredicate().equals(predicate) ){
					System.out.println("matched predicate--");						
					String retObj = tuple.getObject().trim();
					System.out.println("Got object: "+retObj);
					System.out.println("To check with: "+object);
					if(retObj.equals(object.trim())|| retObj==object.trim()){
						System.out.println("matched object---");
						isRelation=true;
					}
				}
			}


		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return isRelation;
	}

	public Vector<String> getObjectsWithPredicate(String subject,String predicate){
		Vector<String> objects=new Vector <String>();
		try
		{
			RelationshipTuple[] tuples = this.fedoraHandler.getRelationships(subject, null);
			System.out.println(tuples.length);
			for(int i=0; i<tuples.length;i++){
				RelationshipTuple tuple = tuples[i];
				if(tuple.getPredicate().equals(predicate) ){
					objects.add(tuple.getObject().trim());
				}
			}

		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return objects;

	}

	/*    public Vector<String> getSubjectsWithPredicateAndObject(String predicate, String object){
    	 Vector<String> subjects=new Vector <String>();
     	try
         {
 			RelationshipTuple[] tuples = this.fedoraHandler.getRelationships(subject, null);

 			System.out.println(tuples.length);
 			for(int i=0; i<tuples.length;i++){
 				RelationshipTuple tuple = tuples[i];
 				if(tuple.getPredicate().equals(predicate) ){
 					objects.add(tuple.getObject().trim());
 				}
 			}

 	} catch (ConfigurationException e) {
 		// TODO Auto-generated catch block
 		e.printStackTrace();
 	} catch (MalformedURLException e) {
 		// TODO Auto-generated catch block
 		e.printStackTrace();
 	} catch (IOException e) {
 		// TODO Auto-generated catch block
 		e.printStackTrace();
 	} catch (ServiceException e) {
 		// TODO Auto-generated catch block
 		e.printStackTrace();
 	}
 	return subjects;
    }*/
	public boolean hasPredicate(String subject, String predicate){
		boolean res = false;
		try
		{
			RelationshipTuple[] tuples = this.fedoraHandler.getRelationships(subject, null);
			//System.out.println(tuples.length);
			for(int i=0; i<tuples.length;i++){
				RelationshipTuple tuple = tuples[i];
				if(tuple.getPredicate().equals(predicate) ){
					res=true;
				}
			}

		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

	public Vector<String> getExperimentRelatedObjectIds(String expId,String objectFormat){
		Vector<String> objectIds = new Vector<String>();
		/*Ideal is to have date in the itql query but currently its 
		 * giving paring error for any complex query
		 * */
		String query ="select $object from " +
		"<#ri>" +
		"where " +
		"$object <dc:format> '"+objectFormat+"'" +
		"and $object <fedora-rels-ext:isPartOf> " +
		"<"+expId+">";
		/**
		 * TODO use the query1 so that it returns DC date of this object type	
		 */
		String query1 ="select $object $id from " +
		"<#ri> " +
		"where " 
		+"$object <dc:format> '"+objectFormat+"' " +
		"and $object <dc:identifier> $id "+
		"and $object <fedora-rels-ext:isPartOf> <"+expId+">";

		Map<String,String> param = new HashMap<String,String>();

		param.put("lang", "itql");
		param.put("query", query);
		//System.out.println(query1);
		//System.out.println();
		//System.out.println(query);
		try {
			TupleIterator tuples = this.fedoraHandler.getTuples(param);

			try {
				String[] keys = tuples.names();
				while (tuples.hasNext()){

					Map <String,org.jrdf.graph.Node> result = tuples.next();
					for (Map.Entry<String,org.jrdf.graph.Node> entry: result.entrySet()){
						//	System.out.println(entry.getValue());
						objectIds.add(entry.getValue().toString());
					}

				}
			} catch (TrippiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return objectIds;
	}

	/*   public Map<String,String> getExperimentRelatedObjectIdsDate(String expId,String objectFormat){
    	 Map<String,String> objectIds = new HashMap<String,String>();
    	Ideal is to have date in the itql query but currently its 
	 * giving paring error for any complex query
	 * 
    	 String expId1 = expId.substring(expId.lastIndexOf(":")+1);
    	 System.out.println(expId1);
      //TODO : make this query work currently not working: this work with 
    	 //http://localhost:9055/fedora/risearch
   	String query ="select $object $date from " +
		"<#ri> " +
		"where " 
		+"$object <dc:format> '"+objectFormat+"' " +	
		"and $object <fedora-rels-ext:isPartOf> <"+expId+">"+
		"and $object <dc:date> $date ";
   		Map<String,String> param = new HashMap<String,String>();

    	param.put("lang", "itql");
    	param.put("query", query);
    	//System.out.println(query1);
    	//System.out.println();
    	//System.out.println(query);
    	try {
    		System.out.println("before");

    		//r = Risearch(server=�http://localhost:8080/fedora�) ;
    		TupleIterator tuples = this.fedoraHandler.getTuples(param);

    		System.out.println("after");
    		try {
    			String[] keys = tuples.names();
				while (tuples.hasNext()){

					Map <String,org.jrdf.graph.Node> result = tuples.next();
					for (Map.Entry<String,org.jrdf.graph.Node> entry: result.entrySet()){
						System.out.println(entry.getValue());
						//TODO
						objectIds.put(entry.getKey(), entry.getValue().toString());
					}

				}
			} catch (TrippiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return objectIds;
    }*/

	public String getObjectDate(String expId, String pid) {
		
		String expId1 = expId.substring(expId.lastIndexOf(":") + 1);		
		String result = null;
		
		if (pid.contains("/")) {
			pid = pid.substring(pid.indexOf("/") + 1);
		}
		
		String risearchURL = this.fedoraHandler.getFedoraURL() + "/risearch";
		String requestParameters = "query="
			+ "select+%24date+from+%3C%23ri%3Ewhere+%24object+%3Cdc%3Aidentifier%3E+%27"
			+ pid
			+ "%27"
			+ "and+%24object+%3Cfedora-rels-ext%3AisPartOf%3E+%3Cinfo%3Afedora%2Fbril%3A"
			+ expId1
			+ "%3Eand+%24object+%3Cdc%3Adate%3E+%24date&format=Sparql&type=tuples&lang=itql";
			// select $object $date
			// from <#ri>
			// where $object <dc:identifier> 'bril:8832220f-81c0-4f9d-8df8-9c70d0bcc169'
			// and $object <fedora-rels-ext:isPartOf><info:fedora/bril:exp-958032df-0260-4494-b164-ab1f482f2dc5>
			// and $object <dc:date> $date	
							
		try {
			// Send data
			if (requestParameters != null && requestParameters.length() > 0) {
				risearchURL += "?" + requestParameters;
			}
			URL url = new URL(risearchURL);
			URLConnection conn = url.openConnection();

			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			rd.close();
			result = sb.toString();			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
	
	public String getExperimentRelatedObjectIdsDate(String expId, String objectFormat) {
		
		String expId1 = expId.substring(expId.lastIndexOf(":") + 1);		
		String result = null;
		
		String risearchURL = this.fedoraHandler.getFedoraURL() + "/risearch";
		String requestParameters = "query="
			+ "select+%24object+%24date+from+%3C%23ri%3Ewhere+%24object+%3Cdc%3Aformat%3E+%27"
			+ objectFormat
			+ "%27"
			+ "and+%24object+%3Cfedora-rels-ext%3AisPartOf%3E+%3Cinfo%3Afedora%2Fbril%3A"
			+ expId1
			+ "%3Eand+%24object+%3Cdc%3Adate%3E+%24date&format=Sparql&type=tuples&lang=itql";		
		try {
			// Send data
			if (requestParameters != null && requestParameters.length() > 0) {
				risearchURL += "?" + requestParameters;
			}
			URL url = new URL(risearchURL);
			URLConnection conn = url.openConnection();

			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			rd.close();
			result = sb.toString();			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public String getExperimentRelatedObjectIdsTitle(String expId,
			String objectFormat) {
		String expId1 = expId.substring(expId.lastIndexOf(":") + 1);
		System.out.println(expId1);
		String result = null;
		// System.out.println( this.fedoraHandler.getFedoraURL());
		String risearchURL = this.fedoraHandler.getFedoraURL() + "/risearch";
		String requestParameters = "query="
			+ "select+%24object+%24title+from+%3C%23ri%3Ewhere+%24object+%3Cdc%3Aformat%3E+%27"
			+ objectFormat
			+ "%27"
			+ "and+%24object+%3Cfedora-rels-ext%3AisPartOf%3E+%3Cinfo%3Afedora%2Fbril%3A"
			+ expId1
			+ "%3Eand+%24object+%3Cdc%3Atitle%3E+%24title&format=Sparql&type=tuples&lang=itql";
		try {
			// Send data

			if (requestParameters != null && requestParameters.length() > 0) {
				risearchURL += "?" + requestParameters;
			}
			URL url = new URL(risearchURL);
			URLConnection conn = url.openConnection();

			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			rd.close();
			result = sb.toString();
			// System.out.println(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}		
	
	public String getObjectIdsUsingDC(String expId, String dcElement,
			String searchString) {
		expId = expId.substring(expId.lastIndexOf(":") + 1);
		String result = null;
		String risearchURL = this.fedoraHandler.getFedoraURL() + "/risearch";

		String requestParameters = "query="
			+ "select+%24object+from+%3C%23ri%3Ewhere+%24object+%3Cdc%3A"
			+ dcElement
			+ "%3E+%27"
			+ searchString
			+ "%27and+%24object+%3Cfedora-rels-ext%3AisPartOf%3E+%3Cinfo%3Afedora%2Fbril%3A"
			+ expId + "%3E&format=Sparql&type=tuples&lang=itql";

		try {
			// Send data

			if (requestParameters != null && requestParameters.length() > 0) {
				risearchURL += "?" + requestParameters;
			}
			URL url = new URL(risearchURL);
			URLConnection conn = url.openConnection();

			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			rd.close();
			result = sb.toString();
			// System.out.println(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
	

	/**
	 * Search for subject with a given dc:title and has predicate e.g. 'used' and object
	 * E.g. Search for CHAINSAW process with 'used' relationship with an ALN object
	 * 
	 * Example ITQL Query:
	 * select $object $id from <#ri>
	 * where $object <dc:title> 'IntegrateDiffractionImagesProcess'
	 * and $object <fedora-rels-ext:isPartOf> <info:fedora/bril:exp-354031ec-1380-42d4-ab1b-37e0c1a0428c>
	 * and $object <opmv:used> <info:fedora/bril:bril:e6063212-ca74-4b36-b041-24d9db50537f>
	 * and $object <dc:identifier> $id
	 * 
	 * @param expId experiment id
	 * @param objectTitle title of the object in DC
	 * @param object the object string in a subject-predicate-object triple
	 * @param predicate the predicate string in a subject-predicate-object triple
	 * 
	 * @return XML string result of ITQL query
	 */
	public String getBRILRelatedObjectIds(String expId,
			String objectTitle, String predicate, String object) {
				
		String expId1 = expId.substring(expId.lastIndexOf(":") + 1);
		System.out.println(expId1);
		String result = null;
		String objectID = object;
		if (objectID.contains("bril:")) {
			objectID = objectID.substring(objectID.lastIndexOf(":") + 1);
		}
		
		String risearchURL = this.fedoraHandler.getFedoraURL() + "/risearch";		
		
		String predicate_ns = null;		
		String requestParameters = "query="
			// select+$object+$id+from+<#ri>where+$object+<dc:title>+'objectTitle'
			+ "select+%24object+%24id+from+%3C%23ri%3Ewhere+%24object+%3Cdc%3Atitle%3E+%27" + objectTitle + "%27"
			// and+$object+<fedora-rels-ext:isPartOf>+<info:fedora/bril:expId1>
			+ "and+%24object+%3Cfedora-rels-ext%3AisPartOf%3E+%3Cinfo%3Afedora%2Fbril%3A" + expId1 + "%3E"
			// and+$object+<http://purl.org/net/opmv/ns#predicate>+<info:fedora/bril:objectID>
			+ "and+%24object+%3Chttp%3A//purl.org/net/opmv/ns%23" + predicate + "%3E+%3Cinfo%3Afedora%2Fbril%3A" + objectID + "%3E"
			// and+$object+<dc:identifier>+$id&format=Sparql&type=tuples&lang=itql
			+ "and+%24object+%3Cdc%3Aidentifier%3E+%24id&format=Sparql&type=tuples&lang=itql";
		
		//System.out.println("requestParameters: (" + requestParameters + ")");
		
		try {
			// Send data

			if (requestParameters != null && requestParameters.length() > 0) {
				risearchURL += "?" + requestParameters;
			}
			URL url = new URL(risearchURL);
			URLConnection conn = url.openConnection();

			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			rd.close();
			result = sb.toString();
			// System.out.println(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;		
	}
	
	/**
	 * Search for subject with a given format eg. coordinateFile and 
	 * has predicate e.g 'wasDerivedFrom' and object e.g bril:iewruiruew3434werf
	 * 
	 * Done by ITQL query: the bril relationship url is present in ITQL rest query
	 * @see FedoraNamespace.BRILRELS.getURI(); and change the url if necessary
	 * 
	 * @param expId experiment id
	 * @param objectFormat format of the object in DC
	 * @param object the object string in a subject-predicate-object triple
	 * @param predicate the predicate string in a subject-predicate-object triple
	 * 
	 * $ = %24
	 * / = %2F
	 * : = %3A
	 * # = %23
	 * < = %3C
	 * > = %3E
	 * @return XML string result of ITQL query
	 */
	public String getBrilPredicateRelatedObjectIds(String expId,
			String objectFormat, String predicate, String object) {
		String expId1 = expId.substring(expId.lastIndexOf(":") + 1);
		System.out.println(expId1);
		String result = null;
		String objectID = object;
		if (objectID.contains("bril:")) {
			objectID = objectID.substring(objectID.lastIndexOf(":") + 1);
		}
		String brilrels_URL = FedoraNamespace.BRILRELS.getURI();
		/*
		 * replace the url string so it can be passed in the rest ITQL query.
		 * 
		 * http://bril-dev.cerch.kcl.ac.uk/relationship#
		 * http%3A%2F%2Fbril-dev.cerch.kcl.ac.uk%2Frelationship%23
		 */
		brilrels_URL = brilrels_URL.replace(":", "%3A");
		brilrels_URL = brilrels_URL.replace("/", "%2F");
		brilrels_URL = brilrels_URL.replace("#", "%23");
		// System.out.println(brilrels_URL);
		/*
		 * select $object $id from <#ri> where $object <dc:format>
		 * 'coordinateFile' and $object <dc:identifier> $id and $object
		 * <fedora-rels-ext:isPartOf>
		 * <info:fedora/bril:expaa4ca950-a82f-49a7-93e9-59641de0ddb8> and
		 * $object <http://bril-dev.cerch.kcl.ac.uk/relationship#wasDerivedFrom>
		 * <info:fedora/cefaeb9f-2e87-40da-b97f-8eb4ba0d1dc2>;
		 */
		String risearchURL = this.fedoraHandler.getFedoraURL() + "/risearch";		

		// where object id example=
		// info:fedora/bril:cefaeb9f-2e87-40da-b97f-8eb4ba0d1dc2
		// test in server

		String requestParameters = "query="
			+ "select+%24object+%24id+from+%3C%23ri%3Ewhere+%24object+%3Cdc%3Aformat%3E+%27"
			+ objectFormat
			+ "%27"
			+ "and+%24object+%3Cfedora-rels-ext%3AisPartOf%3E+%3Cinfo%3Afedora%2Fbril%3A"
			+ expId1
			+ "%3E"
			+ "and+%24object+%3C"
			+ brilrels_URL
			+ predicate
			+ "%3E+%3Cinfo%3Afedora%2Fbril%3A"
			+ objectID
			+ "%3E"
			+ "and+%24object+%3Cdc%3Aidentifier%3E+%24id&format=Sparql&type=tuples&lang=itql";

		// System.out.println(requestParameters);
		try {
			// Send data

			if (requestParameters != null && requestParameters.length() > 0) {
				risearchURL += "?" + requestParameters;
			}
			URL url = new URL(risearchURL);
			URLConnection conn = url.openConnection();

			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			rd.close();
			result = sb.toString();
			// System.out.println(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public static void main(String[]arg){
		try {
			FedoraAdminstrationImpl admin = new FedoraAdminstrationImpl();
			String expId ="info:fedora/bril:00EXPT123";
			String expId1 ="info:fedora/bril:expaa4ca950-a82f-49a7-93e9-59641de0ddb8";
			//String objectFormat="mtzReflectionFile";
			String objectFormat= CrystallographyObjectType.CoordinateFile.getType();
			String identifierToCheck="bril:3224837b-05dd-476e-9ea6-bb8b035352ca";
			System.out.println(objectFormat);
			//Vector objects = admin.getExperimentRelatedObjectIds(expId1, objectFormat);

			String diffset =admin.getObjectIdsUsingDC(expId1,"title","DiffractionImageSet");
			//System.out.println(objects);
			//System.out.println(admin.getExperimentRelatedObjectIdsDate(expId1, objectFormat));
			//admin.getExperimentRelatedObjectIdsDate(expId1, objectFormat);
			System.out.println(diffset);
		} catch (BrilObjectRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}