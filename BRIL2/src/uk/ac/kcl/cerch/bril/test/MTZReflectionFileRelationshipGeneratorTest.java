package uk.ac.kcl.cerch.bril.test;

import java.util.Iterator;
import java.util.List;

import uk.ac.kcl.cerch.bril.relationship.ObjectRelationship;
import uk.ac.kcl.cerch.bril.relationship.Relationship;
import uk.ac.kcl.cerch.bril.relationship.generator.MTZReflectionFileRelationshipGeneratorImpl;
import junit.framework.TestCase;

public class MTZReflectionFileRelationshipGeneratorTest extends TestCase{
	
	public void testGenerateRelationships(){
		
		// bril:33f7e9b9-a62b-4c8d-847c-8e66bc90f830 is the id for a current
		//object in the objectstore whose relationship is being generated
		//This is the file '5d5.mtz' that wasDerivedFrom' --> 'truncate.mtz' using 'wasCausedBy' --> script 'free.com'
		//
		/**
		 * Test case 1: the free.com file and truncate.mtz file are present in the repository 
		 * and all the preceding (creation dates) mtz files are also present in the repository
		 */
		
		/**
		 * Test case 2: truncate.mtz file are present in the repository 
		 * and all the preceding (creation dates) mtz files are also present in the repository
		 */
		
		/**
		 * Test case 3: high-sorted.mtz file are present in the repository 
		 * and all the preceding (creation dates) mtz files are also present in the repository
		 * Also scala1.com that used this mtz file as input is present in the repo.
		 * 
		 */
		//scala1.mtz should check scala1.com in the repo
		String objectID="bril:cc39f1a8-ef8b-4cd4-8e66-158c8cbe0fcb";
		//1ref_001_map_coeffs.mtz should check the ref1.def in the repo
		String objectID1="bril:b46c497b-fe52-425f-9080-723cbbed8337";
		//String objectID2="bril:b46c497b-fe52-425f-9080-723cbbed8337";
		String experimentId="expaa4ca950-a82f-49a7-93e9-59641de0ddb8";
		ObjectRelationship objectRelationship = (ObjectRelationship) new MTZReflectionFileRelationshipGeneratorImpl()
		.generateRelationships(objectID1,experimentId);
		//pid for truncate.mtz present in objectstore
		//boolean rel =new MTZReflectionFileRelationshipGeneratorImpl().generatorUtils.hasRelationship(objectRelationship,"wasDerivedFrom");
		//if(rel==true){
		//System.out.println("value boolean: "+rel);
		//}
		List<Relationship> relsList= objectRelationship.getRelationships();
	    Iterator <Relationship>relationshipIter = relsList.iterator();
	    while(relationshipIter.hasNext()){
		Relationship relation = (Relationship) relationshipIter.next();
		System.out.println("------------------------------------------------------");
		System.out.println(relation.getSubject());
		System.out.println(relation.getPredicate());
		System.out.println(relation.getObject());
		System.out.println("------------------------------------------------------");

		}
	}
	
}