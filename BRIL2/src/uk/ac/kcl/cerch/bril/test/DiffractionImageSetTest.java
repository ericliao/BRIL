package uk.ac.kcl.cerch.bril.test;

import uk.ac.kcl.cerch.bril.relationship.generator.DiffractionImageFileRelationshipGeneratorImpl;

public class DiffractionImageSetTest {
	public static void main(String args[]){
		DiffractionImageFileRelationshipGeneratorImpl df= new DiffractionImageFileRelationshipGeneratorImpl();
	//df.generateRelationships("bril:test", "bril:expa416b0b2-de69-471c-bd63-fcb64dc15a28");
	if(df.checkForDiffractionImageSet("bril:expa416b0b2-de69-471c-bd63-fcb64dc15a28")==true){
		System.out.println(df.getDiffractionImageSetObjectId());
	}
	
}
}
