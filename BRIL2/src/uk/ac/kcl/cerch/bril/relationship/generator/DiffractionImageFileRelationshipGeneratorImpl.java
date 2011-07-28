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

/**
 * @author Shri
 * @author Eric Liao
 * 
 */
public class DiffractionImageFileRelationshipGeneratorImpl implements DiffractionImageFileRelationshipGenerator {
	
	/*
	 * Generate relationships with a diffractionset object 
	 * that belongs to the experiment this object
	 * 
	 * check if a diffractionset object is present for this experiment:
	 * create one if not present.
	 * add isPartOf relationship to the diffractionSet
	 * need the experiment id here?
	 */
	
	private String diffractionImageSetObjectId;	
	
	private GeneratorUtils generatorUtils;
	private ObjectRelationship objectRelationship;
	/* image set object id
	 * 
	 * Generate relationships for a given diffractionImage object.
	 * 1. isPartOf: experiment object
	 * 2. isMemberOf: diffractionImageSet object: searches for diffractionImageSet object
	 * that belongs to this experiment id to add the relationship to the diffractionImage object.
	 * 3. used: integrateDiffractionImages process object: searches for integrateDiffractionImages object
	 * that belongs to this experiment id to add the relationship to the integrateDiffractionImages object.
	 *  
	 * (non-Javadoc)
	 * @see uk.ac.kcl.cerch.bril.relationship.generator.ObjectRelationshipGenerator#generateRelationships(java.lang.String, java.lang.String)
	 */
	public ObjectRelationship generateRelationships(String objectId, String experimentId) {
		generatorUtils = new GeneratorUtils();
		objectRelationship = new ObjectRelationship();
		
		// Diffraction Image ---isPartOf---> Experiment
		objectRelationship.addRelationship(objectId, "isPartOf", experimentId);	 
		
		if (checkForDiffractionImageSet(experimentId) == true) {
			// add diffraction image to existing image set
			System.out.println("existing diffimageset id: " + getDiffractionImageSetObjectId());
			// Diffraction Image ---isMemberOf---> Diffraction Image Set
			objectRelationship.addRelationship(objectId, "isMemberOf", getDiffractionImageSetObjectId());
			System.out.println("relationship: " + objectId + " --isMemberOf--> " + getDiffractionImageSetObjectId());
		}
		else
		{
			// create new diffraction image set and new process that uses the set and add current image to the set
			String diffImageSetId = createDiffractionImageSetObjectInFedora(experimentId);
			System.out.println("new diffimageset id: " + diffImageSetId);
			createIntegrateDiffractionImagesProcessInFedora(experimentId, diffImageSetId);
			// Diffraction Image ---isMemberOf---> Diffraction Image Set
			objectRelationship.addRelationship(objectId, "isMemberOf", diffImageSetId);				
			System.out.println("relationship: " + objectId + " --isMemberOf--> " + diffImageSetId);
		}				
		
		return objectRelationship;
	}
				
	/**
	 * Connects to and searches the repository with the query parameter 'DiffractionImageSet' on the title of DC
	 * 
	 * @param experimentId
	 * @return boolean result indicating if the diffraction image set object for this experiment is present or not
	 */		
	public boolean checkForDiffractionImageSet(String experimentId){
		boolean result = false;
		String expId = experimentId;
		//check if id doesnot have the prefix bril, add the prefix
		if (experimentId.lastIndexOf(':') == -1) {
			expId = "bril:"+experimentId;
		}
		Vector<String> test = generatorUtils.searchForObjectIds(expId, "title", "DiffractionImageSet");
		if (test.size() != 0 ) {
			for (int i=0; i<test.size(); i++) {
				String res = test.get(i);
				if (res != null) {
					diffractionImageSetObjectId = res.substring(res.lastIndexOf("/") + 1);
				}
			}
			result = true;
		}
		return result;
	}
	
	public String getDiffractionImageSetObjectId(){
		return diffractionImageSetObjectId;
	}
	
