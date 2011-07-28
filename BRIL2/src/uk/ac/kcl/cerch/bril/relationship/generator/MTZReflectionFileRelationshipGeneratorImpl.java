package uk.ac.kcl.cerch.bril.relationship.generator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import uk.ac.kcl.cerch.bril.ccp4.TaskObject;
import uk.ac.kcl.cerch.bril.ccp4.TaskObjectVector;
import uk.ac.kcl.cerch.bril.characteriser.TaskObjectElement;
import uk.ac.kcl.cerch.bril.common.fedora.BrilObjectRepositoryException;
import uk.ac.kcl.cerch.bril.common.fedora.FedoraAdminstrationImpl;
import uk.ac.kcl.cerch.bril.common.fedora.FedoraRelsExt;
import uk.ac.kcl.cerch.bril.common.fedora.FedoraNamespaceContext.FedoraNamespace;
import uk.ac.kcl.cerch.bril.common.metadata.DublinCore;
import uk.ac.kcl.cerch.bril.common.types.BrilRelationshipType;
import uk.ac.kcl.cerch.bril.common.types.BrilTransformException;
import uk.ac.kcl.cerch.bril.common.types.CCP4ProcessType;
import uk.ac.kcl.cerch.bril.common.types.DataStreamType;
import uk.ac.kcl.cerch.bril.common.types.DatastreamMimeType;
import uk.ac.kcl.cerch.bril.common.types.DatastreamObjectContainer;
import uk.ac.kcl.cerch.bril.common.util.FileUtil;
import uk.ac.kcl.cerch.bril.fileformat.CrystallographyObjectType;
import uk.ac.kcl.cerch.bril.relationship.ObjectRelationship;
import uk.ac.kcl.cerch.bril.relationship.common.GeneratorUtils;
import uk.ac.kcl.cerch.bril.service.uuid.IDGenerator;
import uk.ac.kcl.cerch.soapi.objectstore.ObjectStoreException;

/**
 * @author Shri
 * @author Eric Liao
 * 
 */
