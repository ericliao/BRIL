package uk.ac.kcl.cerch.bril.relationship.generator;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import uk.ac.kcl.cerch.bril.ccp4.TaskObjectVector;
import uk.ac.kcl.cerch.bril.characteriser.TaskObjectElement;
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
import uk.ac.kcl.cerch.bril.fileformat.CrystallographyObjectType;
import uk.ac.kcl.cerch.bril.relationship.ObjectRelationship;
import uk.ac.kcl.cerch.bril.relationship.Relationship;
import uk.ac.kcl.cerch.bril.relationship.common.GeneratorUtils;
import uk.ac.kcl.cerch.bril.service.uuid.IDGenerator;
import uk.ac.kcl.cerch.soapi.objectstore.ObjectStoreException;

/**
 * @author Eric Liao
 * 
 */
public class COOTScmFileRelationshipGeneratorImpl implements COOTScmFileRelationshipGenerator{
	private ObjectRelationship objectRelationship;
	private GeneratorUtils generatorUtils;	
	private String objectId;
	@Override
	public ObjectRelationship generateRelationships(String objectID,
			String experimentId) {
		if (!experimentId.contains("bril:")) {
			experimentId = "bril:" + experimentId;
		}
		objectRelationship = new ObjectRelationship();
		this.objectId = objectID;
		generatorUtils = new GeneratorUtils();
			
		objectRelationship.addRelationship(objectID, "isPartOf", experimentId);

		try {
			/*
			 * Set the DC title and date of this DEF file object
			 */
			generatorUtils.searchForArchivalObjects(objectId);
		} catch (ObjectStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String objectPath = generatorUtils.getObjectTitle();
		
		String metadata = generatorUtils.getFileCharacterisationMetadata();
		if (metadata != null) {
			byte[] data = metadata.getBytes();
			Vector<String> inputNameList = generatorUtils
					.getFilenamesFromTaskXML(data, TaskObjectElement.INPUT_FILENAME);
			Vector<String> outputNameList = generatorUtils
					.getFilenamesFromTaskXML(data, TaskObjectElement.OUTPUT_FILENAME);
			
			Vector<String> inputObjectIdVector = null;
			Vector<String> outputObjectIdVector = null;
			
			System.out.println("coot scm inputs: (" + inputNameList + ")");
			if (inputNameList.size() != 0) {
				inputObjectIdVector = getFileObjectIds(inputNameList, experimentId);			
				System.out.println("coot scm input ids: (" + inputObjectIdVector + ")");
			}
			
			System.out.println("coot scm outputs: (" + outputNameList + ")");
			if (outputNameList.size() != 0) {
				outputObjectIdVector = getFileObjectIds(outputNameList, experimentId);
				System.out.println("coot scm output ids: (" + outputObjectIdVector + ")");
			}
			
			if (!objectPath.contains("coot.state")) {
				
				// Create Coot process and add 'used' relationship to all input files
				String cootProcessId = createCootProcessInFedora(experimentId, inputNameList);
				System.out.println("Created 'Coot' process: " + cootProcessId);					
				
				// Current SCM object ---wasGeneratedBy---> Coot Process
				this.objectRelationship.addRelationship(objectId, BrilRelationshipType.wasGeneratedBy.getRelation(), cootProcessId);
				System.out.println("relationship: " + objectId + " --wasGeneratedBy--> " + cootProcessId);
								
				// Add 'wasGeneratedBy' relationship between the Coot process and its outputs
				// Add 'wasDerivedFrom' relationship between Coot input and Coot outputs
				if (outputObjectIdVector != null) {
					for (int i = 0; i < outputObjectIdVector.size(); i++) {
						String outputObjectId = outputObjectIdVector.get(i);
						addRelationshipToOtherFedoraObject(outputObjectId, BrilRelationshipType.wasGeneratedBy.getRelation(), 
								cootProcessId, FedoraNamespace.OPMV.getURI());
						System.out.println("relationship: " + outputObjectId + " --wasGeneratedBy--> " + cootProcessId);
						for (int j = 0; j < inputObjectIdVector.size(); j++) {
							String intputObjectId = inputObjectIdVector.get(j);
							addRelationshipToOtherFedoraObject(outputObjectId, BrilRelationshipType.wasDerivedFrom.getRelation(), 
									intputObjectId, FedoraNamespace.OPMV.getURI());
							System.out.println("relationship: " + outputObjectId + " --wasDerivedFrom--> " + intputObjectId);
						}
					}			
				}
			}
		}		
		return objectRelationship;
	}
		
	/**
	 * Creates a Coot process in the repository that consists of 
	 * 1) 'isPartOf' relationship to the experiment	 
	 * 2) 'used' relationship to the input files
	 * 3) 'wasControlledBy' relationship to the user (get this from the form?)
	 * 4) 'wasControlledBy' relationship to 'Coot'
	 *
	 * @param experimentId
	 * @param inputNameList 
	 * 
	 * @return the identifier of the newly created Coot process.
	 */	
	private String createCootProcessInFedora(String experimentId, Vector<String> inputNameList) {
		
		String expId = experimentId;		
		//check if id has the prefix bril, if true true take only the bit after 'bril:'
		if (experimentId.lastIndexOf(':') != -1){
			expId = experimentId.substring(experimentId.lastIndexOf(':') + 1);
		}
		
		String cootProcessID = "bril:process-" + IDGenerator.generateUUID();
		ByteArrayOutputStream relsExt_baos = new ByteArrayOutputStream();
		
		DublinCore digitalObjectDC = new DublinCore(cootProcessID);
		digitalObjectDC.setTitle("CootProcess");
		digitalObjectDC.setDescription("Visual analysis of coordinate data using Coot");
		//digitalObjectDC.setDate("", "dd/MM/yyyy HH:mm:ss");
		System.out.println("-------- END: Create Dublin code metadata created --------------");
		
		DatastreamObjectContainer dsc = new DatastreamObjectContainer(cootProcessID);
		dsc.addMetaData(digitalObjectDC);
		dsc.addDatastreamObject(DataStreamType.OriginalData,
			DatastreamMimeType.TEXT_XML.getMimeType(), "PhaserProcess", "bril", null);
				
		/*
		 * create relationships*/
		FedoraRelsExt relsExt = null;
		try {
			relsExt = new FedoraRelsExt(FedoraNamespace.FEDORA.getURI() + cootProcessID);
			
			// Process ---isPartOf---> Experiment
			QName predicate = new QName(FedoraNamespace.FEDORARELSEXT.getURI(), "isPartOf", FedoraNamespace.FEDORA.getPrefix());
			QName object = new QName("", expId, FedoraNamespace.FEDORA.getURI() + FedoraNamespace.BRIL.getPrefix());				
			relsExt.addRelationship(predicate, object);	
			System.out.println("relationship: " + cootProcessID + " --isPartOf--> " + expId);
								
			// Process ---used---> input files													
			Vector<String> inputObjectIdVector = generatorUtils.getInputFileObjectIds(inputNameList, experimentId);
			for (int i = 0; i < inputObjectIdVector.size(); i++) {
				String inputObjectId = inputObjectIdVector.get(i).toString();
				if (inputObjectId.contains("/")) {
					inputObjectId = inputObjectId.substring(inputObjectId.lastIndexOf("/") + 1);
				}
				if (inputObjectId.contains(":")) {
					inputObjectId = inputObjectId.substring(inputObjectId.lastIndexOf(":") + 1);
				}
				predicate = new QName(FedoraNamespace.OPMV.getURI(), BrilRelationshipType.used.getRelation(), FedoraNamespace.OPMV.getPrefix());
				object = new QName("", inputObjectId, FedoraNamespace.FEDORA.getURI() + FedoraNamespace.BRIL.getPrefix());				
				relsExt.addRelationship(predicate, object);				
				System.out.println("relationship: " + cootProcessID + " --used--> " + inputObjectId);
			}

			// Process ---wasControlledBy---> User
			// TODO: need to get user name from experiment creation form
			predicate = new QName(FedoraNamespace.OPMV.getURI(), BrilRelationshipType.wasControlledBy.getRelation(), FedoraNamespace.OPMV.getPrefix());
			object = new QName("", "Stella Fabiane", "");					
			relsExt.addRelationship(predicate, object);
			System.out.println("relationship: " + cootProcessID + " --wasControlledBy--> Stella Fabiane");			
			
			// Process ---wasControlledBy---> CCP4I
			predicate = new QName(FedoraNamespace.OPMV.getURI(), BrilRelationshipType.wasControlledBy.getRelation(), FedoraNamespace.OPMV.getPrefix());
			object = new QName("", "Coot", "");					
			relsExt.addRelationship(predicate, object);
			System.out.println("relationship: " + cootProcessID + " --wasControlledBy--> Coot");
			
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
		dsc1.addDatastreamObject(DataStreamType.OriginalData, DatastreamMimeType.TEXT_XML.getMimeType(), "CootProcess", "bril", null);
		dsc1.addDatastreamObject(DataStreamType.RelsExt, DatastreamMimeType.APPLICATION_RDF.getMimeType(), "relationship", "bril", relsExtByteArray);
			 
		try {
			FedoraAdminstrationImpl fedoraAdmin = new FedoraAdminstrationImpl();
			fedoraAdmin.storeObject(dsc1);
		} catch (BrilObjectRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return cootProcessID;
	}
	
	/**
	 * Adds rels-exts to other objects in the repository.
	 * 
	 * @param subj object id to which the rels-ext will be added
	 * @param pred predicate or relationship
	 * @param obj object id or literal value for the subj
	 * @param namespaceURI_predicate namespace for the predicate
	 */
	private void addRelationshipToOtherFedoraObject(String subj, String pred, String obj, String namespaceURI_predicate) {

		String brilPrefix = FedoraNamespace.BRIL.getPrefix();
		String fedoraURI = FedoraNamespace.FEDORA.getURI();
		FedoraAdminstrationImpl fedoraAdmin;
		boolean isLiteral = false;

		try {
			// subj must be= info:fedora/bril:6823283273
			System.out.println("current subject: " + subj);
			fedoraAdmin = new FedoraAdminstrationImpl();
			if (subj.contains("bril:") == false) {
				subj = brilPrefix + ":" + subj;
			}			
			if (subj.contains("/")) {
				subj = subj.substring(subj.lastIndexOf("/") + 1);
			}
			
			if (obj.contains("/")) {
				obj = obj.substring(obj.lastIndexOf("/") + 1);
			}
			
			String subject = fedoraURI + subj;
			String predicate = namespaceURI_predicate + pred;
			String object = fedoraURI + obj;
			System.out.println("current object: " + obj);			
			
			if (fedoraAdmin.hasObject(subj) == true) {
				System.out.println("this id is present: " + subject + ", " + object);
				fedoraAdmin.addObjectRelation(subject, predicate, object, isLiteral);
				System.out.println("Relationship added: " + subject + " -> " + predicate + " -> " + object);
			} else {
				System.out.println("Cant find this Id: " + subject);
			}
		} catch (BrilObjectRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private Vector<String> getFileObjectIds(Vector<String> nameList, String experimentId) {
		System.out.println("========== Start Search for files names in repository and get ids ===========");
		Vector<String> objectIds = new Vector<String>();

		System.out.println("Filenames: " + nameList);
		for (int o = 0; o < nameList.size(); o++) {
			String filename = nameList.get(o);

			String suffix = filename.substring(filename.lastIndexOf(".") + 1);
			System.out.println(filename);

			if (suffix.equals("pdb")) {
				System.out.println("===== PDB ====");
				Map<String, Vector<String>> pdbObjectIdTitle = generatorUtils
						.searchForObjectIdsTitle(experimentId, CrystallographyObjectType.CoordinateFile);

				// get the object's filename in title = filename
				for (Map.Entry<String, Vector<String>> ent : pdbObjectIdTitle.entrySet()) {

					String path = ent.getValue().get(0);
					String pdbFileName = null;
					String inputPDBName = null;
					char slash1 = '/';
					char slash2 = '\\';
					if (path.lastIndexOf(slash1) != -1) {
						pdbFileName = path.substring(path.lastIndexOf(slash1) + 1);		
						inputPDBName = filename.substring(filename.lastIndexOf(slash1) + 1);
					}

					if (path.lastIndexOf(slash2) != -1) {
						pdbFileName = path.substring(path.lastIndexOf(slash2) + 1);
						inputPDBName = filename.substring(filename.lastIndexOf(slash2) + 1);
					}

					if (inputPDBName.equals(pdbFileName)) {
						objectIds.add(ent.getKey());
					}
				}
			}
			else if (suffix.equals("mtz")) {
				System.out.println("===== MTZ ====");
				Map<String, Vector<String>> mtzObjectIdTitle = generatorUtils
						.searchForObjectIdsTitle(experimentId, CrystallographyObjectType.MTZReflectionFile);

				// get the object's filename in title = filename
				for (Map.Entry<String, Vector<String>> ent : mtzObjectIdTitle.entrySet()) {

					String path = ent.getValue().get(0);
					String mtzFileName = null;
					String inputMTZName = null;
					char slash1 = '/';
					char slash2 = '\\';
					if (path.lastIndexOf(slash1) != -1) {
						mtzFileName = path.substring(path.lastIndexOf(slash1) + 1);		
						inputMTZName = filename.substring(filename.lastIndexOf(slash1) + 1);
					}

					if (path.lastIndexOf(slash2) != -1) {
						mtzFileName = path.substring(path.lastIndexOf(slash2) + 1);
						inputMTZName = filename.substring(filename.lastIndexOf(slash2) + 1);
					}
					
					if (inputMTZName.equals(mtzFileName)) {
						objectIds.add(ent.getKey());
					}
				}
			}
		}
		System.out.println("========== End Search for files names in repository ===========");

		return objectIds;
	}
}