	/**
	 * Creates a diffraction image set object in the repository that consists of isPartOf relationship to the experiment object
	 * 
	 * @param experimentId
	 * @return the identifier of the newly created diffraction image set object.
	 */
	private String createDiffractionImageSetObjectInFedora(String experimentId){
		String expId = experimentId;
		//check if id has the prefix bril, if true true take only the bit after 'bril:'
		if (experimentId.lastIndexOf(':') != -1) {
			expId = experimentId.substring(experimentId.lastIndexOf(':') + 1);
		}
		String imageSetID = "bril:" + IDGenerator.generateUUID();
		ByteArrayOutputStream relsExt_baos = new ByteArrayOutputStream();
		
		DublinCore digitalObjectDC = new DublinCore(imageSetID);
		digitalObjectDC.setTitle("DiffractionImageSet");
		digitalObjectDC.setDescription("Diffraction Image Set");
		//digitalObjectDC.setDate("", "dd/MM/yyyy HH:mm:ss");
		System.out.println("--------  END: Create Dublin code metadata created --------------");
		
		DatastreamObjectContainer dsc = new DatastreamObjectContainer(imageSetID);
		dsc.addMetaData(digitalObjectDC);
		dsc.addDatastreamObject(DataStreamType.OriginalData,
				DatastreamMimeType.TEXT_XML.getMimeType(), "DiffractionImageSetObject",
				"bril", null);
		/*
		 * create relationships*/
		FedoraRelsExt relsExt = null;
		try{
			relsExt = new FedoraRelsExt(FedoraNamespace.FEDORA.getURI() + imageSetID);
			
			// Image Set ---isPartOf---> Experiment
			QName predicate = new QName( FedoraNamespace.FEDORARELSEXT.getURI(), "isPartOf", FedoraNamespace.FEDORA.getPrefix() );
			QName object = new QName( "", expId, FedoraNamespace.FEDORA.getURI() + FedoraNamespace.BRIL.getPrefix());			
			relsExt.addRelationship(predicate, object);	
			System.out.println("relationship: " + imageSetID + " --isPartOf--> " + expId);
						
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
		dsc1.addDatastreamObject(DataStreamType.OriginalData,DatastreamMimeType.TEXT_XML.getMimeType(), "DiffractionImageSet", "bril", null);
		dsc1.addDatastreamObject(DataStreamType.RelsExt, DatastreamMimeType.APPLICATION_RDF.getMimeType(), "relationship", "bril", relsExtByteArray);
	
		try {
			FedoraAdminstrationImpl fedoraAdmin = new FedoraAdminstrationImpl();
			fedoraAdmin.storeObject(dsc1);
		} catch (BrilObjectRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return imageSetID;
	}
	
	/**
	 * Creates an integrate diffraction images process in the repository that consists of 
	 * 1) isPart relationship to the experiment	 
	 * 2) used relationship to the diffraction image set
	 * 3) wasControlledBy relationship to Mosflm
	 *
	 * @param experimentId
	 * @param diffractionSetId 
	 * @return the identifier of the newly created integrate diffraction images process object.
	 */
	private String createIntegrateDiffractionImagesProcessInFedora(
			String experimentId, String diffractionSetId) {
		
		String expId = experimentId;
		//check if id has the prefix bril, if true true take only the bit after 'bril:'
		if (experimentId.lastIndexOf(':') != -1){
			expId = experimentId.substring(experimentId.lastIndexOf(':') + 1);
		}
		
		String diffId = diffractionSetId;
		if (diffractionSetId.lastIndexOf(':') != -1){
			diffId = diffractionSetId.substring(diffractionSetId.lastIndexOf(':') + 1);
		}
		
		String integrateProcessID = "bril:process-" + IDGenerator.generateUUID();
		ByteArrayOutputStream relsExt_baos = new ByteArrayOutputStream();
		
		DublinCore digitalObjectDC = new DublinCore(integrateProcessID);
		digitalObjectDC.setTitle("IntegrateDiffractionImagesProcess");
		digitalObjectDC.setDescription("Integrate Diffraction Images Process");
		//digitalObjectDC.setDate("", "dd/MM/yyyy HH:mm:ss");
		System.out.println("--------  END: Create Dublin code metadata created --------------");
		
		DatastreamObjectContainer dsc = new DatastreamObjectContainer(integrateProcessID);
		dsc.addMetaData(digitalObjectDC);
		dsc.addDatastreamObject(DataStreamType.OriginalData,
				DatastreamMimeType.TEXT_XML.getMimeType(), "IntegrateDiffractionImagesProcess",
				"bril", null);
		/*
		 * create relationships*/
		FedoraRelsExt relsExt = null;
		try {
			relsExt = new FedoraRelsExt(FedoraNamespace.FEDORA.getURI() + integrateProcessID);
			
			// Process ---isPartOf---> Experiment
			QName predicate = new QName(FedoraNamespace.FEDORARELSEXT.getURI(), "isPartOf", FedoraNamespace.FEDORA.getPrefix() );
			QName object = new QName("", expId, FedoraNamespace.FEDORA.getURI() + FedoraNamespace.BRIL.getPrefix());				
			relsExt.addRelationship(predicate, object);	
			System.out.println("relationship: " + integrateProcessID + " --isPartOf--> " + expId);
			
			// Process ---used---> Diffraction Image Set
			predicate = new QName( FedoraNamespace.OPMV.getURI(), BrilRelationshipType.used.getRelation(), FedoraNamespace.OPMV.getPrefix() );
			object = new QName("", diffId, FedoraNamespace.FEDORA.getURI() + FedoraNamespace.BRIL.getPrefix());				
			relsExt.addRelationship(predicate, object);
			System.out.println("relationship: " + integrateProcessID + " --used--> " + diffractionSetId);
									
			// Process ---wasControlledBy---> Mosflm
			predicate = new QName( FedoraNamespace.OPMV.getURI(), BrilRelationshipType.wasControlledBy.getRelation(), FedoraNamespace.OPMV.getPrefix() );
			object = new QName( "", "Mosflm", "" );					
			relsExt.addRelationship(predicate, object);
			System.out.println("relationship: " + integrateProcessID + " --wasControlledBy--> Mosflm");
			
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
		dsc1.addDatastreamObject(DataStreamType.OriginalData, DatastreamMimeType.TEXT_XML.getMimeType(), "IntegrateDiffractionImagesProcess", "bril", null);
		dsc1.addDatastreamObject(DataStreamType.RelsExt, DatastreamMimeType.APPLICATION_RDF.getMimeType(), "relationship", "bril", relsExtByteArray);
			 
		try {
			FedoraAdminstrationImpl fedoraAdmin = new FedoraAdminstrationImpl();
			fedoraAdmin.storeObject(dsc1);
		} catch (BrilObjectRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		return integrateProcessID;
	}
}