package uk.ac.kcl.cerch.bril.test;

import java.util.Iterator;
import java.util.List;

import uk.ac.kcl.cerch.bril.relationship.ObjectRelationship;
import uk.ac.kcl.cerch.bril.relationship.Relationship;
import uk.ac.kcl.cerch.bril.relationship.generator.DEFFileCCP4RelationshipGeneratorImpl;
import junit.framework.TestCase;

public class DEFFileCCP4RelationshipGeneratorImplTest extends TestCase{
	
	public void testGenerateRelationships(){
		//database.def
		//String objectID="bril:d618be7d-40ee-4b63-b8eb-41aa0a2c0f57";
		//3_chainsaw.def in the object store
		String objectID="bril:3149948d-0a71-44b4-ba05-7f5b0f55b034"; 
		
		//C:/brilstore/00EXPT123/CCP4_DATABASE/database.def
		String objectID1="bril:83af4a6f-8f60-45df-b045-91f252f43d7b";
		
		String experimentId="expaa4ca950-a82f-49a7-93e9-59641de0ddb8";
	ObjectRelationship objectRelationship =  new DEFFileCCP4RelationshipGeneratorImpl()
	.generateRelationships(objectID, experimentId);
	
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