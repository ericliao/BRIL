package uk.ac.kcl.cerch.bril.test;

import java.awt.List;
import java.io.File;
import java.util.Vector;

import uk.ac.kcl.cerch.bril.characteriser.COOTScmFileCharacterisation;
import uk.ac.kcl.cerch.bril.characteriser.COOTScmFileCharacteriserImpl;
import uk.ac.kcl.cerch.bril.common.fedora.FedoraNamespaceContext.FedoraNamespace;
import uk.ac.kcl.cerch.bril.common.types.BrilRelationshipType;
import uk.ac.kcl.cerch.bril.relationship.ObjectRelationship;
import uk.ac.kcl.cerch.bril.relationship.Relationship;
import uk.ac.kcl.cerch.bril.relationship.common.GeneratorUtils;
import uk.ac.kcl.cerch.bril.relationship.generator.COOTScmFileRelationshipGeneratorImpl;
import uk.ac.kcl.cerch.soapi.characteriser.FileCharacteriserException;

public class COOTScmFileCharacterisationMainTest {
	public static void main(String arg[]){
		//File file = new File("C:\\brilstore\\00EXPT123\\phaser\\0-coot.state.scm");
		File file = new File("/home/shrijar/brilstore/expa416b0b2-de69-471c-bd63-fcb64dc15a28/0-coot.state.scm");
		
		/*COOTScmFileCharacterisation fileCharacterisation;
		try {
			fileCharacterisation = (COOTScmFileCharacterisation) new COOTScmFileCharacteriserImpl()
			.characteriseFile(file);
			System.out.println(fileCharacterisation.getMetadata());

		} catch (FileCharacteriserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	/*	String cootId ="bril:0d117557-0db3-42d5-8e8f-76315e864def";
		System.out.println("COOTScmFileRelationshipGeneratorImpl -----------");
		ObjectRelationship objectRelationship = (ObjectRelationship) new COOTScmFileRelationshipGeneratorImpl()
		.generateRelationships(cootId, "bril:expa416b0b2-de69-471c-bd63-fcb64dc15a28");
	int size= objectRelationship.getRelationships().size();
	
	java.util.List<Relationship> rel = objectRelationship.getRelationships();
	for (int i=0; i<size ;i++){
		System.out.println(rel.get(i).getSubject());
		System.out.println(rel.get(i).getPredicate());
		System.out.println(rel.get(i).getObject());
		System.out.println("----------------------------------");
	}
	*/
	testGenerateUtil();
	}
	private static void testGenerateUtil(){
		String phenixObjectId ="bril:acb366f8-73da-47cf-af25-d581bb98f228";
		String experimentId= "bril:expa416b0b2-de69-471c-bd63-fcb64dc15a28";
		GeneratorUtils generatorUtils = new GeneratorUtils();
		Vector<String> resultIdVector =generatorUtils.
		getSubjectsFrom( experimentId, "coordinateFile", "usedPhenixParameterFile", phenixObjectId );
		System.out.println("has relationhsip:---");
		System.out.println(resultIdVector);
		
		//C:\BRIL\experiment\baa5d5\1\1ref_001.pdb
		String subject="bril:ad2dec9b-ea92-4c53-813a-9fbbff700a5d";
		
		//C:\BRIL\experiment\baa5d5\phaser\phaser-solution-new_chain_ids.pdb
		String object ="bril:b5cf8cf3-8fe0-40f9-b12b-3645bc4afaa8";
		String brilrels= FedoraNamespace.BRILRELS.getURI();
		//String predicate=  brilrels+BrilRelationshipType.wasDerivedFrom.getRelation();
		
		boolean res =generatorUtils.hasRelationship(subject,BrilRelationshipType.wasDerivedFrom.getRelation() , object);
		System.out.println(res);
	}
}
