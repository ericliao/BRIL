package uk.ac.kcl.cerch.bril.test;

import java.io.ByteArrayOutputStream;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import uk.ac.kcl.cerch.bril.common.fedora.FedoraRelsExt;
import uk.ac.kcl.cerch.bril.common.fedora.FedoraNamespaceContext.FedoraNamespace;
import uk.ac.kcl.cerch.bril.common.types.BrilTransformException;
import junit.framework.TestCase;

public class FedoraRelsExtTest extends TestCase{
	
	public void testAddRelationship() throws ParserConfigurationException, BrilTransformException{
		System.out.println("Create FedoraRelsExt object---");
		FedoraRelsExt relsExt = new FedoraRelsExt("bril:123");
		QName predicate = new QName( FedoraNamespace.FEDORARELSEXT.getURI(), "isPartOf",FedoraNamespace.FEDORA.getPrefix() );
		QName object = new QName( "","111",FedoraNamespace.FEDORA.getURI()+FedoraNamespace.BRIL.getPrefix());
		//QName(String namespaceURI, String localPart, String prefix) 
		QName predicate1 = new QName( FedoraNamespace.BRILRELS.getURI(), "wasDerivedFrom",FedoraNamespace.BRILRELS.getPrefix() );
		QName object1 = new QName( "","112",FedoraNamespace.FEDORA.getURI()+FedoraNamespace.BRIL.getPrefix());
		
		QName predicate2 = new QName( FedoraNamespace.BRILRELS.getURI(), "wasGeneratedByTask",FedoraNamespace.BRILRELS.getPrefix() );
		QName object2 = new QName( "","phenix.refine","");
	
		
		relsExt.addRelationship(predicate, object);	
		relsExt.addRelationship(predicate1, object1);	
		relsExt.addRelationship(predicate2, object2);	
		ByteArrayOutputStream relsExt_baos = new ByteArrayOutputStream();
		relsExt.serialize(relsExt_baos,"");
		
		byte[] a= relsExt_baos.toByteArray();
		
		System.out.println("Relationship (relsext) created---" );
		System.out.println(new String(a));
	}
}
