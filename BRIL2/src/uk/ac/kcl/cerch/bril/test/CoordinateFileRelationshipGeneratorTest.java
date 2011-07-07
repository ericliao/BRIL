package uk.ac.kcl.cerch.bril.test;

import java.util.Iterator;
import java.util.List;

import uk.ac.kcl.cerch.bril.relationship.ObjectRelationship;
import uk.ac.kcl.cerch.bril.relationship.Relationship;
import uk.ac.kcl.cerch.bril.relationship.generator.CoordinateFileRelationshipGeneratorImpl;
import junit.framework.TestCase;

public class CoordinateFileRelationshipGeneratorTest extends TestCase{
	
	public void testGenerateRelationships(){
		
		String experimentId ="expaa4ca950-a82f-49a7-93e9-59641de0ddb8";
		//1YY8b_chainsaw1.pdb  
		//to test database def works
		String objectID ="bril:e9707751-af1a-4ac3-b0db-c8c3138f49e4";		
		//phaser-solution-new_chain_ids.pdb  // basic relation found
		String objectID1= "bril:c4b1942f-8ea5-4dba-8c8d-776f33f5cbf4";
		
		//1ref_001.pdb
		//search for phenix def file works
		String objectID2= "bril:0788d40c-4e35-484a-b7b8-31df5345b7d8";
		ObjectRelationship objectRelationship = (ObjectRelationship) new CoordinateFileRelationshipGeneratorImpl()
	
		.generateRelationships(objectID2,experimentId);
		
		List<Relationship> relsList= objectRelationship.getRelationships();
	    Iterator<Relationship> relationshipIter = relsList.iterator();
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
