package uk.ac.kcl.cerch.bril.relationship.generator;

import java.io.ByteArrayOutputStream;
import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import uk.ac.kcl.cerch.bril.common.fedora.BrilObjectRepositoryException;
import uk.ac.kcl.cerch.bril.common.fedora.FedoraAdminstrationImpl;
import uk.ac.kcl.cerch.bril.common.fedora.FedoraRelsExt;
import uk.ac.kcl.cerch.bril.common.fedora.FedoraNamespaceContext.FedoraNamespace;
import uk.ac.kcl.cerch.bril.common.metadata.DublinCore;
import uk.ac.kcl.cerch.bril.common.types.BrilRelationshipType;
import uk.ac.kcl.cerch.bril.common.types.BrilTransformException;
import uk.ac.kcl.cerch.bril.common.types.DataStreamType;
import uk.ac.kcl.cerch.bril.common.types.DatastreamMimeType;
import uk.ac.kcl.cerch.bril.common.types.DatastreamObjectContainer;
import uk.ac.kcl.cerch.bril.relationship.ObjectRelationship;
import uk.ac.kcl.cerch.bril.relationship.common.GeneratorUtils;
import uk.ac.kcl.cerch.bril.service.uuid.IDGenerator;
import uk.ac.kcl.cerch.soapi.objectstore.ObjectStoreException;

/**
 * @author Eric Liao
 * 
 */
public class ALNFileRelationshipGeneratorImpl implements ALNFileRelationshipGenerator {
	
	private ObjectRelationship objectRelationship;
	private GeneratorUtils generatorUtils;
	private String clustalProcessId;
	
