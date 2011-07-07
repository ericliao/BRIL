package uk.ac.kcl.cerch.bril.test;

import java.util.Iterator;
import java.util.List;

//import javax.xml.namespace.QName;


//import uk.ac.kcl.cerch.bril.common.fedora.FedoraNamespaceContext.FedoraNamespace;
import uk.ac.kcl.cerch.bril.relationship.ObjectRelationship;
import uk.ac.kcl.cerch.bril.relationship.Relationship;
//import uk.ac.kcl.cerch.bril.relationship.generator.DiffractionImageFileRelationshipGenerator;
import uk.ac.kcl.cerch.bril.relationship.generator.DiffractionImageFileRelationshipGeneratorImpl;
import junit.framework.TestCase;

public class DiffractionImageFileRelationshipGeneratorTest extends TestCase{
	
	public void testGenerateRelationships(){
		ObjectRelationship objectRelationship= (ObjectRelationship) new DiffractionImageFileRelationshipGeneratorImpl()
		.generateRelationships("bril:79uu","00EXPT123");
		//DiffractionImageFileRelationshipGenerator or = new DiffractionImageFileRelationshipGeneratorImpl();
		//or.generateRelationships("bril:79uu","bril:00EXPT123");
	List<Relationship> relsList= objectRelationship.getRelationships();
	Iterator <Relationship>relationshipIter = relsList.iterator();
	while(relationshipIter.hasNext()){
		Relationship relation = (Relationship) relationshipIter.next();
		System.out.println(relation.getSubject());
		System.out.println(relation.getPredicate());
		System.out.println(relation.getObject());
		}
	}

}
