package uk.ac.kcl.cerch.bril.relationship.generator;

import java.util.Map;
import java.util.Vector;

import uk.ac.kcl.cerch.bril.ccp4.TaskObject;
import uk.ac.kcl.cerch.bril.characteriser.TaskObjectElement;
import uk.ac.kcl.cerch.bril.common.fedora.FedoraNamespaceContext.FedoraNamespace;
import uk.ac.kcl.cerch.bril.common.types.BrilRelationshipType;
import uk.ac.kcl.cerch.bril.common.types.CCP4ProcessType;
import uk.ac.kcl.cerch.bril.common.types.DataStreamType;
import uk.ac.kcl.cerch.bril.fileformat.CrystallographyObjectType;
import uk.ac.kcl.cerch.bril.relationship.ObjectRelationship;
import uk.ac.kcl.cerch.bril.relationship.common.GeneratorUtils;
import uk.ac.kcl.cerch.soapi.objectstore.ObjectStoreException;

/**
 * @author Eric Liao
 *
 */
public class LOGFileRelationshipGeneratorImpl implements LOGFileRelationshipGenerator{
	private ObjectRelationship objectRelationship;
	private GeneratorUtils generatorUtils;	
	private String phenixProcessId;
	
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
			e1.printStackTrace();
		}
				
		boolean relationshipsCreated = false;
		
		System.out.println("Looking for COM file ..... ");
		Map<String, Long> sortedCOMObjectMap = generatorUtils
				.getRepositoryDescSortedResult(experimentId,CrystallographyObjectType.COMFile);

		if (sortedCOMObjectMap.size() != 0) {
			processCOMFile(objectID, experimentId, sortedCOMObjectMap);
		}

		relationshipsCreated = generatorUtils.hasRelationship(
				objectRelationship, BrilRelationshipType.wasGeneratedBy.getRelation());
		System.out.println("wasGeneratedBy relationship created: "+ relationshipsCreated);
		
		/**
		 * Look for the CCP4I DEF files -database and task def files that is created before this LOG file	
		 * 
		 * */
		if (relationshipsCreated == false) {
			System.out.println("Looking for CCP4 def file ..... ");
			Map<String, Long> sortedDEFObjectMap = generatorUtils
					.getRepositoryDescSortedResult(experimentId,CrystallographyObjectType.CCP4IDefFile);

			if (sortedDEFObjectMap.size() != 0) {
				processCCP4IDefFile(objectID, experimentId, sortedDEFObjectMap);
			}
			relationshipsCreated = generatorUtils.hasRelationship(
					objectRelationship, BrilRelationshipType.wasGeneratedBy.getRelation());
			System.out.println("wasGeneratedBy relationship created: "+ relationshipsCreated);
		}
		
		/**
		 * Look for the PDB files that is created before this LOG file
		 * 
		 * */	
		if (relationshipsCreated == false) {			
			processPDBFile(objectID, experimentId);
			relationshipsCreated = generatorUtils.hasRelationship(
					objectRelationship, BrilRelationshipType.wasGeneratedBy.getRelation());
			System.out.println("Relationships Created: "+ relationshipsCreated);
		}
		
		return objectRelationship;
	}

	// for processing stage, search for .com file with same filename, then add to same process
	private void processCOMFile(String objectId, String experimentId, Map<String, Long> sortedCOMObjectMap) {
		
		String objectDate = generatorUtils.getObjectDate();
		long date1 = uk.ac.kcl.cerch.bril.common.util.DateTime.getLongDateTime(objectDate, "dd/MM/yyyy HH:mm:ss");
		
		char slash1 = '/';
		char slash2 = '\\';
		
		String originalPathOfCurrentLOGFile = generatorUtils.getObjectTitle();		
		System.out.println("LOGObjectPath: " + originalPathOfCurrentLOGFile);
					
		if (originalPathOfCurrentLOGFile.lastIndexOf(slash1) != -1 ) {
			originalPathOfCurrentLOGFile = originalPathOfCurrentLOGFile.substring(originalPathOfCurrentLOGFile.lastIndexOf(slash1) + 1);
		}
		if (originalPathOfCurrentLOGFile.lastIndexOf(slash2) != -1 ) {
			originalPathOfCurrentLOGFile = originalPathOfCurrentLOGFile.substring(originalPathOfCurrentLOGFile.lastIndexOf(slash2) + 1);
		}
		System.out.println("originalPathOfCurrentLOGFile: " + originalPathOfCurrentLOGFile);
		String LOGName = originalPathOfCurrentLOGFile.substring(0, originalPathOfCurrentLOGFile.lastIndexOf('.'));
		System.out.println("LOGName: " + LOGName);		
		for (Map.Entry<String, Long> entry : sortedCOMObjectMap.entrySet()) {
			
			long date2 = Long.valueOf(entry.getValue());
			long dateDifference = date1 - date2;
			String diffvalue = String.valueOf(dateDifference);
			int gotId = 0;
			if (diffvalue.contains("-") == false && gotId == 0) {
				gotId++;
				
				String COMObjectId = entry.getKey();
				String comObjectPath = generatorUtils.getOriginalPathOfObjectInRepository(COMObjectId);
				System.out.println("comObjectPath: " + comObjectPath);									
				if (comObjectPath.lastIndexOf(slash1) != -1 ) {
					comObjectPath = comObjectPath.substring(comObjectPath.lastIndexOf(slash1) + 1);
				}
				if (comObjectPath.lastIndexOf(slash2) != -1 ) {
					comObjectPath = comObjectPath.substring(comObjectPath.lastIndexOf(slash2) + 1);
				}			
				System.out.println("comObjectPath: " + comObjectPath);						
				
				String searchString = null;
				
				if (comObjectPath.contains(LOGName)) {
					System.out.println("Found matching COM file: (" + comObjectPath + ")");
					
					byte[] comFileMetadata = generatorUtils.getDatastreamType(
							COMObjectId, DataStreamType.ObjectMetadata);
											
					CCP4ProcessType process = CCP4ProcessType.valueOf(getTaskName(comFileMetadata));
					searchString = "CCP4%20Process%3A%20" + process.getTask();					
					
					Vector<String> CCP4Process = generatorUtils.
							getRelatedObjects(experimentId, searchString, BrilRelationshipType.used.getRelation(), COMObjectId);
					
					if (CCP4Process.size() != 0) {						
						System.out.println("Found CCP4 process: (" + CCP4Process + ")");
						// Current LOG object ---wasGeneratedBy---> CCP4 Process
						objectRelationship.addRelationship(objectId,
								BrilRelationshipType.wasGeneratedBy.getRelation(), CCP4Process.get(0));
						System.out.println("relationship: " + objectId + " --wasGeneratedBy--> " + CCP4Process.get(0));
					}
				}		
			}				
		}
	}	
	
	// for building stage, search for .def file with #CCP4I LOG_FILE equal to filename, then add to same process
	private void processCCP4IDefFile(String objectId, String experimentId, Map<String, Long> sortedDEFObjectMap) {
		for (Map.Entry<String, Long> entry : sortedDEFObjectMap.entrySet()) {

			String DEFObjectId = entry.getKey();
			boolean isDatabase = generatorUtils.isDatabaseDEF(DEFObjectId);
			String searchString = null;
			
			if (isDatabase == false) {
				// START TASK DEF FILE
				System.out.println("START processing TASK.DEF file:  --------- ");
				TaskObject taskObject = new TaskObject();
				boolean result = generatorUtils.hasLogInTaskObject(DEFObjectId);
				byte[] taskDEFFileMetadata = generatorUtils.getDatastreamType(DEFObjectId,DataStreamType.ObjectMetadata);
				if (result == true) {
					System.out.println("TASK.DEF file has current object as log file:  --------- ");
					String taskName = getTaskName(taskDEFFileMetadata);
					if (taskName.contains("chainsaw")) {
						searchString = "CHAINSAWProcess";
					} else if (taskName.contains("phaser_MR")) {
						searchString = "PhaserProcess";
					}
				
					Vector<String> CCP4Process = generatorUtils.
							getRelatedObjects(experimentId, searchString, BrilRelationshipType.used.getRelation(), DEFObjectId);									
					
					if (CCP4Process.size() != 0) {						
						System.out.println("Found CCP4 process: (" + CCP4Process + ")");
						// Current PDB object ---wasGeneratedBy---> CCP4 Process
						objectRelationship.addRelationship(objectId,
								BrilRelationshipType.wasGeneratedBy.getRelation(), CCP4Process.get(0));
						System.out.println("relationship: " + objectId + " --wasGeneratedBy--> " + CCP4Process.get(0));
					}					
				}
			}		
		}
	}
	
	// for refinement stage, search for pdb file with same filename, then add to same process
	private void processPDBFile(String objectId, String experimentId) {
		Map<String, Long> returnedSortedPDBFileObjectIds = generatorUtils
				.getRepositoryDescSortedResult(experimentId, CrystallographyObjectType.CoordinateFile);
		
		char slash1 = '/';
		char slash2 = '\\';
		
		String originalPathOfCurrentLOGFile = generatorUtils.getObjectTitle();
		System.out.println("LOGObjectPath: " + originalPathOfCurrentLOGFile);
					
		if (originalPathOfCurrentLOGFile.lastIndexOf(slash1) != -1 ) {
			originalPathOfCurrentLOGFile = originalPathOfCurrentLOGFile.substring(originalPathOfCurrentLOGFile.lastIndexOf(slash1) + 1);
		}
		if (originalPathOfCurrentLOGFile.lastIndexOf(slash2) != -1 ) {
			originalPathOfCurrentLOGFile = originalPathOfCurrentLOGFile.substring(originalPathOfCurrentLOGFile.lastIndexOf(slash2) + 1);
		}
		System.out.println("LOGObjectPath cleaned: " + originalPathOfCurrentLOGFile);
		String LOGName = originalPathOfCurrentLOGFile.substring(0, originalPathOfCurrentLOGFile.lastIndexOf('.'));
		System.out.println("LOGName: " + LOGName);
		
		for (Map.Entry<String, Long> ent : returnedSortedPDBFileObjectIds.entrySet()) {			
			
			String pathPDBFile = generatorUtils.getOriginalPathOfObjectInRepository(ent.getKey());
			System.out.println("pathPDBFile: " + pathPDBFile);			
			if (pathPDBFile.lastIndexOf(slash1) != -1 ) {
				pathPDBFile = pathPDBFile.substring(pathPDBFile.lastIndexOf(slash1) + 1);
			}
			if (pathPDBFile.lastIndexOf(slash2) != -1 ) {
				pathPDBFile = pathPDBFile.substring(pathPDBFile.lastIndexOf(slash2) + 1);
			}			
			System.out.println("pathPDBFile cleaned: " + pathPDBFile);
			System.out.println("PDB PID: " + ent.getKey());
			
			if (pathPDBFile.contains(LOGName)) {
				System.out.println("Found matching PDB file: (" + pathPDBFile + ")");
				Vector<String> results = generatorUtils.searchForObjectIds(experimentId, "title", "PHENIXProcess");
				System.out.println("results: (" + results + ")");
				if (results.size() != 0 ) {
					for (int i = 0; i < results.size(); i++) {																	
						String res = results.get(i);
						if (res != null) {
							phenixProcessId = res.substring(res.lastIndexOf("/") + 1);
														
							boolean hasRel = generatorUtils.hasRelationship(ent.getKey(), 
									FedoraNamespace.OPMV.getURI() + BrilRelationshipType.wasGeneratedBy.getRelation(), phenixProcessId);
							
							if (hasRel) {								
								System.out.println("Found PHENIX process: (" + phenixProcessId + ")");
								// Current LOG object ---wasGeneratedBy---> PHENIX Process
								objectRelationship.addRelationship(objectId,
										BrilRelationshipType.wasGeneratedBy.getRelation(), phenixProcessId);
								System.out.println("relationship: " + objectId + " --wasGeneratedBy--> " + phenixProcessId);
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
}