	@Override
	public ObjectRelationship generateRelationships(String objectID, String experimentId) {
		
		if (experimentId.contains("bril:") == false) {
			experimentId = "bril:" + experimentId;
		}
		
		generatorUtils = new GeneratorUtils();
		objectRelationship = new ObjectRelationship();
		objectRelationship.addRelationship(objectID, "isPartOf", experimentId);
		
		try {
			/**
			 * Search in the object store for this object's DC data that sets
			 * title and date values to the setter methods in the GeneratorUtils
			 */
			generatorUtils.searchForArchivalObjects(objectID);
		
		} catch (ObjectStoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if (checkForClustalProcess(experimentId) == true) {
			// Current ALN object ---wasGeneratedBy---> ClustalW2 Process
			this.objectRelationship.addRelationship(objectID, BrilRelationshipType.wasGeneratedBy.getRelation(),
					getClustalProcessId());
			System.out.println("relationship: " + objectID + " --wasGeneratedBy--> " + getClustalProcessId());		
		} else {		
			String searchProcessId = createSearchProcessInFedora(experimentId);
			System.out.println("Created 'Search' process: " + searchProcessId);
			
			String clustalProcessId = createClustalProcessInFedora(experimentId, searchProcessId);
			System.out.println("Created 'ClustalW2' process: " + clustalProcessId);		
			
			// Current ALN object ---wasGeneratedBy---> ClustalW2 Process
			objectRelationship.addRelationship(objectID, BrilRelationshipType.wasGeneratedBy.getRelation(), clustalProcessId);
			System.out.println("relationship: " + objectID + " --wasGeneratedBy--> " + clustalProcessId);
		}
		
		return objectRelationship;
	}

	/**
	 * Connects to and searches the repository with the query parameter 'ClustalProcess' on the title of DC
	 * 
	 * @param experimentId
	 * @return boolean result indicating if the clustal process for this experiment is present or not
	 */		
	public boolean checkForClustalProcess(String experimentId){
		boolean result = false;
		String expId = experimentId;
		//check if id doesnot have the prefix bril, add the prefix
		if (experimentId.lastIndexOf(':') == -1) {
			expId = "bril:"+experimentId;
		}
		Vector<String> test = generatorUtils.searchForObjectIds(expId, "title", "ClustalProcess");
		if (test.size() != 0 ) {
			for (int i=0; i<test.size(); i++) {
				String res = test.get(i);
				if (res != null) {
					clustalProcessId = res.substring(res.lastIndexOf("/") + 1);
				}
			}
			result = true;
		}
		return result;
	}
	
	public String getClustalProcessId(){
		return clustalProcessId;
	}
	
	/**
	 * Creates a Search process in the repository that consists of 
	 * 1) 'isPartOf' relationship to the experiment	 
	 * 2) 'wasControlledBy' relationship to the user (get this from the form?)
	 * 3) 'wasControlledBy' relationship to 'PDB website'
	 *
	 * @param experimentId
	 * @param objectID 
	 * @return 
	 * @return the identifier of the newly created Search process.
	 */		
	private String createSearchProcessInFedora(String experimentId) {
		
		String expId = experimentId;
		//check if id has the prefix bril, if true true take only the bit after 'bril:'
		if (experimentId.lastIndexOf(':') != -1){
			expId = experimentId.substring(experimentId.lastIndexOf(':') + 1);
		}
		String searchProcessID = "bril:process-" + IDGenerator.generateUUID();					
		ByteArrayOutputStream relsExt_baos = new ByteArrayOutputStream();		
		
		DublinCore digitalObjectDC = new DublinCore(searchProcessID);
		digitalObjectDC.setTitle("SearchProcess");
		digitalObjectDC.setDescription("Searching for similar protein sequences in PDB");
		//digitalObjectDC.setDate("", "dd/MM/yyyy HH:mm:ss");
		System.out.println("--------  END: Create Dublin code metadata created --------------");
		
		DatastreamObjectContainer dsc = new DatastreamObjectContainer(searchProcessID);
		dsc.addMetaData(digitalObjectDC);
		dsc.addDatastreamObject(DataStreamType.OriginalData,
			DatastreamMimeType.TEXT_XML.getMimeType(), "SearchProcess", "bril", null);
		
		/*
		 * create relationships*/
		FedoraRelsExt relsExt = null;
		try {
			relsExt = new FedoraRelsExt(FedoraNamespace.FEDORA.getURI() + searchProcessID);
			
			// Process ---isPartOf---> Experiment
			QName predicate = new QName(FedoraNamespace.FEDORARELSEXT.getURI(), "isPartOf", FedoraNamespace.FEDORA.getPrefix());
			QName object = new QName("", expId, FedoraNamespace.FEDORA.getURI() + FedoraNamespace.BRIL.getPrefix());				
			relsExt.addRelationship(predicate, object);	
			System.out.println("relationship: " + searchProcessID + " --isPartOf--> " + expId);
														
			// Process ---wasControlledBy---> User
			// TODO: need to get user name from experiment creation form
			predicate = new QName(FedoraNamespace.OPMV.getURI(), BrilRelationshipType.wasControlledBy.getRelation(), FedoraNamespace.OPMV.getPrefix());
			object = new QName("", "Stella Fabiane", "");					
			relsExt.addRelationship(predicate, object);
			System.out.println("relationship: " + searchProcessID + " --wasControlledBy--> Stella Fabiane");			
			
			// Process ---wasControlledBy---> PDB Website
			predicate = new QName(FedoraNamespace.OPMV.getURI(), BrilRelationshipType.wasControlledBy.getRelation(), FedoraNamespace.OPMV.getPrefix());
			object = new QName("", "PDB Website", "");					
			relsExt.addRelationship(predicate, object);
			System.out.println("relationship: " + searchProcessID + " --wasControlledBy--> PDB Website");
			
			relsExt.serialize(relsExt_baos, "");
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BrilTransformException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		byte[] relsExtByteArray = relsExt_baos.toByteArray();
		
		/*create datastream object container*/
		DatastreamObjectContainer dsc1 = new DatastreamObjectContainer(digitalObjectDC.getIdentifier());
		dsc1.addMetaData(digitalObjectDC);
		dsc1.addDatastreamObject(DataStreamType.OriginalData, DatastreamMimeType.TEXT_XML.getMimeType(), "SearchProcess", "bril", null);
		dsc1.addDatastreamObject(DataStreamType.RelsExt, DatastreamMimeType.APPLICATION_RDF.getMimeType(), "relationship", "bril", relsExtByteArray);
			 
		try {
			FedoraAdminstrationImpl fedoraAdmin = new FedoraAdminstrationImpl();
			fedoraAdmin.storeObject(dsc1);
		} catch (BrilObjectRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return searchProcessID;
	}

	/**
	 * Creates a ClustalW2 process in the repository that consists of 
	 * 1) 'isPartOf' relationship to the experiment	  
	 * 2) 'wasControlledBy' relationship to 'ClustalW2 Website'
	 * 3) 'wasTriggeredBy' relationship to Search Process
	 * 3) 'used' relationship to the input 
	 *
	 * @param experimentId
	 * @param objectID 
	 * @return 
	 * @return the identifier of the newly created CHAINSAW process.
	 */
	private String createClustalProcessInFedora(String experimentId, String searchProcessId) {
		
		String expId = experimentId;
		//check if id has the prefix bril, if true true take only the bit after 'bril:'
		if (experimentId.lastIndexOf(':') != -1){
			expId = experimentId.substring(experimentId.lastIndexOf(':') + 1);
		}
		String clustalProcessID = "bril:process-" + IDGenerator.generateUUID();
		ByteArrayOutputStream relsExt_baos = new ByteArrayOutputStream();		
		
		DublinCore digitalObjectDC = new DublinCore(clustalProcessID);
		digitalObjectDC.setTitle("ClustalProcess");
		digitalObjectDC.setDescription("Multiple sequence alignment by ClustalW2");
		//digitalObjectDC.setDate("", "dd/MM/yyyy HH:mm:ss");
		System.out.println("--------  END: Create Dublin code metadata created --------------");
		
		DatastreamObjectContainer dsc = new DatastreamObjectContainer(clustalProcessID);
		dsc.addMetaData(digitalObjectDC);
		dsc.addDatastreamObject(DataStreamType.OriginalData,
			DatastreamMimeType.TEXT_XML.getMimeType(), "ClustalProcess", "bril", null);
		
		/*
		 * create relationships*/
		FedoraRelsExt relsExt = null;
		try {
			relsExt = new FedoraRelsExt(FedoraNamespace.FEDORA.getURI() + clustalProcessID);
			
			// Process ---isPartOf---> Experiment
			QName predicate = new QName(FedoraNamespace.FEDORARELSEXT.getURI(), "isPartOf", FedoraNamespace.FEDORA.getPrefix());
			QName object = new QName("", expId, FedoraNamespace.FEDORA.getURI() + FedoraNamespace.BRIL.getPrefix());				
			relsExt.addRelationship(predicate, object);	
			System.out.println("relationship: " + clustalProcessID + " --isPartOf--> " + expId);		
												
			// Process ---wasControllededBy---> ClustalW2 Website
			predicate = new QName(FedoraNamespace.BRILRELS.getURI(), BrilRelationshipType.wasControlledBy.getRelation(), FedoraNamespace.BRILRELS.getPrefix());
			object = new QName("", "ClustalW2 Website", "");					
			relsExt.addRelationship(predicate, object);
			System.out.println("relationship: " + clustalProcessID + " --wasControlledBy--> ClustalW2 Website");
			
			// Process ---wasTriggeredBy---> Search Process
			predicate = new QName(FedoraNamespace.BRILRELS.getURI(), BrilRelationshipType.wasTriggeredBy.getRelation(), FedoraNamespace.BRILRELS.getPrefix());
			object = new QName("", searchProcessId, FedoraNamespace.FEDORA.getURI());					
			relsExt.addRelationship(predicate, object);
			System.out.println("relationship: " + clustalProcessID + " --wasTriggeredBy--> " + searchProcessId);
			
			relsExt.serialize(relsExt_baos, "");
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BrilTransformException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		byte[] relsExtByteArray = relsExt_baos.toByteArray();
		
		/*create datastream object container*/
		DatastreamObjectContainer dsc1 = new DatastreamObjectContainer(digitalObjectDC.getIdentifier());
		dsc1.addMetaData(digitalObjectDC);
		dsc1.addDatastreamObject(DataStreamType.OriginalData, DatastreamMimeType.TEXT_XML.getMimeType(), "ClustalProcess", "bril", null);
		dsc1.addDatastreamObject(DataStreamType.RelsExt, DatastreamMimeType.APPLICATION_RDF.getMimeType(), "relationship", "bril", relsExtByteArray);
			 
		try {
			FedoraAdminstrationImpl fedoraAdmin = new FedoraAdminstrationImpl();
			fedoraAdmin.storeObject(dsc1);
		} catch (BrilObjectRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return clustalProcessID;
	}

}
