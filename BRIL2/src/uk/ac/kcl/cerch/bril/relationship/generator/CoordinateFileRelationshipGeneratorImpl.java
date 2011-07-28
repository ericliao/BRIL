package uk.ac.kcl.cerch.bril.relationship.generator;

import java.io.ByteArrayOutputStream;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

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
import uk.ac.kcl.cerch.bril.common.types.DataStreamType;
import uk.ac.kcl.cerch.bril.common.types.DatastreamMimeType;
import uk.ac.kcl.cerch.bril.common.types.DatastreamObjectContainer;
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
public class CoordinateFileRelationshipGeneratorImpl implements
		CoordinateFileRelationshipGenerator {

	private GeneratorUtils generatorUtils;
	private ObjectRelationship objectRelationship;
	private String phaserProcessId;
	private String searchProcessId;
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.kcl.cerch.bril.relationship.generator.ObjectRelationshipGenerator
	 * #generateRelationships(java.lang.String, java.lang.String)
	 */

	public ObjectRelationship generateRelationships(String objectId, String experimentId) {

		if (experimentId.contains("bril:") == false) {
			experimentId = "bril:" + experimentId;
		}

		generatorUtils = new GeneratorUtils();
		objectRelationship = new ObjectRelationship();
		objectRelationship.addRelationship(objectId, "isPartOf", experimentId);

		try {
			/**
			 * Search in the object store for this object's DC data that sets
			 * title and date values to the setter methods in the GeneratorUtils
			 */
			generatorUtils.searchForArchivalObjects(objectId);

		} catch (ObjectStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		boolean relationshipsCreated = false;
		
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
		}

		/**
		 * Look for the Phenix DEF file that is created before this PDB file
		 * 
		 * */
		if (relationshipsCreated == false) {

			processPHENIXDefFile(objectId, experimentId);
			// check if wasDerivedForm relations is generated
			relationshipsCreated = generatorUtils
					.hasRelationship(objectRelationship, BrilRelationshipType.wasDerivedFrom.getRelation());
			System.out.println("Relationships Created phenix: " + relationshipsCreated);
		}

		/*
		 * if none of them have relationships, then this must either be:
		 * 1) the output coordinate files from the sequence search process
		 * 2) the pdb used in the first refinement stage
		 * 
		 * TODO: leave them in the repo, then add relationships when the DEF file is ingested?
		 * 
		 */

		return objectRelationship;
	}
	
	/**
	 *  The PHENIX DEF files returned from the repository is available. 
	 *  The metadata of the DEF file created before this PDB file is used. 
	 *  If the output file prefix in the metadata is present in the current PDB file name, then this PDB file
	 *  would be the output from this DEF parameter file. In this case, relationships to the input objects
	 *  are created for this PDB file.
	 *     
	 * 
	 * @param objectId This PDB file object identifier
	 * @param experimentId The experiment object that this PDB object belongs to
	 * @param objectRelationship ObjectRelationship object that would hold the relationship triples
	 */
	private void processPHENIXDefFile(String objectId, String experimentId) {
		String objectDate = generatorUtils.getObjectDate();
		String objectPath = generatorUtils.getObjectTitle();
		
		String pdbObjectFileName = objectPath.substring(objectPath.lastIndexOf("/") + 1);
		System.out.println("This PDB filename: " + pdbObjectFileName);
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
						
						Map<String, Long> sortedPDBObjectMap = generatorUtils
							.getRepositoryDescSortedResult(experimentId, CrystallographyObjectType.CoordinateFile);											
						System.out.println("input Ids: (" + sortedPDBObjectMap + ")");
						
						boolean hasRel = false;
						
						for (Map.Entry<String, Long> pdb : sortedPDBObjectMap.entrySet()) {
							hasRel = generatorUtils.hasRelationship(pdb.getKey().toString(), 
											BrilRelationshipType.wasGeneratedBy.getRelation(), PHENIXProcess.get(0));
							if (hasRel) {
								System.out.println("Found a PDB file with a 'wasGeneratedBy' relationship to the process");
								break;
							}
						}												
						
						if (!hasRel) {
							if (!defFileTaskName.contains("subsequent")) {												
								
								// only continue if DEF file does not contain subsequent (i.e. the automatically generated DEF)
								if (pdbObjectFileName.contains(outputPrefix)) {
									System.out.println("Get inputs filename in the PHENIX DEF file ............");
									Vector<String> inputNameList = generatorUtils.getFilenamesFromTaskXML(fileMetadata, 
											TaskObjectElement.INPUT_FILENAME);
									
									System.out.println("Get input ids in the PHENIX DEF file ............");
									Vector<String> inputIdList = generatorUtils.inputObjectIdsInPhenixDefFile(phenixObjectId, 
											fileMetadata, experimentId);
									
									for (int i = 0; i < inputNameList.size(); i++) {
										String inputObjectName = inputNameList.get(i);
										if (inputObjectName.contains(".pdb")) {
											System.out.println("inputObjectName: (" + inputObjectName + ")");
											/*
											 * Add relationship inputs in the Phenix def
											 * file to this pdb file
											 */
											// Current PDB object ---wasDerivedFrom---> Input
											objectRelationship.addRelationship(objectId,
													BrilRelationshipType.wasDerivedFrom.getRelation(), inputIdList.get(i));
											System.out.println("relationship: " + objectId + " --wasDerivedFrom--> " + inputIdList.get(i));
											
											// Current PDB object ---wasGeneratedBy ---> phenix Process
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
			}
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

			// TODO: start by processing taskname.def, database.def will only be needed for phaser_MR
			//		 no need to directly process database.def?
			if (isDatabase == false) {
				// START TASK DEF FILE
				System.out.println("START processing TASK.DEF file:  --------- ");
				TaskObject taskObject = new TaskObject();
				boolean result = generatorUtils.hasOuputInTaskObject(DEFObjectId, taskObject);
				if (result == true) {
					System.out.println("TASK.DEF file has current object as output:  --------- ");
					String taskName = taskObject.getTaskName();
					
					// CHAINSAW Task
					if (taskName.contains("chainsaw")) {						
						
						Vector<String> inputFileNamesInDEFFile = taskObject.getInputFileNames();
						
						// Create CHAINSAW process and add 'used' relationship to all input files
						String chainsawProcessId = createCHAINSAWProcessInFedora(experimentId, objectId, DEFObjectId, 
								inputFileNamesInDEFFile);
						System.out.println("Created 'CHAINSAW' process: " + chainsawProcessId);
						
						// Current PDB object ---wasGeneratedBy---> CHAINSAW Process
						this.objectRelationship.addRelationship(objectId, BrilRelationshipType.wasGeneratedBy.getRelation(), 
								chainsawProcessId);
						
						// Add 'wasDerivedFrom' relationships between the input files and the current PDB file
						for (int i = 0; i < inputFileNamesInDEFFile.size(); i++) {														
							String inputFileNameInDEFFile = inputFileNamesInDEFFile.get(i);
							lookForObjectsAndAddRelationship(inputFileNameInDEFFile, objectId, chainsawProcessId, experimentId, true);
						}					
					}															
				}
			}			
			
			// START OF database.DEF file processing
			else {								
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
							this.objectRelationship.addRelationship(objectId, BrilRelationshipType.wasGeneratedBy.getRelation(), 
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
	 * Creates a phaser process in the repository that consists of 
	 * 1) 'isPartOf' relationship to the experiment	 
	 * 2) 'used' relationship to the DEF file
	 * 3) 'used' relationship to the found DEF file
	 * 4) 'wasControlledBy' relationship to the user (get this from the form?)
	 * 5) 'wasControlledBy' relationship to 'CCP4I'
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
			object = new QName("", foundDEFObjectId, FedoraNamespace.FEDORA.getURI() + FedoraNamespace.BRIL.getPrefix());				
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
				object = new QName("", foundInputObjectId, FedoraNamespace.FEDORA.getURI() + FedoraNamespace.BRIL.getPrefix());				
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
										searchProcessId, FedoraNamespace.OPMV.getURI());
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
	 * Creates a CHAINSAW process in the repository that consists of 
	 * 1) 'isPartOf' relationship to the experiment	 
	 * 2) 'used' relationship to the DEF file
	 * 3) 'wasControlledBy' relationship to the user (get this from the form?)
	 * 4) 'wasControlledBy' relationship to 'CCP4I'
	 *
	 * @param experimentId
	 * @param objectID
	 * @param inputObjectID 
	 * 
	 * @return the identifier of the newly created CHAINSAW process.
	 */
	private String createCHAINSAWProcessInFedora(String experimentId, String objectID, String DEFObjectId, 
						Vector<String> inputNameList) {
		
		String expId = experimentId;		
		//check if id has the prefix bril, if true true take only the bit after 'bril:'
		if (experimentId.lastIndexOf(':') != -1){
			expId = experimentId.substring(experimentId.lastIndexOf(':') + 1);
		}
		
		String chainsawProcessID = "bril:process-" + IDGenerator.generateUUID();
		ByteArrayOutputStream relsExt_baos = new ByteArrayOutputStream();
		
		DublinCore digitalObjectDC = new DublinCore(chainsawProcessID);
		digitalObjectDC.setTitle("CHAINSAWProcess");
		digitalObjectDC.setDescription("Mutating coordinate file according to input sequence alignment");
		//digitalObjectDC.setDate("", "dd/MM/yyyy HH:mm:ss");
		System.out.println("-------- END: Create Dublin code metadata created --------------");
		
		DatastreamObjectContainer dsc = new DatastreamObjectContainer(chainsawProcessID);
		dsc.addMetaData(digitalObjectDC);
		dsc.addDatastreamObject(DataStreamType.OriginalData,
			DatastreamMimeType.TEXT_XML.getMimeType(), "CHAINSAWProcess", "bril", null);
		
		/*
		 * create relationships*/
		FedoraRelsExt relsExt = null;
		try {
			relsExt = new FedoraRelsExt(FedoraNamespace.FEDORA.getURI() + chainsawProcessID);
			
			// Process ---isPartOf---> Experiment
			QName predicate = new QName(FedoraNamespace.FEDORARELSEXT.getURI(), "isPartOf", FedoraNamespace.FEDORA.getPrefix());
			QName object = new QName("", expId, FedoraNamespace.FEDORA.getURI() + FedoraNamespace.BRIL.getPrefix());				
			relsExt.addRelationship(predicate, object);	
			System.out.println("relationship: " + chainsawProcessID + " --isPartOf--> " + expId);
			
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
			System.out.println("relationship: " + chainsawProcessID + " --used--> " + DEFObjectId);
			
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
				System.out.println("relationship: " + chainsawProcessID + " --used--> " + inputObjectId);
			}
												
			// Process ---wasControlledBy---> User
			// TODO: need to get user name from experiment creation form
			predicate = new QName(FedoraNamespace.OPMV.getURI(), BrilRelationshipType.wasControlledBy.getRelation(), FedoraNamespace.OPMV.getPrefix());
			object = new QName("", "Stella Fabiane", "");					
			relsExt.addRelationship(predicate, object);
			System.out.println("relationship: " + chainsawProcessID + " --wasControlledBy--> Stella Fabiane");			
			
			// Process ---wasControlledBy---> CCP4I
			predicate = new QName(FedoraNamespace.OPMV.getURI(), BrilRelationshipType.wasControlledBy.getRelation(), FedoraNamespace.OPMV.getPrefix());
			object = new QName("", "CCP4I", "");					
			relsExt.addRelationship(predicate, object);
			System.out.println("relationship: " + chainsawProcessID + " --wasControlledBy--> CCP4I");
			
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
		dsc1.addDatastreamObject(DataStreamType.OriginalData, DatastreamMimeType.TEXT_XML.getMimeType(), "CHAINSAWProcess", "bril", null);
		dsc1.addDatastreamObject(DataStreamType.RelsExt, DatastreamMimeType.APPLICATION_RDF.getMimeType(), "relationship", "bril", relsExtByteArray);
			 
		try {
			FedoraAdminstrationImpl fedoraAdmin = new FedoraAdminstrationImpl();
			fedoraAdmin.storeObject(dsc1);
		} catch (BrilObjectRepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return chainsawProcessID;
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
	
	private String getTaskName(byte[] fileMetadata){
		String value = new String(fileMetadata);
		System.out.println(value);
	
		String elementvalue = generatorUtils.getValueFromTaskXML(fileMetadata, TaskObjectElement.TASK_NAME);
		return elementvalue;
	}
}