package uk.ac.kcl.cerch.bril.relationship.generator;

import java.io.ByteArrayOutputStream;

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
import uk.ac.kcl.cerch.bril.service.uuid.IDGenerator;

/**
 * @author Eric Liao
 * 
 */
public class DOCFileRelationshipGeneratorImpl implements DOCFileRelationshipGenerator {

	private ObjectRelationship objectRelationship;

	@Override
	public ObjectRelationship generateRelationships(String objectID, String experimentId) {

		if (experimentId.contains("bril:") == false) {
			experimentId = "bril:" + experimentId;
		}
		
		objectRelationship = new ObjectRelationship();
		objectRelationship.addRelationship(objectID, "isPartOf", experimentId);
		
		/* Assume This document is chosen when the experiment is created and forms the root. */
		String splitProcessId = createSplitSequenceProcessInFedora(experimentId, objectID);
		System.out.println("Created 'Split Sequence' process: " + splitProcessId);		
		
		return objectRelationship;
	}

	/**
	 * Creates a split sequence process in the repository that consists of 
	 * 1) 'isPartOf' relationship to the experiment	 
	 * 2) 'used' relationship to the sequence document
	 * 3) 'wasControlledBy' relationship to the user (get this from the form?)
	 *
	 * @param experimentId
	 * @param objectID 
	 * @return 
	 * @return the identifier of the newly created split sequence process.
	 */
	private String createSplitSequenceProcessInFedora(String experimentId,
			String objectID) {
		
		String expId = experimentId;
		//check if id has the prefix bril, if true true take only the bit after 'bril:'
		if (experimentId.lastIndexOf(':') != -1){
			expId = experimentId.substring(experimentId.lastIndexOf(':') + 1);
		}
		
		String oId = objectID;
		//check if id has the prefix bril, if true true take only the bit after 'bril:'
		if (objectID.lastIndexOf(':') != -1){
			oId = objectID.substring(objectID.lastIndexOf(':') + 1);
		}
		
		String splitProcessID = "bril:process-" + IDGenerator.generateUUID();
		ByteArrayOutputStream relsExt_baos = new ByteArrayOutputStream();
		
		DublinCore digitalObjectDC = new DublinCore(splitProcessID);
		digitalObjectDC.setTitle("SplitSequenceProcess");
		digitalObjectDC.setDescription("Splitting sequence into searchable portions");
		//digitalObjectDC.setDate("", "dd/MM/yyyy HH:mm:ss");
		System.out.println("--------  END: Create Dublin code metadata created --------------");
		
		DatastreamObjectContainer dsc = new DatastreamObjectContainer(splitProcessID);
		dsc.addMetaData(digitalObjectDC);
		dsc.addDatastreamObject(DataStreamType.OriginalData,
			DatastreamMimeType.TEXT_XML.getMimeType(), "SplitSequenceProcess", "bril", null);
		
		/*
		 * create relationships*/
		FedoraRelsExt relsExt = null;
		try {
			relsExt = new FedoraRelsExt(FedoraNamespace.FEDORA.getURI() + splitProcessID);
			
			// Process ---isPartOf---> Experiment
			QName predicate = new QName(FedoraNamespace.FEDORARELSEXT.getURI(), "isPartOf", FedoraNamespace.FEDORA.getPrefix());
			QName object = new QName("", expId, FedoraNamespace.FEDORA.getURI() + FedoraNamespace.BRIL.getPrefix());				
			relsExt.addRelationship(predicate, object);	
			System.out.println("relationship: " + splitProcessID + " --isPartOf--> " + expId);
			
			// Process ---used---> Sequence Document
			predicate = new QName(FedoraNamespace.OPMV.getURI(), BrilRelationshipType.used.getRelation(), FedoraNamespace.OPMV.getPrefix());
			object = new QName("", oId, FedoraNamespace.FEDORA.getURI() + FedoraNamespace.BRIL.getPrefix());				
			relsExt.addRelationship(predicate, object);
			System.out.println("relationship: " + splitProcessID + " --used--> " + objectID);
									
			// Process ---wasControlledBy---> User
			// TODO: need to get user name from experiment creation form
			predicate = new QName(FedoraNamespace.OPMV.getURI(), BrilRelationshipType.wasControlledBy.getRelation(), FedoraNamespace.OPMV.getPrefix());
			object = new QName("", "Stella Fabiane", "");					
			relsExt.addRelationship(predicate, object);
			System.out.println("relationship: " + splitProcessID + " --wasControlledBy--> Stella Fabiane");			
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
		dsc1.addDatastreamObject(DataStreamType.OriginalData, DatastreamMimeType.TEXT_XML.getMimeType(), "SplitSequenceProcess", "bril", null);
		dsc1.addDatastreamObject(DataStreamType.RelsExt, DatastreamMimeType.APPLICATION_RDF.getMimeType(), "relationship", "bril", relsExtByteArray);
			 
		try {
			FedoraAdminstrationImpl fedoraAdmin = new FedoraAdminstrationImpl();
			fedoraAdmin.storeObject(dsc1);
		} catch (BrilObjectRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		return splitProcessID;
	}
}