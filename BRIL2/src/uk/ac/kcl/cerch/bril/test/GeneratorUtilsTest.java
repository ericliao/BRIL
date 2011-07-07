package uk.ac.kcl.cerch.bril.test;


import java.util.Map;
import java.util.Vector;

import uk.ac.kcl.cerch.bril.characteriser.TaskObjectElement;
//import uk.ac.kcl.cerch.bril.common.fedora.BrilObjectRepositoryException;
import uk.ac.kcl.cerch.bril.common.types.DataStreamType;
import uk.ac.kcl.cerch.bril.fileformat.CrystallographyObjectType;
import uk.ac.kcl.cerch.bril.relationship.common.GeneratorUtils;
import junit.framework.TestCase;

public class GeneratorUtilsTest extends TestCase{
/*public void testSearchForObjectIdsOfType(){
	GeneratorUtils gu = new GeneratorUtils();
	String experimentId="info:fedora/bril:expaa4ca950-a82f-49a7-93e9-59641de0ddb8";
	CrystallographyObjectType objectType=CrystallographyObjectType.CoordinateFile;
	

	Map<String, Vector<String>> result = new HashMap <String, Vector<String>> ();
//	result =	gu.searchForObjectIdsOfType(experimentId, objectType);
	result =	gu.searchForObjectIdsTitle(experimentId, objectType);
	for(Map.Entry<String, Vector<String>> entry : result.entrySet()){
		System.out.println(entry.getKey());
		System.out.println(entry.getValue());
	}
	Vector d = gu.searchForImageSetObjectIds(experimentId, "title", "DiffractionImageSet");
	System.out.println(d);
	//Vector<String> res = gu.removeDuplicates(d);
	//System.out.println(res);
}*/

public void testGetTaskObjectVectorFromXML(){
	

	//String defCCP4dbId= "bril:3035bb51-d554-4ca4-810d-eaea730d5a8d";
	String defPhenixId= "bril:518921be-c846-4eb0-ba19-df228f7d6b11";
	String selectedCOMObjectId= "bril:c9537b12-b535-417d-b05a-1d15845ddc8a";

	GeneratorUtils generatorUtils = new GeneratorUtils();
	
	byte[] comFileMetadata = generatorUtils.getDatastreamType(
			defPhenixId, DataStreamType.ObjectMetadata);
	
	byte[] defFileMetadata = generatorUtils.getDatastreamType(
			selectedCOMObjectId, DataStreamType.ObjectMetadata);

 String value = new String(defFileMetadata);
		System.out.println(value);
	
	String elementvalue = generatorUtils.getValueFromTaskXML(defFileMetadata, TaskObjectElement.TASK_NAME);
	System.out.println(elementvalue);
}

public void testSearchForObjectIdsOfType(){
	String experimentId="bril:expaa4ca950-a82f-49a7-93e9-59641de0ddb8";
	
	GeneratorUtils generatorUtils = new GeneratorUtils();
	/*
	 * Run ITQL query and puts the SPARQL result in the MAP object
	 */
	 Map<String, Vector<String>>  result = generatorUtils.searchForObjectIdsOfType(experimentId, CrystallographyObjectType.MTZReflectionFile);
	
	 System.out.println(result);
	}

public void testSearchForObjectIdsTitle(){
String experimentId="bril:expaa4ca950-a82f-49a7-93e9-59641de0ddb8";
	
	GeneratorUtils generatorUtils = new GeneratorUtils();
	/*
	 * Run ITQL query and puts the SPARQL result in the MAP object
	 */
	Map<String, Vector<String>>  result = generatorUtils.searchForObjectIdsTitle(experimentId, CrystallographyObjectType.MTZReflectionFile);
		
	System.out.println(result);
	
}
}