public class MTZReflectionFileRelationshipGeneratorImpl implements
		MTZReflectionFileRelationshipGenerator {

	private String diffractionImageSetObjectId;
	private String integrateDiffractionImagesProcessId;
	private GeneratorUtils generatorUtils;
	private ObjectRelationship objectRelationship;
	private String objectIdentifier;
	private String experimentId;
	private String reflectionType;
	private String phaserProcessId;
	private String searchProcessId;
	
	/* 
	 * @see
	 * uk.ac.kcl.cerch.bril.relationship.generator.ObjectRelationshipGenerator
	 * #generateRelationships(java.lang.String, java.lang.String)
	 */
	public ObjectRelationship generateRelationships(String objectId,
			String experimentId) {
		generatorUtils = new GeneratorUtils();
		this.objectIdentifier = objectId;
		if(experimentId.contains("bril:")==false){
			experimentId = "bril:" + experimentId;
		}		
		this.experimentId = experimentId;		
		this.objectRelationship = new ObjectRelationship();

		objectRelationship.addRelationship(objectId, "isPartOf", experimentId);
	
		try {
			/**
			 * Search in the object store for this object's DC data that sets
			 * title and date values to the setter methods in the GeneratorUtils
			 */
			generatorUtils.searchForArchivalObjects(objectId);
		
		} catch (ObjectStoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// get the XML metadata created and stored as objectartifact for this mtz file object
		String metadata = generatorUtils.getFileCharacterisationMetadata();
		if (metadata != null) {
			//set the reflection_type element value in the XML to reflectionType 
			setReflectionType(metadata);	
		}

		boolean relationshipsCreated = false;
	
		processCOMFile(objectId, experimentId);

		relationshipsCreated = generatorUtils.hasRelationship(objectRelationship, BrilRelationshipType.wasDerivedFrom.getRelation());
		System.out.println("Relationships Created: "+ relationshipsCreated);
						
		/**
		 * Look for the CCP4I DEF files -database and task def files that is created
		 * before this PDB file	
		 * 
		 * */
		if (relationshipsCreated == false) {
			System.out.println("Looking for CCP4 def file ..... ");
			Map<String, Long> sortedDEFObjectMap = generatorUtils
					.getRepositoryDescSortedResult(experimentId,CrystallographyObjectType.CCP4IDefFile);

			if (sortedDEFObjectMap.size() != 0) {
				processCCP4IDefFile(objectId, experimentId, sortedDEFObjectMap);
			}
			relationshipsCreated = generatorUtils.hasRelationship(
					objectRelationship, BrilRelationshipType.wasDerivedFrom.getRelation());
			System.out.println("Relationships Created: "+ relationshipsCreated);
		}
		
		/**
		 * Look for the Phenix DEF file that is created before this MTZ file
		 * 
		 * */	
		if (relationshipsCreated == false) {			
			processPHENIXDefFile(objectId, experimentId);
			// check if wasDerivedForm relations is generated
			relationshipsCreated = generatorUtils.hasRelationship(
					this.objectRelationship, BrilRelationshipType.wasDerivedFrom.getRelation());
			System.out.println("Relationships Created: "+ relationshipsCreated);
		}
		
		if (relationshipsCreated == false) {
			
			// Must be the MET reflection files generated through the diffraction imageSet
			if (hasDiffractionImageSetObjectInRepository(experimentId) == true) {
								
				if (getReflectionType().trim().equals("unmerged")) {
					// Current MTZ object ---wasDerivedFrom---> Diffraction Image Set
					this.objectRelationship.addRelationship(objectId, BrilRelationshipType.wasDerivedFrom.getRelation(),
						getDiffractionImageSetObjectId());
					System.out.println("relationship: " + objectId + " --wasDerivedFrom--> " + getDiffractionImageSetObjectId());
					
					if (checkForIntegrateDiffractionImagesProcess(experimentId) == true) {
						// Current MTZ object ---wasGeneratedBy---> Integrate Diffraction Image Process
						this.objectRelationship.addRelationship(objectId, BrilRelationshipType.wasGeneratedBy.getRelation(),
								getIntegrateDiffractionImagesProcessId());
						System.out.println("relationship: " + objectId + " --wasGeneratedBy--> " + getIntegrateDiffractionImagesProcessId());
					}
				}
			}
		}
	
		// END

		return objectRelationship;

	}
	
	/**
	 * Connects to and searches the repository with the query parameter 'IntegrateDiffractionImagesProcess' on the title of DC
	 * 
	 * @param experimentId
	 * @return boolean result indicating if the integrate diffraction images process for this experiment is present or not
	 */		
	public boolean checkForIntegrateDiffractionImagesProcess(String experimentId){
		boolean result = false;
		String expId = experimentId;
		//check if id doesnot have the prefix bril, add the prefix
		if (experimentId.lastIndexOf(':') == -1) {
			expId = "bril:"+experimentId;
		}
		Vector<String> test = generatorUtils.searchForObjectIds(expId, "title", "IntegrateDiffractionImagesProcess");
		if (test.size() != 0 ) {
			for (int i=0; i<test.size(); i++) {
				String res = test.get(i);
				if (res != null) {
					integrateDiffractionImagesProcessId = res.substring(res.lastIndexOf("/") + 1);
				}
			}
			result = true;
		}
		return result;
	}
	
	public String getIntegrateDiffractionImagesProcessId(){
		return integrateDiffractionImagesProcessId;
	}
	
	/**
	 * Get the COM file from that was created before this current MTZ file.
	 * From the metadata of the COM file datastream, search for the output file and 
	 * if this is same as the current MTZ file, it adds the relationships to the COM file and the inputs and taskname.
	 * 
	 * @param objectId Identifier of the current MTZ object
	 * @param experimentId experiment identifier to which this MTZ object belongs to
	 * @param objectRelationship ObjectRelationship object that would hold the relationship triples
	 */
	private void processCOMFile(String objectId, String experimentId){
		System.out.println("COM process.....");
		Map<String, Long> returnedSortedCOMFileObjectIds = generatorUtils
				.getRepositoryDescSortedResult(experimentId, CrystallographyObjectType.COMFile);
		System.out.println("com process file returned.....");
		String objectDate = generatorUtils.getObjectDate();
		long date1 = uk.ac.kcl.cerch.bril.common.util.DateTime.getLongDateTime(
				objectDate, "dd/MM/yyyy HH:mm:ss");

		if (returnedSortedCOMFileObjectIds.size() != 0) {
			System.out.println("COM file is retrieved from the repository");
			for (Map.Entry<String, Long> entry : returnedSortedCOMFileObjectIds
					.entrySet()) {
				long date2 = Long.valueOf(entry.getValue());
				long dateDifference = date1 - date2;
				String diffvalue = String.valueOf(dateDifference);
				int gotId = 0;
				if (diffvalue.contains("-") == false && gotId == 0) {
					gotId++;
					
					String selectedCOMObjectId = entry.getKey();
					System.out.println("COM file id: " + selectedCOMObjectId);
					
					if (selectedCOMObjectId.contains("/")) {
						selectedCOMObjectId = selectedCOMObjectId
								.substring(selectedCOMObjectId.lastIndexOf("/") + 1); 
						// remove
						// info:fedora/
						// from
						// the
						// object
						// id
					}
					System.out.println("selectedCOMObjectId: " + selectedCOMObjectId);
					String comObjectPath = generatorUtils.getOriginalPathOfObjectInRepository(selectedCOMObjectId);
					System.out.println("comObjectPath: " + comObjectPath);
					char slash1 = '/';
					char slash2 = '\\';
					if (comObjectPath.lastIndexOf(slash1) != -1 ) {
						comObjectPath = comObjectPath.substring(0, comObjectPath.lastIndexOf(slash1));
					}
					if (comObjectPath.lastIndexOf(slash2) != -1 ) {
						comObjectPath = comObjectPath.substring(0, comObjectPath.lastIndexOf(slash2));
					}
					System.out.println("comObjectPath: " + comObjectPath);
					
					byte[] comFileMetadata = generatorUtils.getDatastreamType(
							selectedCOMObjectId, DataStreamType.ObjectMetadata);					
					
					/*
					 * Get outputs and taskname from the returned
					 * metadata of the COM file
					 */					
					Vector<String> outputNameList = generatorUtils
							.getFilenamesFromTaskXML(comFileMetadata, TaskObjectElement.OUTPUT_FILENAME);
					
					System.out.println("outputNameList: " + outputNameList);
					
					for (int out = 0; out < outputNameList.size(); out++) {
						String outputFilename = outputNameList.get(out);
						String suffix = outputFilename.substring(outputFilename.lastIndexOf(".") + 1);
						
						if (suffix.equals("mtz")) {

							// output file may not be present in the repository
							String originalPathOfCurrentMTZFile = generatorUtils
									.getObjectTitle();
							String currentMTZFile = null;
							
							if (originalPathOfCurrentMTZFile.lastIndexOf(slash2) != -1) {
								currentMTZFile= originalPathOfCurrentMTZFile
									.substring(originalPathOfCurrentMTZFile.lastIndexOf(slash2) + 1);
							}
							
							if (originalPathOfCurrentMTZFile.lastIndexOf(slash1) != -1) {
								currentMTZFile= originalPathOfCurrentMTZFile
									.substring(originalPathOfCurrentMTZFile.lastIndexOf(slash1) + 1);
							}
							
							System.out.println("outputFilename: " + outputFilename);
							System.out.println("currentMTZFile: " + currentMTZFile);
						
							if (outputFilename.equals(currentMTZFile)) {
								
								System.out.println("An output filename (in COM file) is equal to the current object's filename: Creating new process"
										+ " and adding 'wasDerivedFrom' to the object and 'wasGeneratedBy' and 'used' relationships to the process ............");
																
								// get inputs from the COM file								
								Vector<String> inputNameList = generatorUtils.getFilenamesFromTaskXML(comFileMetadata, TaskObjectElement.INPUT_FILENAME);
								System.out.println("inputNameList: " + inputNameList);
								
								// Get taskname from the COM file metadata XML
								String taskName = generatorUtils.getValueFromTaskXML(comFileMetadata, TaskObjectElement.TASK_NAME);
								String CCP4ProcessId = createCCP4ProcessInFedora(experimentId, taskName, selectedCOMObjectId, inputNameList);								
								
								// Current MTZ object ---wasGeneratedBy---> Process 
								this.objectRelationship.addRelationship(objectId, BrilRelationshipType.wasGeneratedBy.getRelation(), CCP4ProcessId);																													
								System.out.println("relationship: " + objectId + " --wasGeneratedBy--> " + CCP4ProcessId);
																								
								// Current MTZ object ---wasDerivedFrom---> MTZ  
								Vector<String> inputObjectIdVector = generatorUtils.getInputFileObjectIds(inputNameList, experimentId);
								System.out.println("input object ids: " + inputObjectIdVector);
								addToRelationshipToCurrentObject(objectId, inputObjectIdVector);															
							} 
						}						
					}
				}
			}
		}	
	}
	
	/**
	 * Creates a CCP4 process in the repository that consists of isPart relationship to the experiment	 
	 *
	 * @param experimentId experiment identifier to which this process belongs to
	 * @param taskName The name of the CCP4 process
	 * @param selectedCOMObjectId The COM object used by this process
	 * @param inputNameList List of input files used by this process
	 * @return the identifier of the newly created CCP4 process.
	 */
	private String createCCP4ProcessInFedora(String experimentId, String taskName, String selectedCOMObjectId, Vector<String> inputNameList) {
		String expId = experimentId;
		
		//check if id has the prefix bril, if true take only the bit after 'bril:'
		if (experimentId.lastIndexOf(':') != -1){
			expId = experimentId.substring(experimentId.lastIndexOf(':') + 1);
		}
		
		String comId = selectedCOMObjectId;
		if (selectedCOMObjectId.lastIndexOf(':') != -1){
			comId = selectedCOMObjectId.substring(selectedCOMObjectId.lastIndexOf(':') + 1);
		}
		
		String CCP4ProcessID = "bril:process-" + IDGenerator.generateUUID();
		ByteArrayOutputStream relsExt_baos = new ByteArrayOutputStream();
		
		DublinCore digitalObjectDC = new DublinCore(CCP4ProcessID);
		
		CCP4ProcessType process = CCP4ProcessType.valueOf(taskName);		
		digitalObjectDC.setTitle("CCP4 Process: " + process.getTask());		
		digitalObjectDC.setDescription(process.getDescription());
		
		System.out.println("--------  END: Create Dublin code metadata created --------------");
		
		DatastreamObjectContainer dsc = new DatastreamObjectContainer(CCP4ProcessID);
		dsc.addMetaData(digitalObjectDC);
		dsc.addDatastreamObject(DataStreamType.OriginalData,
				DatastreamMimeType.TEXT_XML.getMimeType(), "CCP4Process",
				"bril", null);
		/*
		 * create relationships */
		FedoraRelsExt relsExt = null;
		try {
			relsExt = new FedoraRelsExt(FedoraNamespace.FEDORA.getURI() + CCP4ProcessID);
			
			// Process ---isPartOf---> Experiment
			QName predicate = new QName(FedoraNamespace.FEDORARELSEXT.getURI(), "isPartOf", FedoraNamespace.FEDORA.getPrefix());
			QName object = new QName("", expId, FedoraNamespace.FEDORA.getURI() + FedoraNamespace.BRIL.getPrefix());				
			relsExt.addRelationship(predicate, object);	
			System.out.println("relationship: " + CCP4ProcessID + " --isPartOf--> " + expId);
												
			// Process ---used---> COM file
			predicate = new QName(FedoraNamespace.OPMV.getURI(), BrilRelationshipType.used.getRelation(), FedoraNamespace.OPMV.getPrefix());
			object = new QName("", comId, FedoraNamespace.FEDORA.getURI() + FedoraNamespace.BRIL.getPrefix());				
			relsExt.addRelationship(predicate, object);
			System.out.println("relationship: " + CCP4ProcessID + " --used--> " + selectedCOMObjectId);
			
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
				System.out.println("relationship: " + CCP4ProcessID + " --used--> " + inputObjectId);
			}						
			
			// Process ---wasControlledBy---> User
			// TODO: need to get user name from experiment creation form
			predicate = new QName(FedoraNamespace.OPMV.getURI(), BrilRelationshipType.wasControlledBy.getRelation(), FedoraNamespace.OPMV.getPrefix());
			object = new QName("", "Stella Fabiane", "");					
			relsExt.addRelationship(predicate, object);
			System.out.println("relationship: " + CCP4ProcessID + " --wasControlledBy--> Stella Fabiane");
			
			// Process ---wasControlledBy---> CCP4
			predicate = new QName( FedoraNamespace.OPMV.getURI(), BrilRelationshipType.wasControlledBy.getRelation(), FedoraNamespace.OPMV.getPrefix() );
			object = new QName( "", "CCP4", "" );					
			relsExt.addRelationship(predicate, object);
			System.out.println("relationship: " + CCP4ProcessID + " --wasControlledBy--> CCP4");
						
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
		dsc1.addDatastreamObject(DataStreamType.OriginalData, DatastreamMimeType.TEXT_XML.getMimeType(), "CCP4Process", "bril", null);
		dsc1.addDatastreamObject(DataStreamType.RelsExt, DatastreamMimeType.APPLICATION_RDF.getMimeType(), "relationship", "bril", relsExtByteArray);
			 
		try {
			FedoraAdminstrationImpl fedoraAdmin = new FedoraAdminstrationImpl();
			fedoraAdmin.storeObject(dsc1);
		} catch (BrilObjectRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		return CCP4ProcessID;
	}

	/**
	 * Creates a phaser process in the repository that consists of 
	 * 1) 'isPartOf' relationship to the experiment	 
	 * 2) 'used' relationship to the DEF file
	 * 3) 'used' relationship to the found DEF file
	 * 3) 'wasControlledBy' relationship to the user (get this from the form?)
	 * 4) 'wasControlledBy' relationship to 'CCP4I'
	 *
	 * @param experimentId
	 * @param objectID
	 * @param inputObjectID 
	 * 
	 * @return the identifier of the newly created phaser process.
	 */	
	private String createPhaserProcessInFedora(String experimentId, String objectId, String DEFObjectId, 
			String foundDEFObjectId, Vector<String> allfoundInputFileNamesInDEF) {
		
		String expId = experimentId;		
		//check if id has the prefix bril, if true true take only the bit after 'bril:'
		if (experimentId.lastIndexOf(':') != -1){
			expId = experimentId.substring(experimentId.lastIndexOf(':') + 1);
		}
		
		String phaserProcessID = "bril:process-" + IDGenerator.generateUUID();
		ByteArrayOutputStream relsExt_baos = new ByteArrayOutputStream();
		
		DublinCore digitalObjectDC = new DublinCore(phaserProcessID);
		digitalObjectDC.setTitle("PhaserProcess");
		digitalObjectDC.setDescription("Molecular Replacement using Phaser");
		//digitalObjectDC.setDate("", "dd/MM/yyyy HH:mm:ss");
		System.out.println("-------- END: Create Dublin code metadata created --------------");
		
		DatastreamObjectContainer dsc = new DatastreamObjectContainer(phaserProcessID);
		dsc.addMetaData(digitalObjectDC);
		dsc.addDatastreamObject(DataStreamType.OriginalData,
			DatastreamMimeType.TEXT_XML.getMimeType(), "PhaserProcess", "bril", null);
		
		/*
		 * create relationships*/
		FedoraRelsExt relsExt = null;
		try {
			relsExt = new FedoraRelsExt(FedoraNamespace.FEDORA.getURI() + phaserProcessID);
			
			// Process ---isPartOf---> Experiment
			QName predicate = new QName(FedoraNamespace.FEDORARELSEXT.getURI(), "isPartOf", FedoraNamespace.FEDORA.getPrefix());
			QName object = new QName("", expId, FedoraNamespace.FEDORA.getURI() + FedoraNamespace.BRIL.getPrefix());				
			relsExt.addRelationship(predicate, object);	
			System.out.println("relationship: " + phaserProcessID + " --isPartOf--> " + expId);
			
			// Process ---used---> DEF file
			predicate = new QName(FedoraNamespace.OPMV.getURI(), BrilRelationshipType.used.getRelation(), FedoraNamespace.OPMV.getPrefix());
			if (DEFObjectId.contains("/")) {
				DEFObjectId = DEFObjectId.substring(DEFObjectId.lastIndexOf("/") + 1);
			}
			if (DEFObjectId.contains(":")) {
				DEFObjectId = DEFObjectId.substring(DEFObjectId.lastIndexOf(":") + 1);
			}
			object = new QName("", DEFObjectId, FedoraNamespace.FEDORA.getURI() + FedoraNamespace.BRIL.getPrefix());				
			relsExt.addRelationship(predicate, object);
			System.out.println("relationship: " + phaserProcessID + " --used--> " + DEFObjectId);
			
			// Process ---used---> found DEF file
			predicate = new QName(FedoraNamespace.OPMV.getURI(), BrilRelationshipType.used.getRelation(), FedoraNamespace.OPMV.getPrefix());
			if (foundDEFObjectId.contains("/")) {
				foundDEFObjectId = foundDEFObjectId.substring(foundDEFObjectId.lastIndexOf("/") + 1);
			}
			if (foundDEFObjectId.contains(":")) {
				foundDEFObjectId = foundDEFObjectId.substring(foundDEFObjectId.lastIndexOf(":") + 1);
			}
			object = new QName("", foundDEFObjectId, FedoraNamespace.FEDORA.getURI()+ FedoraNamespace.BRIL.getPrefix());				
			relsExt.addRelationship(predicate, object);
			System.out.println("relationship: " + phaserProcessID + " --used--> " + foundDEFObjectId);
			
			// Process ---used---> found input files
			Vector<String> foundInputObjectIdVector = generatorUtils.getInputFileObjectIds(allfoundInputFileNamesInDEF, experimentId);					
			for (int i = 0; i < foundInputObjectIdVector.size(); i++) {
				String foundInputObjectId = foundInputObjectIdVector.get(i).toString();
				if (foundInputObjectId.contains("/")) {
					foundInputObjectId = foundInputObjectId.substring(foundInputObjectId.lastIndexOf("/") + 1);
				}
				if (foundInputObjectId.contains(":")) {
					foundInputObjectId = foundInputObjectId.substring(foundInputObjectId.lastIndexOf(":") + 1);
				}
				predicate = new QName(FedoraNamespace.OPMV.getURI(), BrilRelationshipType.used.getRelation(), FedoraNamespace.OPMV.getPrefix());
				object = new QName("", foundInputObjectId, FedoraNamespace.FEDORA.getURI());				
				relsExt.addRelationship(predicate, object);				
				System.out.println("relationship: " + phaserProcessID + " --used--> " + foundInputObjectId);
			}
			
			// Process ---wasControlledBy---> User
			// TODO: need to get user name from experiment creation form
			predicate = new QName(FedoraNamespace.OPMV.getURI(), BrilRelationshipType.wasControlledBy.getRelation(), FedoraNamespace.OPMV.getPrefix());
			object = new QName("", "Stella Fabiane", "");					
			relsExt.addRelationship(predicate, object);
			System.out.println("relationship: " + phaserProcessID + " --wasControlledBy--> Stella Fabiane");			
			
			// Process ---wasControlledBy---> CCP4I
			predicate = new QName(FedoraNamespace.OPMV.getURI(), BrilRelationshipType.wasControlledBy.getRelation(), FedoraNamespace.OPMV.getPrefix());
			object = new QName("", "CCP4I", "");					
			relsExt.addRelationship(predicate, object);
			System.out.println("relationship: " + phaserProcessID + " --wasControlledBy--> CCP4I");
			
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
		dsc1.addDatastreamObject(DataStreamType.OriginalData, DatastreamMimeType.TEXT_XML.getMimeType(), "PhaserProcess", "bril", null);
		dsc1.addDatastreamObject(DataStreamType.RelsExt, DatastreamMimeType.APPLICATION_RDF.getMimeType(), "relationship", "bril", relsExtByteArray);
			 
		try {
			FedoraAdminstrationImpl fedoraAdmin = new FedoraAdminstrationImpl();
			fedoraAdmin.storeObject(dsc1);
		} catch (BrilObjectRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return phaserProcessID;
	}
	
	/**
	 * Get all the CCP4 DEF files in the repository created (creation date does not matter). Processes 2 types of DEF file; 
	 * 1. database.def log that holds the list of all the task runs including the inputs and outputs (Not more then 2 may 
	 *    be present for an experiment)
	 * 2. taskname.def log that holds the meta-data of a particular task run. (total number of task run using the CCP4I)
	 * Iterates through the DEF files to search if output of task is same as the current PDB file, 
	 * If true gets the task and its inputs and creates the object relationships
	 * by getting the inputs object ids in the repository.
	 * 
	 * @param objectId Identifier of the current PDB object
	 * @param experimentId experiment identifier to which this PDB object belongs to
	 * @param returnedDEFFileObjectIds List of DEF file object identifiers (currently present in the repository).
	 * @param objectRelationship ObjectRelationship object that would hold the relationship triples
	 */
	private void processCCP4IDefFile(String objectId, String experimentId, Map<String, Long> returnedDEFFileObjectIds) {

		for (Map.Entry<String, Long> entry : returnedDEFFileObjectIds.entrySet()) {

			String DEFObjectId = entry.getKey();
			boolean isDatabase = generatorUtils.isDatabaseDEF(DEFObjectId);
					
			// START OF database.DEF file processing
			// This is the only place where a MTZ can be an output
			
			if (isDatabase == true) {								
				System.out.println("TEST START processing DATABASE.DEF file:  --------- ");
				TaskObjectVector taskObjectVector = new TaskObjectVector();
				TaskObject taskObject = new TaskObject();
				String jobIdFromDatabaseDEF = null;				
				String foundDEFObjectId = null;	
				/*
				 * GET jobId whose output filename matches with the current
				 * object's filename.
				 */
				jobIdFromDatabaseDEF = generatorUtils.getJobIdInDatabaseDEF(DEFObjectId, taskObjectVector, null);
				System.out.println("Get the jobId in database.def file whose output is the current object filename:  --------- "
								+ jobIdFromDatabaseDEF);

				if (jobIdFromDatabaseDEF != null) {
					System.out.println("If JobId is not null- Then get the inputs of this jobid:  --------- "
									+ jobIdFromDatabaseDEF);
					
					String taskName = taskObjectVector.getTaskVectorObject().get(jobIdFromDatabaseDEF);
					
					// Phaser Task
					if	(taskName.contains("phaser_MR")) {																
						
						for (Map.Entry<String, Long> def_entry : returnedDEFFileObjectIds.entrySet()) {
							
							String taskDEFObjectId = def_entry.getKey();
							System.out.println("taskDEFObjectId: " + taskDEFObjectId);
							isDatabase = generatorUtils.isDatabaseDEF(taskDEFObjectId);
							if (isDatabase == false) {								
								String foundJobID = generatorUtils.getJobIdFromTaskObject(taskDEFObjectId, taskObject);
								if ((foundJobID.trim()).equals(jobIdFromDatabaseDEF.trim())) {									
									foundDEFObjectId = taskDEFObjectId;																
								}									
							}
						}																			
						
						taskObject = new TaskObject();
						System.out.println("Found DEF object Id: (" + foundDEFObjectId + ")");
						generatorUtils.setTaskObject(foundDEFObjectId, taskObject);
						
						/*
						 * Get all the input file names of the selected jobid and found jobid
						 */																		
						Vector<String> inputsWithJobIdFromDatabaseDEF = taskObjectVector
								.getInputVectorObject().get(jobIdFromDatabaseDEF);
						
						Vector<String> foundInputFileNamesInDEF = taskObject.getInputFileNames();												
						
						System.out.println("Database DEF inputs: (" + inputsWithJobIdFromDatabaseDEF.toString() + ")");
						System.out.println("Found DEF inputs: (" + foundInputFileNamesInDEF.toString() + ")");
						
						// Merge and remove duplicates
						inputsWithJobIdFromDatabaseDEF.addAll(foundInputFileNamesInDEF);
						Vector<String> allFoundInputs = new Vector<String>(new LinkedHashSet<String>(inputsWithJobIdFromDatabaseDEF));
						
						// Check if phaser_MR process already exists
						if (checkForPhaserProcess(experimentId) == true) {
							
							// Current PDB object ---wasGeneratedBy---> Phaser Process
							this.objectRelationship.addRelationship(objectId, BrilRelationshipType.wasGeneratedBy.getRelation(),
									getPhaserProcessId());
							System.out.println("relationship: " + objectId + " --wasGeneratedBy--> " + getPhaserProcessId());
						
						} else {
						
							// Create phaser_MR process and add 'used' relationship to all input files
							String phaserProcessId = createPhaserProcessInFedora(experimentId, objectId, DEFObjectId,
									foundDEFObjectId, allFoundInputs);
							System.out.println("Created 'phaser' process: " + phaserProcessId);
																			
							// Current PDB object ---wasGeneratedBy---> Phaser Process
							objectRelationship.addRelationship(objectId, BrilRelationshipType.wasGeneratedBy.getRelation(), 
									phaserProcessId);
							System.out.println("relationship: " + objectId + " --wasGeneratedBy--> " + phaserProcessId);
						}
						
						// Add 'wasDerivedFrom' relationships between the input files and the current PDB file
						for (int i = 0; i < allFoundInputs.size(); i++) {								
							String inputFileNameInDEFFile = allFoundInputs.get(i);
							lookForObjectsAndAddRelationship(inputFileNameInDEFFile, objectId, DEFObjectId, experimentId, false);
						}												
					}
				}
			}// END DATABASE DEF FILE processing			
		}
	}

	/**
	 * 
	 * Used by processCCP4IDefFile method to search for object with the input filename in the repository and
	 * if found, create object relationships with current PDB file and add the relationship to the ObjectRelationship 
	 * 
	 * @param inputFileNameInDEFFile The input file name used to search for object in the repository 
	 * @param objectId This PDB file object identifier
	 * @param ProcessId The Process that was generated from the DEF file
	 * @param experimentId The experiment object that this PDB object belongs to
	 * @param objectRelationship ObjectRelationship object that would hold the relationship triples
	 */
	private void lookForObjectsAndAddRelationship(String inputFileNameInDEFFile, String objectId, String processId, String experimentId, boolean isCHAINSAW ){
		
		String inSuffix = inputFileNameInDEFFile.substring(inputFileNameInDEFFile.lastIndexOf(".") + 1);
				
		/*
		 * Look for the file objects in the repository based on the filename suffix 
		 */
		if (inSuffix.equals("mtz")) {			
			// returns the MTZ files sorted in descending order that belongs to this experiment
			Map<String, Long> returnedSortedMTZFileObjectIds = generatorUtils
					.getRepositoryDescSortedResult(experimentId, CrystallographyObjectType.MTZReflectionFile);
			int ct = 0;
			
			// Gets only the first MTZ object id and checks its filename
			if (ct == 0) { 
				
				for (Map.Entry<String, Long> ent : returnedSortedMTZFileObjectIds.entrySet()) {

					String pathMTZFile = generatorUtils
							.getOriginalPathOfObjectInRepository(ent.getKey());
					String MTZFilename = pathMTZFile.substring(pathMTZFile.lastIndexOf("/") + 1);

					if (inputFileNameInDEFFile.equals(MTZFilename)) {
					
						String inputObjectId = ent.getKey();
						if (inputObjectId.contains("/")) {
							inputObjectId = inputObjectId.substring(inputObjectId.lastIndexOf("/") + 1);
						}
						if (!inputObjectId.contains("bril:")) {
							inputObjectId = "bril:" + inputObjectId;
						}
					
						// Current PDB object ---wasDerivedFrom---> MTZ
						objectRelationship.addRelationship(objectId, BrilRelationshipType.wasDerivedFrom.getRelation(), inputObjectId);
						System.out.println("relationship: " + objectId + " --wasDerivedFrom--> " + inputObjectId);
					}
					ct++;
				}
			}
		}

		if (inSuffix.equals("pdb")) {
			Map<String, Long> returnedSortedPDBFileObjectIds = generatorUtils
					.getRepositoryDescSortedResult(experimentId, CrystallographyObjectType.CoordinateFile);
			int ct = 0;
			// Gets only the first PDB object id and checks its filename
			if (ct == 0) {
				for (Map.Entry<String, Long> ent : returnedSortedPDBFileObjectIds.entrySet()) {

					String pathPDBFile = generatorUtils
							.getOriginalPathOfObjectInRepository(ent.getKey());
					String PDBFilename = pathPDBFile.substring(pathPDBFile.lastIndexOf("/") + 1);

					if (inputFileNameInDEFFile.equals(PDBFilename)) {
						String inputObjectId = ent.getKey();
						if (inputObjectId.contains("/")) {
							inputObjectId = inputObjectId.substring(inputObjectId.lastIndexOf("/") + 1);
						}
						
						// Current PDB object ---wasDerivedFrom---> PDB
						objectRelationship.addRelationship(
								objectId, BrilRelationshipType.wasDerivedFrom.getRelation(), inputObjectId);
						System.out.println("relationship: " + objectId + " --wasDerivedFrom--> " + inputObjectId);
						
						// Add 'wasGeneratedBy' relationship between the coordinate file that is used by the CHAINSAW
						// process and the 'Search' Process
						if (isCHAINSAW) {
							if (checkForSearchProcess(experimentId) == true) {							
								// PDB ---wasGeneratedBy ---> Search Process
								addRelationshipToOtherFedoraObject(inputObjectId, BrilRelationshipType.wasGeneratedBy.getRelation(), 
										searchProcessId, FedoraNamespace.BRILRELS.getURI());
								System.out.println("relationship: " + objectId + " --wasGeneratedBy--> " + searchProcessId);
							}
						}
					}
					ct++;
				}
			}
		}
		
		if (inSuffix.endsWith("aln")) {
			Map<String, Long> returnedSortedALNFileObjectIds = generatorUtils
					.getRepositoryDescSortedResult(experimentId,CrystallographyObjectType.AlignmentFile);
			int ct = 0;
			if (ct == 0) { 
				for (Map.Entry<String, Long> ent : returnedSortedALNFileObjectIds.entrySet()) {

					String pathALNFile = generatorUtils
							.getOriginalPathOfObjectInRepository(ent.getKey());
					String ALNFilename = pathALNFile.substring(pathALNFile.lastIndexOf("/") + 1);

					if (inputFileNameInDEFFile.equals(ALNFilename)) {
						
						String inputObjectId = ent.getKey();
						
						if (inputObjectId.contains("/")) {
							inputObjectId = inputObjectId.substring(inputObjectId.lastIndexOf("/") + 1);
						}
						
						// Current PDB object ---wasDerivedFrom---> ALN
						objectRelationship.addRelationship(
								objectId, BrilRelationshipType.wasDerivedFrom.getRelation(), inputObjectId);
						System.out.println("relationship: " + objectId + " --wasDerivedFrom--> " + inputObjectId);
					}
					ct++;
				}
			}
		}
		
		if (inSuffix.endsWith("seq")) {
			Map<String, Long> returnedSortedSEQFileObjectIds = generatorUtils
					.getRepositoryDescSortedResult(experimentId,CrystallographyObjectType.SEQFile);
			int ct = 0;
			if (ct == 0) { 
				for (Map.Entry<String, Long> ent : returnedSortedSEQFileObjectIds.entrySet()) {
		
					String pathSEQFile = generatorUtils
							.getOriginalPathOfObjectInRepository(ent.getKey());
					String SEQFilename = pathSEQFile.substring(pathSEQFile.lastIndexOf("/") + 1);
		
					if (inputFileNameInDEFFile.equals(SEQFilename)) {
						
						String inputObjectId = ent.getKey();						
						if (inputObjectId.contains("/")) {
							inputObjectId = inputObjectId.substring(inputObjectId.lastIndexOf("/") + 1);
						}
						
						if (checkForSearchProcess(experimentId) == true) {							
							// Search Process ---used---> SEQ object
							addRelationshipToOtherFedoraObject(searchProcessId, BrilRelationshipType.used.getRelation(), 
									inputObjectId, FedoraNamespace.OPMV.getURI());
							System.out.println("relationship: " + searchProcessId + " --used--> " + inputObjectId);
						}						
					}
					ct++;
				}
			}
		}
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
			System.out.println("current object1: " + obj);
			fedoraAdmin = new FedoraAdminstrationImpl();
			if (subj.contains("bril:") == false) {
				subj = brilPrefix + ":" + subj;
			}
			String subject = fedoraURI + subj;
			String predicate = namespaceURI_predicate + pred;
			String object = fedoraURI + obj;
			System.out.println("current object2: " + obj);			
			
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
	
	/**
	 * Connects to and searches the repository with the query parameter 'PhaserProcess' on the title of DC
	 * 
	 * @param experimentId
	 * @return boolean result indicating if the phaser process for this experiment is present or not
	 */		
	public boolean checkForPhaserProcess(String experimentId){
		boolean result = false;
		String expId = experimentId;
		//check if id doesnot have the prefix bril, add the prefix
		if (experimentId.lastIndexOf(':') == -1) {
			expId = "bril:"+experimentId;
		}
		Vector<String> test = generatorUtils.searchForObjectIds(expId, "title", "PhaserProcess");
		if (test.size() != 0 ) {
			for (int i=0; i<test.size(); i++) {
				String res = test.get(i);
				if (res != null) {
					phaserProcessId = res.substring(res.lastIndexOf("/") + 1);
				}
			}
			result = true;
		}
		return result;
	}
	
	public String getPhaserProcessId(){
		return phaserProcessId;
	}
	
	/**
	 * Connects to and searches the repository with the query parameter 'SearchProcess' on the title of DC
	 * 
	 * @param experimentId
	 * @return boolean result indicating if the search process for this experiment is present or not
	 */		
	public boolean checkForSearchProcess(String experimentId){
		boolean result = false;
		String expId = experimentId;
		//check if id doesnot have the prefix bril, add the prefix
		if (experimentId.lastIndexOf(':') == -1) {
			expId = "bril:" + experimentId;
		}
		Vector<String> test = generatorUtils.searchForObjectIds(expId, "title", "SearchProcess");
		if (test.size() != 0 ) {
			for (int i = 0; i < test.size(); i++) {
				String res = test.get(i);
				if (res != null) {
					searchProcessId = res.substring(res.lastIndexOf("/") + 1);
				}
			}
			result = true;
		}
		return result;
	}
	
	/**
	 *  The PHENIX DEF files returned from the repository is available. 
	 *  The metadata of the DEF file created before this MTZ file is used. 
	 *  If the output file prefix in the metadata is present in the current MTZ file name, then this MTZ file
	 *  would be the output from this DEF parameter file. In this case, relationships to the input objects
	 *  are created for this MTZ file.
	 *  
	 *  According to the PHENIX source code (util.py)
	 * 
	 *  if self.map_out is None :
     *     self.map_out = os.path.splitext(self.file_name)[0] + "_map_coeffs.mtz"
     *      write_map_coeffs(f_map, df_map, self.map_out)
	 * 
	 *  Thus any MTZ files that has the same prefix as the DEF file used in the process
	 *  AND _map_coeffs.mtz would be an output of the PHENIX process
	 *     
	 * 
	 * @param objectId This MTZ file object identifier
	 * @param experimentId The experiment object that this MTZ object belongs to
	 * @param objectRelationship ObjectRelationship object that would hold the relationship triples
	 */
	private void processPHENIXDefFile(String objectId, String experimentId) {
		String objectDate = generatorUtils.getObjectDate();
		String objectPath = generatorUtils.getObjectTitle();
		
		String mtzObjectFileName = objectPath.substring(objectPath.lastIndexOf("/") + 1);
		System.out.println("This PDB filename: " + mtzObjectFileName);
		// convert to long date
							
		long date1 = uk.ac.kcl.cerch.bril.common.util.DateTime.getLongDateTime(objectDate, "dd/MM/yyyy HH:mm:ss");
		Map<String, Long> sortedPhenixDEFObjectMap = generatorUtils
				.getRepositoryDescSortedResult(experimentId, CrystallographyObjectType.PhenixDefFile);
		
		if (sortedPhenixDEFObjectMap.size() != 0) {
			for (Map.Entry<String, Long> entry : sortedPhenixDEFObjectMap.entrySet()) {
				
				long date2 = Long.valueOf(entry.getValue()).longValue();
				long dateDifference = date1 - date2;
				String diffvalue = String.valueOf(dateDifference);
								
				if (diffvalue.contains("-") == false) {
					
					String phenixObjectId = entry.getKey().toString();

					byte[] fileMetadata = generatorUtils.getDatastreamType(
							phenixObjectId, DataStreamType.ObjectMetadata);

					String outputPrefix = getJobId(fileMetadata);
					String defFileTaskName = getTaskName(fileMetadata);
					System.out.println("phenixObjectId: " + phenixObjectId);
					System.out.println("defFileTaskName: " + defFileTaskName);
					System.out.println("outputPrefix: " + outputPrefix);
					
					if (phenixObjectId.contains("/")) {
						phenixObjectId = phenixObjectId.substring(phenixObjectId.lastIndexOf("/") + 1);
					}
					
					Vector<String> PHENIXProcess = generatorUtils.
							getRelatedObjects(experimentId, "PHENIXProcess", BrilRelationshipType.used.getRelation(), phenixObjectId);
					
					if (PHENIXProcess.size() != 0) {						
						System.out.println("Found PHENIX process: (" + PHENIXProcess + ")");
						
						if (mtzObjectFileName.contains(outputPrefix)) {
							if (mtzObjectFileName.contains("_map_coeffs.mtz")) {
								// Current MTZ object ---wasGeneratedBy ---> phenix Process
								objectRelationship.addRelationship(objectId,
										BrilRelationshipType.wasGeneratedBy.getRelation(), PHENIXProcess.get(0));
								System.out.println("relationship: " + objectId + " --wasGeneratedBy--> " + PHENIXProcess.get(0));
							}
						}																								
					}							
				}
			}
		}
	}

	private String getTaskName(byte[] fileMetadata){
		String value = new String(fileMetadata);
		System.out.println(value);
	
		String elementvalue = generatorUtils.getValueFromTaskXML(fileMetadata, TaskObjectElement.TASK_NAME);
		return elementvalue;
	}
	
	/**
	 * Adds relationship to the current MTZ object with the Vector containing the Ids of the object that derived this MTZ object.
	 *  
	 * @param currentObjectId Identifier of the current MTZ object
	 * @param inputObjectIdVector Vector object containing the list of identifiers that derived this current MTZ object
	 * @param objectRelationship ObjectRelationship object that would hold the relationship triples
	 */
	private void addToRelationshipToCurrentObject(String currentObjectId,
			Vector<String> inputObjectIdVector) {

		for (int i = 0; i < inputObjectIdVector.size(); i++) {
			String inputObjectId = inputObjectIdVector.get(i).toString();
			if (inputObjectId.contains("/")) {
				inputObjectId = inputObjectId.substring(inputObjectId.lastIndexOf("/") + 1);
			}
			this.objectRelationship.addRelationship(currentObjectId,
					BrilRelationshipType.wasDerivedFrom.getRelation(), inputObjectId);
		}
	}
	
	private String getDiffractionImageSetObjectId() {
		return this.diffractionImageSetObjectId;
	}

	/**
	 * Checks if the Diffraction images set object is present in the repository
	 * that belongs to this experiment id. Is calls method
	 * searchForImageSetObjectIds that gets all the object id belonging to this
	 * experiment that has dc title =DiffractinImageSet by running itql query.
	 * Also sets diffractionImageSetObjectId variable with the returned object
	 * id.
	 * 
	 * @see searchForImageSetObjectIds method of class GeneratorUtils
	 * @see getDiffractionSetObjectIds method of class FedoraAdministrator that
	 *      runs the itql query and returns xml string
	 * 
	 * @param experimentId
	 * @return boolean value
	 */
	private boolean hasDiffractionImageSetObjectInRepository(String experimentId) {
		boolean result = false;
		if (experimentId.lastIndexOf(':') == -1) {
			experimentId = "bril:" + experimentId;
		}
		Vector<String> pids = generatorUtils.searchForObjectIds(
				experimentId, "title", "DiffractionImageSet");
		if (pids.size() != 0) {
			for (int i = 0; i < pids.size(); i++) {
				String id = pids.get(i);
				if (id != null) {
					diffractionImageSetObjectId = id.substring(id.lastIndexOf("/") + 1);
				}
			}
			result = true;
		}

		return result;
	}
	
	/**
	 * From the XML metadata in byte array, this methods gets the jobId.
	 * 
	 * @param defPhenixId Identifier for either DEF files or COM file
	 * @return jobid of the task
	 */
	private String getJobId(byte[] fileMetadata){

		 String value = new String(fileMetadata);
			System.out.println(value);
		
		String elementvalue = generatorUtils.getValueFromTaskXML(fileMetadata, TaskObjectElement.JOB_ID);
		return elementvalue;
	}
		
	private void setReflectionType(String metadata){
		//reflectionType
		byte[] data = metadata.getBytes();
		InputStream inputstream = new ByteArrayInputStream(data);
		FileUtil.writeByteArrayToFile("mtzcharacteriserReturned.xml", data);
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
				//System.out.println("taskobject element: "+ taskObjectElement.localName());
				if (localNodeName.equals("reflection_type")) {
					
					//	System.out.println("filenames ---" + element.getTextContent());
					reflectionType = element.getTextContent();
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
	
	private String getReflectionType(){
		return this.reflectionType;
	}
}