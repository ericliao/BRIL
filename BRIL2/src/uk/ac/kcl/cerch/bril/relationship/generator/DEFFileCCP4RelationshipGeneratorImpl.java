package uk.ac.kcl.cerch.bril.relationship.generator;

import java.io.File;
//import java.util.Date;
//import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import uk.ac.kcl.cerch.bril.ccp4.TaskObjectVector;
import uk.ac.kcl.cerch.bril.ccp4.TaskObject;
import uk.ac.kcl.cerch.bril.ccp4.processor.log.DEFDatabaseProcessor;
import uk.ac.kcl.cerch.bril.ccp4.processor.log.DEFTaskProcessor;
import uk.ac.kcl.cerch.bril.common.util.DateTime;
import uk.ac.kcl.cerch.bril.fileformat.CrystallographyObjectType;
import uk.ac.kcl.cerch.bril.relationship.ObjectRelationship;
import uk.ac.kcl.cerch.bril.relationship.common.GeneratorUtils;
import uk.ac.kcl.cerch.soapi.objectstore.ObjectStoreException;

/**
 * @author Shri
 * @author Eric Liao
 * 
 */
public class DEFFileCCP4RelationshipGeneratorImpl implements
		DEFFileCCP4RelationshipGenerator {
	private GeneratorUtils generatorUtils;
	private ObjectRelationship objectRelationship;
	private String filepath;
	private String objectId;
	private TaskObjectVector taskObjectVector;
	private TaskObject taskObject;
	@Override
	public ObjectRelationship generateRelationships(String objectId,
			String experimentId) {
		if (experimentId.contains("bril:") == false) {
			experimentId = "bril:" + experimentId;
		}
		this.objectId = objectId;
		generatorUtils = new GeneratorUtils();

		try {
			/*
			 * Set the DC title and date of this DEF file object and the
			 * filecharacterisation objectartifact
			 */
			generatorUtils.searchForArchivalObjects(objectId);
		} catch (ObjectStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.objectRelationship = new ObjectRelationship();
		this.objectRelationship.addRelationship(objectId, "isPartOf", experimentId);
		mapToTaskObject(objectId);

		return objectRelationship;
	}
	
	public void mapToTaskObject(String objectId){
		/**
		 * Process the file of this object to get the taskobjectVector
		 */
		if (filepath == null) {			
			setFilePathFromArchivalObject(objectId);
		}
		File file = new File(filepath);
		String filename = filepath.substring(filepath.lastIndexOf("/") + 1);
		System.out.println(filename);
		if (filename.equals("database.def") || filename.equals("tmp_database.def")) {
		  	System.out.println("Processing database.def file");
			this.taskObjectVector = getTaskObjectVector(objectId, file);			
		} else {
			System.out.println("Processing task def file");
			this.taskObject = getTaskObject(objectId, file);
			
		}
	}

	private TaskObjectVector getTaskObjectVector(String objectID, File file) {
		// String filepath = null;
		TaskObjectVector taskObjectVector = new TaskObjectVector();
			DEFDatabaseProcessor dbProcessor = new DEFDatabaseProcessor(file);
			taskObjectVector = dbProcessor.processFileData();
		
		return taskObjectVector;
	}
	
	
	public TaskObject getTaskObject(){
		return this.taskObject;
	}
	
	public TaskObjectVector getTaskObjectVector(){
		return this.taskObjectVector;
	}

	private TaskObject getTaskObject(String objectID, File file ) {
		TaskObject taskObject = new TaskObject();
		DEFTaskProcessor taskProcessor = new DEFTaskProcessor(file);
		taskObject =  taskProcessor.readTaskDEFFile();		
		return taskObject;
	}

	public void setFilePath(String filepath) {
		this.filepath = filepath;
	}

	private void setFilePathFromArchivalObject(String objectID) {

		try {
			this.filepath = generatorUtils.getOriginalContentFilePath(objectID);
			System.out.println(filepath);
		} catch (ObjectStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
